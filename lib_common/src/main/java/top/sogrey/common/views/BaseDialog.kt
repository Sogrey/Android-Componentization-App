package top.sogrey.common.views

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import androidx.annotation.NonNull
import top.sogrey.common.R

/**
 * Dialog基类.
 * <p/>
 * example：
 * <pre<code>public class ShareDialog extends BaseDialog {
@Override
protected int[] onSetupDialogFrameSize(int screenWidth, int screenHeight) {
int[] size = new int[2];
size[0] = screenWidth;
size[1] = ViewGroup.LayoutParams.WRAP_CONTENT;
return size;
}
}</code></pre>
<p/>
<pre><code>public class UpgradeTipDialog extends BaseDialog {
@Override
public void onStart() {
super.onStart();
//设置弹窗在底部弹出
Window window = getWindow();
WindowManager.LayoutParams params = window.getAttributes();
params.gravity = Gravity.BOTTOM;
window.setAttributes(params);
}

@Override
protected int[] onSetupDialogFrameSize(int screenWidth, int screenHeight) {
int[] size = new int[2];
size[0] = screenWidth * 0.08f;
size[1] = screenHeight;
return size;
}
}</code></pre>
<p/>
<pre><code>public class UpgradeTipDialog extends BaseDialog {
@Override
protected int[] onSetupDialogFrameSize(int screenWidth, int screenHeight) {
int[] size = new int[2];
size[0] = screenWidth;
size[1] = screenHeight;
return size;
}
}</code></pre>
 * @author Sogrey
 * @date 2019-11-22 11:07
 */

open abstract class BaseDialog(@NonNull context: Context, themeResId: Int = R.style.BaseDialog) :
    Dialog(context, themeResId) {

    init {
        init()
    }

    private fun init() {
        //让Dialog正常化
        normalize()
    }

    /**
     * 正常化Dialog
     */
    private fun normalize() {
        fixBackground()
        fixSize()
    }

    /**
     * 修复背景
     */
    private fun fixBackground() {
        val window = window ?: return
        //1、去掉白色的背景
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    /**
     * 修复大小
     */
    private fun fixSize() {
        val window = window ?: return
        //获取需要显示的宽、高
        val metrics = DisplayMetrics()
        //3、当子类决定弹窗宽高
        getWindow()!!.windowManager.defaultDisplay.getMetrics(metrics)
        val frameSize = onSetupDialogFrameSize(metrics.widthPixels, metrics.heightPixels)
        //4、去掉默认padding间距
        window.decorView.setPadding(0, 0, 0, 0)
        val params = window.attributes
        params.width = frameSize[0]
        params.height = frameSize[1]
        window.attributes = params
    }

    /**
     * 子类需要复写该方法，返回需要的宽高
     */
    protected abstract fun onSetupDialogFrameSize(screenWidth: Int, screenHeight: Int): IntArray
}