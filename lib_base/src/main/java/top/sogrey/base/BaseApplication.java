package top.sogrey.base;

import android.app.Application;

import top.sogrey.base.utils.AppUtils;

/**
 * @author Sogrey
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppUtils.init(this);
    }
}
