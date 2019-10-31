package top.sogrey.common.utils

import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import java.io.File
import android.os.Build
import androidx.core.content.ContextCompat.getNoBackupFilesDir
import androidx.core.content.ContextCompat.getCodeCacheDir
import androidx.core.content.ContextCompat.getDataDir



/**
 * 目录路径相关：
 *
    getRootPath : 获取根路径
    getDataPath : 获取数据路径
    getDownloadCachePath : 获取下载缓存路径
    getInternalAppDataPath : 获取内存应用数据路径
    getInternalAppCodeCacheDir : 获取内存应用代码缓存路径
    getInternalAppCachePath : 获取内存应用缓存路径
    getInternalAppDbsPath : 获取内存应用数据库路径
    getInternalAppDbPath : 获取内存应用数据库路径
    getInternalAppFilesPath : 获取内存应用文件路径
    getInternalAppSpPath : 获取内存应用 SP 路径
    getInternalAppNoBackupFilesPath: 获取内存应用未备份文件路径
    getExternalStoragePath : 获取外存路径
    getExternalMusicPath : 获取外存音乐路径
    getExternalPodcastsPath : 获取外存播客路径
    getExternalRingtonesPath : 获取外存铃声路径
    getExternalAlarmsPath : 获取外存闹铃路径
    getExternalNotificationsPath : 获取外存通知路径
    getExternalPicturesPath : 获取外存图片路径
    getExternalMoviesPath : 获取外存影片路径
    getExternalDownloadsPath : 获取外存下载路径
    getExternalDcimPath : 获取外存数码相机图片路径
    getExternalDocumentsPath : 获取外存文档路径
    getExternalAppDataPath : 获取外存应用数据路径
    getExternalAppCachePath : 获取外存应用缓存路径
    getExternalAppFilesPath : 获取外存应用文件路径
    getExternalAppMusicPath : 获取外存应用音乐路径
    getExternalAppPodcastsPath : 获取外存应用播客路径
    getExternalAppRingtonesPath : 获取外存应用铃声路径
    getExternalAppAlarmsPath : 获取外存应用闹铃路径
    getExternalAppNotificationsPath: 获取外存应用通知路径
    getExternalAppPicturesPath : 获取外存应用图片路径
    getExternalAppMoviesPath : 获取外存应用影片路径
    getExternalAppDownloadPath : 获取外存应用下载路径
    getExternalAppDcimPath : 获取外存应用数码相机图片路径
    getExternalAppDocumentsPath : 获取外存应用文档路径
    getExternalAppObbPath : 获取外存应用 OBB 路径
 *
 * <p/>
 * @author Sogrey
 * @date 2019-10-31 11:02
 */

class SDCardUtils {
    constructor(){
        throw UnsupportedOperationException(this.javaClass.simpleName + " cannot be instantiated")
    }
    companion object{

        /**
         * 获取SD路径
         */
        fun getSDPath(): String {
            val bool = Environment.getExternalStorageState() == "mounted"
            var localFile: File? = null
            if (bool)
                localFile = getExternalStorageDirectory()
            return localFile!!.toString()
        }


        /**
         * Return the path of /system.
         *
         * @return the path of /system
         */
        fun getRootPath(): String {
            return getAbsolutePath(Environment.getRootDirectory())
        }

        /**
         * Return the path of /data.
         *
         * @return the path of /data
         */
        fun getDataPath(): String {
            return getAbsolutePath(Environment.getDataDirectory())
        }

        /**
         * Return the path of /cache.
         *
         * @return the path of /cache
         */
        fun getDownloadCachePath(): String {
            return getAbsolutePath(Environment.getDownloadCacheDirectory())
        }

        /**
         * Return the path of /data/data/package.
         *
         * @return the path of /data/data/package
         */
        fun getInternalAppDataPath(): String {
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                AppUtils.getApp().applicationInfo.dataDir
            } else getAbsolutePath(AppUtils.getApp().dataDir)
        }

        /**
         * Return the path of /data/data/package/code_cache.
         *
         * @return the path of /data/data/package/code_cache
         */
        fun getInternalAppCodeCacheDir(): String {
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                AppUtils.getApp().applicationInfo.dataDir + "/code_cache"
            } else getAbsolutePath(AppUtils.getApp().codeCacheDir)
        }

        /**
         * Return the path of /data/data/package/cache.
         *
         * @return the path of /data/data/package/cache
         */
        fun getInternalAppCachePath(): String {
            return getAbsolutePath(AppUtils.getApp().cacheDir)
        }

        /**
         * Return the path of /data/data/package/databases.
         *
         * @return the path of /data/data/package/databases
         */
        fun getInternalAppDbsPath(): String {
            return AppUtils.getApp().applicationInfo.dataDir + "/databases"
        }

        /**
         * Return the path of /data/data/package/databases/name.
         *
         * @param name The name of database.
         * @return the path of /data/data/package/databases/name
         */
        fun getInternalAppDbPath(name: String): String {
            return getAbsolutePath(AppUtils.getApp().getDatabasePath(name))
        }

        /**
         * Return the path of /data/data/package/files.
         *
         * @return the path of /data/data/package/files
         */
        fun getInternalAppFilesPath(): String {
            return getAbsolutePath(AppUtils.getApp().filesDir)
        }

        /**
         * Return the path of /data/data/package/shared_prefs.
         *
         * @return the path of /data/data/package/shared_prefs
         */
        fun getInternalAppSpPath(): String {
            return AppUtils.getApp().applicationInfo.dataDir + "/shared_prefs"
        }

        /**
         * Return the path of /data/data/package/no_backup.
         *
         * @return the path of /data/data/package/no_backup
         */
        fun getInternalAppNoBackupFilesPath(): String {
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                AppUtils.getApp().applicationInfo.dataDir + "/no_backup"
            } else getAbsolutePath(AppUtils.getApp().noBackupFilesDir)
        }

        /**
         * Return the path of /storage/emulated/0.
         *
         * @return the path of /storage/emulated/0
         */
        fun getExternalStoragePath(): String {
            return if (isExternalStorageDisable()) "" else getAbsolutePath(Environment.getExternalStorageDirectory())
        }

        /**
         * Return the path of /storage/emulated/0/Music.
         *
         * @return the path of /storage/emulated/0/Music
         */
        fun getExternalMusicPath(): String {
            return if (isExternalStorageDisable()) "" else getAbsolutePath(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_MUSIC
                )
            )
        }

        /**
         * Return the path of /storage/emulated/0/Podcasts.
         *
         * @return the path of /storage/emulated/0/Podcasts
         */
        fun getExternalPodcastsPath(): String {
            return if (isExternalStorageDisable()) "" else getAbsolutePath(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PODCASTS
                )
            )
        }

        /**
         * Return the path of /storage/emulated/0/Ringtones.
         *
         * @return the path of /storage/emulated/0/Ringtones
         */
        fun getExternalRingtonesPath(): String {
            return if (isExternalStorageDisable()) "" else getAbsolutePath(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_RINGTONES
                )
            )
        }

        /**
         * Return the path of /storage/emulated/0/Alarms.
         *
         * @return the path of /storage/emulated/0/Alarms
         */
        fun getExternalAlarmsPath(): String {
            return if (isExternalStorageDisable()) "" else getAbsolutePath(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_ALARMS
                )
            )
        }

        /**
         * Return the path of /storage/emulated/0/Notifications.
         *
         * @return the path of /storage/emulated/0/Notifications
         */
        fun getExternalNotificationsPath(): String {
            return if (isExternalStorageDisable()) "" else getAbsolutePath(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_NOTIFICATIONS
                )
            )
        }

        /**
         * Return the path of /storage/emulated/0/Pictures.
         *
         * @return the path of /storage/emulated/0/Pictures
         */
        fun getExternalPicturesPath(): String {
            return if (isExternalStorageDisable()) "" else getAbsolutePath(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
                )
            )
        }

        /**
         * Return the path of /storage/emulated/0/Movies.
         *
         * @return the path of /storage/emulated/0/Movies
         */
        fun getExternalMoviesPath(): String {
            return if (isExternalStorageDisable()) "" else getAbsolutePath(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_MOVIES
                )
            )
        }

        /**
         * Return the path of /storage/emulated/0/Download.
         *
         * @return the path of /storage/emulated/0/Download
         */
        fun getExternalDownloadsPath(): String {
            return if (isExternalStorageDisable()) "" else getAbsolutePath(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
                )
            )
        }

        /**
         * Return the path of /storage/emulated/0/DCIM.
         *
         * @return the path of /storage/emulated/0/DCIM
         */
        fun getExternalDcimPath(): String {
            return if (isExternalStorageDisable()) "" else getAbsolutePath(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM
                )
            )
        }

        /**
         * Return the path of /storage/emulated/0/Documents.
         *
         * @return the path of /storage/emulated/0/Documents
         */
        fun getExternalDocumentsPath(): String {
            if (isExternalStorageDisable()) return ""
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                getAbsolutePath(Environment.getExternalStorageDirectory()) + "/Documents"
            } else getAbsolutePath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS))
        }

        /**
         * Return the path of /storage/emulated/0/Android/data/package.
         *
         * @return the path of /storage/emulated/0/Android/data/package
         */
        fun getExternalAppDataPath(): String {
            if (isExternalStorageDisable()) return ""
            val externalCacheDir = AppUtils.getApp().externalCacheDir ?: return ""
            return getAbsolutePath(externalCacheDir.parentFile)
        }

        /**
         * Return the path of /storage/emulated/0/Android/data/package/cache.
         *
         * @return the path of /storage/emulated/0/Android/data/package/cache
         */
        fun getExternalAppCachePath(): String {
            return if (isExternalStorageDisable()) "" else getAbsolutePath(AppUtils.getApp().externalCacheDir)
        }

        /**
         * Return the path of /storage/emulated/0/Android/data/package/files.
         *
         * @return the path of /storage/emulated/0/Android/data/package/files
         */
        fun getExternalAppFilesPath(): String {
            return if (isExternalStorageDisable()) "" else getAbsolutePath(
                AppUtils.getApp().getExternalFilesDir(
                    null
                )
            )
        }

        /**
         * Return the path of /storage/emulated/0/Android/data/package/files/Music.
         *
         * @return the path of /storage/emulated/0/Android/data/package/files/Music
         */
        fun getExternalAppMusicPath(): String {
            return if (isExternalStorageDisable()) "" else getAbsolutePath(
                AppUtils.getApp().getExternalFilesDir(
                    Environment.DIRECTORY_MUSIC
                )
            )
        }

        /**
         * Return the path of /storage/emulated/0/Android/data/package/files/Podcasts.
         *
         * @return the path of /storage/emulated/0/Android/data/package/files/Podcasts
         */
        fun getExternalAppPodcastsPath(): String {
            return if (isExternalStorageDisable()) "" else getAbsolutePath(
                AppUtils.getApp().getExternalFilesDir(
                    Environment.DIRECTORY_PODCASTS
                )
            )
        }

        /**
         * Return the path of /storage/emulated/0/Android/data/package/files/Ringtones.
         *
         * @return the path of /storage/emulated/0/Android/data/package/files/Ringtones
         */
        fun getExternalAppRingtonesPath(): String {
            return if (isExternalStorageDisable()) "" else getAbsolutePath(
                AppUtils.getApp().getExternalFilesDir(
                    Environment.DIRECTORY_RINGTONES
                )
            )
        }

        /**
         * Return the path of /storage/emulated/0/Android/data/package/files/Alarms.
         *
         * @return the path of /storage/emulated/0/Android/data/package/files/Alarms
         */
        fun getExternalAppAlarmsPath(): String {
            return if (isExternalStorageDisable()) "" else getAbsolutePath(
                AppUtils.getApp().getExternalFilesDir(
                    Environment.DIRECTORY_ALARMS
                )
            )
        }

        /**
         * Return the path of /storage/emulated/0/Android/data/package/files/Notifications.
         *
         * @return the path of /storage/emulated/0/Android/data/package/files/Notifications
         */
        fun getExternalAppNotificationsPath(): String {
            return if (isExternalStorageDisable()) "" else getAbsolutePath(
                AppUtils.getApp().getExternalFilesDir(
                    Environment.DIRECTORY_NOTIFICATIONS
                )
            )
        }

        /**
         * Return the path of /storage/emulated/0/Android/data/package/files/Pictures.
         *
         * @return path of /storage/emulated/0/Android/data/package/files/Pictures
         */
        fun getExternalAppPicturesPath(): String {
            return if (isExternalStorageDisable()) "" else getAbsolutePath(
                AppUtils.getApp().getExternalFilesDir(
                    Environment.DIRECTORY_PICTURES
                )
            )
        }

        /**
         * Return the path of /storage/emulated/0/Android/data/package/files/Movies.
         *
         * @return the path of /storage/emulated/0/Android/data/package/files/Movies
         */
        fun getExternalAppMoviesPath(): String {
            return if (isExternalStorageDisable()) "" else getAbsolutePath(
                AppUtils.getApp().getExternalFilesDir(
                    Environment.DIRECTORY_MOVIES
                )
            )
        }

        /**
         * Return the path of /storage/emulated/0/Android/data/package/files/Download.
         *
         * @return the path of /storage/emulated/0/Android/data/package/files/Download
         */
        fun getExternalAppDownloadPath(): String {
            return if (isExternalStorageDisable()) "" else getAbsolutePath(
                AppUtils.getApp().getExternalFilesDir(
                    Environment.DIRECTORY_DOWNLOADS
                )
            )
        }

        /**
         * Return the path of /storage/emulated/0/Android/data/package/files/DCIM.
         *
         * @return the path of /storage/emulated/0/Android/data/package/files/DCIM
         */
        fun getExternalAppDcimPath(): String {
            return if (isExternalStorageDisable()) "" else getAbsolutePath(
                AppUtils.getApp().getExternalFilesDir(
                    Environment.DIRECTORY_DCIM
                )
            )
        }

        /**
         * Return the path of /storage/emulated/0/Android/data/package/files/Documents.
         *
         * @return the path of /storage/emulated/0/Android/data/package/files/Documents
         */
        fun getExternalAppDocumentsPath(): String {
            if (isExternalStorageDisable()) return ""
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                getAbsolutePath(AppUtils.getApp().getExternalFilesDir(null)) + "/Documents"
            } else getAbsolutePath(AppUtils.getApp().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS))
        }

        /**
         * Return the path of /storage/emulated/0/Android/obb/package.
         *
         * @return the path of /storage/emulated/0/Android/obb/package
         */
        fun getExternalAppObbPath(): String {
            return if (isExternalStorageDisable()) "" else getAbsolutePath(AppUtils.getApp().obbDir)
        }

        private fun isExternalStorageDisable(): Boolean {
            return Environment.MEDIA_MOUNTED != Environment.getExternalStorageState()
        }

        private fun getAbsolutePath(file: File?): String {
            return if (file == null) "" else file.absolutePath
        }
    }
}