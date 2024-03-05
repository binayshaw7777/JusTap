package com.binay.shaw.justap.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.binay.shaw.justap.R
import com.binay.shaw.justap.utilities.Logger
import com.binay.shaw.justap.presentation.MainActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let {
            sendNotification(
                it.title, it.body
            )
        }
    }

    override fun onNewToken(token: String) {
        Logger.debugLog("Firebase Token: $token")
        FirebaseMessaging.getInstance().subscribeToTopic(APP_UPDATES)
    }

    private fun sendNotification(messageTitle: String?, messageBody: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val rawBitmap = BitmapFactory.decodeResource(
            resources,
            R.drawable.logo_light
        )
        val channelId = resources.getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo_light)
            .setLargeIcon(rawBitmap)
            .setColor(Color.parseColor("#f83d3a"))
            .setSound(defaultSoundUri)
            .setContentTitle(messageTitle)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                resources.getString(R.string.default_notification_channel_id),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0 , notificationBuilder.build())
    }

    companion object {
        private const val APP_UPDATES = "app-updates"
    }
}