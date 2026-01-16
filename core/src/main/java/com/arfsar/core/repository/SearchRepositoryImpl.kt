package com.arfsar.core.repository

import com.arfsar.core.source.local.entity.SearchQueryEntity
import com.arfsar.core.source.local.room.SearchQueryDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepositoryImpl @Inject constructor(
    private val searchQueryDao: SearchQueryDao
) : SearchRepository {
    override fun getSearchHistory(): Flow<List<SearchQueryEntity>> {
        return searchQueryDao.getSearchHistory()
    }

    override suspend fun insertQuery(query: String) {
        searchQueryDao.insertQuery(SearchQueryEntity(query = query))
    }

    override suspend fun deleteQuery(query: String) {
        searchQueryDao.deleteQuery(query)
    }

    override suspend fun clearHistory() {
        searchQueryDao.clearHistory()
    }
}
