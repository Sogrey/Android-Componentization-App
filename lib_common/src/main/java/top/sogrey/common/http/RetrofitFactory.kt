package top.sogrey.common.http


import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import top.sogrey.common.utils.AppUtils
import top.sogrey.common.utils.NetworkUtils
import top.sogrey.common.utils.logD
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Created by 眼神 on 2018/3/27.
 * 封装Retrofit配置
 */

class RetrofitFactory {

    companion object {
        //TODO 填写自己的包名
        val CACHE_NAME = AppUtils.getApp().packageName
        const val BASE_URL = "https://api.github.com"
        private const val DEFAULT_CONNECT_TIMEOUT = 30
        private const val DEFAULT_WRITE_TIMEOUT = 30
        private const val DEFAULT_READ_TIMEOUT = 30

        /** GitHub接口身份验证 */
        const val GITHUB_CLIENT_ID = "114d8e520e3c6a65fe47"
        const val GITHUB_CLIENT_SECRET = "7f10e85cab5d88b2c68fc34281ca675f9fa1ec44"

        //获取单例
        val instance: RetrofitFactory
            get() = SingletonHolder.INSTANCE
    }

    var TAG = "RetrofitFactory"

    var retrofit: Retrofit? = null

    var httpApi: HttpApi? = null
    /**
     * 请求失败重连次数
     */
    private val RETRY_COUNT = 0
    private val okHttpBuilder: OkHttpClient.Builder = OkHttpClient.Builder()

    init {
        //手动创建一个OkHttpClient并设置超时时间
        /**
         * 设置缓存
         */
        val cacheFile = File(AppUtils.getApp().externalCacheDir, CACHE_NAME)
        Cache(cacheFile, (1024 * 1024 * 50).toLong())
        Interceptor { chain ->
            var request = chain.request()
            if (!NetworkUtils.isConnected()) {
                request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build()
            }
            val response = chain.proceed(request)
            if (!NetworkUtils.isConnected()) {
                val maxAge = 0
                // 有网络时 设置缓存超时时间0个小时
                response.newBuilder()
                    .header("Cache-Control", "public, max-age=$maxAge")
                    .removeHeader(CACHE_NAME)// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                    .build()
            } else {
                // 无网络时，设置超时为4周
                val maxStale = 60 * 60 * 24 * 28
                response.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                    .removeHeader(CACHE_NAME)
                    .build()
            }
            response
        }

        /**
         * 设置头信息
         */
        val headerInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()

            val requestBuilder = originalRequest.newBuilder()
                .addHeader("Accept-Encoding", "gzip")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .method(originalRequest.method, originalRequest.body)

            //添加请求头信息，服务器进行token有效性验证
//      requestBuilder.addHeader("Authorization", "Bearer " + BaseConstant.TOKEN)

            //添加公共参数
            val globalParam = originalRequest.url.newBuilder()
                .addQueryParameter("client_id", GITHUB_CLIENT_ID)
                .addQueryParameter("client_secret", GITHUB_CLIENT_SECRET)
            requestBuilder.url(globalParam.build())

            val request = requestBuilder.build()
            chain.proceed(request)
        }

        okHttpBuilder.addInterceptor(headerInterceptor)


//        //        if (BuildConfig.DEBUG) {
//        val loggingInterceptor =
//            HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message -> logD(message) })
//        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
//        //设置 Debug Log 模式
//        okHttpBuilder.addInterceptor(loggingInterceptor)
//        //        }

        /**
         * 设置超时和重新连接
         */
        okHttpBuilder.connectTimeout(DEFAULT_CONNECT_TIMEOUT.toLong(), TimeUnit.SECONDS)
        okHttpBuilder.readTimeout(DEFAULT_WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
        okHttpBuilder.writeTimeout(DEFAULT_READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
        //错误重连
        okHttpBuilder.retryOnConnectionFailure(true)


        retrofit = Retrofit.Builder()
            .client(okHttpBuilder.build())
            .addConverterFactory(GsonConverterFactory.create())//json转换成JavaBean
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(BASE_URL)
            .build()
        httpApi = retrofit!!.create<HttpApi>(HttpApi::class.java)
    }

    //在访问HttpMethods时创建单例
    private object SingletonHolder {
        val INSTANCE = RetrofitFactory()

    }

    fun changeBaseUrl(baseUrl: String) {
        retrofit = Retrofit.Builder()
            .client(okHttpBuilder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(baseUrl)
            .build()
        httpApi = retrofit!!.create<HttpApi>(HttpApi::class.java)
    }

    /**
     * 设置订阅 和 所在的线程环境
     */
    fun <T> toSubscribe(o: Observable<T>, s: DisposableObserver<T>) {

        o.subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .retry(RETRY_COUNT.toLong())//请求失败重连次数
            .subscribe(s)

    }

}