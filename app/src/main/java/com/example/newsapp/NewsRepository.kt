package com.example.newsapp

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.newsapp.database.NewsDatabase
import com.example.newsapp.models.News

private const val TAG = "NewsRepository"

class NewsRepository(context: Context) {
    private val database = Room
        .databaseBuilder(context, NewsDatabase::class.java, "News")
        .build()

    private val newsDao = database.newsDao()

    suspend fun getNews(category: String): List<News> {
        Log.d(TAG, "getNews: $category")
        return newsDao.getNews(category)
    }

    suspend fun addNews(news: News) {
        return newsDao.addNews(news)
    }

    companion object {
        var INSTANCE: NewsRepository? = null

        fun initialize(context: Context) {
            INSTANCE = NewsRepository(context)
        }

        fun getRepo(): NewsRepository {
            return INSTANCE ?: throw IllegalStateException()
        }
    }
}