package top.sogrey.common.utils

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresPermission
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.net.UnknownHostException
import java.util.*
import android.text.format.Formatter
import kotlin.collections.HashSet


/**
 * 网络相关
 * @author Sogrey
 * @date 2019/10/30
 */
class NetworkUtils {
    constructor(){
        throw UnsupportedOperationException(this.javaClass.simpleName + " cannot be instantiated")
    }companion object{

        enum class NetworkType {
            NETWORK_ETHERNET,
            NETWORK_WIFI,
            NETWORK_4G,
            NETWORK_3G,
            NETWORK_2G,
            NETWORK_UNKNOWN,
            NETWORK_NO
        }

        /**
         * Open the settings of wireless.
         */
        fun openWirelessSettings() {
            AppUtils.getApp().startActivity(
                Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }

        /**
         * Return whether network is connected.
         *
         * Must hold `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
         *
         * @return `true`: connected<br></br>`false`: disconnected
         */
        @RequiresPermission(ACCESS_NETWORK_STATE)
        fun isConnected(): Boolean {
            val info = getActiveNetworkInfo()
            return info != null && info.isConnected
        }

        /**
         * Return whether network is available.
         *
         * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
         *
         * @param callback The callback.
         * @return the task
         */
        @RequiresPermission(INTERNET)
        fun isAvailableAsync(callback: Utils.Companion.Callback<Boolean>): Utils.Companion.Task<Boolean> {
            return Utils.Companion.doAsync(object : Utils.Companion.Task<Boolean>(callback) {
                @RequiresPermission(INTERNET)
                override fun doInBackground(): Boolean {
                    return isAvailable()
                }
            })
        }

        /**
         * Return whether network is available.
         *
         * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
         *
         * @return `true`: yes<br></br>`false`: no
         */
        @RequiresPermission(INTERNET)
        fun isAvailable(): Boolean {
            return isAvailableByDns() || isAvailableByPing(null)
        }

        /**
         * Return whether network is available using ping.
         *
         * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
         *
         * The default ping ip: 223.5.5.5
         *
         * @param callback The callback.
         */
        @RequiresPermission(INTERNET)
        fun isAvailableByPingAsync(callback: Utils.Companion.Callback<Boolean>) {
            isAvailableByPingAsync("", callback)
        }

        /**
         * Return whether network is available using ping.
         *
         * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
         *
         * @param ip       The ip address.
         * @param callback The callback.
         * @return the task
         */
        @RequiresPermission(INTERNET)
        fun isAvailableByPingAsync(
            ip: String,
            callback: Utils.Companion.Callback<Boolean>
        ): Utils.Companion.Task<Boolean> {
            return Utils.doAsync(object : Utils.Companion.Task<Boolean>(callback) {
                @RequiresPermission(INTERNET)
                override fun doInBackground(): Boolean {
                    return isAvailableByPing(ip)
                }
            })
        }

        /**
         * Return whether network is available using ping.
         *
         * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
         *
         * The default ping ip: 223.5.5.5
         *
         * @return `true`: yes<br></br>`false`: no
         */
        @RequiresPermission(INTERNET)
        fun isAvailableByPing(): Boolean {
            return isAvailableByPing("")
        }

        /**
         * Return whether network is available using ping.
         *
         * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
         *
         * @param ip The ip address.
         * @return `true`: yes<br></br>`false`: no
         */
        @RequiresPermission(INTERNET)
        fun isAvailableByPing(ip: String?): Boolean {
            val realIp = if (TextUtils.isEmpty(ip)) "223.5.5.5" else ip
            val result = ShellUtils.execCmd(String.format("ping -c 1 %s", realIp!!), false)
            return result.result === 0
        }

        /**
         * Return whether network is available using domain.
         *
         * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
         *
         * @param callback The callback.
         */
        @RequiresPermission(INTERNET)
        fun isAvailableByDnsAsync(callback: Utils.Companion.Callback<Boolean>) {
            isAvailableByDnsAsync("", callback)
        }

        /**
         * Return whether network is available using domain.
         *
         * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
         *
         * @param domain   The name of domain.
         * @param callback The callback.
         * @return the task
         */
        @RequiresPermission(INTERNET)
        fun isAvailableByDnsAsync(
            domain: String,
            callback: Utils.Companion.Callback<Boolean>
        ): Utils.Companion.Task<Boolean> {
            return Utils.doAsync(object : Utils.Companion.Task<Boolean>(callback) {
                @RequiresPermission(INTERNET)
                override fun doInBackground(): Boolean {
                    return isAvailableByDns(domain)
                }
            })
        }

        /**
         * Return whether network is available using domain.
         *
         * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
         *
         * @return `true`: yes<br></br>`false`: no
         */
        @RequiresPermission(INTERNET)
        fun isAvailableByDns(): Boolean {
            return isAvailableByDns("")
        }

        /**
         * Return whether network is available using domain.
         *
         * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
         *
         * @param domain The name of domain.
         * @return `true`: yes<br></br>`false`: no
         */
        @RequiresPermission(INTERNET)
        fun isAvailableByDns(domain: String): Boolean {
            val realDomain = if (TextUtils.isEmpty(domain)) "www.baidu.com" else domain
            val inetAddress: InetAddress?
            try {
                inetAddress = InetAddress.getByName(realDomain)
                return inetAddress != null
            } catch (e: UnknownHostException) {
                e.printStackTrace()
            }

            return false
        }

        /**
         * Return whether mobile data is enabled.
         *
         * @return `true`: enabled<br></br>`false`: disabled
         */
        fun getMobileDataEnabled(): Boolean {
            try {
                val tm =
                    AppUtils.getApp().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    return tm.isDataEnabled
                }
                @SuppressLint("PrivateApi")
                val getMobileDataEnabledMethod = tm.javaClass.getDeclaredMethod("getDataEnabled")
                return getMobileDataEnabledMethod.invoke(tm) as Boolean
            } catch (e: Exception) {
                Log.e("NetworkUtils", "getMobileDataEnabled: ", e)
            }

            return false
        }

        /**
         * Return whether using mobile data.
         *
         * Must hold `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
         *
         * @return `true`: yes<br></br>`false`: no
         */
        @RequiresPermission(ACCESS_NETWORK_STATE)
        fun isMobileData(): Boolean {
            val info = getActiveNetworkInfo()
            return (null != info
                    && info.isAvailable
                    && info.type == ConnectivityManager.TYPE_MOBILE)
        }

        /**
         * Return whether using 4G.
         *
         * Must hold `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
         *
         * @return `true`: yes<br></br>`false`: no
         */
        @RequiresPermission(ACCESS_NETWORK_STATE)
        fun is4G(): Boolean {
            val info = getActiveNetworkInfo()
            return (info != null
                    && info.isAvailable
                    && info.subtype == TelephonyManager.NETWORK_TYPE_LTE)
        }

        /**
         * Return whether wifi is enabled.
         *
         * Must hold `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />`
         *
         * @return `true`: enabled<br></br>`false`: disabled
         */
        @RequiresPermission(ACCESS_WIFI_STATE)
        fun getWifiEnabled(): Boolean {
            @SuppressLint("WifiManagerLeak")
            val manager = AppUtils.getApp().getSystemService(Context.WIFI_SERVICE) as WifiManager
            return manager.isWifiEnabled
        }

        /**
         * Enable or disable wifi.
         *
         * Must hold `<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />`
         *
         * @param enabled True to enabled, false otherwise.
         */
        @RequiresPermission(CHANGE_WIFI_STATE)
        fun setWifiEnabled(enabled: Boolean) {
            @SuppressLint("WifiManagerLeak")
            val manager = AppUtils.getApp().getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (enabled == manager.isWifiEnabled) return
            manager.isWifiEnabled = enabled
        }

        /**
         * Return whether wifi is connected.
         *
         * Must hold `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
         *
         * @return `true`: connected<br></br>`false`: disconnected
         */
        @RequiresPermission(ACCESS_NETWORK_STATE)
        fun isWifiConnected(): Boolean {
            val cm =
                AppUtils.getApp().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val ni = cm.activeNetworkInfo
            return ni != null && ni.type == ConnectivityManager.TYPE_WIFI
        }

        /**
         * Return whether wifi is available.
         *
         * Must hold `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />`,
         * `<uses-permission android:name="android.permission.INTERNET" />`
         *
         * @return `true`: available<br></br>`false`: unavailable
         */
        @RequiresPermission(allOf = [ACCESS_WIFI_STATE, INTERNET])
        fun isWifiAvailable(): Boolean {
            return getWifiEnabled() && isAvailable()
        }

        /**
         * Return whether wifi is available.
         *
         * Must hold `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />`,
         * `<uses-permission android:name="android.permission.INTERNET" />`
         *
         * @param callback The callback.
         * @return the task
         */
        @RequiresPermission(allOf = [ACCESS_WIFI_STATE, INTERNET])
        fun isWifiAvailableAsync(callback: Utils.Companion.Callback<Boolean>): Utils.Companion.Task<Boolean> {
            return Utils.doAsync(object : Utils.Companion.Task<Boolean>(callback) {
                @RequiresPermission(allOf = [ACCESS_WIFI_STATE, INTERNET])
                override fun doInBackground(): Boolean {
                    return isWifiAvailable()
                }
            })
        }

        /**
         * Return the name of network operate.
         *
         * @return the name of network operate
         */
        fun getNetworkOperatorName(): String {
            val tm = AppUtils.getApp().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return tm.networkOperatorName
        }

        /**
         * Return type of network.
         *
         * Must hold `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
         *
         * @return type of network
         *
         *  * [NetworkUtils.NetworkType.NETWORK_ETHERNET]
         *  * [NetworkUtils.NetworkType.NETWORK_WIFI]
         *  * [NetworkUtils.NetworkType.NETWORK_4G]
         *  * [NetworkUtils.NetworkType.NETWORK_3G]
         *  * [NetworkUtils.NetworkType.NETWORK_2G]
         *  * [NetworkUtils.NetworkType.NETWORK_UNKNOWN]
         *  * [NetworkUtils.NetworkType.NETWORK_NO]
         *
         */
        @RequiresPermission(ACCESS_NETWORK_STATE)
        fun getNetworkType(): NetworkType {
            if (isEthernet()) {
                return NetworkType.NETWORK_ETHERNET
            }
            val info = getActiveNetworkInfo()
            return if (info != null && info.isAvailable) {
                when {
                    info.type == ConnectivityManager.TYPE_WIFI -> NetworkType.NETWORK_WIFI
                    info.type == ConnectivityManager.TYPE_MOBILE -> when (info.subtype) {
                        TelephonyManager.NETWORK_TYPE_GSM, TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> NetworkType.NETWORK_2G

                        TelephonyManager.NETWORK_TYPE_TD_SCDMA, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> NetworkType.NETWORK_3G

                        TelephonyManager.NETWORK_TYPE_IWLAN, TelephonyManager.NETWORK_TYPE_LTE -> NetworkType.NETWORK_4G

                        else -> {
                            val subtypeName = info.subtypeName
                            if (subtypeName.equals("TD-SCDMA", ignoreCase = true)
                                || subtypeName.equals("WCDMA", ignoreCase = true)
                                || subtypeName.equals("CDMA2000", ignoreCase = true)
                            ) {
                                NetworkType.NETWORK_3G
                            } else {
                                NetworkType.NETWORK_UNKNOWN
                            }
                        }
                    }
                    else -> NetworkType.NETWORK_UNKNOWN
                }
            } else NetworkType.NETWORK_NO
        }

        /**
         * Return whether using ethernet.
         *
         * Must hold
         * `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
         *
         * @return `true`: yes<br></br>`false`: no
         */
        @RequiresPermission(ACCESS_NETWORK_STATE)
        private fun isEthernet(): Boolean {
            val cm =
                AppUtils.getApp().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = cm.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET) ?: return false
            val state = info.state ?: return false
            return state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING
        }

        @RequiresPermission(ACCESS_NETWORK_STATE)
        private fun getActiveNetworkInfo(): NetworkInfo? {
            val cm =
                AppUtils.getApp().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo
        }

        /**
         * Return the ip address.
         *
         * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
         *
         * @param useIPv4  True to use ipv4, false otherwise.
         * @param callback The callback.
         * @return the task
         */
        fun getIPAddressAsync(
            useIPv4: Boolean,
            callback: Utils.Companion.Callback<String>
        ): Utils.Companion.Task<String> {
            return Utils.doAsync(object : Utils.Companion.Task<String>(callback) {
                @RequiresPermission(INTERNET)
                override fun doInBackground(): String {
                    return getIPAddress(useIPv4)
                }
            })
        }

        /**
         * Return the ip address.
         *
         * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
         *
         * @param useIPv4 True to use ipv4, false otherwise.
         * @return the ip address
         */
        @RequiresPermission(INTERNET)
        fun getIPAddress(useIPv4: Boolean): String {
            try {
                val nis = NetworkInterface.getNetworkInterfaces()
                val adds = LinkedList<InetAddress>()
                while (nis.hasMoreElements()) {
                    val ni = nis.nextElement()
                    // To prevent phone of xiaomi return "10.0.2.15"
                    if (!ni.isUp || ni.isLoopback) continue
                    val addresses = ni.inetAddresses
                    while (addresses.hasMoreElements()) {
                        adds.addFirst(addresses.nextElement())
                    }
                }
                for (add in adds) {
                    if (!add.isLoopbackAddress) {
                        val hostAddress = add.hostAddress
                        val isIPv4 = hostAddress.indexOf(':') < 0
                        if (useIPv4) {
                            if (isIPv4) return hostAddress
                        } else {
                            if (!isIPv4) {
                                val index = hostAddress.indexOf('%')
                                return if (index < 0)
                                    hostAddress.toUpperCase(Locale.US)
                                else
                                    hostAddress.substring(0, index).toUpperCase(Locale.US)
                            }
                        }
                    }
                }
            } catch (e: SocketException) {
                e.printStackTrace()
            }
            return ""
        }

        /**
         * Return the ip address of broadcast.
         *
         * @return the ip address of broadcast
         */
        fun getBroadcastIpAddress(): String {
            try {
                val nis = NetworkInterface.getNetworkInterfaces()
                while (nis.hasMoreElements()) {
                    val ni = nis.nextElement()
                    if (!ni.isUp || ni.isLoopback) continue
                    val ias = ni.interfaceAddresses
                    var i = 0
                    val size = ias.size
                    while (i < size) {
                        val ia = ias[i]
                        val broadcast = ia.broadcast
                        if (broadcast != null) {
                            return broadcast!!.hostAddress
                        }
                        i++
                    }
                }
            } catch (e: SocketException) {
                e.printStackTrace()
            }
            return ""
        }

        /**
         * Return the domain address.
         *
         * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
         *
         * @param domain   The name of domain.
         * @param callback The callback.
         * @return the task
         */
        @RequiresPermission(INTERNET)
        fun getDomainAddressAsync(
            domain: String,
            callback: Utils.Companion.Callback<String>
        ): Utils.Companion.Task<String> {
            return Utils.doAsync(object : Utils.Companion.Task<String>(callback) {
                @RequiresPermission(INTERNET)
                override fun doInBackground(): String {
                    return getDomainAddress(domain)
                }
            })
        }

        /**
         * Return the domain address.
         *
         * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
         *
         * @param domain The name of domain.
         * @return the domain address
         */
        @RequiresPermission(INTERNET)
        fun getDomainAddress(domain: String): String {
            val inetAddress: InetAddress
            return try {
                inetAddress = InetAddress.getByName(domain)
                inetAddress.hostAddress
            } catch (e: UnknownHostException) {
                e.printStackTrace()
                ""
            }
        }

        /**
         * Return the ip address by wifi.
         *
         * @return the ip address by wifi
         */
        @RequiresPermission(ACCESS_WIFI_STATE)
        fun getIpAddressByWifi(): String {
            @SuppressLint("WifiManagerLeak")
            val wm = AppUtils.getApp().getSystemService(Context.WIFI_SERVICE) as WifiManager
            return Formatter.formatIpAddress(wm.dhcpInfo.ipAddress)
        }

        /**
         * Return the gate way by wifi.
         *
         * @return the gate way by wifi
         */
        @RequiresPermission(ACCESS_WIFI_STATE)
        fun getGatewayByWifi(): String {
            @SuppressLint("WifiManagerLeak")
            val wm = AppUtils.getApp().getSystemService(Context.WIFI_SERVICE) as WifiManager
            return Formatter.formatIpAddress(wm.dhcpInfo.gateway)
        }

        /**
         * Return the net mask by wifi.
         *
         * @return the net mask by wifi
         */
        @RequiresPermission(ACCESS_WIFI_STATE)
        fun getNetMaskByWifi(): String {
            @SuppressLint("WifiManagerLeak")
            val wm = AppUtils.getApp().getSystemService(Context.WIFI_SERVICE) as WifiManager
            return Formatter.formatIpAddress(wm.dhcpInfo.netmask)
        }

        /**
         * Return the server address by wifi.
         *
         * @return the server address by wifi
         */
        @RequiresPermission(ACCESS_WIFI_STATE)
        fun getServerAddressByWifi(): String {
            @SuppressLint("WifiManagerLeak")
            val wm = AppUtils.getApp().getSystemService(Context.WIFI_SERVICE) as WifiManager
            return Formatter.formatIpAddress(wm.dhcpInfo.serverAddress)
        }

        /**
         * Register the status of network changed listener.
         *
         * @param listener The status of network changed listener
         */
        fun registerNetworkStatusChangedListener(listener: OnNetworkStatusChangedListener) {
            NetworkChangedReceiver.instance.registerListener(listener)
        }

        /**
         * unregister the status of network changed listener.
         *
         * @param listener The status of network changed listener
         */
        fun unregisterNetworkStatusChangedListener(listener: OnNetworkStatusChangedListener) {
            NetworkChangedReceiver.instance.unregisterListener(listener)
        }

        class NetworkChangedReceiver : BroadcastReceiver() {

            companion object{
                val instance: NetworkChangedReceiver
                    get() = LazyHolder.INSTANCE
            }

            private var mType: NetworkType? = null
            private val mListeners = HashSet<OnNetworkStatusChangedListener>()



            internal fun registerListener(listener: OnNetworkStatusChangedListener?) {
                if (listener == null) return
                Utils.runOnUiThread(Runnable {
                    val preSize = mListeners.size
                    mListeners.add(listener)
                    if (preSize == 0 && mListeners.size == 1) {
                        mType = getNetworkType()
                        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                        AppUtils.getApp()
                            .registerReceiver(instance, intentFilter)
                    }
                })
            }

            internal fun unregisterListener(listener: OnNetworkStatusChangedListener?) {
                if (listener == null) return
                Utils.runOnUiThread(Runnable {
                    val preSize = mListeners.size
                    mListeners.remove(listener)
                    if (preSize == 1 && mListeners.size == 0) {
                        AppUtils.getApp().unregisterReceiver(instance)
                    }
                })
            }

            @SuppressLint("MissingPermission")
            override fun onReceive(context: Context, intent: Intent) {
                if (ConnectivityManager.CONNECTIVITY_ACTION == intent.action) {
                    // debouncing
                    Utils.runOnUiThreadDelayed(Runnable {
                        val networkType = NetworkUtils.getNetworkType()
                        if (mType == networkType) return@Runnable
                        LogKtUtils.e(networkType.name)
                        mType = networkType
                        if (networkType == NetworkType.NETWORK_NO) {
                            for (listener in mListeners) {
                                listener.onDisconnected()
                            }
                        } else {
                            for (listener in mListeners) {
                                listener.onConnected(networkType)
                            }
                        }
                    }, 1000)
                }
            }

            private object LazyHolder {
                val INSTANCE = NetworkChangedReceiver()
            }
        }

        interface OnNetworkStatusChangedListener {
            fun onDisconnected()

            fun onConnected(networkType: NetworkType)
        }
    }
}