package com.example.newsapp.api

import com.example.newsapp.models.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("top-headlines")
    suspend fun getNews(
        @Query("q") query: String,
        @Query("category") category: String,
        @Query("country") country: String
    ): ApiResponse
}

