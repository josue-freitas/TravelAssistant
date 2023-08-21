package pt.ipp.estg.myapplication.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import pt.ipp.estg.myapplication.MainActivity
import pt.ipp.estg.myapplication.R


class FireBaseMessaging : FirebaseMessagingService() {

    val TAG = "FireBaseMessaging"

    override fun onNewToken(token: String) {
        Log.d(TAG, "Token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")
        Log.d(TAG, "From 1 : ${remoteMessage.data}")
        Log.d(TAG, "From 2: ${remoteMessage.notification?.body}")


        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

            val gplPrice = remoteMessage.data["lpgPrice"]
            val gplDiff = remoteMessage.data["lpgDiff"]

            val gas95Price = remoteMessage.data["95Price"]
            val gas95Diff = remoteMessage.data["95Diff"]

            val gas98Price = remoteMessage.data["98Price"]
            val gas98Diff = remoteMessage.data["98Diff"]

            val dieselPrice = remoteMessage.data["dieselPrice"]
            val dieselDiff = remoteMessage.data["dieselDiff"]

            val bodyStringBuilder: StringBuilder = StringBuilder()

            bodyStringBuilder.append(this.getString(R.string.fcm_next_week) + "\n\n")

            bodyStringBuilder.append(
                this.getRowString(this.getString(R.string.fcm_lpg), gplDiff, gplPrice)
            )
            bodyStringBuilder.append(
                this.getRowString(this.getString(R.string.fcm_diesel), dieselDiff, dieselPrice)
            )
            bodyStringBuilder.append(
                this.getRowString(this.getString(R.string.fcm_gas_95), gas95Diff, gas95Price)
            )
            bodyStringBuilder.append(
                this.getRowString(this.getString(R.string.fcm_gasoline_98), gas98Diff, gas98Price)
            )

            Log.d(TAG, "Body notification: $bodyStringBuilder")

            notifyUser(
                title = this.getString(R.string.fcm_gas_title),
                body = bodyStringBuilder.toString()
            )
        }

        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }
    }

    private fun getRowString(rowInit: String, diff: String?, price: String?): String {
        if (diff == null && price == null) {
            return ""
        }

        val rowStringBuilder: StringBuilder = StringBuilder()

        rowStringBuilder.append("\t$rowInit ")
        rowStringBuilder.append(
            when (diff) {
                "up" -> this.getString(R.string.fcm_will_rise)
                "down" -> this.getString(R.string.fcm_will_drop)
                else -> this.getString(R.string.fcm_will_remain)
            }
        )
        rowStringBuilder.append(" $price €/L\n")
        return rowStringBuilder.toString()
    }

    private fun notifyUser(title: String, body: String) {
        val CHANNELID = "push notification";

        val channel = NotificationChannel(
            CHANNELID,
            CHANNELID,
            NotificationManager.IMPORTANCE_LOW
        )

        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, CHANNELID)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    this.resources,
                    R.drawable.fuel_prices
                )
            )
            .setSmallIcon(R.drawable.compass_logo)
            .setContentTitle(title)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(body)
            )

        val targetIntent = Intent(this, MainActivity::class.java)
        val contentIntent =
            PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        notification.setContentIntent(contentIntent)
        val nManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nManager.notify(0, notification.build())
    }

    fun notifyUser2(title: String, body: String) {
        val CHANNELID = "push notification";

        val channel = NotificationChannel(
            CHANNELID,
            CHANNELID,
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        val builder = NotificationCompat.Builder(this, "2")
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    this.resources,
                    R.drawable.coupon
                )
            )
            .setSmallIcon(R.drawable.compass_logo)
            .setContentTitle(title)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(body)
            )
        with(NotificationManagerCompat.from(this)) {
            notify(1, builder.build())
        }
    }
}