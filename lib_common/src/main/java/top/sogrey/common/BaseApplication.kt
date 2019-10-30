package top.sogrey.common

import android.app.Application
import top.sogrey.common.utils.AppUtils

open class BaseApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        AppUtils.init(this)
    }

    fun isDebug(debug:Boolean){
        AppConfig.DEBUG = BuildConfig.DEBUG && debug
    }
}