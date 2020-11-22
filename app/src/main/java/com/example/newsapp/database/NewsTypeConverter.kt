package com.example.newsapp.database

import androidx.room.TypeConverter
import java.util.UUID

class NewsTypeConverter {
    @TypeConverter
    fun fromUUID(uuid: UUID): String {
        return uuid.toString()
    }

    @TypeConverter
    fun toUUID(uuid: String): UUID {
        return UUID.fromString(uuid)
    }
}