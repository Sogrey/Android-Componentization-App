package top.sogrey.common.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.Canvas.ALL_SAVE_FLAG
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import top.sogrey.common.R
/**
 * 万能自定义 ImageView
 * app:dsc="@drawable/icon_dsc"
 * app:hasBord="true"
 * app:bord="@drawable/icon_bord"
 * <p/>
 * @author Sogrey
 * @date 2019-10-30 20:43
 */

class CustomImageView(context: Context, attrs: AttributeSet? = null) : AppCompatImageView(context, attrs) {

    //是否有边框
    var isBord = false
        set(value) {
            field = value
            setUp()
        }

    //目标资源
    var dscRes = R.drawable.ic_default_custom_bord
        set(value) {
            field = value
            setUp()
        }

    //边框资源
    var bordRes = R.drawable.ic_default_custom_dsc
        set(value) {
            field = value
            isBord = true
            setUp()
        }

    private var mReady = true
    private var mSetupPending = false
    //源bitmap
    private var mSrcBitmap: Bitmap? = null
    //目标bitmap
    private var mDstBitmap: Bitmap? = null
    //边框bitmap
    private var mBordBitmap: Bitmap? = null

    private val bitmapConfig by lazy { Bitmap.Config.ARGB_8888 }
    private val colorDrawableDimension by lazy { 2 }
    private val drawableRect by lazy { RectF() }
    private val borderRect by lazy { RectF() }
    private val shaderMatrix by lazy { Matrix() }
    private val bitmapPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }

    init {
        attrs?.also {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomImageView)
            isBord = typedArray.getBoolean(R.styleable.CustomImageView_hasBord, false)
            dscRes = typedArray.getResourceId(R.styleable.CustomImageView_dsc, R.drawable.ic_default_custom_dsc)
            bordRes = typedArray.getResourceId(R.styleable.CustomImageView_bord, R.drawable.ic_default_custom_bord)
            typedArray.recycle()
        }
        init()
    }

    private fun init() {
        if (mSetupPending) {
            setUp()
            mSetupPending = false
        }
    }

    override fun setAdjustViewBounds(adjustViewBounds: Boolean) {
        if (adjustViewBounds) {
            throw IllegalArgumentException("adjustViewBounds not supported.")
        }
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        setUp()
    }

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        super.setPaddingRelative(start, top, end, bottom)
        setUp()
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        initializeBitmap()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        initializeBitmap()
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        initializeBitmap()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        initializeBitmap()
    }

    override fun setColorFilter(cf: ColorFilter?) {
        super.setColorFilter(cf)
        cf?.also {
            applyColorFilter()
            invalidate()
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        mSrcBitmap?.also { it ->
            //背景色设为白色，方便比较效果
            canvas?.drawColor(Color.TRANSPARENT)
            //将绘制操作保存到新的图层，因为图像合成是很昂贵的操作，将用到硬件加速，这里将图像合成的处理放到离屏缓存中进行
            val saveCount = canvas?.saveLayer(drawableRect, bitmapPaint, ALL_SAVE_FLAG) ?: 0
            //绘制目标图
            mDstBitmap?.also {
                canvas?.drawBitmap(it, null, drawableRect, bitmapPaint)
            }
            //设置混合模式
            bitmapPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            //绘制源图
            canvas?.drawBitmap(it, null, drawableRect, bitmapPaint)
            //清除混合模式
            bitmapPaint.xfermode = null
            // 还原画布
            canvas?.restoreToCount(saveCount)

            if (isBord) {
                mBordBitmap?.also {
                    canvas?.drawBitmap(it, null, drawableRect, bitmapPaint)
                }
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setUp()
    }

    private fun setUp() {
        if (!mReady) {
            mSetupPending = true
            return
        }
        if (0 == width || 0 == height) {
            return
        }
        mSrcBitmap?.also { it ->
            borderRect.set(calculateBounds())

            drawableRect.set(borderRect)

            val srcShader = BitmapShader(it, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

            mDstBitmap = mDstBitmap ?: getBitmapFromRes(dscRes)

            if (isBord) {
                mBordBitmap = getBitmapFromRes(bordRes)
                mBordBitmap?.also {
                    val bordShader = BitmapShader(it, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                    updateShaderMatrix(it, bordShader)
                }
            }

            applyColorFilter()

            updateShaderMatrix(it, srcShader)

            mDstBitmap?.also {
                val dstShader = BitmapShader(it, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                updateShaderMatrix(it, dstShader)
            }

            invalidate()
        } ?: also {
            invalidate()
        }
    }

    private fun initializeBitmap() {
        mSrcBitmap = getBitmapFromDrawable(drawable)
        setUp()
    }

    private fun getBitmapFromDrawable(drawable: Drawable): Bitmap {
        return when (drawable) {
            is BitmapDrawable -> drawable.bitmap
            is ColorDrawable -> creatBitmap(Bitmap.createBitmap(colorDrawableDimension, colorDrawableDimension, bitmapConfig), drawable)
            else -> creatBitmap(Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, bitmapConfig), drawable)
        }
    }

    private fun creatBitmap(bitmap: Bitmap, drawable: Drawable): Bitmap {
        return bitmap.also {
            val canvas = Canvas(it)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
        }
    }

    @SuppressLint("NewApi")
    private fun applyColorFilter() {
        colorFilter?.also {
            bitmapPaint.colorFilter = it
        }
    }

    private fun getBitmapFromRes(@DrawableRes resId: Int): Bitmap {
        return resId.let {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                creatBitmap(Bitmap.createBitmap(drawableRect.width().toInt(),
                    drawableRect.height().toInt(), bitmapConfig),
                    resources.getDrawable(it, null))
            } else {
                BitmapFactory.decodeResource(resources, it)
            }
        }
    }

    private fun calculateBounds(): RectF {
        val availableWidth = width - paddingLeft - paddingRight
        val availableHeight = height - paddingTop - paddingBottom
        val sideLength = Math.min(availableWidth, availableHeight)
        val left = paddingLeft - (availableWidth - sideLength) / 2.0f
        val top = paddingTop - (availableHeight - sideLength) / 2.0f
        return RectF(left, top, left + sideLength, top + sideLength)
    }

    private fun updateShaderMatrix(bitmap: Bitmap, shader: Shader) {
        shaderMatrix.set(null)
        val scaleX = bitmap.width / drawableRect.width()
        val scaleY = bitmap.height / drawableRect.height()
        shaderMatrix.setScale(scaleX, scaleY)
        shader.setLocalMatrix(shaderMatrix)
    }
}