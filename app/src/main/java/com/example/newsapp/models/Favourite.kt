package com.example.newsapp.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import java.util.*

@Entity
data class Favourite(
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
