package com.arfsar.core.usecase

import com.arfsar.core.model.Movie
import com.arfsar.core.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTrendingMoviesUseCase @Inject constructor(private val movieRepository: MovieRepository) {
    operator fun invoke(): Flow<Result<List<Movie>>> {
        return movieRepository.getTrendingMovies()
    }
}
