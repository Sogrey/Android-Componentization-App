package top.sogrey.common

import android.app.Activity
import android.app.Application
import android.os.Bundle
import top.sogrey.common.utils.AppUtils
import top.sogrey.common.utils.CrashUtils
import top.sogrey.common.utils.KLog
import top.sogrey.common.utils.logE


abstract class BaseApplication : Application() {

    //当前Activity，当app退出到后台后这个curActivity会变为null
    private var curActivity: Activity? = null

    override fun onCreate() {
        super.onCreate()

        initConfig()

        AppUtils.init(this)
        initCrash()
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
                curActivity = activity
            }

            override fun onActivityPaused(activity: Activity) {
                curActivity = null
            }

            override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }
        })
    }

    private fun initConfig() {
        AppConfig.DEBUG = BuildConfig.DEBUG && isDebug()
        AppConfig.APP_TAG = getAppTag()

        KLog.getSettings().setLogEnable(AppConfig.DEBUG)
        KLog.getSettings().setBorderEnable(true)
        KLog.getSettings().setInfoEnable(true)
        KLog.getSettings().setLogEnable(AppConfig.DEBUG)
    }

    protected abstract fun getAppTag(): String

    protected abstract fun isDebug(): Boolean

    private fun initCrash() {

        CrashUtils.init(object : CrashUtils.Companion.OnCrashListener {
            override fun onCrash(crashInfo: String, e: Throwable?) {
                logE(crashInfo)
                AppUtils.relaunchApp()
            }
        })

    }
}