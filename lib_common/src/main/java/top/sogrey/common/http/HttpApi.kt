package top.sogrey.common.http

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

interface HttpApi {
    /**
     * 通过用户名获取用户信息
     * @param userName 用户名
     * @return  用户基本信息https://api.github.com/users/?userName=bigeyechou
     * */
//  @GET("/users/")
//  fun getUserInfoByName(@Query("userName") userName:String): Observable<ResponseBody>

    @GET("/users/{userName}")
    fun getUserInfoByName(@Path("userName") userName: String): Observable<ResponseBody>
}