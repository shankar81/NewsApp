package com.example.newsapp.models

import com.google.gson.annotations.Expose
import androidx.room.*
import java.util.UUID

@Entity
data class News(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    @Expose val title: String,
    @Expose val author: String? = "",
    @Expose val url: String? = "",
    @Expose val urlToImage: String? = "",
    @Expose val publishedAt: String? = "",
    @Expose val content: String? = "",
    @Embedded(prefix = "source_") val source: Source,
    @Expose var category: String? = "",
)
