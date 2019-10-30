package top.sogrey.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ListView

/**
 * ListView 禁止滚动，高度可设
 * <p/>
 * @author Sogrey
 * @date 2019-10-30 17:55
 */
 
class ScrollDisabledListView : ListView {

    private var mPosition: Int = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val actionMasked = ev.actionMasked and MotionEvent.ACTION_MASK

        if (actionMasked == MotionEvent.ACTION_DOWN) {
            // 记录手指按下时的位置
            mPosition = pointToPosition(ev.x.toInt(), ev.y.toInt())
            return super.dispatchTouchEvent(ev)
        }

        if (actionMasked == MotionEvent.ACTION_MOVE) {
            // 最关键的地方，忽略MOVE 事件
            // ListView onTouch获取不到MOVE事件所以不会发生滚动处理
            return true
        }

        // 手指抬起时
        if (actionMasked == MotionEvent.ACTION_UP || actionMasked == MotionEvent.ACTION_CANCEL) {
            // 手指按下与抬起都在同一个视图内，交给父控件处理，这是一个点击事件
            if (pointToPosition(ev.x.toInt(), ev.y.toInt()) == mPosition) {
                super.dispatchTouchEvent(ev)
            } else {
                // 如果手指已经移出按下时的Item，说明是滚动行为，清理Item pressed状态
                isPressed = false
                invalidate()
                return true
            }
        }

        return super.dispatchTouchEvent(ev)
    }
}