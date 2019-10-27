package top.sogrey.common.utils.ktx

import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

/**
 * 描述：RecyclerView 方法扩展
 * Created by Sogrey on 2018/12/11.
 */

fun RecyclerView.runLayoutAnimation(animResID:Int) {
    val context = this.context
    val controller = AnimationUtils.loadLayoutAnimation(context, animResID)

    this.layoutAnimation = controller
    this.adapter!!.notifyDataSetChanged()
    this.scheduleLayoutAnimation()
}

fun RecyclerView.setDefaultItemAnimator(){
    //设置默认动画
    val animator = DefaultItemAnimator()
    //设置动画时间
    animator.addDuration = 500
    animator.removeDuration = 500
    this.itemAnimator = animator
}