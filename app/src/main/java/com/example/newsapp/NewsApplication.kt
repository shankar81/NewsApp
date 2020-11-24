package com.example.newsapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

class NewsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NewsRepository.initialize(this)

        createNotification(
            id = CHANNEL_ID,
            title = "Notification Title",
            description = "Notification Channel Description"
        )
    }

    private fun createNotification(id: String, title: String, description: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(id, title, NotificationManager.IMPORTANCE_HIGH)
            channel.description = description
            channel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "newsApp-notification-channel-id"
    }
}