package top.sogrey.common.utils

import android.Manifest.permission.VIBRATE
import android.content.Context
import android.os.Vibrator
import androidx.annotation.RequiresPermission


/**
 * 震动相关

vibrate: 震动
cancel : 取消
 * <p/>
 * @author Sogrey
 * @date 2019-10-31 12:26
 */

class VibrateUtils {
    constructor() {
        throw UnsupportedOperationException(this.javaClass.simpleName + " cannot be instantiated")
    }

    companion object {
        private var vibrator: Vibrator? = null


        /**
         * Vibrate.
         *
         * Must hold `<uses-permission android:name="android.permission.VIBRATE" />`
         *
         * @param milliseconds The number of milliseconds to vibrate.
         */
        @RequiresPermission(VIBRATE)
        fun vibrate(milliseconds: Long) {
            val vibrator = getVibrator() ?: return
            vibrator.vibrate(milliseconds)
        }

        /**
         * Vibrate.
         *
         * Must hold `<uses-permission android:name="android.permission.VIBRATE" />`
         *
         * @param pattern An array of longs of times for which to turn the vibrator on or off.
         * @param repeat  The index into pattern at which to repeat, or -1 if you don't want to repeat.
         */
        @RequiresPermission(VIBRATE)
        fun vibrate(pattern: LongArray, repeat: Int) {
            val vibrator = getVibrator() ?: return
            vibrator.vibrate(pattern, repeat)
        }

        /**
         * Cancel vibrate.
         *
         * Must hold `<uses-permission android:name="android.permission.VIBRATE" />`
         */
        @RequiresPermission(VIBRATE)
        fun cancel() {
            val vibrator = getVibrator() ?: return
            vibrator.cancel()
        }

        private fun getVibrator(): Vibrator? {
            if (vibrator == null) {
                vibrator = AppUtils.getApp().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
            }
            return vibrator
        }
    }
}