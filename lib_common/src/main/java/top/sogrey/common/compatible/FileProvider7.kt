package top.sogrey.common.compatible

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import top.sogrey.common.utils.FileMIME
import java.io.File
import android.content.pm.PackageManager


/**
 * 兼容 Android 7.0，适配7.0 行为变更 通过FileProvider在应用间共享文件
 */
class FileProvider7 {
    companion object {
        /**
         * 获取文件Uri
         * @param context 上下文对象
         * @param file 文件
         * @return 文件Uri
         */
        fun getUriForFile(context: Context, file: File): Uri {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(
                    context,
                    context.packageName + ".fileprovider",
                    file
                )
            } else {
                Uri.fromFile(file)
            }
        }

        /**
         * 设置意图Intent的dataAndType,兼容android 7.0（API = 24）
         * @param context 上下文对象
         * @param intent 要处理的意图
         * @param file 文件
         * @param writeAble 是否可写
         * @return 处理后的意图对象
         */
        fun setIntentDataAndType(
            context: Context,
            intent: Intent,
            file: File,
            writeAble: Boolean
        ): Intent {
            intent.setDataAndType(
                getUriForFile(context, file),
                FileMIME.getFileMIME(file.absolutePath)
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //赋予临时读权限
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                if (writeAble) {
                    //赋予临时写权限
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }
            }
            return intent
        }

        /**
         * 设置意图Intent的dataAndType,兼容android 7.0（API = 24）
         * @param context 上下文对象
         * @param intent 要处理的意图
         * @param type 文件类型MIME
         * @param file 文件
         * @param writeAble 是否可写
         * @return 处理后的意图对象
         */
        fun setIntentDataAndType(
            context: Context,
            intent: Intent,
            type: String,
            file: File,
            writeAble: Boolean
        ): Intent {
            intent.setDataAndType(getUriForFile(context, file), type)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //赋予临时读权限
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                if (writeAble) {
                    //赋予临时写权限
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }
            }
            return intent
        }

        /**
         * 设置意图Intent的data,兼容android 7.0（API = 24）
         * @param context 上下文对象
         * @param intent 要处理的意图
         * @param file 文件
         * @param writeAble 是否可写
         * @return 处理后的意图对象
         */
        fun setIntentData(
            context: Context,
            intent: Intent,
            file: File,
            writeAble: Boolean
        ) {
            intent.data = getUriForFile(context, file)
            if (Build.VERSION.SDK_INT >= 24) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                if (writeAble) {
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }
            }
        }

        /**
         * Intent.addFlags方式对于ACTION_IMAGE_CAPTURE在5.0以下是无效的，所以需要使用grantUriPermission，如果是正常的通过setData分享的uri，使用addFlags是没有问题的
         * @param context 上下文对象
         * @param intent 要处理的意图
         * @param uri
         * @param writeAble 是否可写
         */
        fun grantPermissions(context: Context, intent: Intent, uri: Uri, writeAble: Boolean) {
            var flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
            if (writeAble) {
                flag = flag or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            }
            intent.addFlags(flag)
            val resInfoList = context.packageManager.queryIntentActivities(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
            for (resolveInfo in resInfoList) {
                val packageName = resolveInfo.activityInfo.packageName
                context.grantUriPermission(packageName, uri, flag)
            }
        }
    }
}