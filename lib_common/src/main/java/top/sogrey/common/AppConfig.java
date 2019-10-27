package top.sogrey.common;

/**
 * 应用配置
 * @author Sogrey
 * @date 2019/10/26
 */
public class AppConfig {
    private AppConfig() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " cannot be instantiated");
    }
    /**
     * 是否debug
     */
    public static final boolean DEBUG = BuildConfig.DEBUG;

    /**
     * 应用tag
     */
    public static final String APP_TAG = "AppTag";

    //SharedPreferences
    /**
     * SharedPreferences name
     */
    public static final String SP_NAME = "AppName";
}
