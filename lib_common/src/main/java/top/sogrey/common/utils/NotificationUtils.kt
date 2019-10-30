package top.sogrey.common.utils

import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationCompat
import android.content.Context.NOTIFICATION_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.Nullable


/**
 * 通知相关
 * <p/>
 * @author Sogrey
 * @date 2019-10-31 0:43
 */

class NotificationUtils {
    constructor() {
        throw UnsupportedOperationException(this.javaClass.simpleName + " cannot be instantiated")
    }

    companion object {
        fun create(
            context: Context,
            id: Int,
            intent: Intent,
            smallIcon: Int,
            contentTitle: String,
            contentText: String
        ) {
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Intent para disparar o broadcast
            val p = PendingIntent.getActivity(
                AppUtils.getApp(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            // Cria a notification
            val builder = NotificationCompat.Builder(context)
                .setContentIntent(p)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(smallIcon)
                .setAutoCancel(true)

            // Dispara a notification
            val n = builder.build()
            manager.notify(id, n)
        }

        fun createStackNotification(
            context: Context,
            id: Int,
            groupId: String,
            intent: Intent?,
            smallIcon: Int,
            contentTitle: String,
            contentText: String
        ) {
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Intent para disparar o broadcast
            val p = if (intent != null) PendingIntent.getActivity(
                AppUtils.getApp(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            ) else null

            // Cria a notification
            val builder = NotificationCompat.Builder(context)
                .setContentIntent(p)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(smallIcon)
                .setGroup(groupId)
                .setAutoCancel(true)

            // Dispara a notification
            val n = builder.build()
            manager.notify(id, n)
        }

        // Notificação simples sem abrir intent (usada para alertas, ex: no wear)
        fun create(smallIcon: Int, contentTitle: String, contentText: String) {
            val manager =
                AppUtils.getApp().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Cria a notification
            val builder = NotificationCompat.Builder(AppUtils.getApp())
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(smallIcon)
                .setAutoCancel(true)

            // Dispara a notification
            val n = builder.build()
            manager.notify(0, n)
        }

        fun cancel(@Nullable tag: String, id: Int) {
            NotificationManagerCompat.from(AppUtils.getApp()).cancel(tag, id)
        }

        fun cancel(id: Int) {
            NotificationManagerCompat.from(AppUtils.getApp()).cancel(id)
        }

        fun cancelAll() {
            NotificationManagerCompat.from(AppUtils.getApp()).cancelAll()
        }
    }
}