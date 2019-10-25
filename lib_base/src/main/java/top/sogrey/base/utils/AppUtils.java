package top.sogrey.base.utils;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.core.content.FileProvider;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Sogrey
 */
public class AppUtils {
    private AppUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    private static final ActivityLifecycleImpl ACTIVITY_LIFECYCLE = new ActivityLifecycleImpl();
    @SuppressLint("StaticFieldLeak")
    private static Application sApplication;

    /**
     * Init utils.
     * <p>Init it in the class of Application.</p>
     *
     * @param context context
     */
    public static void init(final Context context) {
        if (context == null) {
            init(getApplicationByReflect());
            return;
        }
        init((Application) context.getApplicationContext());
    }

    /**
     * Init utils.
     * <p>Init it in the class of Application.</p>
     *
     * @param app application
     */
    public static void init(final Application app) {
        if (sApplication == null) {
            if (app == null) {
                sApplication = getApplicationByReflect();
            } else {
                sApplication = app;
            }
            sApplication.registerActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE);
        } else {
            if (app != null && app.getClass() != sApplication.getClass()) {
                sApplication.unregisterActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE);
                ACTIVITY_LIFECYCLE.mActivityList.clear();
                sApplication = app;
                sApplication.registerActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE);
            }
        }
    }

    /**
     * Return the context of Application object.
     *
     * @return the context of Application object
     */
    public static Application getApp() {
        if (sApplication != null) {
            return sApplication;
        }
        Application app = getApplicationByReflect();
        init(app);
        return app;
    }

    private static Application getApplicationByReflect() {
        try {
            @SuppressLint("PrivateApi")
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object thread = activityThread.getMethod("currentActivityThread").invoke(null);
            Object app = activityThread.getMethod("getApplication").invoke(thread);
            if (app == null) {
                throw new NullPointerException("u should init first");
            }
            return (Application) app;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("u should init first");
    }


    static class ActivityLifecycleImpl implements Application.ActivityLifecycleCallbacks {

        final LinkedList<Activity> mActivityList = new LinkedList<>();
        final Map<Object, OnAppStatusChangedListener> mStatusListenerMap = new HashMap<>();
        final Map<Activity, Set<OnActivityDestroyedListener>> mDestroyedListenerMap = new HashMap<>();

        private int mForegroundCount = 0;
        private int mConfigCount = 0;
        private boolean mIsBackground = false;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            LanguageUtils.applyLanguage(activity);
            setAnimatorsEnabled();
            setTopActivity(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (!mIsBackground) {
                setTopActivity(activity);
            }
            if (mConfigCount < 0) {
                ++mConfigCount;
            } else {
                ++mForegroundCount;
            }
        }

        @Override
        public void onActivityResumed(final Activity activity) {
            setTopActivity(activity);
            if (mIsBackground) {
                mIsBackground = false;
                postStatus(true);
            }
            processHideSoftInputOnActivityDestroy(activity, false);
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            if (activity.isChangingConfigurations()) {
                --mConfigCount;
            } else {
                --mForegroundCount;
                if (mForegroundCount <= 0) {
                    mIsBackground = true;
                    postStatus(false);
                }
            }
            processHideSoftInputOnActivityDestroy(activity, true);
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {/**/}

        @Override
        public void onActivityDestroyed(Activity activity) {
            mActivityList.remove(activity);
            consumeOnActivityDestroyedListener(activity);
            fixSoftInputLeaks(activity.getWindow());
        }

        Activity getTopActivity() {
            if (!mActivityList.isEmpty()) {
                for (int i = mActivityList.size() - 1; i >= 0; i--) {
                    Activity activity = mActivityList.get(i);
                    if (activity == null
                            || activity.isFinishing()
                            || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed())) {
                        continue;
                    }
                    return activity;
                }
            }
            Activity topActivityByReflect = getTopActivityByReflect();
            if (topActivityByReflect != null) {
                setTopActivity(topActivityByReflect);
            }
            return topActivityByReflect;
        }

        void addOnAppStatusChangedListener(final Object object,
                                           final OnAppStatusChangedListener listener) {
            mStatusListenerMap.put(object, listener);
        }

        void removeOnAppStatusChangedListener(final Object object) {
            mStatusListenerMap.remove(object);
        }

        void removeOnActivityDestroyedListener(final Activity activity) {
            if (activity == null) {
                return;
            }
            mDestroyedListenerMap.remove(activity);
        }

        void addOnActivityDestroyedListener(final Activity activity,
                                            final OnActivityDestroyedListener listener) {
            if (activity == null || listener == null) {
                return;
            }
            Set<OnActivityDestroyedListener> listeners;
            if (!mDestroyedListenerMap.containsKey(activity)) {
                listeners = new HashSet<>();
                mDestroyedListenerMap.put(activity, listeners);
            } else {
                listeners = mDestroyedListenerMap.get(activity);
                if (listeners.contains(listener)) {
                    return;
                }
            }
            listeners.add(listener);
        }

        /**
         * To solve close keyboard when activity onDestroy.
         * The preActivity set windowSoftInputMode will prevent
         * the keyboard from closing when curActivity onDestroy.
         */
        private void processHideSoftInputOnActivityDestroy(final Activity activity, boolean isSave) {
            if (isSave) {
                final WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
                final int softInputMode = attrs.softInputMode;
                activity.getWindow().getDecorView().setTag(-123, softInputMode);
                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            } else {
                final Object tag = activity.getWindow().getDecorView().getTag(-123);
                if (!(tag instanceof Integer)) {
                    return;
                }
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activity.getWindow().setSoftInputMode(((Integer) tag));
                    }
                }, 100);
            }
        }

        private void postStatus(final boolean isForeground) {
            if (mStatusListenerMap.isEmpty()) {
                return;
            }
            for (OnAppStatusChangedListener onAppStatusChangedListener : mStatusListenerMap.values()) {
                if (onAppStatusChangedListener == null) {
                    return;
                }
                if (isForeground) {
                    onAppStatusChangedListener.onForeground();
                } else {
                    onAppStatusChangedListener.onBackground();
                }
            }
        }

        //        private static final String PERMISSION_ACTIVITY_CLASS_NAME =
//                "com.blankj.utilcode.util.PermissionUtils$PermissionActivity";
        private void setTopActivity(final Activity activity) {
//            if (PERMISSION_ACTIVITY_CLASS_NAME.equals(activity.getClass().getName())) {
//                return;
//            }
            if (mActivityList.contains(activity)) {
                if (!mActivityList.getLast().equals(activity)) {
                    mActivityList.remove(activity);
                    mActivityList.addLast(activity);
                }
            } else {
                mActivityList.addLast(activity);
            }
        }

        private void consumeOnActivityDestroyedListener(Activity activity) {
            Iterator<Map.Entry<Activity, Set<OnActivityDestroyedListener>>> iterator
                    = mDestroyedListenerMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Activity, Set<OnActivityDestroyedListener>> entry = iterator.next();
                if (entry.getKey() == activity) {
                    Set<OnActivityDestroyedListener> value = entry.getValue();
                    for (OnActivityDestroyedListener listener : value) {
                        listener.onActivityDestroyed(activity);
                    }
                    iterator.remove();
                }
            }
        }

        private Activity getTopActivityByReflect() {
            try {
                @SuppressLint("PrivateApi")
                Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
                Object currentActivityThreadMethod = activityThreadClass.getMethod("currentActivityThread").invoke(null);
                Field mActivityListField = activityThreadClass.getDeclaredField("mActivityList");
                mActivityListField.setAccessible(true);
                Map activities = (Map) mActivityListField.get(currentActivityThreadMethod);
                if (activities == null) {
                    return null;
                }
                for (Object activityRecord : activities.values()) {
                    Class activityRecordClass = activityRecord.getClass();
                    Field pausedField = activityRecordClass.getDeclaredField("paused");
                    pausedField.setAccessible(true);
                    if (!pausedField.getBoolean(activityRecord)) {
                        Field activityField = activityRecordClass.getDeclaredField("activity");
                        activityField.setAccessible(true);
                        return (Activity) activityField.get(activityRecord);
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Set animators enabled.
     */
    private static void setAnimatorsEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && ValueAnimator.areAnimatorsEnabled()) {
            return;
        }
        try {
            //noinspection JavaReflectionMemberAccess
            Field sDurationScaleField = ValueAnimator.class.getDeclaredField("sDurationScale");
            sDurationScaleField.setAccessible(true);
            float sDurationScale = (Float) sDurationScaleField.get(null);
            if (sDurationScale == 0f) {
                sDurationScaleField.set(null, 1f);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static void fixSoftInputLeaks(final Window window) {
        InputMethodManager imm =
                (InputMethodManager) getApp().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        String[] leakViews = new String[]{"mLastSrvView", "mCurRootView", "mServedView", "mNextServedView"};
        for (String leakView : leakViews) {
            try {
                Field leakViewField = InputMethodManager.class.getDeclaredField(leakView);
                if (leakViewField == null) {
                    continue;
                }
                if (!leakViewField.isAccessible()) {
                    leakViewField.setAccessible(true);
                }
                Object obj = leakViewField.get(imm);
                if (!(obj instanceof View)) {
                    continue;
                }
                View view = (View) obj;
                if (view.getRootView() == window.getDecorView().getRootView()) {
                    leakViewField.set(imm, null);
                }
            } catch (Throwable ignore) {/**/}
        }
    }

    public static final class FileProvider4UtilCode extends FileProvider {

        @Override
        public boolean onCreate() {
            init(getContext());
            return true;
        }
    }

    /**
     * 应用状态监测
     */
    public interface OnAppStatusChangedListener {
        /**
         * 在前台
         */
        void onForeground();

        /**
         * 退至后台
         */
        void onBackground();
    }

    /**
     * Activity销毁监听
     */
    public interface OnActivityDestroyedListener {
        /**
         * Activity销毁事件
         *
         * @param activity 被销毁的 Activity
         */
        void onActivityDestroyed(Activity activity);
    }

    //////////////////////////////////////////////////////////////////////////////
    //
    //////////////////////////////////////////////////////////////////////////////

//    /**
//     * 判断App是否安装
//     *
//     * @param context     上下文
//     * @param packageName 包名
//     * @return {@code true}: 已安装<br>{@code false}: 未安装
//     */
//    public static boolean isInstallApp(Context context, String packageName) {
//        return !StringUtils.isSpace(packageName) && IntentUtils.getLaunchAppIntent(context, packageName) != null;
//    }
//
//    /**
//     * 安装App(支持6.0)
//     *
//     * @param context  上下文
//     * @param filePath 文件路径
//     */
//    public static void installApp(Context context, String filePath) {
//        installApp(context, FileUtils.getFileByPath(filePath));
//    }
//
//    /**
//     * 安装App（支持6.0）
//     *
//     * @param context 上下文
//     * @param file    文件
//     */
//    public static void installApp(Context context, File file) {
//        if (!FileUtils.isFileExists(file)) {
//            return;
//        }
//        context.startActivity(IntentUtils.getInstallAppIntent(file));
//    }
//
//    /**
//     * 安装App（支持6.0）
//     *
//     * @param activity    activity
//     * @param filePath    文件路径
//     * @param requestCode 请求值
//     */
//    public static void installApp(Activity activity, String filePath, int requestCode) {
//        installApp(activity, FileUtils.getFileByPath(filePath), requestCode);
//    }
//
//    /**
//     * 安装App(支持6.0)
//     *
//     * @param activity    activity
//     * @param file        文件
//     * @param requestCode 请求值
//     */
//    public static void installApp(Activity activity, File file, int requestCode) {
//        if (!FileUtils.isFileExists(file)) {
//            return;
//        }
//        activity.startActivityForResult(IntentUtils.getInstallAppIntent(file), requestCode);
//    }
//
//    /**
//     * 静默安装App
//     * <p>非root需添加权限 {@code <uses-permission android:name="android.permission.INSTALL_PACKAGES" />}</p>
//     *
//     * @param context  上下文
//     * @param filePath 文件路径
//     * @return {@code true}: 安装成功<br>{@code false}: 安装失败
//     */
//    public static boolean installAppSilent(Context context, String filePath) {
//        File file = FileUtils.getFileByPath(filePath);
//        if (!FileUtils.isFileExists(file)) {
//            return false;
//        }
//        String command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install " + filePath;
//        ShellUtils.CommandResult commandResult = ShellUtils.execCmd(command, !isSystemApp(context), true);
//        return commandResult.successMsg != null && commandResult.successMsg.toLowerCase().contains("success");
//    }
//
//    /**
//     * 卸载App
//     *
//     * @param context     上下文
//     * @param packageName 包名
//     */
//    public static void uninstallApp(Context context, String packageName) {
//        if (StringUtils.isSpace(packageName)) {
//            return;
//        }
//        context.startActivity(IntentUtils.getUninstallAppIntent(packageName));
//    }
//
//    /**
//     * 卸载App
//     *
//     * @param activity    activity
//     * @param packageName 包名
//     * @param requestCode 请求值
//     */
//    public static void uninstallApp(Activity activity, String packageName, int requestCode) {
//        if (StringUtils.isSpace(packageName)) {
//            return;
//        }
//        activity.startActivityForResult(IntentUtils.getUninstallAppIntent(packageName), requestCode);
//    }
//
//    /**
//     * 静默卸载App
//     * <p>非root需添加权限 {@code <uses-permission android:name="android.permission.DELETE_PACKAGES" />}</p>
//     *
//     * @param context     上下文
//     * @param packageName 包名
//     * @param isKeepData  是否保留数据
//     * @return {@code true}: 卸载成功<br>{@code false}: 卸载成功
//     */
//    public static boolean uninstallAppSilent(Context context, String packageName, boolean isKeepData) {
//        if (StringUtils.isSpace(packageName)) {
//            return false;
//        }
//        String command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib pm uninstall " + (isKeepData ? "-k " : "") + packageName;
//        ShellUtils.CommandResult commandResult = ShellUtils.execCmd(command, !isSystemApp(context), true);
//        return commandResult.successMsg != null && commandResult.successMsg.toLowerCase().contains("success");
//    }
//
//
//    /**
//     * 判断App是否有root权限
//     *
//     * @return {@code true}: 是<br>{@code false}: 否
//     */
//    public static boolean isAppRoot() {
//        ShellUtils.CommandResult result = ShellUtils.execCmd("echo root", true);
//        if (result.result == 0) {
//            return true;
//        }
//        if (result.errorMsg != null) {
//            LogUtils.d("isAppRoot", result.errorMsg);
//        }
//        return false;
//    }
//
//    /**
//     * 打开App
//     *
//     * @param context     上下文
//     * @param packageName 包名
//     */
//    public static void launchApp(Context context, String packageName) {
//        if (StringUtils.isSpace(packageName)) {
//            return;
//        }
//        context.startActivity(IntentUtils.getLaunchAppIntent(context, packageName));
//    }
//
//    /**
//     * 打开App
//     *
//     * @param activity    activity
//     * @param packageName 包名
//     * @param requestCode 请求值
//     */
//    public static void launchApp(Activity activity, String packageName, int requestCode) {
//        if (StringUtils.isSpace(packageName)) {
//            return;
//        }
//        activity.startActivityForResult(IntentUtils.getLaunchAppIntent(activity, packageName), requestCode);
//    }
//
//    /**
//     * 获取App包名
//     *
//     * @param context 上下文
//     * @return App包名
//     */
//    public static String getAppPackageName(Context context) {
//        return context.getPackageName();
//    }
//
//    /**
//     * 获取App具体设置
//     *
//     * @param context 上下文
//     */
//    public static void getAppDetailsSettings(Context context) {
//        getAppDetailsSettings(context, context.getPackageName());
//    }
//
//    /**
//     * 获取App具体设置
//     *
//     * @param context     上下文
//     * @param packageName 包名
//     */
//    public static void getAppDetailsSettings(Context context, String packageName) {
//        if (StringUtils.isSpace(packageName)) {
//            return;
//        }
//        context.startActivity(IntentUtils.getAppDetailsSettingsIntent(packageName));
//    }
//
//    /**
//     * 获取App名称
//     *
//     * @param context 上下文
//     * @return App名称
//     */
//    public static String getAppName(Context context) {
//        return getAppName(context, context.getPackageName());
//    }
//
//    /**
//     * 获取App名称
//     *
//     * @param context     上下文
//     * @param packageName 包名
//     * @return App名称
//     */
//    public static String getAppName(Context context, String packageName) {
//        if (StringUtils.isSpace(packageName)) {
//            return null;
//        }
//        try {
//            PackageManager pm = context.getPackageManager();
//            PackageInfo pi = pm.getPackageInfo(packageName, 0);
//            return pi == null ? null : pi.applicationInfo.loadLabel(pm).toString();
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    /**
//     * 获取App图标
//     *
//     * @param context 上下文
//     * @return App图标
//     */
//    public static Drawable getAppIcon(Context context) {
//        return getAppIcon(context, context.getPackageName());
//    }
//
//    /**
//     * 获取App图标
//     *
//     * @param context     上下文
//     * @param packageName 包名
//     * @return App图标
//     */
//    public static Drawable getAppIcon(Context context, String packageName) {
//        if (StringUtils.isSpace(packageName)) {
//            return null;
//        }
//        try {
//            PackageManager pm = context.getPackageManager();
//            PackageInfo pi = pm.getPackageInfo(packageName, 0);
//            return pi == null ? null : pi.applicationInfo.loadIcon(pm);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    /**
//     * 获取App路径
//     *
//     * @param context 上下文
//     * @return App路径
//     */
//    public static String getAppPath(Context context) {
//        return getAppPath(context, context.getPackageName());
//    }
//
//    /**
//     * 获取App路径
//     *
//     * @param context     上下文
//     * @param packageName 包名
//     * @return App路径
//     */
//    public static String getAppPath(Context context, String packageName) {
//        if (StringUtils.isSpace(packageName)) {
//            return null;
//        }
//        try {
//            PackageManager pm = context.getPackageManager();
//            PackageInfo pi = pm.getPackageInfo(packageName, 0);
//            return pi == null ? null : pi.applicationInfo.sourceDir;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    /**
//     * 获取App版本号
//     *
//     * @param context 上下文
//     * @return App版本号
//     */
//    public static String getAppVersionName(Context context) {
//        return getAppVersionName(context, context.getPackageName());
//    }
//
//    /**
//     * 获取App版本号
//     *
//     * @param context     上下文
//     * @param packageName 包名
//     * @return App版本号
//     */
//    public static String getAppVersionName(Context context, String packageName) {
//        if (StringUtils.isSpace(packageName)) {
//            return null;
//        }
//        try {
//            PackageManager pm = context.getPackageManager();
//            PackageInfo pi = pm.getPackageInfo(packageName, 0);
//            return pi == null ? null : pi.versionName;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    /**
//     * 获取App版本码
//     *
//     * @param context 上下文
//     * @return App版本码
//     */
//    public static int getAppVersionCode(Context context) {
//        return getAppVersionCode(context, context.getPackageName());
//    }
//
//    /**
//     * 获取App版本码
//     *
//     * @param context     上下文
//     * @param packageName 包名
//     * @return App版本码
//     */
//    public static int getAppVersionCode(Context context, String packageName) {
//        if (StringUtils.isSpace(packageName)) {
//            return -1;
//        }
//        try {
//            PackageManager pm = context.getPackageManager();
//            PackageInfo pi = pm.getPackageInfo(packageName, 0);
//            return pi == null ? -1 : pi.versionCode;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//            return -1;
//        }
//    }
//
//    /**
//     * 判断App是否是系统应用
//     *
//     * @param context 上下文
//     * @return {@code true}: 是<br>{@code false}: 否
//     */
//    public static boolean isSystemApp(Context context) {
//        return isSystemApp(context, context.getPackageName());
//    }
//
//    /**
//     * 判断App是否是系统应用
//     *
//     * @param context     上下文
//     * @param packageName 包名
//     * @return {@code true}: 是<br>{@code false}: 否
//     */
//    public static boolean isSystemApp(Context context, String packageName) {
//        if (StringUtils.isSpace(packageName)) {
//            return false;
//        }
//        try {
//            PackageManager pm = context.getPackageManager();
//            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
//            return ai != null && (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    /**
//     * 判断App是否是Debug版本
//     *
//     * @param context 上下文
//     * @return {@code true}: 是<br>{@code false}: 否
//     */
//    public static boolean isAppDebug(Context context) {
//        return isAppDebug(context, context.getPackageName());
//    }
//
//    /**
//     * 判断App是否是Debug版本
//     *
//     * @param context     上下文
//     * @param packageName 包名
//     * @return {@code true}: 是<br>{@code false}: 否
//     */
//    public static boolean isAppDebug(Context context, String packageName) {
//        if (StringUtils.isSpace(packageName)) {
//            return false;
//        }
//        try {
//            PackageManager pm = context.getPackageManager();
//            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
//            return ai != null && (ai.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    /**
//     * 获取App签名
//     *
//     * @param context 上下文
//     * @return App签名
//     */
//    public static Signature[] getAppSignature(Context context) {
//        return getAppSignature(context, context.getPackageName());
//    }
//
//    /**
//     * 获取App签名
//     *
//     * @param context     上下文
//     * @param packageName 包名
//     * @return App签名
//     */
//    @SuppressLint("PackageManagerGetSignatures")
//    public static Signature[] getAppSignature(Context context, String packageName) {
//        if (StringUtils.isSpace(packageName)) {
//            return null;
//        }
//        try {
//            PackageManager pm = context.getPackageManager();
//            PackageInfo pi = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
//            return pi == null ? null : pi.signatures;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    /**
//     * 获取应用签名的的SHA1值
//     * <p>可据此判断高德，百度地图key是否正确</p>
//     *
//     * @param context 上下文
//     * @return 应用签名的SHA1字符串, 比如：53:FD:54:DC:19:0F:11:AC:B5:22:9E:F1:1A:68:88:1B:8B:E8:54:42
//     */
//    public static String getAppSignatureSHA1(Context context) {
//        return getAppSignatureSHA1(context, context.getPackageName());
//    }
//
//    /**
//     * 获取应用签名的的SHA1值
//     * <p>可据此判断高德，百度地图key是否正确</p>
//     *
//     * @param context     上下文
//     * @param packageName 包名
//     * @return 应用签名的SHA1字符串, 比如：53:FD:54:DC:19:0F:11:AC:B5:22:9E:F1:1A:68:88:1B:8B:E8:54:42
//     */
//    public static String getAppSignatureSHA1(Context context, String packageName) {
//        Signature[] signature = getAppSignature(context, packageName);
//        if (signature == null) {
//            return null;
//        }
//        return EncryptUtils.encryptSHA1ToString(signature[0].toByteArray()).
//                replaceAll("(?<=[0-9A-F]{2})[0-9A-F]{2}", ":$0");
//    }
//
//    /**
//     * 判断App是否处于前台
//     *
//     * @param context 上下文
//     * @return {@code true}: 是<br>{@code false}: 否
//     */
//    public static boolean isAppForeground(Context context) {
//        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        assert manager != null;
//        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
//        if (infos == null || infos.size() == 0) {
//            return false;
//        }
//        for (ActivityManager.RunningAppProcessInfo info : infos) {
//            if (info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//                return info.processName.equals(context.getPackageName());
//            }
//        }
//        return false;
//    }
//
//    /**
//     * 判断App是否处于前台
//     * <p>当不是查看当前App，且SDK大于21时，
//     * 需添加权限 {@code <uses-permission android:name="android.permission.PACKAGE_USAGE_STATS"/>}</p>
//     *
//     * @param context     上下文
//     * @param packageName 包名
//     * @return {@code true}: 是<br>{@code false}: 否
//     */
//    public static boolean isAppForeground(Context context, String packageName) {
//        return !StringUtils.isSpace(packageName) && packageName.equals(ProcessUtils.getForegroundProcessName(context));
//    }
//
//
//    /**
//     * 封装App信息的Bean类
//     */
//    public static class AppInfo {
//
//        private String name;
//        private Drawable icon;
//        private String packageName;
//        private String packagePath;
//        private String versionName;
//        private int versionCode;
//        private boolean isSystem;
//
//        public Drawable getIcon() {
//            return icon;
//        }
//
//        public void setIcon(Drawable icon) {
//            this.icon = icon;
//        }
//
//        public boolean isSystem() {
//            return isSystem;
//        }
//
//        public void setSystem(boolean isSystem) {
//            this.isSystem = isSystem;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public String getPackageName() {
//            return packageName;
//        }
//
//        public void setPackageName(String packagName) {
//            this.packageName = packagName;
//        }
//
//        public String getPackagePath() {
//            return packagePath;
//        }
//
//        public void setPackagePath(String packagePath) {
//            this.packagePath = packagePath;
//        }
//
//        public int getVersionCode() {
//            return versionCode;
//        }
//
//        public void setVersionCode(int versionCode) {
//            this.versionCode = versionCode;
//        }
//
//        public String getVersionName() {
//            return versionName;
//        }
//
//        public void setVersionName(String versionName) {
//            this.versionName = versionName;
//        }
//
//        /**
//         * @param name        名称
//         * @param icon        图标
//         * @param packageName 包名
//         * @param packagePath 包路径
//         * @param versionName 版本号
//         * @param versionCode 版本码
//         * @param isSystem    是否系统应用
//         */
//        public AppInfo(String packageName, String name, Drawable icon, String packagePath,
//                       String versionName, int versionCode, boolean isSystem) {
//            this.setName(name);
//            this.setIcon(icon);
//            this.setPackageName(packageName);
//            this.setPackagePath(packagePath);
//            this.setVersionName(versionName);
//            this.setVersionCode(versionCode);
//            this.setSystem(isSystem);
//        }
//
//        @Override
//        public String toString() {
//            return "App包名：" + getPackageName() +
//                    "\nApp名称：" + getName() +
//                    "\nApp图标：" + getIcon() +
//                    "\nApp路径：" + getPackagePath() +
//                    "\nApp版本号：" + getVersionName() +
//                    "\nApp版本码：" + getVersionCode() +
//                    "\n是否系统App：" + isSystem();
//        }
//    }
//
//    /**
//     * 获取App信息
//     * <p>AppInfo（名称，图标，包名，版本号，版本Code，是否系统应用）</p>
//     *
//     * @param context 上下文
//     * @return 当前应用的AppInfo
//     */
//    public static AppInfo getAppInfo(Context context) {
//        return getAppInfo(context, context.getPackageName());
//    }
//
//    /**
//     * 获取App信息
//     * <p>AppInfo（名称，图标，包名，版本号，版本Code，是否系统应用）</p>
//     *
//     * @param context     上下文
//     * @param packageName 包名
//     * @return 当前应用的AppInfo
//     */
//    public static AppInfo getAppInfo(Context context, String packageName) {
//        try {
//            PackageManager pm = context.getPackageManager();
//            PackageInfo pi = pm.getPackageInfo(packageName, 0);
//            return getBean(pm, pi);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    /**
//     * 得到AppInfo的Bean
//     *
//     * @param pm 包的管理
//     * @param pi 包的信息
//     * @return AppInfo类
//     */
//    private static AppInfo getBean(PackageManager pm, PackageInfo pi) {
//        if (pm == null || pi == null) {
//            return null;
//        }
//        ApplicationInfo ai = pi.applicationInfo;
//        String packageName = pi.packageName;
//        String name = ai.loadLabel(pm).toString();
//        Drawable icon = ai.loadIcon(pm);
//        String packagePath = ai.sourceDir;
//        String versionName = pi.versionName;
//        int versionCode = pi.versionCode;
//        boolean isSystem = (ApplicationInfo.FLAG_SYSTEM & ai.flags) != 0;
//        return new AppInfo(packageName, name, icon, packagePath, versionName, versionCode, isSystem);
//    }
//
//    /**
//     * 获取所有已安装App信息
//     * <p>{@link #getBean(PackageManager, PackageInfo)}（名称，图标，包名，包路径，版本号，版本Code，是否系统应用）</p>
//     * <p>依赖上面的getBean方法</p>
//     *
//     * @param context 上下文
//     * @return 所有已安装的AppInfo列表
//     */
//    public static List<AppInfo> getAppsInfo(Context context) {
//        List<AppInfo> list = new ArrayList<>();
//        PackageManager pm = context.getPackageManager();
//        // 获取系统中安装的所有软件信息
//        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
//        for (PackageInfo pi : installedPackages) {
//            AppInfo ai = getBean(pm, pi);
//            if (ai == null) {
//                continue;
//            }
//            list.add(ai);
//        }
//        return list;
//    }
//
////    /**
////     * 清除App所有数据
////     *
////     * @param context  上下文
////     * @param dirPaths 目录路径
////     * @return {@code true}: 成功<br>{@code false}: 失败
////     */
////    public static boolean cleanAppData(Context context, String... dirPaths) {
////        File[] dirs = new File[dirPaths.length];
////        int i = 0;
////        for (String dirPath : dirPaths) {
////            dirs[i++] = new File(dirPath);
////        }
////        return cleanAppData(context, dirs);
////    }
//
////    /**
////     * 清除App所有数据
////     *
////     * @param context 上下文
////     * @param dirs    目录
////     * @return {@code true}: 成功<br>{@code false}: 失败
////     */
////    public static boolean cleanAppData(Context context, File... dirs) {
////        boolean isSuccess = CleanUtils.cleanInternalCache(context);
////        isSuccess &= CleanUtils.cleanInternalDbs(context);
////        isSuccess &= CleanUtils.cleanInternalSP(context);
////        isSuccess &= CleanUtils.cleanInternalFiles(context);
////        isSuccess &= CleanUtils.cleanExternalCache(context);
////        for (File dir : dirs) {
////            isSuccess &= CleanUtils.cleanCustomCache(dir);
////        }
////        return isSuccess;
////    }
}
