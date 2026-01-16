package com.arfsar.core.usecase

import com.arfsar.core.repository.SearchRepository
import javax.inject.Inject

class DeleteSearchQueryUseCase @Inject constructor(private val searchRepository: SearchRepository) {
    suspend operator fun invoke(query: String) {
        searchRepository.deleteQuery(query)
    }
}
