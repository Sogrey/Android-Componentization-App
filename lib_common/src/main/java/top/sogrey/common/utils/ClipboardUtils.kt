package top.sogrey.common.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import top.sogrey.common.utils.AppUtils.getApp


/**
 * 剪贴板相关
 * @author Sogrey
 * @date 2019/10/26
 */
open class ClipboardUtils {
    companion object {
        /**
         * 复制文本到剪贴板
         *
         * @param text 文本
         */
        fun copyText(text: CharSequence) {
            val cm = getApp().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newPlainText("text", text))
        }

        /**
         * 获取剪贴板的文本
         *
         * @return 剪贴板的文本
         */
        fun getText(): CharSequence? {
            val cm = getApp().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

            val clip = cm.primaryClip
            return if (clip != null && clip.itemCount > 0) {
                clip.getItemAt(0).coerceToText(getApp())
            } else null
        }

        /**
         * 复制uri到剪贴板
         *
         * @param uri uri
         */
        fun copyUri(uri: Uri) {
            val cm = getApp().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newUri(getApp().contentResolver, "uri", uri))
        }

        /**
         * 获取剪贴板的uri
         *
         * @return 剪贴板的uri
         */
        fun getUri(): Uri? {
            val cm = getApp().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = cm.primaryClip
            return if (clip != null && clip.itemCount > 0) {
                clip.getItemAt(0).uri
            } else null
        }

        /**
         * 复制意图到剪贴板
         *
         * @param intent 意图
         */
        fun copyIntent(intent: Intent) {
            val cm = getApp().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newIntent("intent", intent))
        }

        /**
         * 获取剪贴板的意图
         *
         * @return 剪贴板的意图
         */
        fun getIntent(): Intent? {
            val cm = getApp().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = cm.primaryClip
            return if (clip != null && clip.itemCount > 0) {
                clip.getItemAt(0).intent
            } else null
        }
    }
}
