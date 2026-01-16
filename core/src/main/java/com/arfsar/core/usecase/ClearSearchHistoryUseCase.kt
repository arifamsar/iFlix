package com.arfsar.core.usecase

import com.arfsar.core.repository.SearchRepository
import javax.inject.Inject

class ClearSearchHistoryUseCase @Inject constructor(private val searchRepository: SearchRepository) {
    suspend operator fun invoke() {
        searchRepository.clearHistory()
    }
}
