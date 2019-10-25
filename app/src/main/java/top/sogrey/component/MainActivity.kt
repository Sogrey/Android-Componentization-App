package top.sogrey.component

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import top.sogrey.common.utils.logE

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        logE("ddddd")

    }
}
