package com.arfsar.core.usecase

import com.arfsar.core.repository.SearchRepository
import com.arfsar.core.source.local.entity.SearchQueryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSearchHistoryUseCase @Inject constructor(private val searchRepository: SearchRepository) {
    operator fun invoke(): Flow<List<SearchQueryEntity>> {
        return searchRepository.getSearchHistory()
    }
}
