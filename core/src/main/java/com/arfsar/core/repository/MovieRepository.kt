package com.arfsar.core.repository

import androidx.paging.PagingData
import com.arfsar.core.model.Genre
import com.arfsar.core.model.Movie
import com.arfsar.core.model.MovieDetails
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getTrendingMovies(): Flow<Result<PagingData<Movie>>>
    fun getNowPlayingMovies(): Flow<Result<PagingData<Movie>>>
    fun getPopularMovies(): Flow<Result<PagingData<Movie>>>
    fun getTopRatedMovies(): Flow<Result<PagingData<Movie>>>
    fun searchMovies(query: String): Flow<Result<PagingData<Movie>>>
    fun discoverMovies(genreId: String): Flow<Result<PagingData<Movie>>>
    fun getMovieDetails(movieId: Int): Flow<Result<MovieDetails>>
    fun getMovieGenres(): Flow<Result<List<Genre>>>
    fun getFavoriteMovies(): Flow<List<Movie>>
    suspend fun setFavoriteMovie(movie: Movie, state: Boolean)
    fun isMovieFavorite(movieId: Int): Flow<Boolean>
}
