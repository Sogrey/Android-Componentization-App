package top.sogrey.common.utils

import java.util.*

/**
 * 描述：颜色工具类
 * Created by Sogrey on 2018/12/29.
 */
class ColorUtils {
    companion object {
        private var msg = ""

        private const val regHex = "^#([0-9a-fA-f]{3}|[0-9a-fA-f]{6})$"
        private const val regRgb = "^(RGB\\(|rgb\\()([0-9]{1,3},){2}[0-9]{1,3}\\)$"
        private const val regRepRgb = "(rgb|\\(|\\)|RGB)*"

        /**
         * **颜色十六进制转颜色RGB**<br></br>
         *
         *  * 颜色十六进制参数不合法时，返回null
         *
         * <br></br>
         * @author Aaron.ffp
         * @version V1.0.0: autotest-base cn.ffp.autotest.base.util ColorUtils.java hex2Rgb, 2016-03-24 23:50:42.004 Exp $
         *
         * @param hex 颜色十六进制
         * @return 颜色RGB
         */
        fun hex2Rgb(hex: String): String? {
            val sb = StringBuilder()

            if (!isHex(hex)) {
                msg = "颜色十六进制格式 【$hex】 不合法，请确认！"
                logE(msg= msg)
                return null
            }

            val c = RegUtils.replace(hex.toUpperCase(Locale.US), "#", "")

            val r = Integer.parseInt(if (c.length == 3) c.substring(0, 1) + c.substring(0, 1) else c.substring(0, 2), 16).toString() + ""
            val g = Integer.parseInt(if (c.length == 3) c.substring(1, 2) + c.substring(1, 2) else c.substring(2, 4), 16).toString() + ""
            val b = Integer.parseInt(if (c.length == 3) c.substring(2, 3) + c.substring(2, 3) else c.substring(4, 6), 16).toString() + ""

            sb.append("RGB($r,$g,$b)")

            return sb.toString()
        }

        /**
         * **颜色RGB转十六进制**<br></br>
         *
         *  * 颜色RGB不合法，则返回null
         *
         * <br></br>
         * @author Aaron.ffp
         * @version V1.0.0: autotest-base cn.ffp.autotest.base.util ColorUtils.java rgb2Hex, 2016-03-15 23:49:33.224 Exp $
         *
         * @param rgb 颜色RGB
         * @return 合法时返回颜色十六进制
         */
        fun rgb2Hex(rgb: String): String? {
            val sb = StringBuilder()

            if (!isRgb(rgb)) {
                msg = "颜色 RGB 格式【$rgb】 不合法，请确认！"
                logE(msg= msg)
                return null
            }

            val r = Integer.toHexString(getRed(rgb)).toUpperCase(Locale.US)
            val g = Integer.toHexString(getGreen(rgb)).toUpperCase(Locale.US)
            val b = Integer.toHexString(getBlue(rgb)).toUpperCase(Locale.US)

            sb.append("#")
            sb.append(if (r.length == 1) "0$r" else r)
            sb.append(if (g.length == 1) "0$g" else g)
            sb.append(if (b.length == 1) "0$b" else b)

            return sb.toString()
        }

        /**
         * **获取颜色RGB红色值**<br></br>
         * <br></br>
         * @author Aaron.ffp
         * @version V1.0.0: autotest-base cn.ffp.autotest.base.util ColorUtils.java getRed, 2016-03-22 23:48:50.501 Exp $
         *
         * @param rgb 颜色RGB
         * @return 红色值
         */
        fun getRed(rgb: String): Int {
            return Integer.valueOf(getRGB(rgb)[0])
        }

        /**
         * **获取颜色RGB绿色值**<br></br>
         * <br></br>
         * @author Aaron.ffp
         * @version V1.0.0: autotest-base cn.ffp.autotest.base.util ColorUtils.java getGreen, 2016-03-22 23:48:16.290 Exp $
         *
         * @param rgb 颜色RGB
         * @return 绿色值
         */
        fun getGreen(rgb: String): Int {
            return Integer.valueOf(getRGB(rgb)[1])
        }

        /**
         * **获取颜色RGB蓝色值**<br></br>
         * <br></br>
         * @author Aaron.ffp
         * @version V1.0.0: autotest-base cn.ffp.autotest.base.util ColorUtils.java getBlue, 2016-03-22 23:47:20.801 Exp $
         *
         * @param rgb 颜色RGB
         * @return 蓝色数值
         */
        fun getBlue(rgb: String): Int {
            return Integer.valueOf(getRGB(rgb)[2])
        }

        /**
         * **获取颜色RGB数组**<br></br>
         * <br></br>
         * @author Aaron.ffp
         * @version V1.0.0: autotest-base cn.ffp.autotest.base.util ColorUtils.java getRGB, 2016-03-21 23:46:00.944 Exp $
         *
         * @param rgb 颜色RGB
         * @return 颜色数组[红，绿，蓝]
         */
        fun getRGB(rgb: String): List<String> {
            return RegUtils.replace(RegUtils.replaceSpace(rgb), regRepRgb, "").split(",")
        }

        /**
         * **验证颜色十六进制是否合法**<br></br>
         *
         *  * 规则：#号开头，三位或六位字母数字组成的字符串
         *
         * <br></br>
         * @author Aaron.ffp
         * @version V1.0.0: autotest-base cn.ffp.autotest.base.util ColorUtils.java isHex, 2016-03-20 23:44:03.133 Exp $
         *
         * @param hex 十六进制颜色
         * @return 合法则返回true
         */
        fun isHex(hex: String): Boolean {
            return RegUtils.reg(hex, regHex)
        }

        /**
         * **验证颜色RGB是否合法**<br></br>
         *
         *  * 1、RGB符合正则表达式
         *  * 2、颜色值在0-255之间
         *
         * <br></br>
         * @author Aaron.ffp
         * @version V1.0.0: autotest-base cn.ffp.autotest.base.util ColorUtils.java isRgb, 2016-03-12 23:41:19.925 Exp $
         *
         * @param rgb 颜色RGB
         * @return 是否合法，合法返回true
         */
        fun isRgb(rgb: String): Boolean {
            val r = getRed(rgb) in 0..255
            val g = getGreen(rgb) in 0..255
            val b = getBlue(rgb) in 0..255

            return isRgbFormat(rgb) && r && g && b
        }

        /**
         * **验证颜色RGB是否匹配正则表达式**<br></br>
         *
         *  * 匹配则返回true
         *
         * <br></br>
         * @author Aaron.ffp
         * @version V1.0.0: autotest-base cn.ffp.autotest.base.util ColorUtils.java isRgbFormat, 2016-03-03 23:40:12.267 Exp $
         *
         * @param rgb 颜色RGB
         * @return 是否匹配
         */
        fun isRgbFormat(rgb: String): Boolean {
            return RegUtils.reg(RegUtils.replaceSpace(rgb), regRgb)
        }
    }
}