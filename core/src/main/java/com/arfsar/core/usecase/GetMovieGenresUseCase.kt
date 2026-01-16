package com.arfsar.core.usecase

import com.arfsar.core.model.Genre
import com.arfsar.core.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMovieGenresUseCase @Inject constructor(private val movieRepository: MovieRepository) {
    operator fun invoke(): Flow<Result<List<Genre>>> {
        return movieRepository.getMovieGenres()
    }
}
