package com.arfsar.core.repository

import androidx.paging.PagingData
import com.arfsar.core.model.Movie
import com.arfsar.core.model.MovieDetails
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getTrendingMovies(): Flow<Result<List<Movie>>>
    fun getPopularMovies(): Flow<Result<PagingData<Movie>>>
    fun getTopRatedMovies(): Flow<Result<PagingData<Movie>>>
    fun searchMovies(query: String): Flow<Result<PagingData<Movie>>>
    fun getMovieDetails(movieId: Int): Flow<Result<MovieDetails>>
}
