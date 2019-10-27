package top.sogrey.common.utils.ktx

import android.content.Context
import top.sogrey.common.utils.logD

//Context ktx 扩展

/**
 * 获取应用版本号Name
 */
fun Context.getAppVersion():  String {
    val packageManager = this.packageManager
    val packageInfo = packageManager.getPackageInfo(this.packageName, 0)
    logD("Context.getAppVersion", "versionName={${packageInfo.versionName}}")
    return packageInfo.versionName
}

/**
 * 获取应用版本号Code
 */
fun Context.getAppVersionCode(): Int {
    val packageManager = this.packageManager
    val packageInfo = packageManager.getPackageInfo(this.packageName, 0)
    logD("Context.getVersionCode", "versionCode={${packageInfo.versionCode}}")
    return packageInfo.versionCode
}