package top.sogrey.common.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout

/**
 * 键盘输入相关
 *
 * @author Sogrey
 * @date 2019/10/26
 */
class KeyboardUtils {
    constructor(){
        throw UnsupportedOperationException(this.javaClass.simpleName + " cannot be instantiated")
    }
    companion object{

        /**
         * Show the soft input.
         */
        fun showSoftInput(activity: Activity) {
            if (!isSoftInputVisible(activity)) {
                toggleSoftInput()
            }
        }

        /**
         * Show the soft input.
         *
         * @param view The view.
         */
        fun showSoftInput(view: View) {
            showSoftInput(view, 0)
        }

        /**
         * Show the soft input.
         *
         * @param view  The view.
         * @param flags Provides additional operating flags.  Currently may be
         * 0 or have the [InputMethodManager.SHOW_IMPLICIT] bit set.
         */
        fun showSoftInput(view: View, flags: Int) {
            val imm =
                AppUtils.getApp().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            view.isFocusable = true
            view.isFocusableInTouchMode = true
            view.requestFocus()
            imm.showSoftInput(view, flags, object : ResultReceiver(Handler()) {
                override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
                    if (resultCode == InputMethodManager.RESULT_UNCHANGED_HIDDEN || resultCode == InputMethodManager.RESULT_HIDDEN) {
                        toggleSoftInput()
                    }
                }
            })
            imm.toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY
            )
        }

        /**
         * Hide the soft input.
         *
         * @param activity The activity.
         */
        fun hideSoftInput(activity: Activity) {
            var view = activity.currentFocus
            if (view == null) {
                val decorView = activity.window.decorView
                val focusView = decorView.findViewWithTag<View>("keyboardTagView")
                if (focusView == null) {
                    view = EditText(activity)
                    view.tag = "keyboardTagView"
                    (decorView as ViewGroup).addView(view, 0, 0)
                } else {
                    view = focusView
                }
                view.requestFocus()
            }
            hideSoftInput(view)
        }

        /**
         * Hide the soft input.
         *
         * @param view The view.
         */
        fun hideSoftInput(view: View) {
            val imm =
                AppUtils.getApp().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        /**
         * Toggle the soft input display or not.
         */
        fun toggleSoftInput() {
            val imm =
                AppUtils.getApp().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(0, 0)
        }

        private var sDecorViewDelta = 0

        /**
         * Return whether soft input is visible.
         *
         * @param activity The activity.
         * @return `true`: yes<br></br>`false`: no
         */
        fun isSoftInputVisible(activity: Activity): Boolean {
            return getDecorViewInvisibleHeight(activity.window) > 0
        }

        private fun getDecorViewInvisibleHeight(window: Window): Int {
            val decorView = window.decorView
            val outRect = Rect()
            decorView.getWindowVisibleDisplayFrame(outRect)
            LogKtUtils.d(
                "KeyboardUtils",
                "getDecorViewInvisibleHeight: " + (decorView.bottom - outRect.bottom)
            )
            val delta = Math.abs(decorView.bottom - outRect.bottom)
            if (delta <= getNavBarHeight() + getStatusBarHeight()) {
                sDecorViewDelta = delta
                return 0
            }
            return delta - sDecorViewDelta
        }

        /**
         * Register soft input changed listener.
         *
         * @param activity The activity.
         * @param listener The soft input changed listener.
         */
        fun registerSoftInputChangedListener(
            activity: Activity,
            listener: OnSoftInputChangedListener
        ) {
            registerSoftInputChangedListener(activity.window, listener)
        }

        /**
         * Register soft input changed listener.
         *
         * @param window   The window.
         * @param listener The soft input changed listener.
         */
        fun registerSoftInputChangedListener(
            window: Window,
            listener: OnSoftInputChangedListener
        ) {
            val flags = window.attributes.flags
            if (flags and WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS != 0) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            }
            val contentView = window.findViewById<FrameLayout>(android.R.id.content)
            val decorViewInvisibleHeightPre = intArrayOf(getDecorViewInvisibleHeight(window))
            contentView.viewTreeObserver
                .addOnGlobalLayoutListener {
                    val height = getDecorViewInvisibleHeight(window)
                    if (decorViewInvisibleHeightPre[0] != height) {
                        listener.onSoftInputChanged(height)
                        decorViewInvisibleHeightPre[0] = height
                    }
                }
        }

        /**
         * Fix the bug of 5497 in Android.
         *
         * Don't set adjustResize
         *
         * @param activity The activity.
         */
        fun fixAndroidBug5497(activity: Activity) {
            fixAndroidBug5497(activity.window)
        }

        /**
         * Fix the bug of 5497 in Android.
         *
         * Don't set adjustResize
         *
         * @param window The window.
         */
        fun fixAndroidBug5497(window: Window) {
            val contentView = window.findViewById<FrameLayout>(android.R.id.content)
            val contentViewChild = contentView.getChildAt(0)
            val paddingBottom = contentViewChild.paddingBottom
            val contentViewInvisibleHeightPre5497 =
                intArrayOf(getContentViewInvisibleHeight(window))
            contentView.viewTreeObserver
                .addOnGlobalLayoutListener {
                    val height = getContentViewInvisibleHeight(window)
                    if (contentViewInvisibleHeightPre5497[0] != height) {
                        contentViewChild.setPadding(
                            contentViewChild.paddingLeft,
                            contentViewChild.paddingTop,
                            contentViewChild.paddingRight,
                            paddingBottom + getDecorViewInvisibleHeight(window)
                        )
                        contentViewInvisibleHeightPre5497[0] = height
                    }
                }
        }

        private fun getContentViewInvisibleHeight(window: Window): Int {
            val contentView = window.findViewById<View>(android.R.id.content) ?: return 0
            val outRect = Rect()
            contentView.getWindowVisibleDisplayFrame(outRect)
            LogKtUtils.d(
                "KeyboardUtils",
                "getContentViewInvisibleHeight: " + (contentView.bottom - outRect.bottom)
            )
            val delta = Math.abs(contentView.bottom - outRect.bottom)
            return if (delta <= getStatusBarHeight() + getNavBarHeight()) {
                0
            } else delta
        }

        /**
         * Fix the leaks of soft input.
         *
         * @param activity The activity.
         */
        fun fixSoftInputLeaks(activity: Activity) {
            fixSoftInputLeaks(activity.window)
        }

        /**
         * Fix the leaks of soft input.
         *
         * @param window The window.
         */
        fun fixSoftInputLeaks(window: Window) {
            val imm =
                AppUtils.getApp().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val leakViews =
                arrayOf("mLastSrvView", "mCurRootView", "mServedView", "mNextServedView")
            for (leakView in leakViews) {
                try {
                    val leakViewField =
                        InputMethodManager::class.java.getDeclaredField(leakView)
                    if (!leakViewField.isAccessible) {
                        leakViewField.isAccessible = true
                    }
                    val obj = leakViewField.get(imm)
                    if (obj !is View) {
                        continue
                    }
                    if (obj.rootView === window.decorView.rootView) {
                        leakViewField.set(imm, null)
                    }
                } catch (ignore: Throwable) {/**/
                }

            }
        }

        /**
         * Click blank area to hide soft input.
         *
         * Copy the following code in ur activity.
         */
        fun clickBlankArea2HideSoftInput() {
            LogKtUtils.d("KeyboardUtils", "Please refer to the following code.")
            /*
        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                View v = getCurrentFocus();
                if (isShouldHideKeyboard(v, ev)) {
                    KeyboardUtils.hideSoftInput(this);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // Return whether touch the view.
        private boolean isShouldHideKeyboard(View v, MotionEvent event) {
            if (v != null && (v instanceof EditText)) {
                int[] l = {0, 0};
                v.getLocationInWindow(l);
                int left = l[0],
                        top = l[1],
                        bottom = top + v.getHeight(),
                        right = left + v.getWidth();
                return !(event.getX() > left && event.getX() < right
                        && event.getY() > top && event.getY() < bottom);
            }
            return false;
        }
        */
        }

        private fun getStatusBarHeight(): Int {
            val resources = AppUtils.getApp().resources
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            return resources.getDimensionPixelSize(resourceId)
        }

        private fun getNavBarHeight(): Int {
            val res = AppUtils.getApp().resources
            val resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android")
            return if (resourceId != 0) {
                res.getDimensionPixelSize(resourceId)
            } else {
                0
            }
        }

        private fun getActivityByView(view: View): Activity? {
            return getActivityByContext(view.context)
        }

        private fun getActivityByContext(context: Context): Activity? {
            var contextVar = context
            if (contextVar is Activity) {
                return contextVar
            }
            while (contextVar is ContextWrapper) {
                if (contextVar is Activity) {
                    return contextVar
                }
                contextVar = contextVar.baseContext
            }
            return null
        }

///////////////////////////////////////////////////////////////////////////
// interface
///////////////////////////////////////////////////////////////////////////

        interface OnSoftInputChangedListener {
            /**
             * 软键盘变化
             * @param height
             */
            fun onSoftInputChanged(height: Int)
        }
    }
}