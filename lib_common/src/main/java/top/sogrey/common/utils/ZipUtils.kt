package top.sogrey.common.utils

import java.io.*
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream


/**
 * zip 解压缩相关
 */
class ZipUtils {
    constructor(){
        throw UnsupportedOperationException(this.javaClass.simpleName + " cannot be instantiated")
    }companion object{
        private val BUFFER_LEN = 8192


        /**
         * Zip the files.
         *
         * @param srcFiles    The source of files.
         * @param zipFilePath The path of ZIP file.
         * @return `true`: success<br></br>`false`: fail
         * @throws IOException if an I/O error has occurred
         */
        @Throws(IOException::class)
        fun zipFiles(
            srcFiles: Collection<String>,
            zipFilePath: String
        ): Boolean {
            return zipFiles(srcFiles, zipFilePath, null)
        }

        /**
         * Zip the files.
         *
         * @param srcFilePaths The paths of source files.
         * @param zipFilePath  The path of ZIP file.
         * @param comment      The comment.
         * @return `true`: success<br></br>`false`: fail
         * @throws IOException if an I/O error has occurred
         */
        @Throws(IOException::class)
        fun zipFiles(
            srcFilePaths: Collection<String>?,
            zipFilePath: String?,
            comment: String?
        ): Boolean {
            if (srcFilePaths == null || zipFilePath == null) return false
            var zos: ZipOutputStream? = null
            try {
                zos = ZipOutputStream(FileOutputStream(zipFilePath))
                for (srcFile in srcFilePaths) {
                    if (!zipFile(getFileByPath(srcFile)!!, "", zos, comment)) return false
                }
                return true
            } finally {
                if (zos != null) {
                    zos.finish()
                    zos.close()
                }
            }
        }

        /**
         * Zip the files.
         *
         * @param srcFiles The source of files.
         * @param zipFile  The ZIP file.
         * @return `true`: success<br></br>`false`: fail
         * @throws IOException if an I/O error has occurred
         */
        @Throws(IOException::class)
        fun zipFiles(srcFiles: Collection<File>, zipFile: File): Boolean {
            return zipFiles(srcFiles, zipFile, null)
        }

        /**
         * Zip the files.
         *
         * @param srcFiles The source of files.
         * @param zipFile  The ZIP file.
         * @param comment  The comment.
         * @return `true`: success<br></br>`false`: fail
         * @throws IOException if an I/O error has occurred
         */
        @Throws(IOException::class)
        fun zipFiles(
            srcFiles: Collection<File>?,
            zipFile: File?,
            comment: String?
        ): Boolean {
            if (srcFiles == null || zipFile == null) return false
            var zos: ZipOutputStream? = null
            try {
                zos = ZipOutputStream(FileOutputStream(zipFile))
                for (srcFile in srcFiles) {
                    if (!zipFile(srcFile, "", zos, comment)) return false
                }
                return true
            } finally {
                if (zos != null) {
                    zos!!.finish()
                    zos!!.close()
                }
            }
        }

        /**
         * Zip the file.
         *
         * @param srcFilePath The path of source file.
         * @param zipFilePath The path of ZIP file.
         * @return `true`: success<br></br>`false`: fail
         * @throws IOException if an I/O error has occurred
         */
        @Throws(IOException::class)
        fun zipFile(
            srcFilePath: String,
            zipFilePath: String
        ): Boolean {
            return zipFile(getFileByPath(srcFilePath), getFileByPath(zipFilePath), null)
        }

        /**
         * Zip the file.
         *
         * @param srcFilePath The path of source file.
         * @param zipFilePath The path of ZIP file.
         * @param comment     The comment.
         * @return `true`: success<br></br>`false`: fail
         * @throws IOException if an I/O error has occurred
         */
        @Throws(IOException::class)
        fun zipFile(
            srcFilePath: String,
            zipFilePath: String,
            comment: String
        ): Boolean {
            return zipFile(getFileByPath(srcFilePath), getFileByPath(zipFilePath), comment)
        }

        /**
         * Zip the file.
         *
         * @param srcFile The source of file.
         * @param zipFile The ZIP file.
         * @return `true`: success<br></br>`false`: fail
         * @throws IOException if an I/O error has occurred
         */
        @Throws(IOException::class)
        fun zipFile(
            srcFile: File,
            zipFile: File
        ): Boolean {
            return zipFile(srcFile, zipFile, null)
        }

        /**
         * Zip the file.
         *
         * @param srcFile The source of file.
         * @param zipFile The ZIP file.
         * @param comment The comment.
         * @return `true`: success<br></br>`false`: fail
         * @throws IOException if an I/O error has occurred
         */
        @Throws(IOException::class)
        fun zipFile(
            srcFile: File?,
            zipFile: File?,
            comment: String?
        ): Boolean {
            if (srcFile == null || zipFile == null) return false
            var zos: ZipOutputStream? = null
            try {
                zos = ZipOutputStream(FileOutputStream(zipFile))
                return zipFile(srcFile, "", zos, comment)
            } finally {
                zos?.close()
            }
        }

        @Throws(IOException::class)
        private fun zipFile(
            srcFile: File,
            rootPath: String,
            zos: ZipOutputStream,
            comment: String?
        ): Boolean {
            var rootPath = rootPath
            rootPath =
                rootPath + (if (isSpace(rootPath)) "" else File.separator) + srcFile.name
            if (srcFile.isDirectory) {
                val fileList = srcFile.listFiles()
                if (fileList == null || fileList.isEmpty()) {
                    val entry = ZipEntry("$rootPath/")
                    entry.comment = comment
                    zos.putNextEntry(entry)
                    zos.closeEntry()
                } else {
                    for (file in fileList) {
                        if (!zipFile(file, rootPath, zos, comment)) return false
                    }
                }
            } else {
                var `is`: InputStream? = null
                try {
                    `is` = BufferedInputStream(FileInputStream(srcFile))
                    val entry = ZipEntry(rootPath)
                    entry.comment = comment
                    zos.putNextEntry(entry)
                    val buffer = ByteArray(BUFFER_LEN)
                    var len: Int
//                    while ((len = `is`!!.read(buffer, 0, BUFFER_LEN)) != -1) {
//                        zos.write(buffer, 0, len)
//                    }
                    while (true){
                        len = `is`.read(buffer, 0, BUFFER_LEN)
                        if(len!=-1){
                            zos.write(buffer, 0, len)
                        }
                    }
                    zos.closeEntry()
                } finally {
                    `is`?.close()
                }
            }
            return true
        }

        /**
         * Unzip the file.
         *
         * @param zipFilePath The path of ZIP file.
         * @param destDirPath The path of destination directory.
         * @return the unzipped files
         * @throws IOException if unzip unsuccessfully
         */
        @Throws(IOException::class)
        fun unzipFile(
            zipFilePath: String,
            destDirPath: String
        ): List<File>? {
            return unzipFileByKeyword(zipFilePath, destDirPath, null)
        }

        /**
         * Unzip the file.
         *
         * @param zipFile The ZIP file.
         * @param destDir The destination directory.
         * @return the unzipped files
         * @throws IOException if unzip unsuccessfully
         */
        @Throws(IOException::class)
        fun unzipFile(
            zipFile: File,
            destDir: File
        ): List<File>? {
            return unzipFileByKeyword(zipFile, destDir, null)
        }

        /**
         * Unzip the file by keyword.
         *
         * @param zipFilePath The path of ZIP file.
         * @param destDirPath The path of destination directory.
         * @param keyword     The keyboard.
         * @return the unzipped files
         * @throws IOException if unzip unsuccessfully
         */
        @Throws(IOException::class)
        fun unzipFileByKeyword(
            zipFilePath: String,
            destDirPath: String,
            keyword: String?
        ): List<File>? {
            return unzipFileByKeyword(
                getFileByPath(zipFilePath),
                getFileByPath(destDirPath),
                keyword
            )
        }

        /**
         * Unzip the file by keyword.
         *
         * @param zipFile The ZIP file.
         * @param destDir The destination directory.
         * @param keyword The keyboard.
         * @return the unzipped files
         * @throws IOException if unzip unsuccessfully
         */
        @Throws(IOException::class)
        fun unzipFileByKeyword(
            zipFile: File?,
            destDir: File?,
            keyword: String?
        ): List<File>? {
            if (zipFile == null || destDir == null) return null
            val files = ArrayList<File>()
            val zip = ZipFile(zipFile)
            val entries = zip.entries()
            zip.use { zip ->
                if (isSpace(keyword)) {
                    while (entries.hasMoreElements()) {
                        val entry = entries.nextElement() as ZipEntry
                        val entryName = entry.name
                        if (entryName.contains("../")) {
                            logI("entryName: $entryName is dangerous!")
                            continue
                        }
                        if (!unzipChildFile(destDir, files, zip, entry, entryName)) return files
                    }
                } else {
                    while (entries.hasMoreElements()) {
                        val entry = entries.nextElement() as ZipEntry
                        val entryName = entry.name
                        if (entryName.contains("../")) {
                            logI("entryName: $entryName is dangerous!")
                            continue
                        }
                        if (entryName.contains(keyword!!)) {
                            if (!unzipChildFile(destDir, files, zip, entry, entryName)) return files
                        }
                    }
                }
            }
            return files
        }

        @Throws(IOException::class)
        private fun unzipChildFile(
            destDir: File,
            files: MutableList<File>,
            zip: ZipFile,
            entry: ZipEntry,
            name: String
        ): Boolean {
            val file = File(destDir, name)
            files.add(file)
            if (entry.isDirectory) {
                return createOrExistsDir(file)
            } else {
                if (!createOrExistsFile(file)) return false
                var `in`: InputStream? = null
                var out: OutputStream? = null
                try {
                    `in` = BufferedInputStream(zip.getInputStream(entry))
                    out = BufferedOutputStream(FileOutputStream(file))
                    val buffer = ByteArray(BUFFER_LEN)
                    var len: Int
//                    while ((len = `in`!!.read(buffer)) != -1) {
//                        out!!.write(buffer, 0, len)
//                    }
                    while (true){
                        len = `in`.read(buffer)
                        if(len!=-1){
                            out.write(buffer, 0, len)
                        }
                    }
                } finally {
                    `in`?.close()
                    out?.close()
                }
            }
            return true
        }

        /**
         * Return the files' path in ZIP file.
         *
         * @param zipFilePath The path of ZIP file.
         * @return the files' path in ZIP file
         * @throws IOException if an I/O error has occurred
         */
        @Throws(IOException::class)
        fun getFilesPath(zipFilePath: String): List<String>? {
            return getFilesPath(getFileByPath(zipFilePath))
        }

        /**
         * Return the files' path in ZIP file.
         *
         * @param zipFile The ZIP file.
         * @return the files' path in ZIP file
         * @throws IOException if an I/O error has occurred
         */
        @Throws(IOException::class)
        fun getFilesPath(zipFile: File?): List<String>? {
            if (zipFile == null) return null
            val paths = ArrayList<String>()
            val zip = ZipFile(zipFile)
            val entries = zip.entries()
            while (entries.hasMoreElements()) {
                val entryName = (entries.nextElement() as ZipEntry).name
                if (entryName.contains("../")) {
                    println("entryName: $entryName is dangerous!")
                    paths.add(entryName)
                } else {
                    paths.add(entryName)
                }
            }
            zip.close()
            return paths
        }

        /**
         * Return the files' comment in ZIP file.
         *
         * @param zipFilePath The path of ZIP file.
         * @return the files' comment in ZIP file
         * @throws IOException if an I/O error has occurred
         */
        @Throws(IOException::class)
        fun getComments(zipFilePath: String): List<String>? {
            return getComments(getFileByPath(zipFilePath))
        }

        /**
         * Return the files' comment in ZIP file.
         *
         * @param zipFile The ZIP file.
         * @return the files' comment in ZIP file
         * @throws IOException if an I/O error has occurred
         */
        @Throws(IOException::class)
        fun getComments(zipFile: File?): List<String>? {
            if (zipFile == null) return null
            val comments = ArrayList<String>()
            val zip = ZipFile(zipFile)
            val entries = zip.entries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement() as ZipEntry
                comments.add(entry.comment)
            }
            zip.close()
            return comments
        }

        private fun createOrExistsDir(file: File?): Boolean {
            return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
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

        private fun getFileByPath(filePath: String): File? {
            return if (isSpace(filePath)) null else File(filePath)
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
    }
}