package com.example.newsapp.background

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.work.*
import java.util.concurrent.TimeUnit

private const val PERIODIC_WORK_REQUEST_NAME = "One_Time_Worker"

class OnAppKilledService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        WorkManager
            .getInstance(baseContext)
            .cancelAllWork()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Set up work request only when app is background
        // And change this request to Periodic Request
        // And also keep the time delay between 2 hours - 4 hours
        val constraints = Constraints
            .Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()

        val periodWorkRequest =
            PeriodicWorkRequest
                .Builder(NewsWorkManager::class.java, 3, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

        WorkManager
            .getInstance(this)
            .enqueueUniquePeriodicWork(
                PERIODIC_WORK_REQUEST_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                periodWorkRequest
            )
        stopSelf()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }
}