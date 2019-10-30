package top.sogrey.common.utils

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection


/**
 * Service 服务相关
 * <p/>
 * @author Sogrey
 * @date 2019-10-30 22:15
 */

class ServiceUtils {
    constructor() {
        throw UnsupportedOperationException(this.javaClass.simpleName + " cannot be instantiated")
    }

    companion object {
        /**
         * Return all of the services are running.
         *
         * @return all of the services are running
         */
        fun getAllRunningServices(): Set<*>? {
            val am = AppUtils.getApp().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

            val info = am.getRunningServices(0x7FFFFFFF)
            val names = HashSet<String>()
            if (info == null || info.size == 0) return null
            for (aInfo in info) {
                names.add(aInfo.service.className)
            }
            return names
        }

        /**
         * Start the service.
         *
         * @param className The name of class.
         */
        fun startService(className: String) {
            try {
                startService(Class.forName(className))
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        /**
         * Start the service.
         *
         * @param cls The service class.
         */
        fun startService(cls: Class<*>) {
            val intent = Intent(AppUtils.getApp(), cls)
            AppUtils.getApp().startService(intent)
        }

        /**
         * Stop the service.
         *
         * @param className The name of class.
         * @return `true`: success<br></br>`false`: fail
         */
        fun stopService(className: String): Boolean {
            try {
                return stopService(Class.forName(className))
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }

        }

        /**
         * Stop the service.
         *
         * @param cls The name of class.
         * @return `true`: success<br></br>`false`: fail
         */
        fun stopService(cls: Class<*>): Boolean {
            val intent = Intent(AppUtils.getApp(), cls)
            return AppUtils.getApp().stopService(intent)
        }

        /**
         * Bind the service.
         *
         * @param className The name of class.
         * @param conn      The ServiceConnection object.
         * @param flags     Operation options for the binding.
         *
         *  * 0
         *  * [Context.BIND_AUTO_CREATE]
         *  * [Context.BIND_DEBUG_UNBIND]
         *  * [Context.BIND_NOT_FOREGROUND]
         *  * [Context.BIND_ABOVE_CLIENT]
         *  * [Context.BIND_ALLOW_OOM_MANAGEMENT]
         *  * [Context.BIND_WAIVE_PRIORITY]
         *
         */
        fun bindService(
            className: String,
            conn: ServiceConnection,
            flags: Int
        ) {
            try {
                bindService(Class.forName(className), conn, flags)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        /**
         * Bind the service.
         *
         * @param cls   The service class.
         * @param conn  The ServiceConnection object.
         * @param flags Operation options for the binding.
         *
         *  * 0
         *  * [Context.BIND_AUTO_CREATE]
         *  * [Context.BIND_DEBUG_UNBIND]
         *  * [Context.BIND_NOT_FOREGROUND]
         *  * [Context.BIND_ABOVE_CLIENT]
         *  * [Context.BIND_ALLOW_OOM_MANAGEMENT]
         *  * [Context.BIND_WAIVE_PRIORITY]
         *
         */
        fun bindService(
            cls: Class<*>,
            conn: ServiceConnection,
            flags: Int
        ) {
            val intent = Intent(AppUtils.getApp(), cls)
            AppUtils.getApp().bindService(intent, conn, flags)
        }

        /**
         * Unbind the service.
         *
         * @param conn The ServiceConnection object.
         */
        fun unbindService(conn: ServiceConnection) {
            AppUtils.getApp().unbindService(conn)
        }

        /**
         * Return whether service is running.
         *
         * @param cls The service class.
         * @return `true`: yes<br></br>`false`: no
         */
        fun isServiceRunning(cls: Class<*>): Boolean {
            return isServiceRunning(cls.name)
        }

        /**
         * Return whether service is running.
         *
         * @param className The name of class.
         * @return `true`: yes<br></br>`false`: no
         */
        fun isServiceRunning(className: String): Boolean {
            val am = AppUtils.getApp().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

            val info = am.getRunningServices(0x7FFFFFFF)
            if (info == null || info.size == 0) return false
            for (aInfo in info) {
                if (className == aInfo.service.className) return true
            }
            return false
        }
    }
}