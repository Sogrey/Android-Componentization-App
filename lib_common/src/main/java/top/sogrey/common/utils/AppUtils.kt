package top.sogrey.common.utils

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.AppOpsManager
import android.app.Application
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.WindowManager
import androidx.core.content.FileProvider
import java.io.File
import java.lang.reflect.InvocationTargetException
import java.util.*

class AppUtils {
    constructor(){
        throw UnsupportedOperationException(this.javaClass.simpleName + " cannot be instantiated")}
    companion object{

        private val ACTIVITY_LIFECYCLE = ActivityLifecycleImpl()
//        @SuppressLint("StaticFieldLeak")
        private var sApplication: Application? = null

        /**
         * Init utils.
         *
         * Init it in the class of Application.
         *
         * @param context context
         */
        fun init(context: Context?) {
            if (context == null) {
                init(getApplicationByReflect())
                return
            }
            init(context.applicationContext as Application)
        }

        /**
         * Init utils.
         *
         * Init it in the class of Application.
         *
         * @param app application
         */
        fun init(app: Application?) {
            if (sApplication == null) {
                sApplication = app ?: getApplicationByReflect()
                sApplication!!.registerActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE)
            } else {
                if (app != null && app.javaClass != sApplication!!.javaClass) {
                    sApplication!!.unregisterActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE)
                    ACTIVITY_LIFECYCLE.mActivityList.clear()
                    sApplication = app
                    sApplication!!.registerActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE)
                }
            }
        }

        /**
         * Return the context of Application object.
         *
         * @return the context of Application object
         */
        fun getApp(): Application {
            if (sApplication != null) {
                return sApplication!!
            }
            val app = getApplicationByReflect()
            init(app)
            return app
        }

        private fun getApplicationByReflect(): Application {
            try {
                @SuppressLint("PrivateApi")
                val activityThread = Class.forName("android.app.ActivityThread")
                val thread = activityThread.getMethod("currentActivityThread").invoke(null)
                val app = activityThread.getMethod("getApplication").invoke(thread)
                    ?: throw NullPointerException("u should init first")
                return app as Application
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }

            throw NullPointerException("u should init first")
        }

        /**
         * 应用状态监测
         */
        interface OnAppStatusChangedListener {
            /**
             * 在前台
             */
            fun onForeground()

            /**
             * 退至后台
             */
            fun onBackground()
        }

        /**
         * Activity销毁监听
         */
        interface OnActivityDestroyedListener {
            /**
             * Activity销毁事件
             *
             * @param activity 被销毁的 Activity
             */
            fun onActivityDestroyed(activity: Activity)
        }

        internal class ActivityLifecycleImpl : Application.ActivityLifecycleCallbacks {

            val mActivityList = LinkedList<Activity>()
            private val mStatusListenerMap: MutableMap<Any, OnAppStatusChangedListener> = HashMap()
            private val mDestroyedListenerMap: MutableMap<Activity, MutableSet<OnActivityDestroyedListener>> =
                HashMap()

            private var mForegroundCount = 0
            private var mConfigCount = 0
            private var mIsBackground = false

            var topActivity: Activity?
                get() {
                    if (!mActivityList.isEmpty()) {
                        for (i in mActivityList.indices.reversed()) {
                            val activity = mActivityList[i]
                            if (activity.isFinishing || Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed
                            ) {
                                continue
                            }
                            return activity
                        }
                    }
                    val topActivityByReflect = topActivityByReflect
                    if (topActivityByReflect != null) {
                        topActivity = topActivityByReflect
                    }
                    return topActivityByReflect
                }
                private set(activity) {
                    if (mActivityList.contains(activity)) {
                        if (mActivityList.last != activity) {
                            mActivityList.remove(activity)
                            mActivityList.addLast(activity)
                        }
                    } else {
                        mActivityList.addLast(activity)
                    }
                }

            private val topActivityByReflect: Activity?
                get() {
                    try {
                        @SuppressLint("PrivateApi")
                        val activityThreadClass = Class.forName("android.app.ActivityThread")
                        val currentActivityThreadMethod =
                            activityThreadClass.getMethod("currentActivityThread").invoke(null)
                        val mActivityListField =
                            activityThreadClass.getDeclaredField("mActivityList")
                        mActivityListField.isAccessible = true
                        val activities =
                            mActivityListField.get(currentActivityThreadMethod) as Map<*, *>
                        for (activityRecord in activities.values) {
                            if(activityRecord!=null){
                                val activityRecordClass = activityRecord.javaClass
                                val pausedField = activityRecordClass.getDeclaredField("paused")
                                pausedField.isAccessible = true
                                if (!pausedField.getBoolean(activityRecord)) {
                                    val activityField = activityRecordClass.getDeclaredField("activity")
                                    activityField.isAccessible = true
                                    return activityField.get(activityRecord) as Activity
                                }
                            }
                        }
                    } catch (e: ClassNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                    } catch (e: InvocationTargetException) {
                        e.printStackTrace()
                    } catch (e: NoSuchMethodException) {
                        e.printStackTrace()
                    } catch (e: NoSuchFieldException) {
                        e.printStackTrace()
                    }

                    return null
                }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                LanguageUtils.applyLanguage(activity)
                setAnimatorsEnabled()
                topActivity = activity
            }

            override fun onActivityStarted(activity: Activity) {
                if (!mIsBackground) {
                    topActivity = activity
                }
                if (mConfigCount < 0) {
                    ++mConfigCount
                } else {
                    ++mForegroundCount
                }
            }

            override fun onActivityResumed(activity: Activity) {
                topActivity = activity
                if (mIsBackground) {
                    mIsBackground = false
                    postStatus(true)
                }
                processHideSoftInputOnActivityDestroy(activity, false)
            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {
                if (activity.isChangingConfigurations) {
                    --mConfigCount
                } else {
                    --mForegroundCount
                    if (mForegroundCount <= 0) {
                        mIsBackground = true
                        postStatus(false)
                    }
                }
                processHideSoftInputOnActivityDestroy(activity, true)
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {/**/
            }

            override fun onActivityDestroyed(activity: Activity) {
                mActivityList.remove(activity)
                consumeOnActivityDestroyedListener(activity)
                KeyboardUtils.fixSoftInputLeaks(activity.window)
            }

            fun addOnAppStatusChangedListener(
                `object`: Any,
                listener: OnAppStatusChangedListener
            ) {
                mStatusListenerMap[`object`] = listener
            }

            fun removeOnAppStatusChangedListener(`object`: Any) {
                mStatusListenerMap.remove(`object`)
            }

            fun removeOnActivityDestroyedListener(activity: Activity?) {
                if (activity == null) {
                    return
                }
                mDestroyedListenerMap.remove(activity)
            }

            fun addOnActivityDestroyedListener(
                activity: Activity?,
                listener: OnActivityDestroyedListener?
            ) {
                if (activity == null || listener == null) {
                    return
                }
                val listeners: MutableSet<OnActivityDestroyedListener>?
                if (!mDestroyedListenerMap.containsKey(activity)) {
                    listeners = HashSet()
                    mDestroyedListenerMap[activity] = listeners
                } else {
                    listeners = mDestroyedListenerMap[activity]
                    if (listeners!!.contains(listener)) {
                        return
                    }
                }
                listeners.add(listener)
            }

            /**
             * To solve close keyboard when activity onDestroy.
             * The preActivity set windowSoftInputMode will prevent
             * the keyboard from closing when curActivity onDestroy.
             */
            private fun processHideSoftInputOnActivityDestroy(activity: Activity, isSave: Boolean) {
                if (isSave) {
                    val attrs = activity.window.attributes
                    val softInputMode = attrs.softInputMode
                    activity.window.decorView.setTag(-123, softInputMode)
                    activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                } else {
                    val tag = activity.window.decorView.getTag(-123)
                    if (tag !is Int) {
                        return
                    }
                    val handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({ activity.window.setSoftInputMode(tag) }, 100)
                }
            }

            private fun postStatus(isForeground: Boolean) {
                if (mStatusListenerMap.isEmpty()) {
                    return
                }
                for (onAppStatusChangedListener in mStatusListenerMap.values) {
                    if (isForeground) {
                        onAppStatusChangedListener.onForeground()
                    } else {
                        onAppStatusChangedListener.onBackground()
                    }
                }
            }

            private fun consumeOnActivityDestroyedListener(activity: Activity) {
                val iterator = mDestroyedListenerMap.entries.iterator()
                while (iterator.hasNext()) {
                    val entry = iterator.next()
                    if (entry.key === activity) {
                        val value = entry.value
                        for (listener in value) {
                            listener.onActivityDestroyed(activity)
                        }
                        iterator.remove()
                    }
                }
            }
        }

        /**
         * Set animators enabled.
         */
        private fun setAnimatorsEnabled() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && ValueAnimator.areAnimatorsEnabled()) {
                return
            }
            try {

                val sDurationScaleField =
                    ValueAnimator::class.java.getDeclaredField("sDurationScale")
                sDurationScaleField.isAccessible = true
                val sDurationScale = sDurationScaleField.get(null) as Float
                if (sDurationScale == 0f) {
                    sDurationScaleField.set(null, 1f)
                    LogKtUtils.i("Utils", "setAnimatorsEnabled: Animators are enabled now!")
                }
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }

        }

        ////////////////////////////////////////////////////////////////////////////////////////////////
        // 扩展方法
        ////////////////////////////////////////////////////////////////////////////////////////////////

        /**
         * 检测手机是否安装某个应用,Return whether the app is installed.
         *
         * @param context
         * @param appPackageName 应用包名
         * @return true-安装，false-未安装
         */
        fun isAppInstalled(context: Context, appPackageName: String): Boolean {
            // 获取所有已安装程序的包信息
            val pinfos = context.packageManager.getInstalledApplications(0)
            if (!pinfos.isEmpty()) {
                for (info in pinfos) {
                    val packageName = info.packageName
                    if (packageName == appPackageName) {
                        return true
                    }
                }
            }
            return false
        }

        /**
         * 检测手机是否安装某个应用,Return whether the app is installed.
         *
         * @param appPackageName The name of the package.
         * @return `true`: yes<br></br>`false`: no
         */
        fun isAppInstalled(appPackageName: String): Boolean {
            // 获取所有已安装程序的包信息
            val pinfos = getApp().packageManager.getInstalledApplications(0)
            if (!pinfos.isEmpty()) {
                for (info in pinfos) {
                    val packageName = info.packageName
                    if (packageName == appPackageName) {
                        return true
                    }
                }
            }
            return false
        }

        /****************
         * 安装应用
         */
        /**
         * Install the app.
         *
         * Target APIs greater than 25 must hold
         * `<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />`
         *
         * @param filePath The path of file.
         */
        fun installApp(filePath: String) {
            installApp(FileKtUtils.getFileByPath(filePath))
        }

        /**
         * Install the app.
         *
         * Target APIs greater than 25 must hold
         * `<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />`
         *
         * @param file The file.
         */
        fun installApp(file: File?) {
            if (!FileKtUtils.isFileExists(file)) {
                return
            }
            getApp().startActivity(getInstallAppIntent(file, true))
        }

        /**
         * Install the app.
         *
         * Target APIs greater than 25 must hold
         * `<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />`
         *
         * @param activity    The activity.
         * @param filePath    The path of file.
         * @param requestCode If &gt;= 0, this code will be returned in
         * onActivityResult() when the activity exits.
         */
        fun installApp(
            activity: Activity,
            filePath: String,
            requestCode: Int
        ) {
            installApp(activity, FileKtUtils.getFileByPath(filePath), requestCode)
        }

        /**
         * Install the app.
         *
         * Target APIs greater than 25 must hold
         * `<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />`
         *
         * @param activity    The activity.
         * @param file        The file.
         * @param requestCode If &gt;= 0, this code will be returned in
         * onActivityResult() when the activity exits.
         */
        fun installApp(
            activity: Activity,
            file: File?,
            requestCode: Int
        ) {
            if (!FileKtUtils.isFileExists(file)) {
                return
            }
            activity.startActivityForResult(getInstallAppIntent(file), requestCode)
        }

        /**
         * Uninstall the app.
         *
         * @param packageName The name of the package.
         */
        fun uninstallApp(packageName: String) {
            if (StringUtils.isSpace(packageName)) {
                return
            }
            getApp().startActivity(getUninstallAppIntent(packageName, true))
        }

        /**
         * Uninstall the app.
         *
         * @param activity    The activity.
         * @param packageName The name of the package.
         * @param requestCode If &gt;= 0, this code will be returned in
         * onActivityResult() when the activity exits.
         */
        fun uninstallApp(
            activity: Activity,
            packageName: String,
            requestCode: Int
        ) {
            if (StringUtils.isSpace(packageName)) {
                return
            }
            activity.startActivityForResult(getUninstallAppIntent(packageName), requestCode)
        }


        private fun getInstallAppIntent(file: File?): Intent {
            return getInstallAppIntent(file, false)
        }

        private fun getInstallAppIntent(file: File?, isNewTask: Boolean): Intent {
            val intent = Intent(Intent.ACTION_VIEW)
            val data: Uri
            val type = "application/vnd.android.package-archive"
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                data = Uri.fromFile(file)
            } else {
                val authority = getApp().packageName + ".provider"
                data = FileProvider.getUriForFile(getApp(), authority, file!!)
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            getApp().grantUriPermission(
                getApp().packageName,
                data,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            intent.setDataAndType(data, type)
            return if (isNewTask) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) else intent
        }

        private fun getUninstallAppIntent(packageName: String): Intent {
            return getUninstallAppIntent(packageName, false)
        }

        private fun getUninstallAppIntent(packageName: String, isNewTask: Boolean): Intent {
            val intent = Intent(Intent.ACTION_DELETE)
            intent.data = Uri.parse("package:$packageName")
            return if (isNewTask) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) else intent
        }

        private fun getLaunchAppIntent(packageName: String): Intent? {
            return getLaunchAppIntent(packageName, false)
        }

        private fun getLaunchAppIntent(packageName: String, isNewTask: Boolean): Intent? {
            val launcherActivity = getLauncherActivity(packageName)
            if (!launcherActivity.isEmpty()) {
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_LAUNCHER)
                val cn = ComponentName(packageName, launcherActivity)
                intent.component = cn
                return if (isNewTask) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) else intent
            }
            return null
        }

        private fun getLauncherActivity(pkg: String): String {
            val intent = Intent(Intent.ACTION_MAIN, null)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.setPackage(pkg)
            val pm = getApp().packageManager
            val info = pm.queryIntentActivities(intent, 0)
            val size = info.size
            if (size == 0) {
                return ""
            }
            for (i in 0 until size) {
                val ri = info[i]
                if (ri.activityInfo.processName == pkg) {
                    return ri.activityInfo.name
                }
            }
            return info[0].activityInfo.name
        }

        private fun getForegroundProcessName(): String? {
            val am = getApp().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

            val pInfo = am.runningAppProcesses
            if (pInfo != null && pInfo.size > 0) {
                for (aInfo in pInfo) {
                    if (aInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        return aInfo.processName
                    }
                }
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                val pm = getApp().packageManager
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                val list = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                logI("ProcessUtils", list.toString())
                if (list.size <= 0) {
                    logI(
                        "ProcessUtils",
                        "getForegroundProcessName: noun of access to usage information."
                    )
                    return ""
                }
                try {// Access to usage information.
                    val info = pm.getApplicationInfo(getApp().packageName, 0)
                    val aom = getApp().getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager

                    if (aom.checkOpNoThrow(
                            AppOpsManager.OPSTR_GET_USAGE_STATS,
                            info.uid,
                            info.packageName
                        ) != AppOpsManager.MODE_ALLOWED
                    ) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        getApp().startActivity(intent)
                    }
                    if (aom.checkOpNoThrow(
                            AppOpsManager.OPSTR_GET_USAGE_STATS,
                            info.uid,
                            info.packageName
                        ) != AppOpsManager.MODE_ALLOWED
                    ) {
                        logI(
                            "ProcessUtils",
                            "getForegroundProcessName: refuse to device usage stats."
                        )
                        return ""
                    }
                    val usageStatsManager = getApp()
                        .getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
                    var usageStatsList: List<UsageStats>? = null
                    val endTime = System.currentTimeMillis()
                    val beginTime = endTime - 86400000 * 7
                    usageStatsList = usageStatsManager
                        .queryUsageStats(
                            UsageStatsManager.INTERVAL_BEST,
                            beginTime, endTime
                        )
                    if (usageStatsList == null || usageStatsList.isEmpty()) {
                        return null
                    }
                    var recentStats: UsageStats? = null
                    for (usageStats in usageStatsList) {
                        if (recentStats == null || usageStats.lastTimeUsed > recentStats.lastTimeUsed) {
                            recentStats = usageStats
                        }
                    }
                    return recentStats?.packageName
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }

            }
            return ""
        }
    }
}