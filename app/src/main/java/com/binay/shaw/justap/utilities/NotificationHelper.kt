package com.binay.shaw.justap.utilities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

/**
 * Created by binay on 06,February,2023
 */
class NotificationHelper(context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun createNotificationChannel(channelId: String, channelName: String) {
        // Handle Notification Channel creation for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(notificationId: Int, builder: NotificationCompat.Builder) {
        notificationManager.notify(notificationId, builder.build())
    }
}