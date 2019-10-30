package top.sogrey.common.utils.ktx

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import top.sogrey.common.compatible.FileProvider7
import top.sogrey.common.utils.ConvertUtils
import top.sogrey.common.utils.logD
import java.io.File

//Context ktx 扩展

/**
 * 获取应用程序名称
 */
fun Context.getAppName(): String {
    val packageInfo = this.packageManager.getPackageInfo(this.packageName, 0)
    val labelRes = packageInfo.applicationInfo.labelRes
    return this.resources.getString(labelRes)
}

/**
 * 获取应用版本号Name
 */
fun Context.getAppVersionName(): String {
    val packageInfo = this.packageManager.getPackageInfo(this.packageName, 0)
    return packageInfo.versionName
}


/**
 * 获取应用版本号Code
 */
fun Context.getAppVersionCode(): Int {
    val packageInfo = this.packageManager.getPackageInfo(this.packageName, 0)
    return packageInfo.versionCode
}

/**
 * 获取包名
 */
fun Context.getAppPackageName(): String {
    val packageInfo = this.packageManager.getPackageInfo(this.packageName, 0)
    return packageInfo.packageName
}

/**
 * 获取应用图标
 * @return 图标 Drawable 对象
 */
fun Context.getAppIconDrawable(): Drawable {
    val packageManager = applicationContext.packageManager
    val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
    return packageManager.getApplicationIcon(applicationInfo)
}

/**
 * 获取应用图标
 * @return 图标bitmap对象
 */
fun Context.getAppIconBitmap(): Bitmap {
   return ConvertUtils.drawable2Bitmap(getAppIconDrawable())
}



/**
 * 获取文件Uri,兼容android 7.0
 * @param file 文件
 * @return 文件Uri
 */
fun Context.getUriForFile(file: File): Uri {
    return FileProvider7.getUriForFile(this,file)
}