package com.example.newsapp.background

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.newsapp.NewsApplication
import com.example.newsapp.NewsRepository
import com.example.newsapp.R
import com.example.newsapp.api.NetworkService
import com.example.newsapp.api.NewsApi
import com.example.newsapp.models.News
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
        coroutineScope.launch {
            repository.truncateTable()
        }

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
                showNewsNotification(categories.size + 3, res.articles[0])
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
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setContentText("(${currentCategory})")
            .build()
    }

    private fun showNewsNotification(index: Int, news: News) {
        // Custom Notification Views
        val remoteCustomContent = RemoteViews(packageName, R.layout.notification_content_view)
        remoteCustomContent.setTextViewText(R.id.notificationTitle, news.title)
        remoteCustomContent.setTextViewText(R.id.notificationTitle, news.title)
        val remoteCustomContentLarge = RemoteViews(packageName, R.layout.notification_large_view)
        // Converting Resource id to Bitmap with Glide
        Glide.with(this).asBitmap().load(R.mipmap.travel).into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                val notification =
                    NotificationCompat.Builder(baseContext, NewsApplication.CHANNEL_ID)
                        .setSmallIcon(R.drawable.icon_delete)
                        .setCustomContentView(remoteCustomContent)
                        .setCustomBigContentView(remoteCustomContentLarge)
                        .build()

                notificationManager?.notify(
                    index,
                    notification
                )
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSelf()
    }
}