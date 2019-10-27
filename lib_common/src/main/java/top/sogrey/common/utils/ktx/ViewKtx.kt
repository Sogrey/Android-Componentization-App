package top.sogrey.common.utils.ktx

import android.view.View
import android.view.ViewGroup

/**
 * 描述：View 扩展方法
 * Created by Sogrey on 2018/12/13.
 */
/**
 * 获取View第一子级View
 */
fun View.getTopChildViews(): List<View> {
    var topChildren = ArrayList<View>()
    if (this is ViewGroup && childCount > 0) {
        for (i in 0..(childCount)) {
            var view = getChildAt(i)
            if (null != view) {
                topChildren.add(view)
            }
        }
    }
    return topChildren
}

/**
 * 递归获取View的所有子孙View
 */
fun View.getAllChildViews(): List<View> {
    var allChildren = ArrayList<View>()
    if (this is ViewGroup && childCount > 0) {
        for (i in 0..(childCount)) {
            var view = getChildAt(i)
            if (null != view) {
                allChildren.add(view)
                //再次 调用本身（递归）
                allChildren.addAll(view.getAllChildViews())
            }
        }
    }
    return allChildren
}