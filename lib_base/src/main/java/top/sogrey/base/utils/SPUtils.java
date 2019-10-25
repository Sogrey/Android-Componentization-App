package top.sogrey.base.utils;

import android.content.Context;
import android.content.SharedPreferences;

import top.sogrey.base.Constants;

/**
 * @author Sogrey
 */
public class SPUtils {
    private SPUtils() {
    }

    private static SPUtils instance = new SPUtils();
    public static SPUtils getInstance(){
        return instance;
    }

    private SharedPreferences getSharedPreferences() {
        return AppUtils.getApp().getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getSharedPreferencesEdit() {
        return getSharedPreferences().edit();
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param key   键值
     * @param value value
     */
    public void put(String key, Object value) {
        SharedPreferences.Editor editor = getSharedPreferencesEdit();
        if (value != null) {
            editor.putString(key, "null");
        } else {
            switch (value.getClass().getSimpleName()) {
                case "String":
                    editor.putString(key, String.valueOf(value));
                    break;
                case "Integer":
                    editor.putInt(key, (int) value);
                    break;
                case "Boolean":
                    editor.putBoolean(key, (boolean) value);
                    break;
                case "Float":
                    editor.putFloat(key, (float) value);
                    break;
                case "Long":
                    editor.putLong(key, (long) value);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported Data Type...");
            }
        }
        editor.apply();
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param key          键值
     * @param defaultValue 默认值
     * @return
     */
    public Object get(String key, Object defaultValue) {
        SharedPreferences sp = getSharedPreferences();
        switch (defaultValue.getClass().getSimpleName()) {
            case "String":
                return sp.getString(key, String.valueOf(defaultValue));
            case "Integer":
                return sp.getInt(key, (int) defaultValue);
            case "Boolean":
                return sp.getBoolean(key, (boolean) defaultValue);
            case "Float":
                return sp.getFloat(key, (float) defaultValue);
            case "Long":
                return sp.getLong(key, (long) defaultValue);
            default:
                throw new UnsupportedOperationException("Unsupported Data Type...");
        }
    }

    /**
     * 判断是否存在 SharedPreferences
     *
     * @param key 键值
     * @return true:包含，false:不包含
     */
    public boolean contains(String key) {
        SharedPreferences sp = getSharedPreferences();
        return sp.contains(key);
    }

    /**
     * 移除SharedPreferences
     *
     * @param key 键值
     */
    public void remove(String key) {
        SharedPreferences.Editor editor = getSharedPreferencesEdit();
        editor.remove(key).apply();
    }

    /**
     * 清空SharedPreferences
     */
    public void clear() {
        SharedPreferences.Editor editor = getSharedPreferencesEdit();
        editor.clear().apply();
    }
}
