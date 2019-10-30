package top.sogrey.common.utils.ktx

import android.content.Context
import android.content.SharedPreferences
import top.sogrey.common.AppConfig

//SP 相关
/**
 * 获取SharedPreferences对象
 */
fun Context.getSharedPreferences(): SharedPreferences =
    getSharedPreferences(AppConfig.SP_NAME, Context.MODE_PRIVATE)

/**
 * 获取SharedPreferences.Editor对象
 */
fun Context.getSharedPreferencesEditor(): SharedPreferences.Editor = getSharedPreferences().edit()

/**
 * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
 * @param key 键值
 * @param object value
 */
fun Context.setSP(key: String, `object`: Any?) {
    val editor = getSharedPreferencesEditor()

    if (`object` == null) {
        editor.putString(key, "null")
        return
    } else {
        when (`object`.javaClass.simpleName) {
            "String" -> editor.putString(key, `object` as String?)
            "Integer" -> editor.putInt(key, (`object` as Int?)!!)
            "Boolean" -> editor.putBoolean(key, (`object` as Boolean?)!!)
            "Float" -> editor.putFloat(key, (`object` as Float?)!!)
            "Long" -> editor.putLong(key, (`object` as Long?)!!)
        }
    }
    editor.apply()
}

/**
 * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
 * @param key 键值
 * @param defaultObject 默认值
 * @return
 */
fun Context.getSP(key: String, defaultObject: Any): Any? {
    val type = defaultObject.javaClass.simpleName
    val sp = getSharedPreferences()

    return when (type) {
        "String" -> sp.getString(key, defaultObject as String)
        "Integer" -> sp.getInt(key, defaultObject as Int)
        "Boolean" -> sp.getBoolean(key, defaultObject as Boolean)
        "Float" -> sp.getFloat(key, defaultObject as Float)
        "Long" -> sp.getLong(key, defaultObject as Long)
        else -> null
    }
}

/**
 * 查询所有SharedPreferences
 * @return  Map<String, ?>
 */
fun Context.getAllSP()=getSharedPreferences().all

/**
 * 判断是否存在 SharedPreferences
 * @param key 键值
 * @return true:包含，false:不包含
 */
fun Context.containsSP(key: String): Boolean = getSharedPreferences().contains(key)

/**
 * 移除SharedPreferences
 * @param key 键值
 */
fun Context.removeSP(key: String) = getSharedPreferencesEditor().remove(key).apply()

/**
 * 清空SharedPreferences
 */
fun Context.clear() = getSharedPreferencesEditor().clear().apply()