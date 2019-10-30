package top.sogrey.common.utils

import android.text.Html
import android.os.Build
import android.util.Base64;
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * 编码相关
 * <p/>
 * @author Sogrey
 * @date 2019-10-30 22:47
 */
 class EncodeUtils {
    constructor() {
        throw UnsupportedOperationException(this.javaClass.simpleName + " cannot be instantiated")
    }

    companion object {

        /**
         * Return the urlencoded string.
         *
         * @param input The input.
         * @return the urlencoded string
         */
        fun urlEncode(input: String): String {
            return urlEncode(input, "UTF-8")
        }

        /**
         * Return the urlencoded string.
         *
         * @param input       The input.
         * @param charsetName The name of charset.
         * @return the urlencoded string
         */
        fun urlEncode(input: String?, charsetName: String): String {
            if (input == null || input.isEmpty()) return ""
            try {
                return URLEncoder.encode(input, charsetName)
            } catch (e: UnsupportedEncodingException) {
                throw AssertionError(e)
            }

        }

        /**
         * Return the string of decode urlencoded string.
         *
         * @param input The input.
         * @return the string of decode urlencoded string
         */
        fun urlDecode(input: String): String {
            return urlDecode(input, "UTF-8")
        }

        /**
         * Return the string of decode urlencoded string.
         *
         * @param input       The input.
         * @param charsetName The name of charset.
         * @return the string of decode urlencoded string
         */
        fun urlDecode(input: String?, charsetName: String): String {
            if (input == null || input.isEmpty()) return ""
            try {
                return URLDecoder.decode(input, charsetName)
            } catch (e: UnsupportedEncodingException) {
                throw AssertionError(e)
            }

        }

        /**
         * Return Base64-encode bytes.
         *
         * @param input The input.
         * @return Base64-encode bytes
         */
        fun base64Encode(input: String): ByteArray {
            return base64Encode(input.toByteArray())
        }

        /**
         * Return Base64-encode bytes.
         *
         * @param input The input.
         * @return Base64-encode bytes
         */
        fun base64Encode(input: ByteArray?): ByteArray {
            return if (input == null || input.isEmpty()) ByteArray(0) else Base64.encode(
                input,
                Base64.NO_WRAP
            )
        }

        /**
         * Return Base64-encode string.
         *
         * @param input The input.
         * @return Base64-encode string
         */
        fun base64Encode2String(input: ByteArray?): String {
            return if (input == null || input.isEmpty()) "" else Base64.encodeToString(
                input,
                Base64.NO_WRAP
            )
        }

        /**
         * Return the bytes of decode Base64-encode string.
         *
         * @param input The input.
         * @return the string of decode Base64-encode string
         */
        fun base64Decode(input: String?): ByteArray {
            return if (input == null || input.isEmpty()) ByteArray(0) else Base64.decode(
                input,
                Base64.NO_WRAP
            )
        }

        /**
         * Return the bytes of decode Base64-encode bytes.
         *
         * @param input The input.
         * @return the bytes of decode Base64-encode bytes
         */
        fun base64Decode(input: ByteArray?): ByteArray {
            return if (input == null || input.isEmpty()) ByteArray(0) else Base64.decode(
                input,
                Base64.NO_WRAP
            )
        }

        /**
         * Return html-encode string.
         *
         * @param input The input.
         * @return html-encode string
         */
        fun htmlEncode(input: CharSequence?): String {
            if (input == null || input.isEmpty()) return ""
            val sb = StringBuilder()
            var c: Char
            var i = 0
            val len = input.length
            while (i < len) {
                c = input[i]
                when (c) {
                    '<' -> sb.append("&lt;") //$NON-NLS-1$
                    '>' -> sb.append("&gt;") //$NON-NLS-1$
                    '&' -> sb.append("&amp;") //$NON-NLS-1$
                    '\'' ->
                        //http://www.w3.org/TR/xhtml1
                        // The named character reference &apos; (the apostrophe, U+0027) was
                        // introduced in XML 1.0 but does not appear in HTML. Authors should
                        // therefore use &#39; instead of &apos; to work as expected in HTML 4
                        // user agents.
                        sb.append("&#39;") //$NON-NLS-1$
                    '"' -> sb.append("&quot;") //$NON-NLS-1$
                    else -> sb.append(c)
                }
                i++
            }
            return sb.toString()
        }

        /**
         * Return the string of decode html-encode string.
         *
         * @param input The input.
         * @return the string of decode html-encode string
         */
        fun htmlDecode(input: String?): CharSequence {
            if (input == null || input.isEmpty()) return ""
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(input, Html.FROM_HTML_MODE_LEGACY)
            } else {
                Html.fromHtml(input)
            }
        }

        /**
         * Return the binary encoded string padded with one space
         *
         * @param input
         * @return binary string
         */
        fun binEncode(input: String): String {
            val stringBuilder = StringBuilder()
            for (i in input.toCharArray()) {
                stringBuilder.append(Integer.toBinaryString(i.toInt()))
                stringBuilder.append(' ')
            }
            return stringBuilder.toString()
        }

        /**
         * Return UTF-8 String from binary
         *
         * @param input binary string
         * @return UTF-8 String
         */
        fun binDecode(input: String): String {
            val splitted = input.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val sb = StringBuilder()
            for (i in splitted) {
                sb.append(Integer.parseInt(i.replace(" ", ""), 2).toChar())
            }
            return sb.toString()
        }
    }
}