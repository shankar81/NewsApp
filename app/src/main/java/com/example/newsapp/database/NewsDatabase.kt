package com.example.newsapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.newsapp.models.Favourite
import com.example.newsapp.models.News

@Database(entities = [News::class, Favourite::class], version = 1)
@TypeConverters(NewsTypeConverter::class)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao
    abstract fun favouriteDao(): FavouriteDao
}