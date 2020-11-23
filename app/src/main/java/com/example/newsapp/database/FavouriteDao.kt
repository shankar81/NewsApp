package com.example.newsapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.newsapp.models.Favourite

@Dao
interface FavouriteDao {
    @Query("SELECT * FROM favourite")
    suspend fun getFavourites(): List<Favourite>

    @Insert
    suspend fun addFavourite(news: Favourite)

    @Delete
    suspend fun removeFavourite(news: Favourite)
}