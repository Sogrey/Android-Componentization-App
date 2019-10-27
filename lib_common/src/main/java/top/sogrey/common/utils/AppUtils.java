package top.sogrey.common.utils;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * App工具类
 * @author Sogrey
 * @date 2019/10/26
 */
public class AppUtils {
    private AppUtils() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " cannot be instantiated");
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
            KeyboardUtils.fixSoftInputLeaks(activity.getWindow());
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
                LogKtUtils.Companion.i("Utils", "setAnimatorsEnabled: Animators are enabled now!");
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 扩展方法
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 检测手机是否安装某个应用
     *
     * @param context
     * @param appPackageName 应用包名
     * @return true-安装，false-未安装
     */
    public static boolean isAppInstalled(Context context,String appPackageName){
        // 获取所有已安装程序的包信息
        List<ApplicationInfo> pinfos = context.getPackageManager().getInstalledApplications(0);
        if(!pinfos.isEmpty()){
            for (ApplicationInfo info:pinfos                 ) {
                String packageName = info.packageName;
                if(packageName.equals(appPackageName)){
                    return true;
                }
            }
        }
        return false;
    }
}
