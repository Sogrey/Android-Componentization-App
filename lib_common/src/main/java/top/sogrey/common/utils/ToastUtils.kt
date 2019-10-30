package top.sogrey.common.utils

import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.widget.Toast
import androidx.core.content.ContextCompat
import top.sogrey.common.R

class ToastUtils {
    constructor() {
        throw UnsupportedOperationException(this.javaClass.simpleName + " cannot be instantiated")
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var mContext: Context? = null
        // Toast对象
        private var mToast: Toast? = null
        // 文字显示的颜色 <color name="white">#FFFFFFFF</color>
        private val mMessageColor = R.color.white

        /**
         * 在Application中初始化ToastUtils.init(this)
         *
         * @param context
         */
        fun init(context: Context) {
            mContext = context.applicationContext
        }
        /**
         * 显示 Toast,默认LENGTH_SHORT
         *
         * @param resId 字符串资源ID
         */
        fun show(resId: Int) {
            if (mContext == null) mContext = AppUtils.getApp()
            show(mContext!!.getString(resId))
        }
        /**
         * 显示 Toast,默认LENGTH_SHORT
         * @param resId 字符串资源ID
         * @param duration 时间间隔 ,Toast.LENGTH_SHORT OR Toast.LENGTH_LONG
         */
        fun show(resId: Int, duration: Int) {
            if (mContext == null) mContext = AppUtils.getApp()
            show(mContext!!.getString(resId), duration)
        }
        /**
         * 显示 Toast,默认LENGTH_SHORT
         * @param message 字符串
         */
        fun show(message: CharSequence) {
            if (mContext == null) mContext = AppUtils.getApp()
            showToast(mContext, message, Toast.LENGTH_SHORT)
        }
        /**
         * 显示 Toast,默认LENGTH_SHORT
         * @param message 字符串
         * @param duration 时间间隔 ,Toast.LENGTH_SHORT OR Toast.LENGTH_LONG
         */
        fun show(message: CharSequence, duration: Int) {
            if (mContext == null) mContext = AppUtils.getApp()
            showToast(mContext, message, duration)
        }


//        fun show(resId: Int, vararg args: Any) {
//            show(
//                String.format(mContext!!.resources.getString(resId), args),
//                Toast.LENGTH_SHORT
//            )
//        }
//
//        fun show(format: String, vararg args: Any) {
//            show(String.format(format, *args), Toast.LENGTH_SHORT)
//        }
//
//        fun show(resId: Int, duration: Int, vararg args: Any) {
//            show(
//                String.format(mContext!!.resources.getString(resId), args),
//                duration
//            )
//        }
//
//        fun show(format: String, duration: Int,vararg args: Any) {
//            show(String.format(format, *args), duration)
//        }

        private fun showToast(context: Context?, massage: CharSequence, duration: Int) {
            // 设置显示文字的颜色
            val spannableString = SpannableString(massage)
            val colorSpan = ForegroundColorSpan(ContextCompat.getColor(context!!, mMessageColor))
            spannableString.setSpan(
                colorSpan,
                0,
                spannableString.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            if (mToast == null) {
                mToast = Toast.makeText(context, spannableString, duration)
            } else {
                mToast!!.setText(spannableString)
                mToast!!.duration = duration
            }
            // 设置显示的背景
            val view = mToast!!.view
            view.setBackgroundResource(R.drawable.toast_frame_style)
            // 设置Toast要显示的位置，水平居中并在底部，X轴偏移0个单位，Y轴偏移200个单位，
            mToast!!.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM, 0, 200)
            mToast!!.show()
        }

        /**
         * 在UI界面隐藏或者销毁前取消Toast显示
         */
        fun cancel() {
            if (mToast != null) {
                mToast!!.cancel()
                mToast = null
            }
        }
    }
}