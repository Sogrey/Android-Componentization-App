package top.sogrey.common.utils

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import top.sogrey.common.utils.ktx.getSP
import top.sogrey.common.utils.ktx.setSP
import java.io.File
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*


/**
 * 设备相关
 * <p/>
 * @author Sogrey
 * @date 2019-10-30 14:07
 */

class DeviceUtils {
    constructor() {
        throw UnsupportedOperationException(this.javaClass.simpleName + " cannot be instantiated")
    }

    companion object {
        /**
         * Return whether device is rooted.
         *
         * @return `true`: yes<br></br>`false`: no
         */
        fun isDeviceRooted(): Boolean {
            val su = "su"
            val locations = arrayOf(
                "/system/bin/",
                "/system/xbin/",
                "/sbin/",
                "/system/sd/xbin/",
                "/system/bin/failsafe/",
                "/data/local/xbin/",
                "/data/local/bin/",
                "/data/local/",
                "/system/sbin/",
                "/usr/bin/",
                "/vendor/bin/"
            )
            for (location in locations) {
                if (File(location + su).exists()) {
                    return true
                }
            }
            return false
        }

        /**
         * Return whether ADB is enabled.
         *
         * @return `true`: yes<br></br>`false`: no
         */
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        fun isAdbEnabled(): Boolean {
            return Settings.Secure.getInt(
                AppUtils.getApp().contentResolver,
                Settings.Global.ADB_ENABLED, 0
            ) > 0
        }

        /**
         * Return the version name of device's system.
         *
         * @return the version name of device's system
         */
        fun getSDKVersionName(): String {
            return android.os.Build.VERSION.RELEASE
        }

        /**
         * Return version code of device's system.
         *
         * @return version code of device's system
         */
        fun getSDKVersionCode(): Int {
            return android.os.Build.VERSION.SDK_INT
        }

        /**
         * Return the android id of device.
         *
         * @return the android id of device
         */
        @SuppressLint("HardwareIds")
        fun getAndroidID(): String {
            val id = Settings.Secure.getString(
                AppUtils.getApp().contentResolver,
                Settings.Secure.ANDROID_ID
            )
            if ("9774d56d682e549c" == id) return ""
            return id ?: ""
        }

        /**
         * Return the MAC address.
         *
         * Must hold `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />`,
         * `<uses-permission android:name="android.permission.INTERNET" />`,
         * `<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />`
         *
         * @return the MAC address
         */
        @RequiresPermission(allOf = [ACCESS_WIFI_STATE, INTERNET, CHANGE_WIFI_STATE])
        fun getMacAddress(): String {
            val macAddress = getMacAddress("")
            if (macAddress != "" || getWifiEnabled()) return macAddress
            setWifiEnabled(true)
            setWifiEnabled(false)
            return getMacAddress("")
        }

        private fun getWifiEnabled(): Boolean {
            @SuppressLint("WifiManagerLeak")
            val manager = AppUtils.getApp().getSystemService(WIFI_SERVICE) as WifiManager
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
        private fun setWifiEnabled(enabled: Boolean) {
            @SuppressLint("WifiManagerLeak")
            val manager = AppUtils.getApp().getSystemService(WIFI_SERVICE) as WifiManager
            if (enabled == manager.isWifiEnabled) return
            manager.isWifiEnabled = enabled
        }

        /**
         * Return the MAC address.
         *
         * Must hold `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />`,
         * `<uses-permission android:name="android.permission.INTERNET" />`
         *
         * @return the MAC address
         */
        @RequiresPermission(allOf = [ACCESS_WIFI_STATE, INTERNET])
        fun getMacAddress(vararg excepts: String): String {
            var macAddress = getMacAddressByNetworkInterface()
            if (isAddressNotInExcepts(macAddress, *excepts)) {
                return macAddress
            }
            macAddress = getMacAddressByInetAddress()
            if (isAddressNotInExcepts(macAddress, *excepts)) {
                return macAddress
            }
            macAddress = getMacAddressByWifiInfo()
            if (isAddressNotInExcepts(macAddress, *excepts)) {
                return macAddress
            }
            macAddress = getMacAddressByFile()
            return if (isAddressNotInExcepts(macAddress, *excepts)) {
                macAddress
            } else ""
        }

        private fun isAddressNotInExcepts(address: String, vararg excepts: String): Boolean {
            if (excepts.isEmpty()) {
                return "02:00:00:00:00:00" != address
            }
            for (filter in excepts) {
                if (address == filter) {
                    return false
                }
            }
            return true
        }

        @SuppressLint("MissingPermission", "HardwareIds")
        private fun getMacAddressByWifiInfo(): String {
            try {
                val wifi =
                    AppUtils.getApp().applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
                val info = wifi.connectionInfo
                if (info != null) return info.macAddress
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return "02:00:00:00:00:00"
        }

        private fun getMacAddressByNetworkInterface(): String {
            try {
                val nis = NetworkInterface.getNetworkInterfaces()
                while (nis.hasMoreElements()) {
                    val ni = nis.nextElement()
                    if (ni == null || ni.name.toLowerCase(Locale.US) != "wlan0") continue
                    val macBytes = ni.hardwareAddress
                    if (macBytes != null && macBytes.isNotEmpty()) {
                        val sb = StringBuilder()
                        for (b in macBytes) {
                            sb.append(String.format("%02x:", b))
                        }
                        return sb.substring(0, sb.length - 1)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return "02:00:00:00:00:00"
        }

        private fun getMacAddressByInetAddress(): String {
            try {
                val inetAddress = getInetAddress()
                if (inetAddress != null) {
                    val ni = NetworkInterface.getByInetAddress(inetAddress)
                    if (ni != null) {
                        val macBytes = ni.hardwareAddress
                        if (macBytes != null && macBytes.isNotEmpty()) {
                            val sb = StringBuilder()
                            for (b in macBytes) {
                                sb.append(String.format("%02x:", b))
                            }
                            return sb.substring(0, sb.length - 1)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return "02:00:00:00:00:00"
        }

        private fun getInetAddress(): InetAddress? {
            try {
                val nis = NetworkInterface.getNetworkInterfaces()
                while (nis.hasMoreElements()) {
                    val ni = nis.nextElement()
                    // To prevent phone of xiaomi return "10.0.2.15"
                    if (!ni.isUp) continue
                    val addresses = ni.inetAddresses
                    while (addresses.hasMoreElements()) {
                        val inetAddress = addresses.nextElement()
                        if (!inetAddress.isLoopbackAddress) {
                            val hostAddress = inetAddress.hostAddress
                            if (hostAddress.indexOf(':') < 0) return inetAddress
                        }
                    }
                }
            } catch (e: SocketException) {
                e.printStackTrace()
            }
            return null
        }

        private fun getMacAddressByFile(): String {
            var result = ShellUtils.execCmd("getprop wifi.interface", false)
            if (result.result == 0) {
                val name = result.successMsg
                result = ShellUtils.execCmd("cat /sys/class/net/$name/address", false)
                if (result.result == 0) {
                    val address = result.successMsg
                    if (address.isNotEmpty()) {
                        return address
                    }
                }
            }
            return "02:00:00:00:00:00"
        }

        /**
         * Return the manufacturer of the product/hardware.
         *
         * e.g. Xiaomi
         *
         * @return the manufacturer of the product/hardware
         */
        fun getManufacturer(): String {
            return Build.MANUFACTURER
        }

        /**
         * Return the model of device.
         *
         * e.g. MI2SC
         *
         * @return the model of device
         */
        fun getModel(): String {
            var model: String? = Build.MODEL
            if (model != null) {
                model = model.trim { it <= ' ' }.replace("\\s*".toRegex(), "")
            } else {
                model = ""
            }
            return model
        }

        /**
         * Return an ordered list of ABIs supported by this device. The most preferred ABI is the first
         * element in the list.
         *
         * @return an ordered list of ABIs supported by this device
         */
        fun getABIs(): Array<String> {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Build.SUPPORTED_ABIS
            } else {
                if (!TextUtils.isEmpty(Build.CPU_ABI2)) {
                    arrayOf(Build.CPU_ABI, Build.CPU_ABI2)
                } else arrayOf(Build.CPU_ABI)
            }
        }

        /**
         * Return whether device is tablet.
         *
         * @return `true`: yes<br></br>`false`: no
         */
        fun isTablet(): Boolean {
            return AppUtils.getApp().resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
        }

        /**
         * Return whether device is emulator.
         *
         * @return `true`: yes<br></br>`false`: no
         */
        fun isEmulator(): Boolean {
            val checkProperty = (Build.FINGERPRINT.startsWith("generic")
                    || Build.FINGERPRINT.toLowerCase(Locale.US).contains("vbox")
                    || Build.FINGERPRINT.toLowerCase(Locale.US).contains("test-keys")
                    || Build.MODEL.contains("google_sdk")
                    || Build.MODEL.contains("Emulator")
                    || Build.MODEL.contains("Android SDK built for x86")
                    || Build.MANUFACTURER.contains("Genymotion")
                    || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                    || "google_sdk" == Build.PRODUCT)
            if (checkProperty) return true

            var operatorName = ""
            val tm = AppUtils.getApp().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val name = tm.networkOperatorName
            if (name != null) {
                operatorName = name
            }
            val checkOperatorName = operatorName.toLowerCase(Locale.US) == "android"
            if (checkOperatorName) return true

            val url = "tel:" + "123456"
            val intent = Intent()
            intent.data = Uri.parse(url)
            intent.action = Intent.ACTION_DIAL
            return intent.resolveActivity(AppUtils.getApp().packageManager) == null

            //        boolean checkDebuggerConnected = Debug.isDebuggerConnected();
            //        if (checkDebuggerConnected) return true;

        }


        private val KEY_UDID = "KEY_UDID"
        @Volatile
        private var udid: String? = null

        /**
         * Return the unique device id.
         * <pre>{1}{UUID(macAddress)}</pre>
         * <pre>{2}{UUID(androidId )}</pre>
         * <pre>{9}{UUID(random    )}</pre>
         *
         * @return the unique device id
         */
        @SuppressLint("MissingPermission", "HardwareIds")
        fun getUniqueDeviceId(): String? {
            return getUniqueDeviceId("")
        }

        /**
         * Return the unique device id.
         * <pre>{prefix}{1}{UUID(macAddress)}</pre>
         * <pre>{prefix}{2}{UUID(androidId )}</pre>
         * <pre>{prefix}{9}{UUID(random    )}</pre>
         *
         * @param prefix The prefix of the unique device id.
         * @return the unique device id
         */
        @SuppressLint("MissingPermission", "HardwareIds")
        fun getUniqueDeviceId(prefix: String): String? {
            if (udid == null) {
                synchronized(DeviceUtils::class.java) {
                    if (udid == null) {
                        val id:String? = AppUtils.getApp().getSP(KEY_UDID, "") as String?
                        if (id != null) {
                            udid = id
                            return udid
                        }
                        try {
                            val macAddress = getMacAddress()
                            if (macAddress != "") {
                                return saveUdid(prefix + 1, macAddress)
                            }

                            val androidId = getAndroidID()
                            if (!TextUtils.isEmpty(androidId)) {
                                return saveUdid(prefix + 2, androidId)
                            }

                        } catch (ignore: Exception) {/**/
                        }

                        return saveUdid(prefix + 9, "")
                    }
                }
            }
            return udid
        }

        @SuppressLint("MissingPermission", "HardwareIds")
        fun isSameDevice(uniqueDeviceId: String): Boolean {
            // {prefix}{type}{32id}
            if (TextUtils.isEmpty(uniqueDeviceId) && uniqueDeviceId.length < 33) return false
            if (uniqueDeviceId == udid) return true
            val cachedId:String? = AppUtils.getApp().getSP(KEY_UDID, "") as String?
            if (uniqueDeviceId == cachedId) return true
            val st = uniqueDeviceId.length - 33
            val type = uniqueDeviceId.substring(st, st + 1)
            if (type.startsWith("1")) {
                val macAddress = getMacAddress()
                return if (macAddress == "") {
                    false
                } else uniqueDeviceId.substring(st + 1) == getUdid("", macAddress)
            } else if (type.startsWith("2")) {
                val androidId = getAndroidID()
                return if (TextUtils.isEmpty(androidId)) {
                    false
                } else uniqueDeviceId.substring(st + 1) == getUdid("", androidId)
            }
            return false
        }

        private fun saveUdid(prefix: String, id: String): String? {
            udid = getUdid(prefix, id)
            AppUtils.getApp().setSP(KEY_UDID, udid)
            return udid
        }

        private fun getUdid(prefix: String, id: String): String {
            return if (id == "") {
                prefix + UUID.randomUUID().toString().replace("-", "")
            } else prefix + UUID.nameUUIDFromBytes(id.toByteArray()).toString().replace("-", "")
        }
    }
}