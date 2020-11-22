package com.example.newsapp.database

import androidx.room.RoomDatabase
import androidx.room.Database
import androidx.room.TypeConverters
import com.example.newsapp.models.News

@Database(entities = [News::class], version = 1)
@TypeConverters(NewsTypeConverter::class)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao
}