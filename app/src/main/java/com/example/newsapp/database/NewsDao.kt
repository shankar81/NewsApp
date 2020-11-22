package com.example.newsapp.database

import androidx.room.*
import com.example.newsapp.models.News

@Dao
interface NewsDao {
    @Query("SELECT * FROM news WHERE category = :category")
    suspend fun getNews(category: String): List<News>

    @Insert
    suspend fun addNews(news: News)
}