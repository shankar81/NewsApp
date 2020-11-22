package com.example.newsapp.models

import com.example.newsapp.models.News
import com.google.gson.annotations.Expose

data class ApiResponse(
    @Expose val status: String,
    @Expose val totalResults: Int,
    @Expose val articles: List<News>
)
