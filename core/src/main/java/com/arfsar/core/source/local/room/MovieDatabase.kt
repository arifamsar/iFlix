package com.arfsar.core.source.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.arfsar.core.source.local.entity.MovieEntity
import com.arfsar.core.source.local.entity.SearchQueryEntity

@Database(entities = [MovieEntity::class, SearchQueryEntity::class], version = 1, exportSchema = false)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun searchQueryDao(): SearchQueryDao
}
