package top.sogrey.common.utils

import android.graphics.*
import android.graphics.Bitmap.CompressFormat
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.CropTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import java.io.*


class ImageUtils {
    companion object {
        fun setImage(path: String, imageView: ImageView) {
            Glide.with(AppUtils.getApp()).load(path).into(imageView)
        }







        /**
         * Return bitmap.
         *
         * @param file The file.
         * @return bitmap
         */
        fun getBitmap(file: File?): Bitmap? {
            return if (file == null) null else BitmapFactory.decodeFile(file.absolutePath)
        }

        /**
         * Return bitmap.
         *
         * @param file      The file.
         * @param maxWidth  The maximum width.
         * @param maxHeight The maximum height.
         * @return bitmap
         */
        fun getBitmap(file: File?, maxWidth: Int, maxHeight: Int): Bitmap? {
            if (file == null) return null
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(file.absolutePath, options)
            options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeFile(file.absolutePath, options)
        }

        /**
         * Return bitmap.
         *
         * @param filePath The path of file.
         * @return bitmap
         */
        fun getBitmap(filePath: String): Bitmap? {
            return if (isSpace(filePath)) null else BitmapFactory.decodeFile(filePath)
        }

        /**
         * Return bitmap.
         *
         * @param filePath  The path of file.
         * @param maxWidth  The maximum width.
         * @param maxHeight The maximum height.
         * @return bitmap
         */
        fun getBitmap(filePath: String, maxWidth: Int, maxHeight: Int): Bitmap? {
            if (isSpace(filePath)) return null
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(filePath, options)
            options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeFile(filePath, options)
        }

        /**
         * Return bitmap.
         *
         * @param is The input stream.
         * @return bitmap
         */
        fun getBitmap(`is`: InputStream?): Bitmap? {
            return if (`is` == null) null else BitmapFactory.decodeStream(`is`)
        }

        /**
         * Return bitmap.
         *
         * @param is        The input stream.
         * @param maxWidth  The maximum width.
         * @param maxHeight The maximum height.
         * @return bitmap
         */
        fun getBitmap(`is`: InputStream?, maxWidth: Int, maxHeight: Int): Bitmap? {
            if (`is` == null) return null
            val bytes = input2Byte(`is`)
            return getBitmap(bytes!!, 0, maxWidth, maxHeight)
        }

        /**
         * Return bitmap.
         *
         * @param data   The data.
         * @param offset The offset.
         * @return bitmap
         */
        fun getBitmap(data: ByteArray, offset: Int): Bitmap? {
            return if (data.isEmpty()) null else BitmapFactory.decodeByteArray(
                data,
                offset,
                data.size
            )
        }

        /**
         * Return bitmap.
         *
         * @param data      The data.
         * @param offset    The offset.
         * @param maxWidth  The maximum width.
         * @param maxHeight The maximum height.
         * @return bitmap
         */
        fun getBitmap(
            data: ByteArray,
            offset: Int,
            maxWidth: Int,
            maxHeight: Int
        ): Bitmap? {
            if (data.isEmpty()) return null
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeByteArray(data, offset, data.size, options)
            options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeByteArray(data, offset, data.size, options)
        }

        /**
         * Return bitmap.
         *
         * @param resId The resource id.
         * @return bitmap
         */
        fun getBitmap(@DrawableRes resId: Int): Bitmap {
            val drawable = ContextCompat.getDrawable(AppUtils.getApp(), resId)
            val canvas = Canvas()
            val bitmap = Bitmap.createBitmap(
                drawable!!.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            canvas.setBitmap(bitmap)
            drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            drawable.draw(canvas)
            return bitmap
        }

        /**
         * Return bitmap.
         *
         * @param resId     The resource id.
         * @param maxWidth  The maximum width.
         * @param maxHeight The maximum height.
         * @return bitmap
         */
        fun getBitmap(
            @DrawableRes resId: Int,
            maxWidth: Int,
            maxHeight: Int
        ): Bitmap {
            val options = BitmapFactory.Options()
            val resources = AppUtils.getApp().resources
            options.inJustDecodeBounds = true
            BitmapFactory.decodeResource(resources, resId, options)
            options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeResource(resources, resId, options)
        }

        /**
         * Return bitmap.
         *
         * @param fd The file descriptor.
         * @return bitmap
         */
        fun getBitmap(fd: FileDescriptor?): Bitmap? {
            return if (fd == null) null else BitmapFactory.decodeFileDescriptor(fd)
        }

        /**
         * Return bitmap.
         *
         * @param fd        The file descriptor
         * @param maxWidth  The maximum width.
         * @param maxHeight The maximum height.
         * @return bitmap
         */
        fun getBitmap(
            fd: FileDescriptor?,
            maxWidth: Int,
            maxHeight: Int
        ): Bitmap? {
            if (fd == null) return null
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFileDescriptor(fd, null, options)
            options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeFileDescriptor(fd, null, options)
        }

        /**
         * Return the bitmap with the specified color.
         *
         * @param src   The source of bitmap.
         * @param color The color.
         * @return the bitmap with the specified color
         */
        fun drawColor(src: Bitmap, @ColorInt color: Int): Bitmap? {
            return drawColor(src, color, false)
        }

        /**
         * Return the bitmap with the specified color.
         *
         * @param src     The source of bitmap.
         * @param color   The color.
         * @param recycle True to recycle the source of bitmap, false otherwise.
         * @return the bitmap with the specified color
         */
        fun drawColor(
            src: Bitmap,
            @ColorInt color: Int,
            recycle: Boolean
        ): Bitmap? {
            if (isEmptyBitmap(src)) return null
            val ret = if (recycle) src else src.copy(src.config, true)
            val canvas = Canvas(ret)
            canvas.drawColor(color, PorterDuff.Mode.DARKEN)
            return ret
        }

        /**
         * Return the scaled bitmap.
         *
         * @param src       The source of bitmap.
         * @param newWidth  The new width.
         * @param newHeight The new height.
         * @return the scaled bitmap
         */
        fun scale(src: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
            return scale(src, newWidth, newHeight, false)
        }

        /**
         * Return the scaled bitmap.
         *
         * @param src       The source of bitmap.
         * @param newWidth  The new width.
         * @param newHeight The new height.
         * @param recycle   True to recycle the source of bitmap, false otherwise.
         * @return the scaled bitmap
         */
        fun scale(
            src: Bitmap,
            newWidth: Int,
            newHeight: Int,
            recycle: Boolean
        ): Bitmap? {
            if (isEmptyBitmap(src)) return null
            val ret = Bitmap.createScaledBitmap(src, newWidth, newHeight, true)
            if (recycle && !src.isRecycled && ret != src) src.recycle()
            return ret
        }

        /**
         * Return the scaled bitmap
         *
         * @param src         The source of bitmap.
         * @param scaleWidth  The scale of width.
         * @param scaleHeight The scale of height.
         * @return the scaled bitmap
         */
        fun scale(src: Bitmap, scaleWidth: Float, scaleHeight: Float): Bitmap? {
            return scale(src, scaleWidth, scaleHeight, false)
        }

        /**
         * Return the scaled bitmap
         *
         * @param src         The source of bitmap.
         * @param scaleWidth  The scale of width.
         * @param scaleHeight The scale of height.
         * @param recycle     True to recycle the source of bitmap, false otherwise.
         * @return the scaled bitmap
         */
        fun scale(
            src: Bitmap,
            scaleWidth: Float,
            scaleHeight: Float,
            recycle: Boolean
        ): Bitmap? {
            if (isEmptyBitmap(src)) return null
            val matrix = Matrix()
            matrix.setScale(scaleWidth, scaleHeight)
            val ret = Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
            if (recycle && !src.isRecycled && ret != src) src.recycle()
            return ret
        }

        /**
         * Return the clipped bitmap.
         *
         * @param src    The source of bitmap.
         * @param x      The x coordinate of the first pixel.
         * @param y      The y coordinate of the first pixel.
         * @param width  The width.
         * @param height The height.
         * @return the clipped bitmap
         */
        fun clip(
            src: Bitmap,
            x: Int,
            y: Int,
            width: Int,
            height: Int
        ): Bitmap? {
            return clip(src, x, y, width, height, false)
        }

        /**
         * Return the clipped bitmap.
         *
         * @param src     The source of bitmap.
         * @param x       The x coordinate of the first pixel.
         * @param y       The y coordinate of the first pixel.
         * @param width   The width.
         * @param height  The height.
         * @param recycle True to recycle the source of bitmap, false otherwise.
         * @return the clipped bitmap
         */
        fun clip(
            src: Bitmap,
            x: Int,
            y: Int,
            width: Int,
            height: Int,
            recycle: Boolean
        ): Bitmap? {
            if (isEmptyBitmap(src)) return null
            val ret = Bitmap.createBitmap(src, x, y, width, height)
            if (recycle && !src.isRecycled && ret != src) src.recycle()
            return ret
        }

        /**
         * Return the skewed bitmap.
         *
         * @param src The source of bitmap.
         * @param kx  The skew factor of x.
         * @param ky  The skew factor of y.
         * @return the skewed bitmap
         */
        fun skew(src: Bitmap, kx: Float, ky: Float): Bitmap? {
            return skew(src, kx, ky, 0f, 0f, false)
        }

        /**
         * Return the skewed bitmap.
         *
         * @param src     The source of bitmap.
         * @param kx      The skew factor of x.
         * @param ky      The skew factor of y.
         * @param recycle True to recycle the source of bitmap, false otherwise.
         * @return the skewed bitmap
         */
        fun skew(
            src: Bitmap,
            kx: Float,
            ky: Float,
            recycle: Boolean
        ): Bitmap? {
            return skew(src, kx, ky, 0f, 0f, recycle)
        }

        /**
         * Return the skewed bitmap.
         *
         * @param src The source of bitmap.
         * @param kx  The skew factor of x.
         * @param ky  The skew factor of y.
         * @param px  The x coordinate of the pivot point.
         * @param py  The y coordinate of the pivot point.
         * @return the skewed bitmap
         */
        fun skew(
            src: Bitmap,
            kx: Float,
            ky: Float,
            px: Float,
            py: Float
        ): Bitmap? {
            return skew(src, kx, ky, px, py, false)
        }

        /**
         * Return the skewed bitmap.
         *
         * @param src     The source of bitmap.
         * @param kx      The skew factor of x.
         * @param ky      The skew factor of y.
         * @param px      The x coordinate of the pivot point.
         * @param py      The y coordinate of the pivot point.
         * @param recycle True to recycle the source of bitmap, false otherwise.
         * @return the skewed bitmap
         */
        fun skew(
            src: Bitmap,
            kx: Float,
            ky: Float,
            px: Float,
            py: Float,
            recycle: Boolean
        ): Bitmap? {
            if (isEmptyBitmap(src)) return null
            val matrix = Matrix()
            matrix.setSkew(kx, ky, px, py)
            val ret = Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
            if (recycle && !src.isRecycled && ret != src) src.recycle()
            return ret
        }

        /**
         * Return the rotated bitmap.
         *
         * @param src     The source of bitmap.
         * @param degrees The number of degrees.
         * @param px      The x coordinate of the pivot point.
         * @param py      The y coordinate of the pivot point.
         * @return the rotated bitmap
         */
        fun rotate(
            src: Bitmap,
            degrees: Int,
            px: Float,
            py: Float
        ): Bitmap? {
            return rotate(src, degrees, px, py, false)
        }

        /**
         * Return the rotated bitmap.
         *
         * @param src     The source of bitmap.
         * @param degrees The number of degrees.
         * @param px      The x coordinate of the pivot point.
         * @param py      The y coordinate of the pivot point.
         * @param recycle True to recycle the source of bitmap, false otherwise.
         * @return the rotated bitmap
         */
        fun rotate(
            src: Bitmap,
            degrees: Int,
            px: Float,
            py: Float,
            recycle: Boolean
        ): Bitmap? {
            if (isEmptyBitmap(src)) return null
            if (degrees == 0) return src
            val matrix = Matrix()
            matrix.setRotate(degrees*1f, px, py)
            val ret = Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
            if (recycle && !src.isRecycled && ret != src) src.recycle()
            return ret
        }

        /**
         * Return the rotated degree.
         *
         * @param filePath The path of file.
         * @return the rotated degree
         */
        fun getRotateDegree(filePath: String): Int {
            try {
                val exifInterface = ExifInterface(filePath)
                val orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                return when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
                    else -> 0
                }
            } catch (e: IOException) {
                e.printStackTrace()
                return -1
            }
        }

        /**
         * Return the round bitmap.
         *
         * @param src The source of bitmap.
         * @return the round bitmap
         */
        fun toRound(src: Bitmap): Bitmap? {
            return toRound(src, 0, 0, false)
        }

        /**
         * Return the round bitmap.
         *
         * @param src     The source of bitmap.
         * @param recycle True to recycle the source of bitmap, false otherwise.
         * @return the round bitmap
         */
        fun toRound(src: Bitmap, recycle: Boolean): Bitmap? {
            return toRound(src, 0, 0, recycle)
        }

        /**
         * Return the round bitmap.
         *
         * @param src         The source of bitmap.
         * @param borderSize  The size of border.
         * @param borderColor The color of border.
         * @return the round bitmap
         */
        fun toRound(
            src: Bitmap,
            borderSize: Int,
            @ColorInt borderColor: Int
        ): Bitmap? {
            if(borderSize<0) return null
            return toRound(src, borderSize, borderColor, false)
        }

        /**
         * Return the round bitmap.
         *
         * @param src         The source of bitmap.
         * @param recycle     True to recycle the source of bitmap, false otherwise.
         * @param borderSize  The size of border.
         * @param borderColor The color of border.
         * @return the round bitmap
         */
        fun toRound(
            src: Bitmap,
            borderSize: Int,
            @ColorInt borderColor: Int,
            recycle: Boolean
        ): Bitmap? {
            if (isEmptyBitmap(src)) return null
            if(borderSize<0) return null
            val width = src.width
            val height = src.height
            val size = Math.min(width, height)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            val ret = Bitmap.createBitmap(width, height, src.config)
            val center = size / 2f
            val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
            rectF.inset((width - size) / 2f, (height - size) / 2f)
            val matrix = Matrix()
            matrix.setTranslate(rectF.left, rectF.top)
            if (width != height) {
                matrix.preScale(size.toFloat() / width, size.toFloat() / height)
            }
            val shader = BitmapShader(src, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            shader.setLocalMatrix(matrix)
            paint.shader = shader
            val canvas = Canvas(ret)
            canvas.drawRoundRect(rectF, center, center, paint)
            if (borderSize > 0) {
                paint.shader = null
                paint.color = borderColor
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = borderSize*1f
                val radius = center - borderSize / 2f
                canvas.drawCircle(width / 2f, height / 2f, radius, paint)
            }
            if (recycle && !src.isRecycled && ret != src) src.recycle()
            return ret
        }

        /**
         * Return the round corner bitmap.
         *
         * @param src    The source of bitmap.
         * @param radius The radius of corner.
         * @return the round corner bitmap
         */
        fun toRoundCorner(src: Bitmap, radius: Float): Bitmap? {
            return toRoundCorner(src, radius, 0, 0, false)
        }

        /**
         * Return the round corner bitmap.
         *
         * @param src     The source of bitmap.
         * @param radius  The radius of corner.
         * @param recycle True to recycle the source of bitmap, false otherwise.
         * @return the round corner bitmap
         */
        fun toRoundCorner(
            src: Bitmap,
            radius: Float,
            recycle: Boolean
        ): Bitmap? {
            return toRoundCorner(src, radius, 0, 0, recycle)
        }

        /**
         * Return the round corner bitmap.
         *
         * @param src         The source of bitmap.
         * @param radius      The radius of corner.
         * @param borderSize  The size of border.
         * @param borderColor The color of border.
         * @return the round corner bitmap
         */
        fun toRoundCorner(
            src: Bitmap,
            radius: Float,
            borderSize: Int,
            @ColorInt borderColor: Int
        ): Bitmap? {
            if(borderSize<0) return null
            return toRoundCorner(src, radius, borderSize, borderColor, false)
        }

        /**
         * Return the round corner bitmap.
         *
         * @param src         The source of bitmap.
         * @param radius      The radius of corner.
         * @param borderSize  The size of border.
         * @param borderColor The color of border.
         * @param recycle     True to recycle the source of bitmap, false otherwise.
         * @return the round corner bitmap
         */
        fun toRoundCorner(
            src: Bitmap,
            radius: Float,
            borderSize: Int,
            @ColorInt borderColor: Int,
            recycle: Boolean
        ): Bitmap? {
            if (isEmptyBitmap(src)) return null
            if(borderSize<0) return null
            val width = src.width
            val height = src.height
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            val ret = Bitmap.createBitmap(width, height, src.config)
            val shader = BitmapShader(src, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            paint.shader = shader
            val canvas = Canvas(ret)
            val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
            val halfBorderSize = borderSize / 2f
            rectF.inset(halfBorderSize, halfBorderSize)
            canvas.drawRoundRect(rectF, radius, radius, paint)
            if (borderSize > 0) {
                paint.shader = null
                paint.color = borderColor
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = borderSize*1f
                paint.strokeCap = Paint.Cap.ROUND
                canvas.drawRoundRect(rectF, radius, radius, paint)
            }
            if (recycle && !src.isRecycled && ret != src) src.recycle()
            return ret
        }

        /**
         * Return the round corner bitmap with border.
         *
         * @param src          The source of bitmap.
         * @param borderSize   The size of border.
         * @param color        The color of border.
         * @param cornerRadius The radius of corner.
         * @return the round corner bitmap with border
         */
        fun addCornerBorder(
            src: Bitmap,
            borderSize: Int,
            @ColorInt color: Int,
            @FloatRange(from = 0.0) cornerRadius: Float
        ): Bitmap? {
            if (borderSize < 1) return null
            return addBorder(src, borderSize, color, false, cornerRadius, false)
        }

        /**
         * Return the round corner bitmap with border.
         *
         * @param src          The source of bitmap.
         * @param borderSize   The size of border.
         * @param color        The color of border.
         * @param cornerRadius The radius of corner.
         * @param recycle      True to recycle the source of bitmap, false otherwise.
         * @return the round corner bitmap with border
         */
        fun addCornerBorder(
            src: Bitmap,
            borderSize: Int,
            @ColorInt color: Int,
            @FloatRange(from = 0.0) cornerRadius: Float,
            recycle: Boolean
        ): Bitmap? {
            if (borderSize < 1) return null
            return addBorder(src, borderSize, color, false, cornerRadius, recycle)
        }

        /**
         * Return the round bitmap with border.
         *
         * @param src        The source of bitmap.
         * @param borderSize The size of border.
         * @param color      The color of border.
         * @return the round bitmap with border
         */
        fun addCircleBorder(
            src: Bitmap,
            borderSize: Int,
            @ColorInt color: Int
        ): Bitmap? {
            if (borderSize < 1) return null
            return addBorder(src, borderSize, color, true, 0f, false)
        }

        /**
         * Return the round bitmap with border.
         *
         * @param src        The source of bitmap.
         * @param borderSize The size of border.
         * @param color      The color of border.
         * @param recycle    True to recycle the source of bitmap, false otherwise.
         * @return the round bitmap with border
         */
        fun addCircleBorder(
            src: Bitmap,
            borderSize: Int,
            @ColorInt color: Int,
            recycle: Boolean
        ): Bitmap? {
            if (borderSize < 1) return null
            return addBorder(src, borderSize, color, true, 0f, recycle)
        }

        /**
         * Return the bitmap with border.
         *
         * @param src          The source of bitmap.
         * @param borderSize   The size of border.
         * @param color        The color of border.
         * @param isCircle     True to draw circle, false to draw corner.
         * @param cornerRadius The radius of corner.
         * @param recycle      True to recycle the source of bitmap, false otherwise.
         * @return the bitmap with border
         */
        private fun addBorder(
            src: Bitmap,
            borderSize: Int,
            @ColorInt color: Int,
            isCircle: Boolean,
            cornerRadius: Float,
            recycle: Boolean
        ): Bitmap? {
            if (isEmptyBitmap(src)) return null
            if (borderSize < 1) return null
            val ret = if (recycle) src else src.copy(src.config, true)
            val width = ret.width
            val height = ret.height
            val canvas = Canvas(ret)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.color = color
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = borderSize * 1f
            if (isCircle) {
                val radius = Math.min(width, height) / 2f - borderSize / 2f
                canvas.drawCircle(width / 2f, height / 2f, radius, paint)
            } else {
                val halfBorderSize = borderSize shr 1
                val rectF = RectF(
                    halfBorderSize.toFloat(), halfBorderSize.toFloat(),
                    (width - halfBorderSize).toFloat(), (height - halfBorderSize).toFloat()
                )
                canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint)
            }
            return ret
        }

        /**
         * Return the bitmap with reflection.
         *
         * @param src              The source of bitmap.
         * @param reflectionHeight The height of reflection.
         * @return the bitmap with reflection
         */
        fun addReflection(src: Bitmap, reflectionHeight: Int): Bitmap? {
            return addReflection(src, reflectionHeight, false)
        }

        /**
         * Return the bitmap with reflection.
         *
         * @param src              The source of bitmap.
         * @param reflectionHeight The height of reflection.
         * @param recycle          True to recycle the source of bitmap, false otherwise.
         * @return the bitmap with reflection
         */
        fun addReflection(
            src: Bitmap,
            reflectionHeight: Int,
            recycle: Boolean
        ): Bitmap? {
            if (isEmptyBitmap(src)) return null
            val REFLECTION_GAP = 0
            val srcWidth = src.width
            val srcHeight = src.height
            val matrix = Matrix()
            matrix.preScale(1f, -1f)
            val reflectionBitmap = Bitmap.createBitmap(
                src, 0, srcHeight - reflectionHeight,
                srcWidth, reflectionHeight, matrix, false
            )
            val ret = Bitmap.createBitmap(srcWidth, srcHeight + reflectionHeight, src.config)
            val canvas = Canvas(ret)
            canvas.drawBitmap(src, 0f, 0f, null)
            canvas.drawBitmap(reflectionBitmap, 0f, (srcHeight + REFLECTION_GAP) * 1f, null)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            val shader = LinearGradient(
                0f, srcHeight * 1f,
                0f, (ret.height + REFLECTION_GAP) * 1f,
                0x70FFFFFF,
                0x00FFFFFF,
                Shader.TileMode.MIRROR
            )
            paint.shader = shader
            paint.xfermode = PorterDuffXfermode(android.graphics.PorterDuff.Mode.DST_IN)
            canvas.drawRect(
                0f,
                (srcHeight + REFLECTION_GAP) * 1f,
                srcWidth * 1f,
                ret.height * 1f,
                paint
            )
            if (!reflectionBitmap.isRecycled) reflectionBitmap.recycle()
            if (recycle && !src.isRecycled && ret != src) src.recycle()
            return ret
        }

        /**
         * Return the bitmap with text watermarking.
         *
         * @param src      The source of bitmap.
         * @param content  The content of text.
         * @param textSize The size of text.
         * @param color    The color of text.
         * @param x        The x coordinate of the first pixel.
         * @param y        The y coordinate of the first pixel.
         * @return the bitmap with text watermarking
         */
        fun addTextWatermark(
            src: Bitmap,
            content: String,
            textSize: Int,
            @ColorInt color: Int,
            x: Float,
            y: Float
        ): Bitmap? {
            return addTextWatermark(src, content, textSize.toFloat(), color, x, y, false)
        }

        /**
         * Return the bitmap with text watermarking.
         *
         * @param src      The source of bitmap.
         * @param content  The content of text.
         * @param textSize The size of text.
         * @param color    The color of text.
         * @param x        The x coordinate of the first pixel.
         * @param y        The y coordinate of the first pixel.
         * @param recycle  True to recycle the source of bitmap, false otherwise.
         * @return the bitmap with text watermarking
         */
        fun addTextWatermark(
            src: Bitmap,
            content: String?,
            textSize: Float,
            @ColorInt color: Int,
            x: Float,
            y: Float,
            recycle: Boolean
        ): Bitmap? {
            if (isEmptyBitmap(src) || content == null) return null
            val ret = src.copy(src.config, true)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            val canvas = Canvas(ret)
            paint.setColor(color)
            paint.setTextSize(textSize)
            val bounds = Rect()
            paint.getTextBounds(content, 0, content.length, bounds)
            canvas.drawText(content, x, y + textSize, paint)
            if (recycle && !src.isRecycled && ret != src) src.recycle()
            return ret
        }

        /**
         * Return the bitmap with image watermarking.
         *
         * @param src       The source of bitmap.
         * @param watermark The image watermarking.
         * @param x         The x coordinate of the first pixel.
         * @param y         The y coordinate of the first pixel.
         * @param alpha     The alpha of watermark.
         * @return the bitmap with image watermarking
         */
        fun addImageWatermark(
            src: Bitmap,
            watermark: Bitmap,
            x: Int, y: Int,
            alpha: Int
        ): Bitmap? {
            return addImageWatermark(src, watermark, x, y, alpha, false)
        }

        /**
         * Return the bitmap with image watermarking.
         *
         * @param src       The source of bitmap.
         * @param watermark The image watermarking.
         * @param x         The x coordinate of the first pixel.
         * @param y         The y coordinate of the first pixel.
         * @param alpha     The alpha of watermark.
         * @param recycle   True to recycle the source of bitmap, false otherwise.
         * @return the bitmap with image watermarking
         */
        fun addImageWatermark(
            src: Bitmap,
            watermark: Bitmap,
            x: Int,
            y: Int,
            alpha: Int,
            recycle: Boolean
        ): Bitmap? {
            if (isEmptyBitmap(src)) return null
            val ret = src.copy(src.config, true)
            if (!isEmptyBitmap(watermark)) {
                val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                val canvas = Canvas(ret)
                paint.alpha = alpha
                canvas.drawBitmap(watermark, x * 1f, y * 1f, paint)
            }
            if (recycle && !src.isRecycled && ret != src) src.recycle()
            return ret
        }

        /**
         * Return the alpha bitmap.
         *
         * @param src The source of bitmap.
         * @return the alpha bitmap
         */
        fun toAlpha(src: Bitmap): Bitmap? {
            return toAlpha(src, false)
        }

        /**
         * Return the alpha bitmap.
         *
         * @param src     The source of bitmap.
         * @param recycle True to recycle the source of bitmap, false otherwise.
         * @return the alpha bitmap
         */
        fun toAlpha(src: Bitmap, recycle: Boolean?): Bitmap? {
            if (isEmptyBitmap(src)) return null
            val ret = src.extractAlpha()
            if (recycle!! && !src.isRecycled && ret != src) src.recycle()
            return ret
        }

        /**
         * Return the gray bitmap.
         *
         * @param src The source of bitmap.
         * @return the gray bitmap
         */
        fun toGray(src: Bitmap): Bitmap? {
            return toGray(src, false)
        }

        /**
         * Return the gray bitmap.
         *
         * @param src     The source of bitmap.
         * @param recycle True to recycle the source of bitmap, false otherwise.
         * @return the gray bitmap
         */
        fun toGray(src: Bitmap, recycle: Boolean): Bitmap? {
            if (isEmptyBitmap(src)) return null
            val ret = Bitmap.createBitmap(src.width, src.height, src.config)
            val canvas = Canvas(ret)
            val paint = Paint()
            val colorMatrix = ColorMatrix()
            colorMatrix.setSaturation(0f)
            val colorMatrixColorFilter = ColorMatrixColorFilter(colorMatrix)
            paint.colorFilter = colorMatrixColorFilter
            canvas.drawBitmap(src, 0f, 0f, paint)
            if (recycle && !src.isRecycled && ret != src) src.recycle()
            return ret
        }

        /**
         * Return the blur bitmap fast.
         *
         * zoom out, blur, zoom in
         *
         * @param src    The source of bitmap.
         * @param scale  The scale(0...1).
         * @param radius The radius(0...25).
         * @return the blur bitmap
         */
        fun fastBlur(
            src: Bitmap,
            @FloatRange(from = 0.0, to = 1.0, fromInclusive = false)
            scale: Float,
            @FloatRange(from = 0.0, to = 25.0, fromInclusive = false)
            radius: Float
        ): Bitmap? {
            return fastBlur(src, scale, radius, false, false)
        }

        /**
         * Return the blur bitmap fast.
         *
         * zoom out, blur, zoom in
         *
         * @param src    The source of bitmap.
         * @param scale  The scale(0...1).
         * @param radius The radius(0...25).
         * @return the blur bitmap
         */
        fun fastBlur(
            src: Bitmap,
            @FloatRange(from = 0.0, to = 1.0, fromInclusive = false)
            scale: Float,
            @FloatRange(from = 0.0, to = 25.0, fromInclusive = false)
            radius: Float,
            recycle: Boolean
        ): Bitmap? {
            return fastBlur(src, scale, radius, recycle, false)
        }

        /**
         * Return the blur bitmap fast.
         *
         * zoom out, blur, zoom in
         *
         * @param src           The source of bitmap.
         * @param scale         The scale(0...1).
         * @param radius        The radius(0...25).
         * @param recycle       True to recycle the source of bitmap, false otherwise.
         * @param isReturnScale True to return the scale blur bitmap, false otherwise.
         * @return the blur bitmap
         */
        fun fastBlur(
            src: Bitmap,
            @FloatRange(from = 0.0, to = 1.0, fromInclusive = false)
            scale: Float,
            @FloatRange(from = 0.0, to = 25.0, fromInclusive = false)
            radius: Float,
            recycle: Boolean,
            isReturnScale: Boolean
        ): Bitmap? {
            if (isEmptyBitmap(src)) return null
            val width = src.width
            val height = src.height
            val matrix = Matrix()
            matrix.setScale(scale, scale)
            var scaleBitmap = Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
            val paint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
            val canvas = Canvas()
            val filter = PorterDuffColorFilter(
                Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP
            )
            paint.colorFilter = filter
            canvas.scale(scale, scale)
            canvas.drawBitmap(scaleBitmap, 0f, 0f, paint)
            scaleBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                renderScriptBlur(scaleBitmap, radius, recycle)
            } else {
                stackBlur(scaleBitmap, radius.toInt(), recycle)
            }
            if (scale == 1f || isReturnScale) {
                if (recycle && !src.isRecycled && scaleBitmap != src) src.recycle()
                return scaleBitmap
            }
            val ret = Bitmap.createScaledBitmap(scaleBitmap, width, height, true)
            if (!scaleBitmap.isRecycled) scaleBitmap.recycle()
            if (recycle && !src.isRecycled && ret != src) src.recycle()
            return ret
        }

        /**
         * Return the blur bitmap using render script.
         *
         * @param src    The source of bitmap.
         * @param radius The radius(0...25).
         * @return the blur bitmap
         */
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        fun renderScriptBlur(
            src: Bitmap,
            @FloatRange(from = 0.0, to = 25.0, fromInclusive = false)
            radius: Float
        ): Bitmap {
            return renderScriptBlur(src, radius, false)
        }

        /**
         * Return the blur bitmap using render script.
         *
         * @param src     The source of bitmap.
         * @param radius  The radius(0...25).
         * @param recycle True to recycle the source of bitmap, false otherwise.
         * @return the blur bitmap
         */
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        fun renderScriptBlur(
            src: Bitmap,
            @FloatRange(from = 0.0, to = 25.0, fromInclusive = false)
            radius: Float,
            recycle: Boolean
        ): Bitmap {
            var rs: RenderScript? = null
            val ret = if (recycle) src else src.copy(src.config, true)
            try {
                rs = RenderScript.create(AppUtils.getApp())
                rs!!.messageHandler = RenderScript.RSMessageHandler()
                val input = Allocation.createFromBitmap(
                    rs,
                    ret,
                    Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT
                )
                val output = Allocation.createTyped(rs, input.type)
                val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
                blurScript.setInput(input)
                blurScript.setRadius(radius)
                blurScript.forEach(output)
                output.copyTo(ret)
            } finally {
                rs?.destroy()
            }
            return ret
        }

        /**
         * Return the blur bitmap using stack.
         *
         * @param src    The source of bitmap.
         * @param radius The radius(0...25).
         * @return the blur bitmap
         */
        fun stackBlur(src: Bitmap, radius: Int): Bitmap {
            return stackBlur(src, radius, false)
        }

        /**
         * Return the blur bitmap using stack.
         *
         * @param src     The source of bitmap.
         * @param radius  The radius(0...25).
         * @param recycle True to recycle the source of bitmap, false otherwise.
         * @return the blur bitmap
         */
        fun stackBlur(src: Bitmap, radiusInt: Int, recycle: Boolean): Bitmap {
            var radius = radiusInt
            val ret = if (recycle) src else src.copy(src.config, true)
            if (radius < 1) {
                radius = 1
            }
            val w = ret.width
            val h = ret.height

            val pix = IntArray(w * h)
            ret.getPixels(pix, 0, w, 0, 0, w, h)

            val wm = w - 1
            val hm = h - 1
            val wh = w * h
            val div = radius + radius + 1

            val r = IntArray(wh)
            val g = IntArray(wh)
            val b = IntArray(wh)
            var rsum: Int
            var gsum: Int
            var bsum: Int
            var x: Int
            var y: Int
            var i: Int
            var p: Int
            var yp: Int
            var yi: Int
            var yw: Int
            val vmin = IntArray(Math.max(w, h))

            var divsum = div + 1 shr 1
            divsum *= divsum
            val dv = IntArray(256 * divsum)
            i = 0
            while (i < 256 * divsum) {
                dv[i] = i / divsum
                i++
            }

            yi = 0
            yw = yi

            val stack = Array(div) { IntArray(3) }
            var stackpointer: Int
            var stackstart: Int
            var sir: IntArray
            var rbs: Int
            val r1 = radius + 1
            var routsum: Int
            var goutsum: Int
            var boutsum: Int
            var rinsum: Int
            var ginsum: Int
            var binsum: Int

            y = 0
            while (y < h) {
                bsum = 0
                gsum = bsum
                rsum = gsum
                boutsum = rsum
                goutsum = boutsum
                routsum = goutsum
                binsum = routsum
                ginsum = binsum
                rinsum = ginsum
                i = -radius
                while (i <= radius) {
                    p = pix[yi + Math.min(wm, Math.max(i, 0))]
                    sir = stack[i + radius]
                    sir[0] = p and 0xff0000 shr 16
                    sir[1] = p and 0x00ff00 shr 8
                    sir[2] = p and 0x0000ff
                    rbs = r1 - Math.abs(i)
                    rsum += sir[0] * rbs
                    gsum += sir[1] * rbs
                    bsum += sir[2] * rbs
                    if (i > 0) {
                        rinsum += sir[0]
                        ginsum += sir[1]
                        binsum += sir[2]
                    } else {
                        routsum += sir[0]
                        goutsum += sir[1]
                        boutsum += sir[2]
                    }
                    i++
                }
                stackpointer = radius

                x = 0
                while (x < w) {

                    r[yi] = dv[rsum]
                    g[yi] = dv[gsum]
                    b[yi] = dv[bsum]

                    rsum -= routsum
                    gsum -= goutsum
                    bsum -= boutsum

                    stackstart = stackpointer - radius + div
                    sir = stack[stackstart % div]

                    routsum -= sir[0]
                    goutsum -= sir[1]
                    boutsum -= sir[2]

                    if (y == 0) {
                        vmin[x] = Math.min(x + radius + 1, wm)
                    }
                    p = pix[yw + vmin[x]]

                    sir[0] = p and 0xff0000 shr 16
                    sir[1] = p and 0x00ff00 shr 8
                    sir[2] = p and 0x0000ff

                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]

                    rsum += rinsum
                    gsum += ginsum
                    bsum += binsum

                    stackpointer = (stackpointer + 1) % div
                    sir = stack[stackpointer % div]

                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]

                    rinsum -= sir[0]
                    ginsum -= sir[1]
                    binsum -= sir[2]

                    yi++
                    x++
                }
                yw += w
                y++
            }
            x = 0
            while (x < w) {
                bsum = 0
                gsum = bsum
                rsum = gsum
                boutsum = rsum
                goutsum = boutsum
                routsum = goutsum
                binsum = routsum
                ginsum = binsum
                rinsum = ginsum
                yp = -radius * w
                i = -radius
                while (i <= radius) {
                    yi = Math.max(0, yp) + x

                    sir = stack[i + radius]

                    sir[0] = r[yi]
                    sir[1] = g[yi]
                    sir[2] = b[yi]

                    rbs = r1 - Math.abs(i)

                    rsum += r[yi] * rbs
                    gsum += g[yi] * rbs
                    bsum += b[yi] * rbs

                    if (i > 0) {
                        rinsum += sir[0]
                        ginsum += sir[1]
                        binsum += sir[2]
                    } else {
                        routsum += sir[0]
                        goutsum += sir[1]
                        boutsum += sir[2]
                    }

                    if (i < hm) {
                        yp += w
                    }
                    i++
                }
                yi = x
                stackpointer = radius
                y = 0
                while (y < h) {
                    // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                    pix[yi] =
                        -0x1000000 and pix[yi] or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]

                    rsum -= routsum
                    gsum -= goutsum
                    bsum -= boutsum

                    stackstart = stackpointer - radius + div
                    sir = stack[stackstart % div]

                    routsum -= sir[0]
                    goutsum -= sir[1]
                    boutsum -= sir[2]

                    if (x == 0) {
                        vmin[y] = Math.min(y + r1, hm) * w
                    }
                    p = x + vmin[y]

                    sir[0] = r[p]
                    sir[1] = g[p]
                    sir[2] = b[p]

                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]

                    rsum += rinsum
                    gsum += ginsum
                    bsum += binsum

                    stackpointer = (stackpointer + 1) % div
                    sir = stack[stackpointer]

                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]

                    rinsum -= sir[0]
                    ginsum -= sir[1]
                    binsum -= sir[2]

                    yi += w
                    y++
                }
                x++
            }
            ret.setPixels(pix, 0, w, 0, 0, w, h)
            return ret
        }

        /**
         * Save the bitmap.
         *
         * @param src      The source of bitmap.
         * @param filePath The path of file.
         * @param format   The format of the image.
         * @return `true`: success<br></br>`false`: fail
         */
        fun save(
            src: Bitmap,
            filePath: String,
            format: CompressFormat
        ): Boolean {
            return save(src, getFileByPath(filePath), format, false)
        }

        /**
         * Save the bitmap.
         *
         * @param src    The source of bitmap.
         * @param file   The file.
         * @param format The format of the image.
         * @return `true`: success<br></br>`false`: fail
         */
        fun save(src: Bitmap, file: File, format: CompressFormat): Boolean {
            return save(src, file, format, false)
        }

        /**
         * Save the bitmap.
         *
         * @param src      The source of bitmap.
         * @param filePath The path of file.
         * @param format   The format of the image.
         * @param recycle  True to recycle the source of bitmap, false otherwise.
         * @return `true`: success<br></br>`false`: fail
         */
        fun save(
            src: Bitmap,
            filePath: String,
            format: CompressFormat,
            recycle: Boolean
        ): Boolean {
            return save(src, getFileByPath(filePath), format, recycle)
        }

        /**
         * Save the bitmap.
         *
         * @param src     The source of bitmap.
         * @param file    The file.
         * @param format  The format of the image.
         * @param recycle True to recycle the source of bitmap, false otherwise.
         * @return `true`: success<br></br>`false`: fail
         */
        fun save(
            src: Bitmap,
            file: File?,
            format: CompressFormat,
            recycle: Boolean
        ): Boolean {
            if (isEmptyBitmap(src) || !createFileByDeleteOldFile(file)) return false
            var os: OutputStream? = null
            var ret = false
            try {
                os = BufferedOutputStream(FileOutputStream(file!!))
                ret = src.compress(format, 100, os)
                if (recycle && !src.isRecycled) src.recycle()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    os?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            return ret
        }

        /**
         * Return whether it is a image according to the file name.
         *
         * @param file The file.
         * @return `true`: yes<br></br>`false`: no
         */
        fun isImage(file: File?): Boolean {
            return if (file == null || !file.exists()) {
                false
            } else isImage(file.path)
        }

        /**
         * Return whether it is a image according to the file name.
         *
         * @param filePath The path of file.
         * @return `true`: yes<br></br>`false`: no
         */
        fun isImage(filePath: String): Boolean {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            return try {
                BitmapFactory.decodeFile(filePath, options)
                options.outWidth != -1 && options.outHeight != -1
            } catch (e: Exception) {
                false
            }
        }

//        /**
//         * Return the type of image.
//         *
//         * @param filePath The path of file.
//         * @return the type of image
//         */
//        fun getImageType(filePath: String): ImageType? {
//            return getImageType(getFileByPath(filePath))
//        }
//
//        /**
//         * Return the type of image.
//         *
//         * @param file The file.
//         * @return the type of image
//         */
//        fun getImageType(file: File?): ImageType? {
//            if (file == null) return null
//            var `is`: InputStream? = null
//            try {
//                `is` = FileInputStream(file)
//                val type = getImageType(`is`)
//                if (type != null) {
//                    return type
//                }
//            } catch (e: IOException) {
//                e.printStackTrace()
//            } finally {
//                try {
//                    `is`?.close()
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//
//            }
//            return null
//        }
//
//        private fun getImageType(`is`: InputStream?): ImageType? {
//            if (`is` == null) return null
//            return try {
//                val bytes = ByteArray(12)
//                if (`is`!!.read(bytes) !== -1) getImageType(bytes) else null
//            } catch (e: IOException) {
//                e.printStackTrace()
//                null
//            }
//        }
//
//        private fun getImageType(bytes: ByteArray): ImageType {
//            val type = bytes2HexString(bytes).toUpperCase(Locale.US)
//            return if (type.contains("FFD8FF")) {
//                ImageType.TYPE_JPG
//            } else if (type.contains("89504E47")) {
//                ImageType.TYPE_PNG
//            } else if (type.contains("47494638")) {
//                ImageType.TYPE_GIF
//            } else if (type.contains("49492A00") || type.contains("4D4D002A")) {
//                ImageType.TYPE_TIFF
//            } else if (type.contains("424D")) {
//                ImageType.TYPE_BMP
//            } else if (type.startsWith("52494646") && type.endsWith("57454250")) {//524946461c57000057454250-12个字节
//                ImageType.TYPE_WEBP
//            } else if (type.contains("00000100") || type.contains("00000200")) {
//                ImageType.TYPE_ICO
//            } else {
//                ImageType.TYPE_UNKNOWN
//            }
//        }
//
//        private val hexDigits = charArrayOf(
//            '0',
//            '1',
//            '2',
//            '3',
//            '4',
//            '5',
//            '6',
//            '7',
//            '8',
//            '9',
//            'A',
//            'B',
//            'C',
//            'D',
//            'E',
//            'F'
//        )
//
//        private fun bytes2HexString(bytes: ByteArray?): String {
//            if (bytes == null) return ""
//            val len = bytes.size
//            if (len <= 0) return ""
//            val ret = CharArray(len shl 1)
//            var i = 0
//            var j = 0
//            while (i < len) {
//                ret[j++] = hexDigits[bytes[i] shr 4 and 0x0f]
//                ret[j++] = hexDigits[bytes[i] and 0x0f]
//                i++
//            }
//            return String(ret)
//        }


        private fun isJPEG(b: ByteArray): Boolean {
            return (b.size >= 2
                    && b[0] == 0xFF.toByte() && b[1] == 0xD8.toByte())
        }

        private fun isGIF(b: ByteArray): Boolean {
            return (b.size >= 6
                    && b[0] == 'G'.toByte() && b[1] == 'I'.toByte()
                    && b[2] == 'F'.toByte() && b[3] == '8'.toByte()
                    && (b[4] == '7'.toByte() || b[4] == '9'.toByte()) && b[5] == 'a'.toByte())
        }

        private fun isPNG(b: ByteArray): Boolean {
            return b.size >= 8 && (b[0] == 137.toByte() && b[1] == 80.toByte()
                    && b[2] == 78.toByte() && b[3] == 71.toByte()
                    && b[4] == 13.toByte() && b[5] == 10.toByte()
                    && b[6] == 26.toByte() && b[7] == 10.toByte())
        }

        private fun isBMP(b: ByteArray): Boolean {
            return (b.size >= 2
                    && b[0].toInt() == 0x42 && b[1].toInt() == 0x4d)
        }

        private fun isEmptyBitmap(src: Bitmap?): Boolean {
            return src == null || src.width == 0 || src.height == 0
        }

        ///////////////////////////////////////////////////////////////////////////
        // about compress
        ///////////////////////////////////////////////////////////////////////////

        /**
         * Return the compressed bitmap using scale.
         *
         * @param src       The source of bitmap.
         * @param newWidth  The new width.
         * @param newHeight The new height.
         * @return the compressed bitmap
         */
        fun compressByScale(
            src: Bitmap,
            newWidth: Int,
            newHeight: Int
        ): Bitmap? {
            return scale(src, newWidth, newHeight, false)
        }

        /**
         * Return the compressed bitmap using scale.
         *
         * @param src       The source of bitmap.
         * @param newWidth  The new width.
         * @param newHeight The new height.
         * @param recycle   True to recycle the source of bitmap, false otherwise.
         * @return the compressed bitmap
         */
        fun compressByScale(
            src: Bitmap,
            newWidth: Int,
            newHeight: Int,
            recycle: Boolean
        ): Bitmap? {
            return scale(src, newWidth, newHeight, recycle)
        }

        /**
         * Return the compressed bitmap using scale.
         *
         * @param src         The source of bitmap.
         * @param scaleWidth  The scale of width.
         * @param scaleHeight The scale of height.
         * @return the compressed bitmap
         */
        fun compressByScale(
            src: Bitmap,
            scaleWidth: Float,
            scaleHeight: Float
        ): Bitmap? {
            return scale(src, scaleWidth, scaleHeight, false)
        }

        /**
         * Return the compressed bitmap using scale.
         *
         * @param src         The source of bitmap.
         * @param scaleWidth  The scale of width.
         * @param scaleHeight The scale of height.
         * @param recycle     True to recycle the source of bitmap, false otherwise.
         * @return he compressed bitmap
         */
        fun compressByScale(
            src: Bitmap,
            scaleWidth: Float,
            scaleHeight: Float,
            recycle: Boolean
        ): Bitmap? {
            return scale(src, scaleWidth, scaleHeight, recycle)
        }

        /**
         * Return the compressed bitmap using quality.
         *
         * @param src     The source of bitmap.
         * @param quality The quality.
         * @return the compressed bitmap
         */
        fun compressByQuality(
            src: Bitmap,
            quality: Int
        ): Bitmap? {
            if (quality < 0 || quality > 100) throw UnsupportedOperationException("Please enter a valid value:[0,100]")
            return compressByQuality(src, quality, false)
        }

        /**
         * Return the compressed bitmap using quality.
         *
         * @param src     The source of bitmap.
         * @param quality The quality.
         * @param recycle True to recycle the source of bitmap, false otherwise.
         * @return the compressed bitmap
         */
        fun compressByQuality(
            src: Bitmap,
            quality: Int,
            recycle: Boolean
        ): Bitmap? {
            if (isEmptyBitmap(src)) return null
            if (quality < 0 || quality > 100) throw UnsupportedOperationException("Please enter a valid value:[0,100]")
            val baos = ByteArrayOutputStream()
            src.compress(Bitmap.CompressFormat.JPEG, quality, baos)
            val bytes = baos.toByteArray()
            if (recycle && !src.isRecycled) src.recycle()
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }

        /**
         * Return the compressed bitmap using quality.
         *
         * @param src         The source of bitmap.
         * @param maxByteSize The maximum size of byte.
         * @return the compressed bitmap
         */
        fun compressByQuality(src: Bitmap, maxByteSize: Long): Bitmap? {
            return compressByQuality(src, maxByteSize, false)
        }

        /**
         * Return the compressed bitmap using quality.
         *
         * @param src         The source of bitmap.
         * @param maxByteSize The maximum size of byte.
         * @param recycle     True to recycle the source of bitmap, false otherwise.
         * @return the compressed bitmap
         */
        fun compressByQuality(
            src: Bitmap,
            maxByteSize: Long,
            recycle: Boolean
        ): Bitmap? {
            if (isEmptyBitmap(src) || maxByteSize <= 0) return null
            val baos = ByteArrayOutputStream()
            src.compress(CompressFormat.JPEG, 100, baos)
            val bytes: ByteArray
            if (baos.size() <= maxByteSize) {
                bytes = baos.toByteArray()
            } else {
                baos.reset()
                src.compress(CompressFormat.JPEG, 0, baos)
                if (baos.size() >= maxByteSize) {
                    bytes = baos.toByteArray()
                } else {
                    // find the best quality using binary search
                    var st = 0
                    var end = 100
                    var mid = 0
                    while (st < end) {
                        mid = (st + end) / 2
                        baos.reset()
                        src.compress(CompressFormat.JPEG, mid, baos)
                        val len = baos.size()
                        if (len.toLong() == maxByteSize) {
                            break
                        } else if (len > maxByteSize) {
                            end = mid - 1
                        } else {
                            st = mid + 1
                        }
                    }
                    if (end == mid - 1) {
                        baos.reset()
                        src.compress(CompressFormat.JPEG, st, baos)
                    }
                    bytes = baos.toByteArray()
                }
            }
            if (recycle && !src.isRecycled) src.recycle()
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }

        /**
         * Return the compressed bitmap using sample size.
         *
         * @param src        The source of bitmap.
         * @param sampleSize The sample size.
         * @return the compressed bitmap
         */

        fun compressBySampleSize(src: Bitmap, sampleSize: Int): Bitmap? {
            return compressBySampleSize(src, sampleSize, false)
        }

        /**
         * Return the compressed bitmap using sample size.
         *
         * @param src        The source of bitmap.
         * @param sampleSize The sample size.
         * @param recycle    True to recycle the source of bitmap, false otherwise.
         * @return the compressed bitmap
         */
        fun compressBySampleSize(
            src: Bitmap,
            sampleSize: Int,
            recycle: Boolean
        ): Bitmap? {
            if (isEmptyBitmap(src)) return null
            val options = BitmapFactory.Options()
            options.inSampleSize = sampleSize
            val baos = ByteArrayOutputStream()
            src.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val bytes = baos.toByteArray()
            if (recycle && !src.isRecycled) src.recycle()
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
        }

        /**
         * Return the compressed bitmap using sample size.
         *
         * @param src       The source of bitmap.
         * @param maxWidth  The maximum width.
         * @param maxHeight The maximum height.
         * @return the compressed bitmap
         */
        fun compressBySampleSize(
            src: Bitmap,
            maxWidth: Int,
            maxHeight: Int
        ): Bitmap? {
            return compressBySampleSize(src, maxWidth, maxHeight, false)
        }

        /**
         * Return the compressed bitmap using sample size.
         *
         * @param src       The source of bitmap.
         * @param maxWidth  The maximum width.
         * @param maxHeight The maximum height.
         * @param recycle   True to recycle the source of bitmap, false otherwise.
         * @return the compressed bitmap
         */
        fun compressBySampleSize(
            src: Bitmap,
            maxWidth: Int,
            maxHeight: Int,
            recycle: Boolean
        ): Bitmap? {
            if (isEmptyBitmap(src)) return null
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            val baos = ByteArrayOutputStream()
            src.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val bytes = baos.toByteArray()
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
            options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
            options.inJustDecodeBounds = false
            if (recycle && !src.isRecycled) src.recycle()
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
        }

        /**
         * Return the size of bitmap.
         *
         * @param filePath The path of file.
         * @return the size of bitmap
         */
        fun getSize(filePath: String): IntArray {
            return getSize(getFileByPath(filePath))
        }

        /**
         * Return the size of bitmap.
         *
         * @param file The file.
         * @return the size of bitmap
         */
        fun getSize(file: File?): IntArray {
            if (file == null) return intArrayOf(0, 0)
            val opts = BitmapFactory.Options()
            opts.inJustDecodeBounds = true
            BitmapFactory.decodeFile(file.absolutePath, opts)
            return intArrayOf(opts.outWidth, opts.outHeight)
        }

        /**
         * Return the sample size.
         *
         * @param options   The options.
         * @param maxWidth  The maximum width.
         * @param maxHeight The maximum height.
         * @return the sample size
         */
        private fun calculateInSampleSize(
            options: BitmapFactory.Options,
            maxWidth: Int,
            maxHeight: Int
        ): Int {
            var height = options.outHeight
            var width = options.outWidth
            var inSampleSize = 1
            while (height > maxHeight || width > maxWidth) {
                height = height shr 1
                width = width shr 1
                inSampleSize = inSampleSize shl 1
            }
            return inSampleSize
        }

        ///////////////////////////////////////////////////////////////////////////
        // other utils methods
        ///////////////////////////////////////////////////////////////////////////

        private fun getFileByPath(filePath: String): File? {
            return if (isSpace(filePath)) null else File(filePath)
        }

        private fun createFileByDeleteOldFile(file: File?): Boolean {
            if (file == null) return false
            if (file.exists() && !file.delete()) return false
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

        private fun input2Byte(`is`: InputStream?): ByteArray? {
            if (`is` == null) return null
            try {
                val os = ByteArrayOutputStream()
                val b = ByteArray(1024)
                var len: Int
//                while ((len = `is`!!.read(b, 0, 1024)) != -1) {
//                    os.write(b, 0, len)
//                }
                while (true) {
                    len = `is`.read(b, 0, 1024)
                    if (len == -1) {
                        break
                    }
                    os.write(b, 0, len)
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

            }
        }

        enum class ImageType private constructor(value: String) {
            TYPE_JPG("jpg"),

            TYPE_PNG("png"),

            TYPE_GIF("gif"),

            TYPE_TIFF("tiff"),

            TYPE_BMP("bmp"),

            TYPE_WEBP("webp"),

            TYPE_ICO("ico"),

            TYPE_UNKNOWN("unknown");

            var value: String
                internal set

            init {
                this.value = value
            }
        }
    }
}


fun ImageView.loadImage(uri: String?) {
    Glide.with(this).load(uri).into(this)
}

fun ImageView.loadImage(uri: Uri?) {
    Glide.with(this).load(uri).into(this)
}

fun ImageView.loadImage(uri: String?, @DrawableRes holder: Int) {
    Glide.with(this).load(uri).apply(RequestOptions.placeholderOf(holder)).into(this)
}

fun ImageView.loadImage(uri: String?, requestListener: RequestListener<Drawable?>) {
    Glide.with(this).load(uri).listener(requestListener).into(this)
}

fun ImageView.loadImage(uri: String?, width: Int, height: Int) {
    val multi = MultiTransformation(CropTransformation(width, height))
    Glide.with(this).load(uri).apply(RequestOptions.bitmapTransform(multi).dontAnimate()).into(this)
}

fun ImageView.loadImageCenterCrop(uri: String?, @DrawableRes holder: Int? = null) {
    Glide.with(this).load(uri).apply(RequestOptions().dontAnimate().dontTransform().centerCrop().apply {
        if (holder != null) {
            this.placeholder(holder)
        }
    }).into(this)
}

fun ImageView.loadGif(uri: String?, requestListener: RequestListener<GifDrawable?>? = null, centerCrop: Boolean? = null, @DrawableRes holder: Int? = null) {
    var requestOptions = RequestOptions().dontTransform()
    if (centerCrop != null) {
        requestOptions = requestOptions.centerCrop()
    }
    if (holder != null) {
        requestOptions = requestOptions.placeholder(holder)
    }
    if (requestListener != null) {
        Glide.with(this).asGif().load(uri).apply(requestOptions).listener(requestListener).into(this)
    } else {
        Glide.with(this).asGif().load(uri).apply(requestOptions).into(this)
    }
}
//
//fun ImageView.loadGifMark(uri: String?, holder: String?, mark: Int) {
//    Glide.with(this).asGif().load(uri).apply(RequestOptions().dontTransform().signature(StringSignature("$uri$mark")).apply {
//        if (holder != null) {
//            this.placeholder(holder.toDrawable())
//        }
//    }).into(this)
//}
//
//fun ImageView.loadGifMark(uri: String?, mark: Int) {
//    Glide.with(this).asGif().load(uri).apply(RequestOptions().dontTransform().signature(StringSignature("$uri$mark")))
//        .listener(object : RequestListener<GifDrawable> {
//            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<GifDrawable>?, isFirstResource: Boolean): Boolean {
//                return true
//            }
//
//            override fun onResourceReady(resource: GifDrawable?, model: Any?, target: Target<GifDrawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
//                setImageDrawable(resource)
//                return true
//            }
//        })
//        .submit(layoutParams.width, layoutParams.height)
//}
//
//fun ImageView.loadImageMark(uri: String?, holder: String?, mark: Int) {
//    Glide.with(this).load(uri).apply(RequestOptions().dontAnimate()
//        .signature(StringSignature("$uri$mark")).apply {
//            if (holder != null) {
//                this.placeholder(holder.toDrawable())
//            }
//        }).into(this)
//}
//
//fun ImageView.loadImageMark(uri: String?, mark: Int) {
//    Glide.with(this).load(uri).apply(RequestOptions().dontAnimate()
//        .signature(StringSignature("$uri$mark")))
//        .listener(object : RequestListener<Drawable> {
//            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
//                return true
//            }
//
//            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
//                setImageDrawable(resource)
//                return true
//            }
//        })
//        .submit(layoutParams.width, layoutParams.height)
//}
//
//fun ImageView.loadLongImageMark(uri: String?, holder: String?, mark: Int) {
//    Glide.with(this).load(uri).apply(
//        RequestOptions.bitmapTransform(CropTransformation(0, layoutParams.height, CropTransformation.CropType.TOP))
//        .dontAnimate()
//        .signature(StringSignature("$uri$mark")).apply {
//            if (holder != null) {
//                this.placeholder(holder.toDrawable())
//            }
//        }).into(this)
//}
//
//fun ImageView.loadLongImageMark(uri: String?, mark: Int) {
//    Glide.with(this).load(uri).apply(RequestOptions.bitmapTransform(CropTransformation(0, layoutParams.height, CropTransformation.CropType.TOP))
//        .dontAnimate()
//        .signature(StringSignature("$uri$mark"))).listener(object : RequestListener<Drawable> {
//        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
//            return true
//        }
//
//        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
//            setImageDrawable(resource)
//            return true
//        }
//    }).submit(layoutParams.width, layoutParams.height)
//}
//
//fun ImageView.loadVideoMark(uri: String?, holder: String?, mark: Int) {
//    Glide.with(this).load(uri).apply(RequestOptions().frame(0)
//        .signature(StringSignature("$uri$mark"))
//        .centerCrop().dontAnimate().apply {
//            if (holder != null) {
//                this.placeholder(holder.toDrawable())
//            }
//        }
//    ).into(this)
//}
//
//fun ImageView.loadVideoMark(uri: String?, mark: Int) {
//    Glide.with(this).load(uri).apply(RequestOptions().frame(0)
//        .signature(StringSignature("$uri$mark"))
//        .centerCrop().dontAnimate()).listener(object : RequestListener<Drawable> {
//        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
//            return true
//        }
//
//        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
//            setImageDrawable(resource)
//            return true
//        }
//    }).submit(layoutParams.width, layoutParams.height)
//}

fun ImageView.loadVideo(uri: String, @DrawableRes holder: Int) {
    Glide.with(this).load(uri).apply(RequestOptions().frame(0)
        .centerCrop().placeholder(holder).dontAnimate()).into(this)
}

fun ImageView.loadSticker(uri: String?, type: String?) {
    uri?.let {
        when (type) {
            "GIF" -> {
                loadGif(uri)
            }
            else -> loadImage(uri)
        }
    }
}

//fun ImageView.loadBase64(uri: ByteArray?, width: Int, height: Int, mark: Int) {
//    val multi = MultiTransformation(CropTransformation(width, height))
//    Glide.with(this).load(uri)
//        .apply(RequestOptions().centerCrop()
//            .transform(multi).signature(StringSignature("$uri$mark"))
//            .dontAnimate()).into(this)
//}

fun ImageView.loadCircleImage(uri: String?, @DrawableRes holder: Int? = null) {
    if (uri.isNullOrBlank()) {
        if (holder != null) {
            setImageResource(holder)
        }
    } else if (holder == null) {
        Glide.with(this).load(uri).apply(RequestOptions().circleCrop()).into(this)
    } else {
        Glide.with(this).load(uri).apply(RequestOptions().placeholder(holder).circleCrop()).into(this)
    }
}

fun ImageView.loadRoundImage(uri: String?, radius: Int, @DrawableRes holder: Int? = null) {
    if (uri.isNullOrBlank() && holder != null) {
        setImageResource(holder)
    } else if (holder == null) {
        Glide.with(this).load(uri).apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(radius, 0))).into(this)
    } else {
        Glide.with(this).load(uri).apply(RequestOptions().transform(RoundedCornersTransformation(radius, 0))
            .placeholder(holder))
            .into(this)
    }
}