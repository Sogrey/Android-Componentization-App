package top.sogrey.common.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.view.View
import androidx.core.content.FileProvider
import top.sogrey.common.compatible.FileProvider7
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * 类型转换
 */
open class ConvertUtils {
    companion object {
        /**
         * Bitmap to bytes.
         *
         * @param bitmap The bitmap.
         * @param format The format of bitmap.
         * @return bytes
         */
        fun bitmap2Bytes(bitmap: Bitmap?, format: Bitmap.CompressFormat): ByteArray? {
            if (bitmap == null) return null
            val baos = ByteArrayOutputStream()
            bitmap.compress(format, 100, baos)
            return baos.toByteArray()
        }

        /**
         * Bytes to bitmap.
         *
         * @param bytes The bytes.
         * @return bitmap
         */
        fun bytes2Bitmap(bytes: ByteArray?): Bitmap? {
            return if (bytes == null || bytes.isEmpty())
                null
            else
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }

        /**
         * Drawable to bitmap.
         *
         * @param drawable The drawable.
         * @return bitmap
         */
        fun drawable2Bitmap(drawable: Drawable): Bitmap {
            if (drawable is BitmapDrawable) {
                if (drawable.bitmap != null) {
                    return drawable.bitmap
                }
            }
            val bitmap: Bitmap
            if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
                bitmap = Bitmap.createBitmap(
                    1, 1,
                    if (drawable.opacity != PixelFormat.OPAQUE)
                        Bitmap.Config.ARGB_8888
                    else
                        Bitmap.Config.RGB_565
                )
            } else {
                bitmap = Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    if (drawable.opacity != PixelFormat.OPAQUE)
                        Bitmap.Config.ARGB_8888
                    else
                        Bitmap.Config.RGB_565
                )
            }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }

        /**
         * Bitmap to drawable.
         *
         * @param bitmap The bitmap.
         * @return drawable
         */
        fun bitmap2Drawable(bitmap: Bitmap?): Drawable? {
            return if (bitmap == null) null else BitmapDrawable(
                AppUtils.getApp().resources,
                bitmap
            )
        }

        /**
         * Drawable to bytes.
         *
         * @param drawable The drawable.
         * @param format   The format of bitmap.
         * @return bytes
         */
        fun drawable2Bytes(
            drawable: Drawable?,
            format: Bitmap.CompressFormat
        ): ByteArray? {
            return if (drawable == null) null else bitmap2Bytes(drawable2Bitmap(drawable), format)
        }

        /**
         * Bytes to drawable.
         *
         * @param bytes The bytes.
         * @return drawable
         */
        fun bytes2Drawable(bytes: ByteArray?): Drawable? {
            return if (bytes == null) null else bitmap2Drawable(bytes2Bitmap(bytes))
        }

        /**
         * View to bitmap.
         *
         * @param view The view.
         * @return bitmap
         */
        fun view2Bitmap(view: View?): Bitmap? {
            if (view == null) return null
            val ret =
                Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(ret)
            val bgDrawable = view.background
            if (bgDrawable != null) {
                bgDrawable.draw(canvas)
            } else {
                canvas.drawColor(Color.WHITE)
            }
            view.draw(canvas)
            return ret
        }

        /**
         * Value of dp to value of px.
         *
         * @param dpValue The value of dp.
         * @return value of px
         */
        fun dp2px(dpValue: Float): Int {
            val scale = AppUtils.getApp().resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }

        /**
         * Value of px to value of dp.
         *
         * @param pxValue The value of px.
         * @return value of dp
         */
        fun px2dp(pxValue: Float): Int {
            val scale = AppUtils.getApp().resources.displayMetrics.density
            return (pxValue / scale + 0.5f).toInt()
        }

        /**
         * Value of sp to value of px.
         *
         * @param spValue The value of sp.
         * @return value of px
         */
        fun sp2px(spValue: Float): Int {
            val fontScale = AppUtils.getApp().resources.displayMetrics.scaledDensity
            return (spValue * fontScale + 0.5f).toInt()
        }

        /**
         * Value of px to value of sp.
         *
         * @param pxValue The value of px.
         * @return value of sp
         */
        fun px2sp(pxValue: Float): Int {
            val fontScale = AppUtils.getApp().resources.displayMetrics.scaledDensity
            return (pxValue / fontScale + 0.5f).toInt()
        }


        /**
         * 文件路径转Uri
         * @param context 上下文
         * @param filePath 文件路径
         * @return 文件Uri:content://xxx
         */
        fun path2Uri(context: Context, filePath: String): Uri {
            val file = File(filePath)
            return file2Uri(context, file)
        }

        /**
         * 文件转Uri,(兼容android 7.0)
         * @param context 上下文
         * @param file 文件
         * @return 文件Uri:content://xxx
         */
        fun file2Uri(context: Context, file: File): Uri = FileProvider7.getUriForFile(context, file)
    }
}