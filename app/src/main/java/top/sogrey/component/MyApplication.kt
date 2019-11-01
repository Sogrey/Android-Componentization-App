package top.sogrey.component

import android.content.Context
import com.alibaba.android.arouter.launcher.ARouter
import top.sogrey.common.BaseApplication
import androidx.multidex.MultiDex
import top.sogrey.common.utils.ktx.getAppName


class MyApplication : BaseApplication() {

    override fun getAppTag(): String = getAppName()
    override fun isDebug(): Boolean = BuildConfig.DEBUG

    override fun onCreate() {
        super.onCreate()

//        isDebug(BuildConfig.DEBUG)

        initARouter()
    }

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
        MultiDex.install(this)
    }

    private fun initARouter() {
        if (BuildConfig.DEBUG) {           // These two lines must be written before init, otherwise these configurations will be invalid in the init process
            ARouter.openLog()    // Print log
            ARouter.openDebug()   // Turn on debugging mode (If you are running in InstantRun mode, you must turn on debug mode! Online version needs to be closed, otherwise there is a security risk)
        }
        ARouter.init(this) // As early as possible, it is recommended to initialize in the Application
    }
}