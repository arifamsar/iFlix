package com.arfsar.core.usecase

import com.arfsar.core.repository.SearchRepository
import javax.inject.Inject

class AddSearchQueryUseCase @Inject constructor(private val searchRepository: SearchRepository) {
    suspend operator fun invoke(query: String) {
        if (query.isNotBlank()) {
            searchRepository.insertQuery(query.trim())
        }
    }
}
