package com.arfsar.core.repository

import com.arfsar.core.source.local.entity.SearchQueryEntity
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun getSearchHistory(): Flow<List<SearchQueryEntity>>
    suspend fun insertQuery(query: String)
    suspend fun deleteQuery(query: String)
    suspend fun clearHistory()
}
