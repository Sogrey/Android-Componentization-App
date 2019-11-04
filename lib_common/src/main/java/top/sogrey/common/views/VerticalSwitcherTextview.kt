package top.sogrey.common.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.TranslateAnimation
import android.widget.TextSwitcher
import android.widget.TextView
import android.widget.ViewSwitcher

/**
 * 垂直滚动文本textview
 *
TextView = (VerticalTextview) findViewById(R.id.text);
titleList.add("你是天上最受宠的一架钢琴");
titleList.add("我是丑人脸上的鼻涕");
titleList.add("你发出完美的声音");
titleList.add("我被默默揩去");
titleList.add("你冷酷外表下藏着诗情画意");
titleList.add("我已经够胖还吃东西");
titleList.add("你踏着七彩祥云离去");
titleList.add("我被留在这里");
TextView.setTextList(titleList);
TextView.setText(26, 5, Color.RED);//设置属性
TextView.setTextStillTime(3000);//设置停留时长间隔
TextView.setAnimTime(300);//设置进入和退出的时间间隔
TextView.setOnItemClickListener(new VerticalTextview.OnItemClickListener() {
@Override
public void onItemClick(int position) {
Toast.makeText(MainActivity.this, "点击了 : " + titleList.get(position), Toast.LENGTH_SHORT).show();
}
});
 * <p/>
 * @author Sogrey
 * @date 2019-11-04 22:08
 */

class VerticalSwitcherTextview : TextSwitcher, ViewSwitcher.ViewFactory, IBaseView {


    private val FLAG_START_AUTO_SCROLL = 0
    private val FLAG_STOP_AUTO_SCROLL = 1

    private val STATE_PAUSE = 2
    private val STATE_SCROLL = 3

    private var mTextSize = 16f
    private var mPadding = 5
    private var textColor = Color.BLACK

    private var mScrollState = STATE_PAUSE

    private var itemClickListener: OnItemClickListener? = null
    private var mContext: Context? = null
    private var currentId = -1
    private var textList: ArrayList<String>? = null


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        //init(context)要在retrieveAttributes(attrs)前调用
        //因为属性赋值，会直接赋值到控件上去。如:
        //调用label = ""时，相当于调用了label的set方法。
        init(context)
        //retrieveAttributes(attrs: AttributeSet)方法只接受非空参数
        attrs?.let { retrieveAttributes(attrs) }
    }

    override fun init(context: Context) {
        mContext = context
        textList = ArrayList()
    }

    override fun retrieveAttributes(attrs: AttributeSet) {
    }

    override fun makeView(): View {
        val t = TextView(mContext)
        t.gravity = Gravity.CENTER_VERTICAL or Gravity.LEFT
        t.maxLines = 1
        t.setPadding(mPadding, mPadding, mPadding, mPadding)
        t.setTextColor(textColor)
        t.textSize = mTextSize

        t.isClickable = true
        t.setOnClickListener {
            if (itemClickListener != null && textList!!.size > 0 && currentId != -1) {
                itemClickListener!!.onItemClick(currentId % textList!!.size)
            }
        }
        return t
    }

    /**
     * @param textSize  textsize
     * @param padding   padding
     * @param textColor textcolor
     */
    fun setText(textSize: Float, padding: Int, textColor: Int) {
        mTextSize = textSize
        mPadding = padding
        this.textColor = textColor
    }


    fun setAnimTime(animDuration: Long) {
        setFactory(this)
        val `in` = TranslateAnimation(0f, 0f, animDuration.toFloat(), 0f)
        `in`.duration = animDuration
        `in`.interpolator = AccelerateInterpolator()
        val out = TranslateAnimation(0f, 0f, 0f, (-animDuration).toFloat())
        out.duration = animDuration
        out.interpolator = AccelerateInterpolator()
        inAnimation = `in`
        outAnimation = out
    }

    /**
     * set time.
     *
     * @param time
     */
    fun setTextStillTime(time: Long) {
        var handler: Handler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    FLAG_START_AUTO_SCROLL -> {
                        if (textList!!.size > 0) {
                            currentId++
                            setText(textList!![currentId % textList!!.size])
                        }
                        handler.sendEmptyMessageDelayed(FLAG_START_AUTO_SCROLL, time)
                    }
                    FLAG_STOP_AUTO_SCROLL -> handler.removeMessages(FLAG_START_AUTO_SCROLL)
                }
            }
        }
    }

    /**
     * set Data list.
     *
     * @param titles
     */
    fun setTextList(titles: ArrayList<String>) {
        textList!!.clear()
        textList!!.addAll(titles)
        currentId = -1
    }

    /**
     * start auto scroll
     */
    fun startAutoScroll() {
        mScrollState = STATE_SCROLL
        handler.sendEmptyMessage(FLAG_START_AUTO_SCROLL)
    }

    /**
     * stop auto scroll
     */
    fun stopAutoScroll() {
        mScrollState = STATE_PAUSE
        handler.sendEmptyMessage(FLAG_STOP_AUTO_SCROLL)
    }


    /**
     * set onclick listener
     *
     * @param itemClickListener listener
     */
    fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    /**
     * item click listener
     */
    interface OnItemClickListener {
        /**
         * callback
         *
         * @param position position
         */
        fun onItemClick(position: Int)
    }

    fun isScroll(): Boolean {
        return mScrollState == STATE_SCROLL
    }

    fun isPause(): Boolean {
        return mScrollState == STATE_PAUSE
    }

    //memory leancks.
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handler.removeCallbacksAndMessages(null)
    }
}