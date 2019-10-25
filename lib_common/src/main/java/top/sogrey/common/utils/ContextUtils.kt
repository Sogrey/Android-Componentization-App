package top.sogrey.common.utils

import android.R
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ProgressBar
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

//上下文相关


/**
 * 描述：Context 扩展
 * Created by Sogrey on 2018/12/3.
 */

/**
 * 获取应用版本号Name
 */
fun Context.getAppVersion(): String {
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

/**
 * 对话框
 */
fun Context.alert(title: String? = "", message: String? = "",
                  ok: String? = "", okListener: DialogInterface.OnClickListener? = null,
                  cancle: String? = "", cancleListener: DialogInterface.OnClickListener? = null,
                  ignore: String? = "", ignoreListener: DialogInterface.OnClickListener? = null,
                  customView: View? = null): AlertDialog? {
    var builder = AlertDialog.Builder(this)
    builder.setTitle(title!!)
        .setMessage(message!!)
        .setPositiveButton(ok!!, okListener)
        .setNegativeButton(cancle!!, cancleListener)
        .setNeutralButton(ignore!!, ignoreListener)
        // 禁止响应按back键的事件
        .setCancelable(false)
    if (customView != null) builder.setView(customView)
    var dialog = builder.create()
    dialog.show()
    return dialog
}

@SuppressLint("RtlHardcoded")
fun Context.waitingDialog(title: String? = "", message: String? = "",
                          ok: String? = "", okListener: DialogInterface.OnClickListener? = null,
                          cancle: String? = "", cancleListener: DialogInterface.OnClickListener? = null,
                          ignore: String? = "", ignoreListener: DialogInterface.OnClickListener? = null): AlertDialog? {
    var builder = AlertDialog.Builder(this)
    val customView = ProgressBar(this, null, R.attr.progressBarStyleHorizontal)
    customView.isIndeterminate = true
//    customView.progressDrawable = ClipDrawable(ColorDrawable(Color.parseColor("#3F51B5")), Gravity.LEFT, ClipDrawable.HORIZONTAL)
    builder.setTitle(title!!)
        .setMessage(message!!)
        .setView(customView)
        .setPositiveButton(ok!!, okListener)
        .setNegativeButton(cancle!!, cancleListener)
        .setNeutralButton(ignore!!, ignoreListener)
        // 禁止响应按back键的事件
        .setCancelable(false)
    var dialog = builder.create()
    dialog.show()
    return dialog
}


fun Context.getResString(stringId: Int): String = resources.getString(stringId)

fun Context.getResString(resId: Int, vararg o: Any): String = resources.getString(resId, *o)

fun Context.getResDrawable(@DrawableRes id: Int): Drawable? = ContextCompat.getDrawable(this, id)

fun Context.getResColor(@ColorRes id: Int): Int = ContextCompat.getColor(this, id)

