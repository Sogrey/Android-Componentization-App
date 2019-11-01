package top.sogrey.common.utils

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.RequiresPermission
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import kotlin.system.exitProcess
import java.lang.reflect.AccessibleObject.setAccessible




/**
 * Crash 相关
 * <p/>
 * @author Sogrey
 * @date 2019-11-01 17:26
 */

class CrashUtils {
    constructor() {
        throw UnsupportedOperationException(this.javaClass.simpleName + " cannot be instantiated")
    }

    companion object {


        private var defaultDir: String? = null
        private var dir: String? = null
        private var packageName: String?=null
        private var versionName: String? = null
        private var versionCode: Int = 0

        private val FILE_SEP = System.getProperty("file.separator")
        @SuppressLint("SimpleDateFormat")
        private val FORMAT = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS")

        private var DEFAULT_UNCAUGHT_EXCEPTION_HANDLER: Thread.UncaughtExceptionHandler? =
            Thread.getDefaultUncaughtExceptionHandler()
        private var UNCAUGHT_EXCEPTION_HANDLER: Thread.UncaughtExceptionHandler =
            object : Thread.UncaughtExceptionHandler {
                override fun uncaughtException(t: Thread, e: Throwable?) {

                    try {
                        packageName = AppUtils.getApp().packageName
                        val pi = AppUtils.getApp().packageManager.getPackageInfo(packageName, 0)
                        if (pi != null) {
                            versionName = pi.versionName
                            versionCode = pi.versionCode
                        }
                    } catch (e: PackageManager.NameNotFoundException) {
                        e.printStackTrace()
                    }


                    if (e == null) {
                        if (DEFAULT_UNCAUGHT_EXCEPTION_HANDLER != null) {
                            DEFAULT_UNCAUGHT_EXCEPTION_HANDLER!!.uncaughtException(t, null)
                        } else {
                            android.os.Process.killProcess(android.os.Process.myPid())
                            exitProcess(1)
                        }
                        return
                    }

                    val fields = Build::class.java.declaredFields
                    val infos:StringBuilder = StringBuilder()
                    for (field in fields) {
                        try {
                            field.isAccessible = true
                            val name = String.format("%-30s", field.name)
                            infos.append("\n$name : ${field.get(null)!!}")
                        } catch (e: Exception) {
                            logE("CrashUtils", "an error occured when collect crash info", e)
                        }
                    }

                    val time = FORMAT.format(Date(System.currentTimeMillis()))
                    val sb = StringBuilder()
                    val head = "************************** Log Head ********************************" +
                            "\nPackageName                    : " + packageName +
                            "\nTime Of Crash                  : " + time +
                            "\nDevice Manufacturer            : " + Build.MANUFACTURER +
                            "\nDevice Model                   : " + Build.MODEL +
                            "\nAndroid Version                : " + Build.VERSION.RELEASE +
                            "\nAndroid SDK                    : " + Build.VERSION.SDK_INT +
                            "\nApp VersionName                : " + versionName +
                            "\nApp VersionCode                : " + versionCode +
                            "\n************************* Other Info *******************************"+
                            infos.toString()+
                            "\n************************** Log Head ********************************\n\n"
                    sb.append(head)
                        .append(ThrowableUtils.getFullStackTrace(e))
                    val crashInfo = sb.toString()
                    val fullPath =
                        "${(if (dir == null) defaultDir else dir)}crash_$time.txt"
                    if (createOrExistsFile(fullPath)) {
                        input2File(crashInfo, fullPath)
                    } else {
                        logE("CrashUtils", "create $fullPath failed!")
                    }

                    sOnCrashListener?.onCrash(crashInfo, e)

                    DEFAULT_UNCAUGHT_EXCEPTION_HANDLER?.uncaughtException(t, e)
                }
            }

        private var sOnCrashListener: OnCrashListener? = null

        /**
         * Initialization.
         *
         * Must hold `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />`
         */
        @SuppressLint("MissingPermission")
        fun init() {
            init("")
        }

        /**
         * Initialization
         *
         * Must hold `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />`
         *
         * @param crashDir The directory of saving crash information.
         */
        @RequiresPermission(WRITE_EXTERNAL_STORAGE)
        fun init(@NonNull crashDir: File) {
            init(crashDir.absolutePath, null)
        }

        /**
         * Initialization
         *
         * Must hold `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />`
         *
         * @param crashDirPath The directory's path of saving crash information.
         */
        @RequiresPermission(WRITE_EXTERNAL_STORAGE)
        fun init(crashDirPath: String) {
            init(crashDirPath, null)
        }

        /**
         * Initialization
         *
         * Must hold `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />`
         *
         * @param onCrashListener The crash listener.
         */
        @SuppressLint("MissingPermission")
        fun init(onCrashListener: OnCrashListener) {
            init("", onCrashListener)
        }

        /**
         * Initialization
         *
         * Must hold `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />`
         *
         * @param crashDir        The directory of saving crash information.
         * @param onCrashListener The crash listener.
         */
        @RequiresPermission(WRITE_EXTERNAL_STORAGE)
        fun init(@NonNull crashDir: File, onCrashListener: OnCrashListener) {
            init(crashDir.absolutePath, onCrashListener)
        }

        /**
         * Initialization
         *
         * Must hold `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />`
         *
         * @param crashDirPath    The directory's path of saving crash information.
         * @param onCrashListener The crash listener.
         */
        @RequiresPermission(WRITE_EXTERNAL_STORAGE)
        fun init(crashDirPath: String, onCrashListener: OnCrashListener?) {
            dir = if (isSpace(crashDirPath)) {
                null
            } else {
                if (crashDirPath.endsWith(FILE_SEP!!)) crashDirPath else crashDirPath + FILE_SEP
            }
            defaultDir =
                if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() && AppUtils.getApp().externalCacheDir != null)
                    "${AppUtils.getApp().externalCacheDir}${FILE_SEP}crash${FILE_SEP}"
                else {
                    "${AppUtils.getApp().cacheDir}${FILE_SEP}crash${FILE_SEP}"
                }
            sOnCrashListener = onCrashListener
            Thread.setDefaultUncaughtExceptionHandler(UNCAUGHT_EXCEPTION_HANDLER)
        }

        ///////////////////////////////////////////////////////////////////////////
        // interface
        ///////////////////////////////////////////////////////////////////////////

        interface OnCrashListener {
            fun onCrash(crashInfo: String, e: Throwable?)
        }

        ///////////////////////////////////////////////////////////////////////////
        // other utils methods
        ///////////////////////////////////////////////////////////////////////////

        private fun input2File(input: String, filePath: String) {
            val submit = Executors.newSingleThreadExecutor().submit(Callable<Boolean> {
                var bw: BufferedWriter? = null
                try {
                    bw = BufferedWriter(FileWriter(filePath, true))
                    bw.write(input)
                    true
                } catch (e: IOException) {
                    e.printStackTrace()
                    false
                } finally {
                    try {
                        bw?.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            })
            try {
                if (submit.get()) return
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            }

            logE("CrashUtils", "write crash info to $filePath failed!")
        }

        private fun createOrExistsFile(filePath: String): Boolean {
            val file = File(filePath)
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
    }
}