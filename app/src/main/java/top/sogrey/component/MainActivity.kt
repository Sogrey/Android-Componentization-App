package top.sogrey.component

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.launcher.ARouter
import kotlinx.android.synthetic.main.activity_main.*
import top.sogrey.common.utils.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ARouter.getInstance().inject(this)

        button.setOnClickListener {
            //            ARouter.getInstance().build("/test/TestActivity").navigation()
            startIntent(top.sogrey.component.test.TestActivity::class.java)

            ToastUtils.show("open TestActivity")
        }
        button2.setOnClickListener {
            ARouter.getInstance().build("/center/CenterMainActivity").navigation()
//            startIntent(top.sogrey.component.center.CenterMainActivity::class.java)

            ToastUtils.show("open /center/CenterMainActivity")
        }
    }
}
