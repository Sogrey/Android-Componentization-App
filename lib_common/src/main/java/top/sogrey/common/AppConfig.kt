package top.sogrey.common

class AppConfig {
    constructor() {}

    companion object {
        /**
         * 是否debug
         */
        var DEBUG = BuildConfig.DEBUG

        /**
         * 应用tag
         */
        val APP_TAG = "AppTag"

        //SharedPreferences
        /**
         * SharedPreferences name
         */
        val SP_NAME = "AppName"
    }
}