package top.sogrey.componentization

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import top.sogrey.module_d.fragment.ModuleAFragment
import top.sogrey.module_d.fragment.ModuleBFragment
import top.sogrey.module_d.fragment.ModuleCFragment


class MainActivity : AppCompatActivity() , BottomNavigationView.OnNavigationItemSelectedListener{
    //定义碎片集合
    private val fragments: Array<Fragment?> = arrayOfNulls<Fragment>(3)

    //当前显示的fragment的索引位置
    private var currentIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initFragment()
        val navigation = findViewById<View>(R.id.navigation) as BottomNavigationView
        navigation.setOnNavigationItemSelectedListener(this)
    }

    /**
     * 初始化Fragment碎片
     */
    private fun initFragment() {
        if (fragments[0] == null) {
            fragments[0] = ModuleAFragment()
            fragments[0]?.let {
                supportFragmentManager.beginTransaction().add(R.id.content, it, "moduleA")
                    .commit()
            }
        } else {
            fragments[0]?.let { supportFragmentManager.beginTransaction().show(it) }
        }
    }

    /**
     * 导航选择事件
     * @param item
     * @return
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.navigation_a -> {
                if (currentIndex == 0) return true //如果已经是当前的fragment，不用切换
                val transition0: FragmentTransaction = supportFragmentManager.beginTransaction()
                hideAndShow(0, transition0)
                return true
            }
            R.id.navigation_b -> {
                if (currentIndex == 1) return true //如果已经是当前的fragment，不用切换
                val transition1: FragmentTransaction = supportFragmentManager.beginTransaction()
                if (fragments[1] == null) {
                    fragments[1] = ModuleBFragment()
                    fragments[1]?.let { transition1.add(R.id.content, it, "moduleB") }
                }
                hideAndShow(1, transition1)
                return true
            }
            R.id.navigation_c -> {
                if (currentIndex == 2) return true //如果已经是当前的fragment，不用切换
                val transition2: FragmentTransaction = supportFragmentManager.beginTransaction()
                if (fragments[2] == null) {
                    fragments[2] = ModuleCFragment()
                    fragments[2]?.let { transition2.add(R.id.content, it, "modulec") }
                }
                hideAndShow(2, transition2)
                return true
            }
            R.id.navigation_d -> {
                if (currentIndex == 3) return true //如果已经是当前的fragment，不用切换
                val transition3: FragmentTransaction = supportFragmentManager.beginTransaction()
                if (fragments[3] == null) {
                    fragments[3] = ModuleCFragment()
                    fragments[3]?.let { transition3.add(R.id.content, it, "moduled") }
                }
                hideAndShow(3, transition3)
                return true
            }
        }
        return false
    }

    /**
     * 除了指定的fragment不hide，其他fragment全hide
     * @param expectIndex 指定的fragment在fragments中的位置
     * @param transition
     */
    private fun hideAndShow(expectIndex: Int, transition: FragmentTransaction) {
        for (i in fragments.indices) {
            if (i != expectIndex && fragments[i] != null) {
                fragments[i]?.let { transition.hide(it) }
            }
        }
        fragments[expectIndex]?.let { transition.show(it) }
        transition.commit()
        currentIndex = expectIndex
    }
}