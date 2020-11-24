package com.example.newsapp.background

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

private const val TAG = "NewsWorkManager"

class NewsWorkManager(private val context: Context, workParams: WorkerParameters) :
    Worker(context, workParams) {
    override fun doWork(): Result {
        Log.d(TAG, "doWork: ")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(Intent(context, NewsBackgroundService::class.java))
        } else {
            context.startService(Intent(context, NewsBackgroundService::class.java))
        }
        return Result.success()
    }
}