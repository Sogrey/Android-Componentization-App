package top.sogrey.component.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import kotlinx.android.synthetic.main.activity_test.*
import top.sogrey.common.utils.CrashUtils
import top.sogrey.common.utils.ImageUtils
import top.sogrey.common.utils.KLog
import top.sogrey.common.utils.logE
import top.sogrey.component.R

@Route(path = "/test/TestActivity")
class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        ImageUtils.setImage("https://www.baidu.com/img/baidu_jgylogo3.gif", imageView = imgTest)
//        ARouter.getInstance().inject(this)

//        CrashUtils()
        KLog.e("${this.javaClass.simpleName} cannot be instantiated")
    }
}
