package top.sogrey.common.utils

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

    }
}