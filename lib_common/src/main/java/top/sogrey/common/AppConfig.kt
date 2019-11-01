package top.sogrey.common

class AppConfig {

    companion object {
        /**
         * 是否debug
         */
        var DEBUG = BuildConfig.DEBUG

        /**
         * 应用tag
         */
        var APP_TAG = "AppTag"

        //SharedPreferences
        /**
         * SharedPreferences name
         */
        var SP_NAME = "AppName"
    }
}