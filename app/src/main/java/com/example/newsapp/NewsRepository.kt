package com.example.newsapp

import android.content.Context
import androidx.room.Room
import com.example.newsapp.database.NewsDatabase
import com.example.newsapp.models.Favourite
import com.example.newsapp.models.News

class NewsRepository(context: Context) {
    private val database = Room
        .databaseBuilder(context, NewsDatabase::class.java, "News")
        .build()

    private val newsDao = database.newsDao()
    private val favouriteDao = database.favouriteDao()

    suspend fun getNews(category: String): List<News> {
        return newsDao.getNews(category)
    }

    suspend fun addNews(news: News) {
        return newsDao.addNews(news)
    }

    suspend fun getFavourites(): List<Favourite> {
        return favouriteDao.getFavourites()
    }

    suspend fun addFavourite(news: News) {
        news.apply {
            return favouriteDao.addFavourite(
                Favourite(
                    id,
                    title,
                    author,
                    url,
                    urlToImage,
                    publishedAt,
                    content,
                    source,
                    category
                )
            )
        }
    }

    suspend fun removeFavourite(favourite: Favourite) {
        return favouriteDao.removeFavourite(favourite)
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