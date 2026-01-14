package com.arfsar.core.usecase

import com.arfsar.core.model.MovieDetails
import com.arfsar.core.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMovieDetailsUseCase @Inject constructor(private val movieRepository: MovieRepository) {
    operator fun invoke(movieId: Int): Flow<Result<MovieDetails>> {
        return movieRepository.getMovieDetails(movieId)
    }
}
