package top.sogrey.common.views

import android.content.Context
import android.util.AttributeSet
import android.widget.ListView


/**
 * ListView 禁止滚动
 * <p/>
 * @author Sogrey
 * @date 2019-10-30 17:50
 */
class NoScrollListview : ListView {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    /**
     * 设置不滚动
     */
    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val expandSpec = MeasureSpec.makeMeasureSpec(
            Integer.MAX_VALUE shr 2,
            MeasureSpec.AT_MOST
        )
        super.onMeasure(widthMeasureSpec, expandSpec)
    }
}