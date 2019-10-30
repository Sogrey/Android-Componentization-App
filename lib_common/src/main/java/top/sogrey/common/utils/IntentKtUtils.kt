package top.sogrey.common.utils

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import top.sogrey.common.compatible.FileProvider7
import java.io.File

/**
 * 意图相关
 * @author Sogrey
 * @date 2019/10/29
 */
class IntentKtUtils {
    constructor() {
        throw UnsupportedOperationException(this.javaClass.simpleName + " cannot be instantiated")
    }

    companion object {

    }
}

/**
 * 设置意图Intent的dataAndType,兼容android 7.0（API = 24）
 * @param type 文件类型MIME
 * @param file 文件
 * @param writeAble 是否可写
 * @return 处理后的意图对象
 */
fun Intent.setIntentDataAndType(
    type: String,
    file: File,
    writeAble: Boolean
) = FileProvider7.setIntentDataAndType(AppUtils.getApp(), this, type, file, writeAble)

/**
 * 设置意图Intent的dataAndType,兼容android 7.0（API = 24）
 * @param file 文件
 * @param writeAble 是否可写
 * @return 处理后的意图对象
 */
fun Intent.setIntentDataAndType(
    file: File,
    writeAble: Boolean
) = FileProvider7.setIntentDataAndType(AppUtils.getApp(), this, file, writeAble)

/**
 * 设置意图Intent的data,兼容android 7.0（API = 24）
 * @param file 文件
 * @param writeAble 是否可写
 * @return 处理后的意图对象
 */
fun Intent.setIntentData(
    file: File,
    writeAble: Boolean
) {
    data = FileProvider7.getUriForFile(AppUtils.getApp(), file)
    if (Build.VERSION.SDK_INT >= 24) {
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (writeAble) {
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
    }
}

/**
 * Intent.addFlags方式对于ACTION_IMAGE_CAPTURE在5.0以下是无效的，所以需要使用grantUriPermission，如果是正常的通过setData分享的uri，使用addFlags是没有问题的
 * @param uri
 * @param writeAble 是否可写
 */
fun Intent.grantPermissions(uri: Uri, writeAble: Boolean) {
    var flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
    if (writeAble) {
        flag = flag or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
    }
    addFlags(flag)
    val resInfoList = AppUtils.getApp().packageManager.queryIntentActivities(
        this,
        PackageManager.MATCH_DEFAULT_ONLY
    )
    for (resolveInfo in resInfoList) {
        val packageName = resolveInfo.activityInfo.packageName
        AppUtils.getApp().grantUriPermission(packageName, uri, flag)
    }
}

