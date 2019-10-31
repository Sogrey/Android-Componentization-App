package top.sogrey.common.utils

import android.annotation.SuppressLint
import android.graphics.*
import android.graphics.BlurMaskFilter.Blur
import android.graphics.Paint.FontMetricsInt
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.*
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.core.content.ContextCompat
import java.lang.ref.WeakReference


/**
 *
SpannableString 相关

with : 设置控件
setFlag : 设置标识
setForegroundColor: 设置前景色
setBackgroundColor: 设置背景色
setLineHeight : 设置行高
setQuoteColor : 设置引用线的颜色
setLeadingMargin : 设置缩进
setBullet : 设置列表标记
setFontSize : 设置字体尺寸
setFontProportion : 设置字体比例
setFontXProportion: 设置字体横向比例
setStrikethrough : 设置删除线
setUnderline : 设置下划线
setSuperscript : 设置上标
setSubscript : 设置下标
setBold : 设置粗体
setItalic : 设置斜体
setBoldItalic : 设置粗斜体
setFontFamily : 设置字体系列
setTypeface : 设置字体
setAlign : 设置对齐
setClickSpan : 设置点击事件
setUrl : 设置超链接
setBlur : 设置模糊
setShader : 设置着色器
setShadow : 设置阴影
setSpans : 设置样式
append : 追加样式字符串
appendLine : 追加一行样式字符串
appendImage : 追加图片
appendSpace : 追加空白
create : 创建样式字符串
 * <p/>
 * @author Sogrey
 * @date 2019-10-31 11:41
 */
class SpannableStringUtils {
    constructor() {
        throw UnsupportedOperationException(this.javaClass.simpleName + " cannot be instantiated")
    }

    companion object {
        private const val COLOR_DEFAULT = -0x1000001

        const val ALIGN_BOTTOM = 0
        const val ALIGN_BASELINE = 1
        const val ALIGN_CENTER = 2
        const val ALIGN_TOP = 3

        //        @IntDef(ALIGN_BOTTOM, ALIGN_BASELINE, ALIGN_CENTER, ALIGN_TOP)
//        @Retention(RetentionPolicy.SOURCE)
        annotation class Align

        private val LINE_SEPARATOR = System.getProperty("line.separator")

        @SuppressLint("StaticFieldLeak")
        private var mTextView: TextView? = null
        private var mText: CharSequence? = null
        private var flag: Int = 0
        private var foregroundColor: Int = 0
        private var backgroundColor: Int = 0
        private var lineHeight: Int = 0
        private var alignLine: Int = 0
        private var quoteColor: Int = 0
        private var stripeWidth: Int = 0
        private var quoteGapWidth: Int = 0
        private var first: Int = 0
        private var rest: Int = 0
        private var bulletColor: Int = 0
        private var bulletRadius: Int = 0
        private var bulletGapWidth: Int = 0
        private var fontSize: Int = 0
        private var fontSizeIsDp: Boolean = false
        private var proportion: Float = 0.toFloat()
        private var xProportion: Float = 0.toFloat()
        private var isStrikethrough: Boolean = false
        private var isUnderline: Boolean = false
        private var isSuperscript: Boolean = false
        private var isSubscript: Boolean = false
        private var isBold: Boolean = false
        private var isItalic: Boolean = false
        private var isBoldItalic: Boolean = false
        private var fontFamily: String? = null
        private var typeface: Typeface? = null
        private var alignment: Layout.Alignment? = null
        private var verticalAlign: Int = 0
        private var clickSpan: ClickableSpan? = null
        private var url: String? = null
        private var blurRadius: Float = 0.toFloat()
        private var style: Blur? = null
        private var shader: Shader? = null
        private var shadowRadius: Float = 0.toFloat()
        private var shadowDx: Float = 0.toFloat()
        private var shadowDy: Float = 0.toFloat()
        private var shadowColor: Int = 0
        private var spans: Array<Any>? = null

        private var imageBitmap: Bitmap? = null
        private var imageDrawable: Drawable? = null
        private var imageUri: Uri? = null
        private var imageResourceId: Int = 0
        private var alignImage: Int = 0

        private var spaceSize: Int = 0
        private var spaceColor: Int = 0

        private var mBuilder: SerializableSpannableStringBuilder

        private var mType: Int = 0
        private const val mTypeCharSequence = 0
        private const val mTypeImage = 1
        private const val mTypeSpace = 2

//        private fun SpannableStringUtils(textView: TextView):SpannableStringUtils{
//            this()
//            mTextView = textView
//        }

//        fun SpannableStringUtils(): ??? {
//            mBuilder = SerializableSpannableStringBuilder()
//            mText = ""
//            mType = -1
//            setDefault()
//        }

        init {
            mBuilder = SerializableSpannableStringBuilder()
            mText = ""
            mType = -1
            setDefault()
        }

        fun withTextView(textView: TextView): Companion {
            mTextView = textView
            return this
        }

        private fun setDefault() {
            flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            foregroundColor = COLOR_DEFAULT
            backgroundColor = COLOR_DEFAULT
            lineHeight = -1
            quoteColor = COLOR_DEFAULT
            first = -1
            bulletColor = COLOR_DEFAULT
            fontSize = -1
            proportion = -1f
            xProportion = -1f
            isStrikethrough = false
            isUnderline = false
            isSuperscript = false
            isSubscript = false
            isBold = false
            isItalic = false
            isBoldItalic = false
            fontFamily = null
            typeface = null
            alignment = null
            verticalAlign = -1
            clickSpan = null
            url = null
            blurRadius = -1f
            shader = null
            shadowRadius = -1f
            spans = null

            imageBitmap = null
            imageDrawable = null
            imageUri = null
            imageResourceId = -1

            spaceSize = -1
        }

        /**
         * Set the span of flag.
         *
         * @param flag The flag.
         *
         *  * [Spanned.SPAN_INCLUSIVE_EXCLUSIVE]
         *  * [Spanned.SPAN_INCLUSIVE_INCLUSIVE]
         *  * [Spanned.SPAN_EXCLUSIVE_EXCLUSIVE]
         *  * [Spanned.SPAN_EXCLUSIVE_INCLUSIVE]
         *
         * @return the single [SpannableStringUtils] instance
         */
        fun setFlag(flag: Int): Companion {
            this.flag = flag
            return this
        }

        /**
         * Set the span of foreground's color.
         *
         * @param color The color of foreground
         * @return the single [SpannableStringUtils] instance
         */
        fun setForegroundColor(@ColorInt color: Int): Companion {
            this.foregroundColor = color
            return this
        }

        /**
         * Set the span of background's color.
         *
         * @param color The color of background
         * @return the single [SpannableStringUtils] instance
         */
        fun setBackgroundColor(@ColorInt color: Int): Companion {
            this.backgroundColor = color
            return this
        }

        /**
         * Set the span of line height.
         *
         * @param lineHeight The line height, in pixel.
         * @return the single [SpannableStringUtils] instance
         */
        fun setLineHeight(lineHeight: Int): Companion {
            return setLineHeight(lineHeight, ALIGN_CENTER)
        }

        /**
         * Set the span of line height.
         *
         * @param lineHeight The line height, in pixel.
         * @param align      The alignment.
         *
         *  * [Align.ALIGN_TOP]
         *  * [Align.ALIGN_CENTER]
         *  * [Align.ALIGN_BOTTOM]
         *
         * @return the single [SpannableStringUtils] instance
         */
        fun setLineHeight(
            lineHeight: Int,
            @Align align: Int
        ): Companion {
            this.lineHeight = lineHeight
            this.alignLine = align
            return this
        }

        /**
         * Set the span of quote's color.
         *
         * @param color The color of quote
         * @return the single [SpannableStringUtils] instance
         */
        fun setQuoteColor(@ColorInt color: Int): Companion {
            return setQuoteColor(color, 2, 2)
        }

        /**
         * Set the span of quote's color.
         *
         * @param color       The color of quote.
         * @param stripeWidth The width of stripe, in pixel.
         * @param gapWidth    The width of gap, in pixel.
         * @return the single [SpannableStringUtils] instance
         */
        fun setQuoteColor(
            @ColorInt color: Int,
            stripeWidth: Int,
            gapWidth: Int
        ): Companion {
            this.quoteColor = color
            this.stripeWidth = stripeWidth
            this.quoteGapWidth = gapWidth
            return this
        }

        /**
         * Set the span of leading margin.
         *
         * @param first The indent for the first line of the paragraph.
         * @param rest  The indent for the remaining lines of the paragraph.
         * @return the single [SpannableStringUtils] instance
         */
        fun setLeadingMargin(
            first: Int,
            rest: Int
        ): Companion {
            this.first = first
            this.rest = rest
            return this
        }

        /**
         * Set the span of bullet.
         *
         * @param gapWidth The width of gap, in pixel.
         * @return the single [SpannableStringUtils] instance
         */
        fun setBullet(gapWidth: Int): Companion {
            return setBullet(0, 3, gapWidth)
        }

        /**
         * Set the span of bullet.
         *
         * @param color    The color of bullet.
         * @param radius   The radius of bullet, in pixel.
         * @param gapWidth The width of gap, in pixel.
         * @return the single [SpannableStringUtils] instance
         */
        fun setBullet(
            @ColorInt color: Int,
            radius: Int,
            gapWidth: Int
        ): Companion {
            this.bulletColor = color
            this.bulletRadius = radius
            this.bulletGapWidth = gapWidth
            return this
        }

        /**
         * Set the span of font's size.
         *
         * @param size The size of font.
         * @return the single [SpannableStringUtils] instance
         */
        fun setFontSize(size: Int): Companion {
            return setFontSize(size, false)
        }

        /**
         * Set the span of size of font.
         *
         * @param size The size of font.
         * @param isSp True to use sp, false to use pixel.
         * @return the single [SpannableStringUtils] instance
         */
        fun setFontSize(size: Int, isSp: Boolean): Companion {
            this.fontSize = size
            this.fontSizeIsDp = isSp
            return this
        }

        /**
         * Set the span of proportion of font.
         *
         * @param proportion The proportion of font.
         * @return the single [SpannableStringUtils] instance
         */
        fun setFontProportion(proportion: Float): Companion {
            this.proportion = proportion
            return this
        }

        /**
         * Set the span of transverse proportion of font.
         *
         * @param proportion The transverse proportion of font.
         * @return the single [SpannableStringUtils] instance
         */
        fun setFontXProportion(proportion: Float): Companion {
            this.xProportion = proportion
            return this
        }

        /**
         * Set the span of strikethrough.
         *
         * @return the single [SpannableStringUtils] instance
         */
        fun setStrikethrough(): Companion {
            this.isStrikethrough = true
            return this
        }

        /**
         * Set the span of underline.
         *
         * @return the single [SpannableStringUtils] instance
         */
        fun setUnderline(): Companion {
            this.isUnderline = true
            return this
        }

        /**
         * Set the span of superscript.
         *
         * @return the single [SpannableStringUtils] instance
         */
        fun setSuperscript(): Companion {
            this.isSuperscript = true
            return this
        }

        /**
         * Set the span of subscript.
         *
         * @return the single [SpannableStringUtils] instance
         */
        fun setSubscript(): Companion {
            this.isSubscript = true
            return this
        }

        /**
         * Set the span of bold.
         *
         * @return the single [SpannableStringUtils] instance
         */
        fun setBold(): Companion {
            isBold = true
            return this
        }

        /**
         * Set the span of italic.
         *
         * @return the single [SpannableStringUtils] instance
         */
        fun setItalic(): Companion {
            isItalic = true
            return this
        }

        /**
         * Set the span of bold italic.
         *
         * @return the single [SpannableStringUtils] instance
         */
        fun setBoldItalic(): Companion {
            isBoldItalic = true
            return this
        }

        /**
         * Set the span of font family.
         *
         * @param fontFamily The font family.
         *
         *  * monospace
         *  * serif
         *  * sans-serif
         *
         * @return the single [SpannableStringUtils] instance
         */
        fun setFontFamily(fontFamily: String): Companion {
            this.fontFamily = fontFamily
            return this
        }

        /**
         * Set the span of typeface.
         *
         * @param typeface The typeface.
         * @return the single [SpannableStringUtils] instance
         */
        fun setTypeface(typeface: Typeface): Companion {
            this.typeface = typeface
            return this
        }

        /**
         * Set the span of horizontal alignment.
         *
         * @param alignment The alignment.
         *
         *  * [Alignment.ALIGN_NORMAL]
         *  * [Alignment.ALIGN_OPPOSITE]
         *  * [Alignment.ALIGN_CENTER]
         *
         * @return the single [SpannableStringUtils] instance
         */
        fun setHorizontalAlign(alignment: Layout.Alignment): Companion {
            this.alignment = alignment
            return this
        }

        /**
         * Set the span of vertical alignment.
         *
         * @param align The alignment.
         *
         *  * [Align.ALIGN_TOP]
         *  * [Align.ALIGN_CENTER]
         *  * [Align.ALIGN_BASELINE]
         *  * [Align.ALIGN_BOTTOM]
         *
         * @return the single [SpannableStringUtils] instance
         */
        fun setVerticalAlign(@Align align: Int): Companion {
            this.verticalAlign = align
            return this
        }

        /**
         * Set the span of click.
         *
         * Must set `view.setMovementMethod(LinkMovementMethod.getInstance())`
         *
         * @param clickSpan The span of click.
         * @return the single [SpannableStringUtils] instance
         */
        fun setClickSpan(clickSpan: ClickableSpan): Companion {
            if (mTextView != null && mTextView!!.movementMethod == null) {
                mTextView!!.movementMethod = LinkMovementMethod.getInstance()
            }
            this.clickSpan = clickSpan
            return this
        }

        /**
         * Set the span of url.
         *
         * Must set `view.setMovementMethod(LinkMovementMethod.getInstance())`
         *
         * @param url The url.
         * @return the single [SpannableStringUtils] instance
         */
        fun setUrl(url: String): Companion {
            if (mTextView != null && mTextView!!.movementMethod == null) {
                mTextView!!.movementMethod = LinkMovementMethod.getInstance()
            }
            this.url = url
            return this
        }

        /**
         * Set the span of blur.
         *
         * @param radius The radius of blur.
         * @param style  The style.
         *
         *  * [Blur.NORMAL]
         *  * [Blur.SOLID]
         *  * [Blur.OUTER]
         *  * [Blur.INNER]
         *
         * @return the single [SpannableStringUtils] instance
         */
        fun setBlur(
            @FloatRange(from = 0.0, fromInclusive = false) radius: Float,
            style: Blur
        ): Companion {
            this.blurRadius = radius
            this.style = style
            return this
        }

        /**
         * Set the span of shader.
         *
         * @param shader The shader.
         * @return the single [SpannableStringUtils] instance
         */
        fun setShader(shader: Shader): Companion {
            this.shader = shader
            return this
        }

        /**
         * Set the span of shadow.
         *
         * @param radius      The radius of shadow.
         * @param dx          X-axis offset, in pixel.
         * @param dy          Y-axis offset, in pixel.
         * @param shadowColor The color of shadow.
         * @return the single [SpannableStringUtils] instance
         */
        fun setShadow(
            @FloatRange(from = 0.0, fromInclusive = false) radius: Float,
            dx: Float,
            dy: Float,
            shadowColor: Int
        ): Companion {
            this.shadowRadius = radius
            this.shadowDx = dx
            this.shadowDy = dy
            this.shadowColor = shadowColor
            return this
        }


        /**
         * Set the spans.
         *
         * @param spans The spans.
         * @return the single [SpannableStringUtils] instance
         */
        fun setSpans(vararg spans: Any): Companion {
            if (spans.isNotEmpty()) {
                this.spans = arrayOf(spans)
            }
            return this
        }

        /**
         * Append the text text.
         *
         * @param text The text.
         * @return the single [SpannableStringUtils] instance
         */
        fun append(text: CharSequence): Companion {
            apply(mTypeCharSequence)
            mText = text
            return this
        }

        /**
         * Append one line.
         *
         * @return the single [SpannableStringUtils] instance
         */
        fun appendLine(): Companion {
            apply(mTypeCharSequence)
            mText = LINE_SEPARATOR
            return this
        }

        /**
         * Append text and one line.
         *
         * @return the single [SpannableStringUtils] instance
         */
        fun appendLine(text: CharSequence): Companion {
            apply(mTypeCharSequence)
            mText = text.toString() + LINE_SEPARATOR!!
            return this
        }

        /**
         * Append one image.
         *
         * @param bitmap The bitmap of image.
         * @return the single [SpannableStringUtils] instance
         */
        fun appendImage(bitmap: Bitmap): Companion {
            return appendImage(bitmap, ALIGN_BOTTOM)
        }

        /**
         * Append one image.
         *
         * @param bitmap The bitmap.
         * @param align  The alignment.
         *
         *  * [Align.ALIGN_TOP]
         *  * [Align.ALIGN_CENTER]
         *  * [Align.ALIGN_BASELINE]
         *  * [Align.ALIGN_BOTTOM]
         *
         * @return the single [SpannableStringUtils] instance
         */
        fun appendImage(bitmap: Bitmap, @Align align: Int): Companion {
            apply(mTypeImage)
            this.imageBitmap = bitmap
            this.alignImage = align
            return this
        }

        /**
         * Append one image.
         *
         * @param drawable The drawable of image.
         * @return the single [SpannableStringUtils] instance
         */
        fun appendImage(drawable: Drawable): Companion {
            return appendImage(drawable, ALIGN_BOTTOM)
        }

        /**
         * Append one image.
         *
         * @param drawable The drawable of image.
         * @param align    The alignment.
         *
         *  * [Align.ALIGN_TOP]
         *  * [Align.ALIGN_CENTER]
         *  * [Align.ALIGN_BASELINE]
         *  * [Align.ALIGN_BOTTOM]
         *
         * @return the single [SpannableStringUtils] instance
         */
        fun appendImage(drawable: Drawable, @Align align: Int): Companion {
            apply(mTypeImage)
            this.imageDrawable = drawable
            this.alignImage = align
            return this
        }

        /**
         * Append one image.
         *
         * @param uri The uri of image.
         * @return the single [SpannableStringUtils] instance
         */
        fun appendImage(uri: Uri): Companion {
            return appendImage(uri, ALIGN_BOTTOM)
        }

        /**
         * Append one image.
         *
         * @param uri   The uri of image.
         * @param align The alignment.
         *
         *  * [Align.ALIGN_TOP]
         *  * [Align.ALIGN_CENTER]
         *  * [Align.ALIGN_BASELINE]
         *  * [Align.ALIGN_BOTTOM]
         *
         * @return the single [SpannableStringUtils] instance
         */
        fun appendImage(uri: Uri, @Align align: Int): Companion {
            apply(mTypeImage)
            this.imageUri = uri
            this.alignImage = align
            return this
        }

        /**
         * Append one image.
         *
         * @param resourceId The resource id of image.
         * @return the single [SpannableStringUtils] instance
         */
        fun appendImage(@DrawableRes resourceId: Int): Companion {
            return appendImage(resourceId, ALIGN_BOTTOM)
        }

        /**
         * Append one image.
         *
         * @param resourceId The resource id of image.
         * @param align      The alignment.
         *
         *  * [Align.ALIGN_TOP]
         *  * [Align.ALIGN_CENTER]
         *  * [Align.ALIGN_BASELINE]
         *  * [Align.ALIGN_BOTTOM]
         *
         * @return the single [SpannableStringUtils] instance
         */
        fun appendImage(@DrawableRes resourceId: Int, @Align align: Int): Companion {
            apply(mTypeImage)
            this.imageResourceId = resourceId
            this.alignImage = align
            return this
        }

        /**
         * Append space.
         *
         * @param size The size of space.
         * @return the single [SpannableStringUtils] instance
         */
        fun appendSpace(size: Int): Companion {
            return appendSpace(size, Color.TRANSPARENT)
        }

        /**
         * Append space.
         *
         * @param size  The size of space.
         * @param color The color of space.
         * @return the single [SpannableStringUtils] instance
         */
        fun appendSpace(size: Int, @ColorInt color: Int): Companion {
            apply(mTypeSpace)
            spaceSize = size
            spaceColor = color
            return this
        }

        private fun apply(type: Int) {
            applyLast()
            mType = type
        }

        fun get(): SpannableStringBuilder {
            return mBuilder
        }

        /**
         * Create the span string.
         *
         * @return the span string
         */
        fun create(): SpannableStringBuilder {
            applyLast()
            if (mTextView != null) {
                mTextView!!.text = mBuilder
            }
            return mBuilder
        }

        private fun applyLast() {
            when (mType) {
                mTypeCharSequence -> updateCharCharSequence()
                mTypeImage -> updateImage()
                mTypeSpace -> updateSpace()
            }
            setDefault()
        }

        private fun updateCharCharSequence() {
            if (mText!!.isEmpty()) return
            var start = mBuilder.length
            if (start == 0 && lineHeight != -1) {// bug of LineHeightSpan when first line
                mBuilder.append(2.toChar().toString())
                    .append("\n")
                    .setSpan(AbsoluteSizeSpan(0), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                start = 2
            }
            mBuilder.append(mText)
            val end = mBuilder.length
            if (verticalAlign != -1) {
                mBuilder.setSpan(VerticalAlignSpan(verticalAlign), start, end, flag)
            }
            if (foregroundColor != COLOR_DEFAULT) {
                mBuilder.setSpan(ForegroundColorSpan(foregroundColor), start, end, flag)
            }
            if (backgroundColor != COLOR_DEFAULT) {
                mBuilder.setSpan(BackgroundColorSpan(backgroundColor), start, end, flag)
            }
            if (first != -1) {
                mBuilder.setSpan(LeadingMarginSpan.Standard(first, rest), start, end, flag)
            }
            if (quoteColor != COLOR_DEFAULT) {
                mBuilder.setSpan(
                    CustomQuoteSpan(quoteColor, stripeWidth, quoteGapWidth),
                    start,
                    end,
                    flag
                )
            }
            if (bulletColor != COLOR_DEFAULT) {
                mBuilder.setSpan(
                    CustomBulletSpan(bulletColor, bulletRadius, bulletGapWidth),
                    start,
                    end,
                    flag
                )
            }
            if (fontSize != -1) {
                mBuilder.setSpan(AbsoluteSizeSpan(fontSize, fontSizeIsDp), start, end, flag)
            }
            if (proportion != -1f) {
                mBuilder.setSpan(RelativeSizeSpan(proportion), start, end, flag)
            }
            if (xProportion != -1f) {
                mBuilder.setSpan(ScaleXSpan(xProportion), start, end, flag)
            }
            if (lineHeight != -1) {
                mBuilder.setSpan(CustomLineHeightSpan(lineHeight, alignLine), start, end, flag)
            }
            if (isStrikethrough) {
                mBuilder.setSpan(StrikethroughSpan(), start, end, flag)
            }
            if (isUnderline) {
                mBuilder.setSpan(UnderlineSpan(), start, end, flag)
            }
            if (isSuperscript) {
                mBuilder.setSpan(SuperscriptSpan(), start, end, flag)
            }
            if (isSubscript) {
                mBuilder.setSpan(SubscriptSpan(), start, end, flag)
            }
            if (isBold) {
                mBuilder.setSpan(StyleSpan(Typeface.BOLD), start, end, flag)
            }
            if (isItalic) {
                mBuilder.setSpan(StyleSpan(Typeface.ITALIC), start, end, flag)
            }
            if (isBoldItalic) {
                mBuilder.setSpan(StyleSpan(Typeface.BOLD_ITALIC), start, end, flag)
            }
            if (fontFamily != null) {
                mBuilder.setSpan(TypefaceSpan(fontFamily), start, end, flag)
            }
            if (typeface != null) {
                mBuilder.setSpan(CustomTypefaceSpan(typeface!!), start, end, flag)
            }
            if (alignment != null) {
                mBuilder.setSpan(AlignmentSpan.Standard(alignment!!), start, end, flag)
            }
            if (clickSpan != null) {
                mBuilder.setSpan(clickSpan, start, end, flag)
            }
            if (url != null) {
                mBuilder.setSpan(URLSpan(url), start, end, flag)
            }
            if (blurRadius != -1f) {
                mBuilder.setSpan(
                    MaskFilterSpan(BlurMaskFilter(blurRadius, style)),
                    start,
                    end,
                    flag
                )
            }
            if (shader != null) {
                mBuilder.setSpan(ShaderSpan(shader!!), start, end, flag)
            }
            if (shadowRadius != -1f) {
                mBuilder.setSpan(
                    ShadowSpan(shadowRadius, shadowDx, shadowDy, shadowColor),
                    start,
                    end,
                    flag
                )
            }
            if (spans != null) {
                for (span in spans!!) {
                    mBuilder.setSpan(span, start, end, flag)
                }
            }
        }

        private fun updateImage() {
            val start = mBuilder.length
            mText = "<img>"
            updateCharCharSequence()
            val end = mBuilder.length
            if (imageBitmap != null) {
                mBuilder.setSpan(CustomImageSpan(imageBitmap!!, alignImage), start, end, flag)
            } else if (imageDrawable != null) {
                mBuilder.setSpan(CustomImageSpan(imageDrawable!!, alignImage), start, end, flag)
            } else if (imageUri != null) {
                mBuilder.setSpan(CustomImageSpan(imageUri!!, alignImage), start, end, flag)
            } else if (imageResourceId != -1) {
                mBuilder.setSpan(CustomImageSpan(imageResourceId, alignImage), start, end, flag)
            }
        }

        private fun updateSpace() {
            val start = mBuilder.length
            mText = "< >"
            updateCharCharSequence()
            val end = mBuilder.length
            mBuilder.setSpan(SpaceSpan(spaceSize, spaceColor), start, end, flag)
        }

        internal class VerticalAlignSpan(val mVerticalAlignment: Int) : ReplacementSpan() {

            override fun getSize(
                paint: Paint,
                text: CharSequence?,
                start: Int,
                end: Int,
                p4: FontMetricsInt?
            ): Int {
                var text = text
                text = text!!.subSequence(start, end)
                return paint.measureText(text.toString()).toInt()
            }

            override fun draw(
                canvas: Canvas,
                text: CharSequence,
                start: Int,
                end: Int,
                x: Float,
                top: Int,
                y: Int,
                bottom: Int,
                paint: Paint
            ) {
                var text = text
                text = text.subSequence(start, end)
                val fm = paint.fontMetricsInt
                //            int need = height - (v + fm.descent - fm.ascent - spanstartv);
                //            if (need > 0) {
                //                if (mVerticalAlignment == ALIGN_TOP) {
                //                    fm.descent += need;
                //                } else if (mVerticalAlignment == ALIGN_CENTER) {
                //                    fm.descent += need / 2;
                //                    fm.ascent -= need / 2;
                //                } else {
                //                    fm.ascent -= need;
                //                }
                //            }
                //            need = height - (v + fm.bottom - fm.top - spanstartv);
                //            if (need > 0) {
                //                if (mVerticalAlignment == ALIGN_TOP) {
                //                    fm.bottom += need;
                //                } else if (mVerticalAlignment == ALIGN_CENTER) {
                //                    fm.bottom += need / 2;
                //                    fm.top -= need / 2;
                //                } else {
                //                    fm.top -= need;
                //                }
                //            }

                canvas.drawText(
                    text.toString(),
                    x,
                    (y - ((y + fm.descent + y + fm.ascent) / 2 - (bottom + top) / 2)).toFloat(),
                    paint
                )
            }

            companion object {

                val ALIGN_CENTER = 2
                val ALIGN_TOP = 3
            }
        }

        internal class CustomLineHeightSpan(
            private val height: Int,
            private val mVerticalAlignment: Int
        ) :
            LineHeightSpan {

            override fun chooseHeight(
                text: CharSequence, start: Int, end: Int,
                spanstartv: Int, v: Int, fm: Paint.FontMetricsInt
            ) {
                if (sfm == null) {
                    sfm = Paint.FontMetricsInt()
                    sfm!!.top = fm.top
                    sfm!!.ascent = fm.ascent
                    sfm!!.descent = fm.descent
                    sfm!!.bottom = fm.bottom
                    sfm!!.leading = fm.leading
                } else {
                    fm.top = sfm!!.top
                    fm.ascent = sfm!!.ascent
                    fm.descent = sfm!!.descent
                    fm.bottom = sfm!!.bottom
                    fm.leading = sfm!!.leading
                }
                var need = height - (v + fm.descent - fm.ascent - spanstartv)
                if (need > 0) {
                    when (mVerticalAlignment) {
                        ALIGN_TOP -> fm.descent += need
                        ALIGN_CENTER -> {
                            fm.descent += need / 2
                            fm.ascent -= need / 2
                        }
                        else -> fm.ascent -= need
                    }
                }
                need = height - (v + fm.bottom - fm.top - spanstartv)
                if (need > 0) {
                    when (mVerticalAlignment) {
                        ALIGN_TOP -> fm.bottom += need
                        ALIGN_CENTER -> {
                            fm.bottom += need / 2
                            fm.top -= need / 2
                        }
                        else -> fm.top -= need
                    }
                }
                if (end == (text as Spanned).getSpanEnd(this)) {
                    sfm = null
                }
            }

            companion object {

                const val ALIGN_CENTER = 2
                const val ALIGN_TOP = 3
                var sfm: Paint.FontMetricsInt? = null
            }
        }

        internal class SpaceSpan(
            private val width: Int,
            color: Int = Color.TRANSPARENT
        ) : ReplacementSpan() {
            private val paint = Paint()

            init {
                paint.color = color
                paint.style = Paint.Style.FILL
            }

            override fun getSize(
                paint: Paint,
                text: CharSequence?,
                start: Int,
                end: Int,
                p4: FontMetricsInt?
            ): Int {
                return width
            }

            override fun draw(
                canvas: Canvas, text: CharSequence,
                start: Int,
                end: Int,
                x: Float, top: Int, y: Int, bottom: Int,
                paint: Paint
            ) {
                canvas.drawRect(x, top.toFloat(), x + width, bottom.toFloat(), this.paint)
            }
        }

        internal class CustomQuoteSpan(
            private val color: Int,
            private val stripeWidth: Int,
            private val gapWidth: Int
        ) : LeadingMarginSpan {

            override fun getLeadingMargin(first: Boolean): Int {
                return stripeWidth + gapWidth
            }

            override fun drawLeadingMargin(
                c: Canvas, p: Paint, x: Int, dir: Int,
                top: Int, baseline: Int, bottom: Int,
                text: CharSequence, start: Int, end: Int,
                first: Boolean, layout: Layout
            ) {
                val style = p.style
                val color = p.color

                p.style = Paint.Style.FILL
                p.color = this.color

                c.drawRect(
                    x.toFloat(),
                    top.toFloat(),
                    (x + dir * stripeWidth).toFloat(),
                    bottom.toFloat(),
                    p
                )

                p.style = style
                p.color = color
            }
        }

        internal class CustomBulletSpan(
            private val color: Int,
            private val radius: Int,
            private val gapWidth: Int
        ) : LeadingMarginSpan {

            private var sBulletPath: Path? = null

            override fun getLeadingMargin(first: Boolean): Int {
                return 2 * radius + gapWidth
            }

            override fun drawLeadingMargin(
                c: Canvas, p: Paint, x: Int, dir: Int,
                top: Int, baseline: Int, bottom: Int,
                text: CharSequence, start: Int, end: Int,
                first: Boolean, l: Layout
            ) {
                if ((text as Spanned).getSpanStart(this) == start) {
                    val style = p.style
                    var oldColor = 0
                    oldColor = p.color
                    p.color = color
                    p.style = Paint.Style.FILL
                    if (c.isHardwareAccelerated) {
                        if (sBulletPath == null) {
                            sBulletPath = Path()
                            // Bullet is slightly better to avoid aliasing artifacts on mdpi devices.
                            sBulletPath!!.addCircle(0.0f, 0.0f, radius.toFloat(), Path.Direction.CW)
                        }
                        c.save()
                        c.translate((x + dir * radius).toFloat(), (top + bottom) / 2.0f)
                        c.drawPath(sBulletPath!!, p)
                        c.restore()
                    } else {
                        c.drawCircle(
                            (x + dir * radius).toFloat(),
                            (top + bottom) / 2.0f,
                            radius.toFloat(),
                            p
                        )
                    }
                    p.color = oldColor
                    p.style = style
                }
            }
        }

        @SuppressLint("ParcelCreator")
        internal class CustomTypefaceSpan(private val newType: Typeface) :
            TypefaceSpan("") {

            override fun updateDrawState(textPaint: TextPaint) {
                apply(textPaint, newType)
            }

            override fun updateMeasureState(paint: TextPaint) {
                apply(paint, newType)
            }

            private fun apply(paint: Paint, tf: Typeface) {
                val oldStyle: Int
                val old = paint.typeface
                oldStyle = old?.style ?: 0

                val fake = oldStyle and tf.style.inv()
                if (fake and Typeface.BOLD != 0) {
                    paint.isFakeBoldText = true
                }

                if (fake and Typeface.ITALIC != 0) {
                    paint.textSkewX = -0.25f
                }

                paint.shader

                paint.typeface = tf
            }
        }

        internal class CustomImageSpan : CustomDynamicDrawableSpan {
            private var mDrawable: Drawable? = null
            private var mContentUri: Uri? = null
            private var mResourceId: Int = 0

            override val drawable: Drawable
                get() {
                    var drawable: Drawable? = null
                    when {
                        mDrawable != null -> drawable = mDrawable
                        mContentUri != null -> {
                            val bitmap: Bitmap
                            try {
                                val `is` =
                                    AppUtils.getApp().contentResolver.openInputStream(mContentUri!!)
                                bitmap = BitmapFactory.decodeStream(`is`)
                                drawable = BitmapDrawable(AppUtils.getApp().resources, bitmap)
                                drawable.setBounds(
                                    0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight
                                )
                                if (`is` != null) {
                                    `is`!!.close()
                                }
                            } catch (e: Exception) {
                                logE(
                                    "sms",
                                    "Failed to loaded content $mContentUri", e
                                )
                            }

                        }
                        else -> try {
                            drawable = ContextCompat.getDrawable(AppUtils.getApp(), mResourceId)
                            drawable!!.setBounds(
                                0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight
                            )
                        } catch (e: Exception) {
                            logE("sms", "Unable to find resource: $mResourceId")
                        }
                    }
                    return drawable!!
                }

            constructor(b: Bitmap, verticalAlignment: Int) : super(verticalAlignment) {
                mDrawable = BitmapDrawable(AppUtils.getApp().resources, b)
                mDrawable!!.setBounds(
                    0, 0, mDrawable!!.intrinsicWidth, mDrawable!!.intrinsicHeight
                )
            }

            constructor(d: Drawable, verticalAlignment: Int) : super(verticalAlignment) {
                mDrawable = d
                mDrawable!!.setBounds(
                    0, 0, mDrawable!!.intrinsicWidth, mDrawable!!.intrinsicHeight
                )
            }

            constructor(uri: Uri, verticalAlignment: Int) : super(verticalAlignment) {
                mContentUri = uri
            }

            constructor(@DrawableRes resourceId: Int, verticalAlignment: Int) : super(
                verticalAlignment
            ) {
                mResourceId = resourceId
            }
        }

        internal abstract class CustomDynamicDrawableSpan : ReplacementSpan {

            val mVerticalAlignment: Int

            abstract val drawable: Drawable

            private val cachedDrawable: Drawable
                get() {
                    val wr = mDrawableRef
                    var d: Drawable? = null
                    if (wr != null) {
                        d = wr!!.get()
                    }
                    if (d == null) {
                        d = drawable
                        mDrawableRef = WeakReference(d)
                    }
                    return d
                }

            private var mDrawableRef: WeakReference<Drawable>? = null

            private constructor() {
                mVerticalAlignment = ALIGN_BOTTOM
            }

            protected constructor(verticalAlignment: Int) {
                mVerticalAlignment = verticalAlignment
            }

            override fun getSize(
                paint: Paint, text: CharSequence,
                start: Int, end: Int, fm: Paint.FontMetricsInt?
            ): Int {
                val d = cachedDrawable
                val rect = d.bounds
                if (fm != null) {
                    val lineHeight = fm.bottom - fm.top
                    if (lineHeight < rect.height()) {
                        when (mVerticalAlignment) {
                            ALIGN_TOP -> {
                                fm.top = fm.top
                                fm.bottom = rect.height() + fm.top
                            }
                            ALIGN_CENTER -> {
                                fm.top = -rect.height() / 2 - lineHeight / 4
                                fm.bottom = rect.height() / 2 - lineHeight / 4
                            }
                            else -> {
                                fm.top = -rect.height() + fm.bottom
                                fm.bottom = fm.bottom
                            }
                        }
                        fm.ascent = fm.top
                        fm.descent = fm.bottom
                    }
                }
                return rect.right
            }

            override fun draw(
                canvas: Canvas, text: CharSequence,
                start: Int, end: Int, x: Float,
                top: Int, y: Int, bottom: Int, paint: Paint
            ) {
                val d = cachedDrawable
                val rect = d.bounds
                canvas.save()
                val transY: Float
                val lineHeight = bottom - top
                //            LogUtils.d("rectHeight: " + rect.height(),
                //                    "lineHeight: " + (bottom - top));
                if (rect.height() < lineHeight) {
                    transY = when (mVerticalAlignment) {
                        ALIGN_TOP -> top.toFloat()
                        ALIGN_CENTER -> (bottom + top - rect.height()) / 2f
                        ALIGN_BASELINE -> (y - rect.height()) * 1f
                        else -> (bottom - rect.height()) * 1f
                    }
                    canvas.translate(x, transY)
                } else {
                    canvas.translate(x, top * 1f)
                }
                d.draw(canvas)
                canvas.restore()
            }

            companion object {

                const val ALIGN_BOTTOM = 0

                const val ALIGN_BASELINE = 1

                const val ALIGN_CENTER = 2

                const val ALIGN_TOP = 3
            }
        }

        internal class ShaderSpan(private val mShader: Shader) :
            CharacterStyle(), UpdateAppearance {

            override fun updateDrawState(tp: TextPaint) {
                tp.shader = mShader
            }
        }

        internal class ShadowSpan(
            private val radius: Float,
            private val dx: Float,
            private val dy: Float,
            private val shadowColor: Int
        ) : CharacterStyle(), UpdateAppearance {

            override fun updateDrawState(tp: TextPaint) {
                tp.setShadowLayer(radius, dx, dy, shadowColor)
            }
        }

        private class SerializableSpannableStringBuilder : SpannableStringBuilder(),
            java.io.Serializable {
            companion object {
                private const val serialVersionUID = 4909567650765875771L
            }
        }

        ///////////////////////////////////////////////////////////////////////////
        // static
        ///////////////////////////////////////////////////////////////////////////

        fun with(textView: TextView): Companion {
            return withTextView(textView)
        }
    }
}