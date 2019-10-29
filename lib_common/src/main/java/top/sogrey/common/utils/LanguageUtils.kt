package top.sogrey.common.utils

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.text.TextUtils
import top.sogrey.common.utils.ktx.getSP
import top.sogrey.common.utils.ktx.setSP
import java.util.*

/**
 * 语言相关
 * @author Sogrey
 * @date 2019/10/26
 */
class LanguageUtils {
    constructor() {
        throw UnsupportedOperationException(this.javaClass.simpleName + " cannot be instantiated")
    }

    companion object {
        private val KEY_LOCALE = "KEY_LOCALE"
        private val VALUE_FOLLOW_SYSTEM = "VALUE_FOLLOW_SYSTEM"

        /**
         * 应用系统语言
         * Apply the system language.
         *
         * @param activityClz The class of activity will be started after apply system language.
         */
        fun applySystemLanguage(activityClz: Class<out Activity>) {
            applyLanguage(Resources.getSystem().configuration.locale, activityClz, true)
        }

        /**
         * 应用系统语言
         * Apply the system language.
         *
         * @param activityClassName The full class name of activity will be started after apply system language.
         */
        fun applySystemLanguage(activityClassName: String) {
            applyLanguage(Resources.getSystem().configuration.locale, activityClassName, true)
        }

        /**
         * 应用语言
         * Apply the language.
         *
         * @param locale      The language of locale.
         * @param activityClz The class of activity will be started after apply system language.
         * It will start the launcher activity if the class is null.
         */
        fun applyLanguage(
            locale: Locale,
            activityClz: Class<out Activity>
        ) {
            applyLanguage(locale, activityClz, false)
        }

        /**
         * 应用语言
         * Apply the language.
         *
         * @param locale            The language of locale.
         * @param activityClassName The class of activity will be started after apply system language.
         * It will start the launcher activity if the class name is null.
         */
        fun applyLanguage(
            locale: Locale,
            activityClassName: String
        ) {
            applyLanguage(locale, activityClassName, false)
        }

        private fun applyLanguage(
            locale: Locale,
            activityClz: Class<out Activity>?,
            isFollowSystem: Boolean
        ) {
            if (activityClz == null) {
                applyLanguage(locale, "", isFollowSystem)
                return
            }
            applyLanguage(locale, activityClz.name, isFollowSystem)
        }

        private fun applyLanguage(
            locale: Locale,
            activityClassName: String,
            isFollowSystem: Boolean
        ) {
            if (isFollowSystem) {
                AppUtils.getApp().setSP(KEY_LOCALE, VALUE_FOLLOW_SYSTEM)
            } else {
                val localLanguage = locale.language
                val localCountry = locale.country
                AppUtils.getApp().setSP(KEY_LOCALE, "$localLanguage$$localCountry")
            }

            updateLanguage(AppUtils.getApp(), locale)

            val intent = Intent()
            val realActivityClassName =
                if (TextUtils.isEmpty(activityClassName)) getLauncherActivity() else activityClassName
            intent.component = ComponentName(AppUtils.getApp(), realActivityClassName)
            intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
            )
            AppUtils.getApp().startActivity(intent)
        }

        internal fun applyLanguage(activity: Activity) {
            val spLocale = activity.getSP(KEY_LOCALE, "") as String?
            if (TextUtils.isEmpty(spLocale)) {
                return
            }

            if (VALUE_FOLLOW_SYSTEM == spLocale) {
                val sysLocale = Resources.getSystem().configuration.locale
                updateLanguage(AppUtils.getApp(), sysLocale)
                updateLanguage(activity, sysLocale)
                return
            }

            val languageCountry =
                spLocale!!.split("\\$".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (languageCountry.size != 2) {
                LogKtUtils.e(
                    "LanguageUtils",
                    "The string of $spLocale is not in the correct format."
                )
                return
            }

            val settingLocale = Locale(languageCountry[0], languageCountry[1])
            updateLanguage(AppUtils.getApp(), settingLocale)
            updateLanguage(activity, settingLocale)
        }

        private fun updateLanguage(context: Context, locale: Locale) {
            val resources = context.resources
            val config = resources.configuration
            val contextLocale = config.locale
            if (equals(contextLocale.language, locale.language) && equals(
                    contextLocale.country,
                    locale.country
                )
            ) {
                return
            }
            val dm = resources.displayMetrics
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLocale(locale)
                context.createConfigurationContext(config)
            } else {
                config.locale = locale
            }
            resources.updateConfiguration(config, dm)
        }

        private fun equals(s1: CharSequence?, s2: CharSequence?): Boolean {
            if (s1 === s2) {
                return true
            }
            if (s1 != null && s2 != null) {
                val length: Int = s1.length
                if (length == s2.length) {
                    if (s1 is String && s2 is String) {
                        return s1 == s2
                    } else {
                        for (i in 0 until length) {
                            if (s1[i] != s2[i]) {
                                return false
                            }
                        }
                        return true
                    }
                }
            }
            return false
        }

        private fun getLauncherActivity(): String {
            val intent = Intent(Intent.ACTION_MAIN, null)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.setPackage(AppUtils.getApp().packageName)
            val pm = AppUtils.getApp().packageManager
            val info = pm.queryIntentActivities(intent, 0)
            val next = info.iterator().next()
            return if (next != null) {
                next.activityInfo.name
            } else "no launcher activity"
        }
    }
}