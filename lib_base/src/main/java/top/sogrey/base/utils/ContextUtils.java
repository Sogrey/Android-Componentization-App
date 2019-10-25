package top.sogrey.base.utils;

import android.content.Context;

import top.sogrey.base.BaseApplication;

/**
 * @author Sogrey
 */
public class ContextUtils {
    private ContextUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }
    public static Context getContext(){
        return AppUtils.getApp().getApplicationContext();
    }


}
