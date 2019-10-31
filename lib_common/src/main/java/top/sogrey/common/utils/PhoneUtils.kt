package top.sogrey.common.utils

import android.content.pm.PackageManager
import android.content.Intent
import android.content.Context.TELEPHONY_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat.startActivity
import android.Manifest.permission.CALL_PHONE
import androidx.annotation.RequiresPermission
import android.Manifest.permission.READ_PHONE_STATE
import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import java.lang.reflect.InvocationTargetException


/**
 * 手机相关
 *
        isPhone : 判断设备是否是手机
        getDeviceId : 获取设备码
        getSerial : 获取序列号
        getIMEI : 获取 IMEI 码
        getMEID : 获取 MEID 码
        getIMSI : 获取 IMSI 码
        getPhoneType : 获取移动终端类型
        isSimCardReady : 判断 sim 卡是否准备好
        getSimOperatorName : 获取 Sim 卡运营商名称
        getSimOperatorByMnc: 获取 Sim 卡运营商名称
        getPhoneStatus : 获取手机状态信息
        dial : 跳至拨号界面
        call : 拨打 phoneNumber
        sendSms : 跳至发送短信界面
        sendSmsSilent : 发送短信
 * <p/>
 * @author Sogrey
 * @date 2019-10-31 11:06
 */

class PhoneUtils {
    constructor() {
        throw UnsupportedOperationException(this.javaClass.simpleName + " cannot be instantiated")
    }

    companion object {
        /**
         * Return whether the device is phone.
         *
         * @return `true`: yes<br></br>`false`: no
         */
        fun isPhone(): Boolean {
            val tm = getTelephonyManager()
            return tm.phoneType != TelephonyManager.PHONE_TYPE_NONE
        }

        /**
         * Return the unique device id.
         *
         * If the version of SDK is greater than 28, it will return an empty string.
         *
         * Must hold `<uses-permission android:name="android.permission.READ_PHONE_STATE" />`
         *
         * @return the unique device id
         */
        @SuppressLint("HardwareIds")
        @RequiresPermission(READ_PHONE_STATE)
        fun getDeviceId(): String {
            if (Build.VERSION.SDK_INT >= 29) {
                return ""
            }
            val tm = getTelephonyManager()
            val deviceId = tm.deviceId
            if (!TextUtils.isEmpty(deviceId)) return deviceId
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val imei = tm.imei
                if (!TextUtils.isEmpty(imei)) return imei
                val meid = tm.meid
                return if (TextUtils.isEmpty(meid)) "" else meid
            }
            return ""
        }

        /**
         * Return the serial of device.
         *
         * @return the serial of device
         */
        @SuppressLint("HardwareIds")
        @RequiresPermission(READ_PHONE_STATE)
        fun getSerial(): String {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Build.getSerial() else Build.SERIAL
        }

        /**
         * Return the IMEI.
         *
         * If the version of SDK is greater than 28, it will return an empty string.
         *
         * Must hold `<uses-permission android:name="android.permission.READ_PHONE_STATE" />`
         *
         * @return the IMEI
         */
        @RequiresPermission(READ_PHONE_STATE)
        fun getIMEI(): String? {
            return getImeiOrMeid(true)
        }

        /**
         * Return the MEID.
         *
         * If the version of SDK is greater than 28, it will return an empty string.
         *
         * Must hold `<uses-permission android:name="android.permission.READ_PHONE_STATE" />`
         *
         * @return the MEID
         */
        @RequiresPermission(READ_PHONE_STATE)
        fun getMEID(): String? {
            return getImeiOrMeid(false)
        }

        @SuppressLint("HardwareIds")
        @RequiresPermission(READ_PHONE_STATE)
        fun getImeiOrMeid(isImei: Boolean): String? {
            if (Build.VERSION.SDK_INT >= 29) {
                return ""
            }
            val tm = getTelephonyManager()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return if (isImei) {
                    getMinOne(tm.getImei(0), tm.getImei(1))
                } else {
                    getMinOne(tm.getMeid(0), tm.getMeid(1))
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val ids =
                    getSystemPropertyByReflect(if (isImei) "ril.gsm.imei" else "ril.cdma.meid")
                if (!TextUtils.isEmpty(ids)) {
                    val idArr =
                        ids.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    return if (idArr.size == 2) {
                        getMinOne(idArr[0], idArr[1])
                    } else {
                        idArr[0]
                    }
                }

                var id0: String? = tm.deviceId
                var id1: String? = ""
                try {
                    val method = tm.javaClass.getMethod("getDeviceId", Int::class.javaPrimitiveType)
                    id1 = method.invoke(
                        tm,
                        if (isImei)
                            TelephonyManager.PHONE_TYPE_GSM
                        else
                            TelephonyManager.PHONE_TYPE_CDMA
                    ) as String
                } catch (e: NoSuchMethodException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: InvocationTargetException) {
                    e.printStackTrace()
                }

                if (isImei) {
                    if (id0 != null && id0.length < 15) {
                        id0 = ""
                    }
                    if (id1 != null && id1.length < 15) {
                        id1 = ""
                    }
                } else {
                    if (id0 != null && id0.length == 14) {
                        id0 = ""
                    }
                    if (id1 != null && id1.length == 14) {
                        id1 = ""
                    }
                }
                return getMinOne(id0, id1)
            } else {
                val deviceId = tm.deviceId
                if (isImei) {
                    if (deviceId != null && deviceId.length >= 15) {
                        return deviceId
                    }
                } else {
                    if (deviceId != null && deviceId.length == 14) {
                        return deviceId
                    }
                }
            }
            return ""
        }

        private fun getMinOne(s0: String?, s1: String?): String? {
            val empty0 = TextUtils.isEmpty(s0)
            val empty1 = TextUtils.isEmpty(s1)
            if (empty0 && empty1) return ""
            if (!empty0 && !empty1) {
                return if (s0!! <= s1!!) {
                    s0
                } else {
                    s1
                }
            }
            return if (!empty0) s0 else s1
        }

        private fun getSystemPropertyByReflect(key: String): String {
            try {
                @SuppressLint("PrivateApi")
                val clz = Class.forName("android.os.SystemProperties")
                val getMethod = clz.getMethod("get", String::class.java, String::class.java)
                return getMethod.invoke(clz, key, "") as String
            } catch (e: Exception) {/**/
            }

            return ""
        }

        /**
         * Return the IMSI.
         *
         * Must hold `<uses-permission android:name="android.permission.READ_PHONE_STATE" />`
         *
         * @return the IMSI
         */
        @SuppressLint("HardwareIds")
        @RequiresPermission(READ_PHONE_STATE)
        fun getIMSI(): String {
            return getTelephonyManager().subscriberId
        }

        /**
         * Returns the current phone type.
         *
         * @return the current phone type
         *
         *  * [TelephonyManager.PHONE_TYPE_NONE]
         *  * [TelephonyManager.PHONE_TYPE_GSM]
         *  * [TelephonyManager.PHONE_TYPE_CDMA]
         *  * [TelephonyManager.PHONE_TYPE_SIP]
         *
         */
        fun getPhoneType(): Int {
            val tm = getTelephonyManager()
            return tm.phoneType
        }

        /**
         * Return whether sim card state is ready.
         *
         * @return `true`: yes<br></br>`false`: no
         */
        fun isSimCardReady(): Boolean {
            val tm = getTelephonyManager()
            return tm.simState == TelephonyManager.SIM_STATE_READY
        }

        /**
         * Return the sim operator name.
         *
         * @return the sim operator name
         */
        fun getSimOperatorName(): String {
            val tm = getTelephonyManager()
            return tm.simOperatorName
        }

        /**
         * Return the sim operator using mnc.
         *
         * @return the sim operator
         */
        fun getSimOperatorByMnc(): String {
            val tm = getTelephonyManager()
            when (val operator = tm.simOperator ?: return "") {
                "46000", "46002", "46007", "46020" -> return "中国移动"
                "46001", "46006", "46009" -> return "中国联通"
                "46003", "46005", "46011" -> return "中国电信"
                else -> return operator
            }
        }

        /**
         * Skip to dial.
         *
         * @param phoneNumber The phone number.
         * @return `true`: operate successfully<br></br>`false`: otherwise
         */
        fun dial(phoneNumber: String): Boolean {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
            if (isIntentAvailable(intent)) {
                AppUtils.getApp().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                return true
            }
            return false
        }

        /**
         * Make a phone call.
         *
         * Must hold `<uses-permission android:name="android.permission.CALL_PHONE" />`
         *
         * @param phoneNumber The phone number.
         * @return `true`: operate successfully<br></br>`false`: otherwise
         */
        @RequiresPermission(CALL_PHONE)
        fun call(phoneNumber: String): Boolean {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
            if (isIntentAvailable(intent)) {
                AppUtils.getApp().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                return true
            }
            return false
        }

        /**
         * Send sms.
         *
         * @param phoneNumber The phone number.
         * @param content     The content.
         * @return `true`: operate successfully<br></br>`false`: otherwise
         */
        fun sendSms(phoneNumber: String, content: String): Boolean {
            val uri = Uri.parse("smsto:$phoneNumber")
            val intent = Intent(Intent.ACTION_SENDTO, uri)
            if (isIntentAvailable(intent)) {
                intent.putExtra("sms_body", content)
                AppUtils.getApp().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                return true
            }
            return false
        }

        private fun getTelephonyManager(): TelephonyManager {
            return AppUtils.getApp().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        }

        private fun isIntentAvailable(intent: Intent): Boolean {
            return AppUtils.getApp()
                .packageManager
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                .size > 0
        }
    }
}