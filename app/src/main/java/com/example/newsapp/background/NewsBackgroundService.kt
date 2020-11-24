package com.example.newsapp.background

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.newsapp.NewsApplication
import com.example.newsapp.NewsRepository
import com.example.newsapp.R
import com.example.newsapp.api.NetworkService
import com.example.newsapp.api.NewsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

private const val TAG = "NewsBackgroundService"
private const val NOTIFICATION_ID = 2

class NewsBackgroundService : Service() {
    private val repository = NewsRepository.getRepo()
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var notificationManager: NotificationManager? = null
    private val categories = arrayListOf(
        "business",
        "entertainment",
        "general",
        "health",
        "science",
        "sports",
        "technology",
    )

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notificationManager =
            baseContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        startForeground(NOTIFICATION_ID, getNotification())
        getNewsOnline(categories[0])
        categories.removeFirstOrNull()

        return START_STICKY
    }

    private fun getNewsOnline(category: String) {
        try {
            coroutineScope.launch {
                val res =
                    NetworkService.retrofitService.create(NewsApi::class.java)
                        .getNews("", category, "in")
                Log.d(TAG, "getNewsOnline: DO API CALL WORKER $category $categories")
                res.articles.map {
                    it.category = category
                    it.id = UUID.randomUUID().toString()
                }
                res.articles.map {
                    repository.addNews(it)
                }
                if (categories.isNotEmpty()) {
                    startForeground(NOTIFICATION_ID, getNotification())
                    getNewsOnline(categories[0])
                    categories.removeFirstOrNull()
                } else {
                    stopSelf()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getNotification(currentCategory: String = categories[0]): Notification {
        return NotificationCompat.Builder(baseContext, NewsApplication.CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_delete)
            .setProgress(7, 7 - (categories.size - 1), false)
            .setContentTitle("Getting Latest News")
            .setContentText("(${currentCategory})")
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSelf()
    }
}