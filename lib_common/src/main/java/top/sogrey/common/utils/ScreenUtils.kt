package top.sogrey.common.utils

import android.Manifest.permission.WRITE_SETTINGS
import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Point
import android.os.Build
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.provider.Settings.System.SCREEN_OFF_TIMEOUT
import android.util.DisplayMetrics
import android.view.Surface
import android.view.WindowManager
import androidx.annotation.RequiresPermission


/**
 * 屏幕相关
 * @author Sogrey
 * @date 2019/10/30
 */
class ScreenUtils {
    constructor(){
        throw UnsupportedOperationException(this.javaClass.simpleName + " cannot be instantiated")
    }companion object{

        /**
         * Return the width of screen, in pixel.
         *
         * @return the width of screen, in pixel
         */
        fun getScreenWidth(): Int {
            val wm = AppUtils.getApp().getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val point = Point()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                wm.defaultDisplay.getRealSize(point)
            } else {
                wm.defaultDisplay.getSize(point)
            }
            return point.x
        }

        /**
         * Return the height of screen, in pixel.
         *
         * @return the height of screen, in pixel
         */
        fun getScreenHeight(): Int {
            val wm = AppUtils.getApp().getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val point = Point()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                wm.defaultDisplay.getRealSize(point)
            } else {
                wm.defaultDisplay.getSize(point)
            }
            return point.y
        }

        /**
         * Return the application's width of screen, in pixel.
         *
         * @return the application's width of screen, in pixel
         */
        fun getAppScreenWidth(): Int {
            val wm = AppUtils.getApp().getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val point = Point()
            wm.defaultDisplay.getSize(point)
            return point.x
        }

        /**
         * Return the application's height of screen, in pixel.
         *
         * @return the application's height of screen, in pixel
         */
        fun getAppScreenHeight(): Int {
            val wm = AppUtils.getApp().getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val point = Point()
            wm.defaultDisplay.getSize(point)
            return point.y
        }

        /**
         * Return the density of screen.
         *
         * @return the density of screen
         */
        fun getScreenDensity(): Float {
            return AppUtils.getApp().resources.displayMetrics.density
        }

        /**
         * Return the screen density expressed as dots-per-inch.
         *
         * @return the screen density expressed as dots-per-inch
         */
        fun getScreenDensityDpi(): Int {
            return AppUtils.getApp().resources.displayMetrics.densityDpi
        }

        /**
         * Set full screen.
         *
         * @param activity The activity.
         */
        fun setFullScreen(activity: Activity) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        /**
         * Set non full screen.
         *
         * @param activity The activity.
         */
        fun setNonFullScreen(activity: Activity) {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        /**
         * Toggle full screen.
         *
         * @param activity The activity.
         */
        fun toggleFullScreen(activity: Activity) {
            val isFullScreen = isFullScreen(activity)
            val window = activity.window
            if (isFullScreen) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }
        }

        /**
         * Return whether screen is full.
         *
         * @param activity The activity.
         * @return `true`: yes<br></br>`false`: no
         */
        fun isFullScreen(activity: Activity): Boolean {
            val fullScreenFlag = WindowManager.LayoutParams.FLAG_FULLSCREEN
            return activity.window.attributes.flags and fullScreenFlag == fullScreenFlag
        }

        /**
         * Set the screen to landscape.
         *
         * @param activity The activity.
         */
        fun setLandscape(activity: Activity) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        /**
         * Set the screen to portrait.
         *
         * @param activity The activity.
         */
        fun setPortrait(activity: Activity) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        /**
         * Return whether screen is landscape.
         *
         * @return `true`: yes<br></br>`false`: no
         */
        fun isLandscape(): Boolean {
            return AppUtils.getApp().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        }

        /**
         * Return whether screen is portrait.
         *
         * @return `true`: yes<br></br>`false`: no
         */
        fun isPortrait(): Boolean {
            return AppUtils.getApp().resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        }

        /**
         * Return the rotation of screen.
         *
         * @param activity The activity.
         * @return the rotation of screen
         */
        fun getScreenRotation(activity: Activity): Int {
            return when (activity.windowManager.defaultDisplay.rotation) {
                Surface.ROTATION_0 -> 0
                Surface.ROTATION_90 -> 90
                Surface.ROTATION_180 -> 180
                Surface.ROTATION_270 -> 270
                else -> 0
            }
        }

        /**
         * Return the bitmap of screen.
         *
         * @param activity The activity.
         * @return the bitmap of screen
         */
        fun screenShot(activity: Activity): Bitmap? {
            return screenShot(activity, false)
        }

        /**
         * Return the bitmap of screen.
         *
         * @param activity          The activity.
         * @param isDeleteStatusBar True to delete status bar, false otherwise.
         * @return the bitmap of screen
         */
        fun screenShot(activity: Activity, isDeleteStatusBar: Boolean): Bitmap? {
            val decorView = activity.window.decorView
            decorView.isDrawingCacheEnabled = true
            decorView.setWillNotCacheDrawing(false)
            val bmp = decorView.drawingCache ?: return null
            val dm = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(dm)
            val ret: Bitmap
            ret = if (isDeleteStatusBar) {
                val resources = activity.resources
                val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
                val statusBarHeight = resources.getDimensionPixelSize(resourceId)
                Bitmap.createBitmap(
                    bmp,
                    0,
                    statusBarHeight,
                    dm.widthPixels,
                    dm.heightPixels - statusBarHeight
                )
            } else {
                Bitmap.createBitmap(bmp, 0, 0, dm.widthPixels, dm.heightPixels)
            }
            decorView.destroyDrawingCache()
            return ret
        }

        /**
         * Return whether screen is locked.
         *
         * @return `true`: yes<br></br>`false`: no
         */
        fun isScreenLock(): Boolean {
            val km = AppUtils.getApp().getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            return km.inKeyguardRestrictedInputMode()
        }

        /**
         * Set the duration of sleep.
         *
         * Must hold `<uses-permission android:name="android.permission.WRITE_SETTINGS" />`
         *
         * @param duration The duration.
         */
        @RequiresPermission(WRITE_SETTINGS)
        fun setSleepDuration(duration: Int) {
            Settings.System.putInt(
                AppUtils.getApp().contentResolver,
                SCREEN_OFF_TIMEOUT,
                duration
            )
        }

        /**
         * Return the duration of sleep.
         *
         * @return the duration of sleep.
         */
        fun getSleepDuration(): Int {
            return try {
                Settings.System.getInt(
                    AppUtils.getApp().contentResolver,
                    SCREEN_OFF_TIMEOUT
                )
            } catch (e: SettingNotFoundException) {
                e.printStackTrace()
                -123
            }

        }
    }
}