package top.sogrey.common.utils

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import top.sogrey.common.AppConfig
import top.sogrey.common.R
import top.sogrey.common.compatible.FileProvider7
import java.io.File


/**
 * 描述：
 * Created by Sogrey on 2019/1/22.
 */
class FileKtUtils {
    companion object {
        /**
         * 根据文件路径获取文件名
         */
        fun getFileNameByPath(path: String): String {
            val file = File(path)
            return if (file.exists() && file.isFile)
                file.name
            else ""
        }

        /**
         * 判断指定路径是不是个文件
         */
        fun isFile(path: String): Boolean {
            val file = File(path)
            return file.exists() && file.isFile
        }

        /**
         * 根据文件路径获取文件File对象
         * @param filePath 文件路径
         * @return 文件File对象
         */
        fun getFileByPath(filePath: String): File? {
            return if (StringUtils.isSpace(filePath)) null else File(filePath)
        }

        /**
         * 文件是否存在
         * @param file 欲判断的文件对象
         *  @return true:文件存在;false:文件不存在
         */
        fun isFileExists(file: File?): Boolean {
            return file != null && file.exists()
        }

        /**
         * 根据文件路径打开文件
         *
         * @param context
         * @param path
         * @author Sogrey 2015年4月14日
         */
        @SuppressLint("ShowToast")
        fun openFile(context: Context, path: String) {

            val file = File(path)
            openFile(context, file)
        }

        /**
         * 根据文件打开文件
         *
         * @param context
         * @param file
         * @author Sogrey 2015年4月14日
         */
        fun openFile(context: Context, file: File) {

            val intent = Intent()
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            // 设置intent的Action属性
            intent.action = Intent.ACTION_VIEW
            //设置意图Intent的dataAndType,兼容android 7.0（API = 24）
            FileProvider7.setIntentDataAndType(context, intent, file, true)

            // 跳转
            try {
                if (file.exists()) {
                    // 这里最好try一下，有可能会报错。
                    context.startActivity(intent)
                } else {
                    ToastUtils.show(R.string.txt_bad_file)
                }
                //比如说你的MIME类型是打开邮箱，但是你手机里面没装邮箱客户端，就会报错。
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
                LogKtUtils.e(context.getString(R.string.txt_no_match_application))
                if (AppConfig.DEBUG) {
                    ToastUtils.show(
                        context.resources.getString(
                            R.string.txt_no_match_application2,
                            file.absolutePath
                        )
                    )
                }
            }
        }
    }
}


/**
 * 获取文件Uri
 * @param context 上下文对象
 * @param file 文件
 * @return 文件Uri
 */
fun File.getUri(): Uri {
    return FileProvider7.getUriForFile(AppUtils.getApp(), this)
}