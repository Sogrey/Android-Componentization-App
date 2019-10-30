package top.sogrey.common.utils

import androidx.annotation.RawRes
import java.io.*
import java.nio.charset.Charset


/**
 * 资源相关
 * <p/>
 * @author Sogrey
 * @date 2019-10-30 22:16
 */
class ResourceUtils {
    constructor() {
        throw UnsupportedOperationException(this.javaClass.simpleName + " cannot be instantiated")
    }

    companion object {
        private val BUFFER_SIZE = 8192


        /**
         * Return the id identifier by name.
         *
         * @param name The name of id.
         * @return the id identifier by name
         */
        fun getIdByName(name: String): Int {
            return AppUtils.getApp().resources
                .getIdentifier(name, "id", AppUtils.getApp().packageName)
        }

        /**
         * Return the string identifier by name.
         *
         * @param name The name of string.
         * @return the string identifier by name
         */
        fun getStringIdByName(name: String): Int {
            return AppUtils.getApp().resources
                .getIdentifier(name, "string", AppUtils.getApp().packageName)
        }

        /**
         * Return the color identifier by name.
         *
         * @param name The name of color.
         * @return the color identifier by name
         */
        fun getColorIdByName(name: String): Int {
            return AppUtils.getApp().resources
                .getIdentifier(name, "color", AppUtils.getApp().packageName)
        }

        /**
         * Return the dimen identifier by name.
         *
         * @param name The name of dimen.
         * @return the dimen identifier by name
         */
        fun getDimenIdByName(name: String): Int {
            return AppUtils.getApp().resources
                .getIdentifier(name, "dimen", AppUtils.getApp().packageName)
        }

        /**
         * Return the drawable identifier by name.
         *
         * @param name The name of drawable.
         * @return the drawable identifier by name
         */
        fun getDrawableIdByName(name: String): Int {
            return AppUtils.getApp().resources
                .getIdentifier(name, "drawable", AppUtils.getApp().packageName)
        }

        /**
         * Return the mipmap identifier by name.
         *
         * @param name The name of mipmap.
         * @return the mipmap identifier by name
         */
        fun getMipmapIdByName(name: String): Int {
            return AppUtils.getApp().resources
                .getIdentifier(name, "mipmap", AppUtils.getApp().packageName)
        }

        /**
         * Return the layout identifier by name.
         *
         * @param name The name of layout.
         * @return the layout identifier by name
         */
        fun getLayoutIdByName(name: String): Int {
            return AppUtils.getApp().resources
                .getIdentifier(name, "layout", AppUtils.getApp().packageName)
        }

        /**
         * Return the style identifier by name.
         *
         * @param name The name of style.
         * @return the style identifier by name
         */
        fun getStyleIdByName(name: String): Int {
            return AppUtils.getApp().resources
                .getIdentifier(name, "style", AppUtils.getApp().packageName)
        }

        /**
         * Return the anim identifier by name.
         *
         * @param name The name of anim.
         * @return the anim identifier by name
         */
        fun getAnimIdByName(name: String): Int {
            return AppUtils.getApp().resources
                .getIdentifier(name, "anim", AppUtils.getApp().packageName)
        }

        /**
         * Return the menu identifier by name.
         *
         * @param name The name of menu.
         * @return the menu identifier by name
         */
        fun getMenuIdByName(name: String): Int {
            return AppUtils.getApp().resources
                .getIdentifier(name, "menu", AppUtils.getApp().packageName)
        }

        /**
         * Copy the file from assets.
         *
         * @param assetsFilePath The path of file in assets.
         * @param destFilePath   The path of destination file.
         * @return `true`: success<br></br>`false`: fail
         */
        fun copyFileFromAssets(assetsFilePath: String, destFilePath: String): Boolean {
            var res = true
            try {
                val assets = AppUtils.getApp().assets.list(assetsFilePath)
                if (assets!!.isNotEmpty()) {
                    for (asset in assets) {
                        res = res and copyFileFromAssets(
                            "$assetsFilePath/$asset",
                            "$destFilePath/$asset"
                        )
                    }
                } else {
                    res = writeFileFromIS(
                        destFilePath,
                        AppUtils.getApp().assets.open(assetsFilePath),
                        false
                    )
                }
            } catch (e: IOException) {
                e.printStackTrace()
                res = false
            }

            return res
        }

        /**
         * Return the content of assets.
         *
         * @param assetsFilePath The path of file in assets.
         * @return the content of assets
         */
        fun readAssets2String(assetsFilePath: String): String? {
            return readAssets2String(assetsFilePath, null)
        }

        /**
         * Return the content of assets.
         *
         * @param assetsFilePath The path of file in assets.
         * @param charsetName    The name of charset.
         * @return the content of assets
         */
        fun readAssets2String(assetsFilePath: String, charsetName: String?): String? {
            val `is`: InputStream
            try {
                `is` = AppUtils.getApp().assets.open(assetsFilePath)
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }

            val bytes = is2Bytes(`is`) ?: return null
            return if (isSpace(charsetName)) {
                String(bytes)
            } else {
                try {
                    String(bytes, Charset.forName(charsetName!!))
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                    ""
                }

            }
        }

        /**
         * Return the content of file in assets.
         *
         * @param assetsPath The path of file in assets.
         * @return the content of file in assets
         */
        fun readAssets2List(assetsPath: String): List<String>? {
            return readAssets2List(assetsPath, null)
        }

        /**
         * Return the content of file in assets.
         *
         * @param assetsPath  The path of file in assets.
         * @param charsetName The name of charset.
         * @return the content of file in assets
         */
        fun readAssets2List(
            assetsPath: String,
            charsetName: String?
        ): List<String>? {
            return try {
                is2List(
                    AppUtils.getApp().resources.assets.open(assetsPath),
                    charsetName
                )
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }

        }


        /**
         * Copy the file from raw.
         *
         * @param resId        The resource id.
         * @param destFilePath The path of destination file.
         * @return `true`: success<br></br>`false`: fail
         */
        fun copyFileFromRaw(@RawRes resId: Int, destFilePath: String): Boolean {
            return writeFileFromIS(
                destFilePath,
                AppUtils.getApp().resources.openRawResource(resId),
                false
            )
        }

        /**
         * Return the content of resource in raw.
         *
         * @param resId The resource id.
         * @return the content of resource in raw
         */
        fun readRaw2String(@RawRes resId: Int): String? {
            return readRaw2String(resId, null)
        }

        /**
         * Return the content of resource in raw.
         *
         * @param resId       The resource id.
         * @param charsetName The name of charset.
         * @return the content of resource in raw
         */
        fun readRaw2String(@RawRes resId: Int, charsetName: String?): String? {
            val `is` = AppUtils.getApp().resources.openRawResource(resId)
            val bytes = is2Bytes(`is`) ?: return null
            return if (isSpace(charsetName)) {
                String(bytes)
            } else {
                try {
                    String(bytes, Charset.forName(charsetName!!))
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                    ""
                }
            }
        }

        /**
         * Return the content of resource in raw.
         *
         * @param resId The resource id.
         * @return the content of file in assets
         */
        fun readRaw2List(@RawRes resId: Int): List<String>? {
            return readRaw2List(resId, null)
        }

        /**
         * Return the content of resource in raw.
         *
         * @param resId       The resource id.
         * @param charsetName The name of charset.
         * @return the content of file in assets
         */
        fun readRaw2List(
            @RawRes resId: Int,
            charsetName: String?
        ): List<String>? {
            return is2List(AppUtils.getApp().resources.openRawResource(resId), charsetName)
        }

        ///////////////////////////////////////////////////////////////////////////
        // other utils methods
        ///////////////////////////////////////////////////////////////////////////

        private fun writeFileFromIS(
            filePath: String,
            `is`: InputStream,
            append: Boolean
        ): Boolean {
            return writeFileFromIS(getFileByPath(filePath), `is`, append)
        }

        private fun writeFileFromIS(
            file: File?,
            `is`: InputStream?,
            append: Boolean
        ): Boolean {
            if (!createOrExistsFile(file) || `is` == null) return false
            var os: OutputStream? = null
            try {
                os = BufferedOutputStream(FileOutputStream(file!!, append))
                val data = ByteArray(BUFFER_SIZE)
                var len: Int
//                while ((len = `is`!!.read(data, 0, BUFFER_SIZE)) != -1) {
//                    os!!.write(data, 0, len)
//                }
                while (true) {
                    len = `is`.read(data, 0, BUFFER_SIZE)
                    if (len != -1) {
                        os.write(data, 0, len)
                    } else {
                        break
                    }
                }
                return true
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            } finally {
                try {
                    `is`.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                try {
                    os?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        private fun getFileByPath(filePath: String): File? {
            return if (isSpace(filePath)) null else File(filePath)
        }

        private fun createOrExistsFile(file: File?): Boolean {
            if (file == null) return false
            if (file.exists()) return file.isFile
            if (!createOrExistsDir(file.parentFile)) return false
            return try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        }

        private fun createOrExistsDir(file: File?): Boolean {
            return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
        }

        private fun isSpace(s: String?): Boolean {
            if (s == null) return true
            var i = 0
            val len = s.length
            while (i < len) {
                if (!Character.isWhitespace(s[i])) {
                    return false
                }
                ++i
            }
            return true
        }

        private fun is2Bytes(`is`: InputStream?): ByteArray? {
            if (`is` == null) return null
            var os: ByteArrayOutputStream? = null
            try {
                os = ByteArrayOutputStream()
                val b = ByteArray(BUFFER_SIZE)
                var len: Int
//                while ((len = `is`!!.read(b, 0, BUFFER_SIZE)) != -1) {
//                    os!!.write(b, 0, len)
//                }
                while (true) {
                    len = `is`.read(b, 0, BUFFER_SIZE)
                    if (len != -1) {
                        os.write(b, 0, len)
                    } else {
                        break
                    }
                }
                return os.toByteArray()
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            } finally {
                try {
                    `is`.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                try {
                    os?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }

        private fun is2List(
            `is`: InputStream,
            charsetName: String?
        ): List<String>? {
            var reader: BufferedReader? = null
            try {
                val list = ArrayList<String>()
                reader = if (isSpace(charsetName)) {
                    BufferedReader(InputStreamReader(`is`))
                } else {
                    BufferedReader(InputStreamReader(`is`, charsetName!!))
                }
                var line: String
//                while ((line = reader!!.readLine()) != null) {
//                    list.add(line)
//                }
                while (true) {
                    line = reader.readLine()
                    if (line != null) {
                        list.add(line)
                    } else {
                        break
                    }
                }
                return list
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            } finally {
                try {
                    reader?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
    }
}