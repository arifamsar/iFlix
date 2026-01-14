package com.arfsar.core.usecase

import androidx.paging.PagingData
import com.arfsar.core.model.Movie
import com.arfsar.core.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchMoviesUseCase @Inject constructor(private val movieRepository: MovieRepository) {
    operator fun invoke(query: String): Flow<Result<PagingData<Movie>>> {
        return movieRepository.searchMovies(query)
    }
}
