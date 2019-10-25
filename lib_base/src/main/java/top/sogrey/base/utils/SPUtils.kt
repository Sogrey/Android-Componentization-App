package top.sogrey.base.utils

import android.content.Context
import top.sogrey.base.Constants

//SP 相关


/**
 * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
 * @param key 键值
 * @param object value
 */
fun Context.setSP(key: String, `object`: Any?) {
    val sp = getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE)
    val editor = sp.edit()

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
    val sp = getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE)

    return when (type) {
        "String" -> sp.getString(key, defaultObject as String)
        "Integer" -> sp.getInt(key, defaultObject as Int)
        "Boolean" -> sp.getBoolean(key, defaultObject as Boolean)
        "Float" -> sp.getFloat(key, defaultObject as Float)
        "Long" -> sp.getLong(key, defaultObject as Long)
        else -> {
        }
    }
}

/**
 * 判断是否存在 SharedPreferences
 * @param key 键值
 * @return true:包含，false:不包含
 */
fun Context.containsSP(key:String):Boolean{
    val sp = getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE)
    return sp.contains(key)
}

/**
 * 移除SharedPreferences
 * @param key 键值
 */
fun Context.removeSP(key:String){
    val sp = getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE)
    sp.edit().remove(key).apply()
}
/**
 * 清空SharedPreferences
 */
fun Context.clear(){
    val sp = getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE)
    sp.edit().clear().apply()
}