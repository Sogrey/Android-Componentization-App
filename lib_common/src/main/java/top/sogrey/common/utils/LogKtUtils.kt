package top.sogrey.common.utils

import android.util.Log
import top.sogrey.common.AppConfig
import java.util.*

/**
 * 日志<br/>
 * 用法：<br/>
 * java:<br/>
 *     LogKtUtils.Companion.e("LogKtUtils Message...")<br/>
 * kotlin:<br/>
 *     logE("logE Message...")<br/>
 *     LogKtUtils.e("LogKtUtils Message...")<br/>
 * @author Sogrey
 * @date 2019/10/26
 */
open class LogKtUtils {
    private fun LogKtUtils(): LogKtUtils {
        throw UnsupportedOperationException(this.javaClass.simpleName + " cannot be instantiated")
    }

    companion object {
        private var tag = AppConfig.APP_TAG
        private val sCachedTag = HashMap<String, String>()
        private var sJsonFormatter: JsonFormatter = DefaultFormatter()

        fun init(appTag: String, formatter: JsonFormatter) {
            tag = appTag
            sJsonFormatter = formatter
        }

        fun i(message: String) {
            logI(buildMessage(message), buildTag(tag))
        }

        fun d(message: String) {
            logD(buildMessage(message), buildTag(tag))
        }

        fun w(message: String) {
            logW(buildMessage(message), buildTag(tag))
        }

        fun e(message: String) {
            logE(buildMessage(message), buildTag(tag))
        }

        fun v(message: String) {
            logV(buildMessage(message), buildTag(tag))
        }

        fun wtf(message: String) {
            logWtf(buildMessage(message), buildTag(tag))
        }

        fun json(message: String) {
            logI(buildMessage(message), buildTag(tag))
        }

        fun i(tag: String, message: String) {
            logI(buildMessage(message), buildTag(tag))
        }

        fun d(tag: String, message: String) {
            logD(buildMessage(message), buildTag(tag))
        }

        fun w(tag: String, message: String) {
            logW(buildMessage(message), buildTag(tag))
        }

        fun e(tag: String, message: String) {
            logE(buildMessage(message), buildTag(tag))
        }

        fun v(tag: String, message: String) {
            logV(buildMessage(message), buildTag(tag))
        }

        fun wtf(tag: String, message: String) {
            logWtf(buildMessage(message), buildTag(tag))
        }

        fun json(tag: String, message: String) {
            logI(buildMessage(message), buildTag(tag))
        }

        private fun buildTag(tag: String): String {
            val key = String.format(Locale.US, "%s@%s", tag, Thread.currentThread().name)

            if (!sCachedTag.containsKey(key)) {
                if (this.tag == tag) {
                    sCachedTag[key] = String.format(
                        Locale.US, "|%s|%s|",
                        tag,
                        Thread.currentThread().name
                    )
                } else {
                    sCachedTag[key] = String.format(
                        Locale.US, "|%s_%s|%s|",
                        this.tag,
                        tag,
                        Thread.currentThread().name
                    )
                }
            } else {
                if (sCachedTag[key].isNullOrEmpty()) {
                    sCachedTag[key] = String.format(
                        Locale.US, "|%s|%s|",
                        tag,
                        Thread.currentThread().name
                    )
                }
            }
            return sCachedTag[key]!!
        }

        private fun buildMessage(message: String): String {
            val traceElements = Thread.currentThread().stackTrace

            if (traceElements == null || traceElements.size < 4) {
                return message
            }
            val traceElement = traceElements[4]

            return String.format(
                Locale.US, "%s.%s(%s:%d) %s",
                traceElement.className.substring(traceElement.className.lastIndexOf(".") + 1),
                traceElement.methodName,
                traceElement.fileName,
                traceElement.lineNumber,
                message
            )
        }

        private fun formatJson(content: String): String {
            return String.format(Locale.US, "\n%s", sJsonFormatter.formatJson(content))
        }

        interface JsonFormatter {
            /**
             * 格式化JSON
             * @param content
             * @return
             */
            fun formatJson(content: String): String
        }

        private class DefaultFormatter : JsonFormatter {
            override fun formatJson(content: String): String {
                return content
            }
        }
    }
}

fun logE(msg: String, tag: String = AppConfig.APP_TAG) {
    if (AppConfig.DEBUG) Log.e(tag, msg)
}

fun logW(msg: String, tag: String = AppConfig.APP_TAG) {
    if (AppConfig.DEBUG) Log.w(tag, msg)
}

fun logI(msg: String, tag: String = AppConfig.APP_TAG) {
    if (AppConfig.DEBUG) Log.i(tag, msg)
}

fun logD(msg: String, tag: String = AppConfig.APP_TAG) {
    if (AppConfig.DEBUG) Log.d(tag, msg)
}

fun logV(msg: String, tag: String = AppConfig.APP_TAG) {
    if (AppConfig.DEBUG) Log.v(tag, msg)
}

fun logWtf(msg: String, tag: String = AppConfig.APP_TAG) {
    if (AppConfig.DEBUG) Log.wtf(tag, msg)
}