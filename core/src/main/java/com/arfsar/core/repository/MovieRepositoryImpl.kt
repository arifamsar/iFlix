package com.arfsar.core.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.arfsar.core.source.remote.MoviePagingSource
import com.arfsar.core.source.remote.network.ApiService
import com.arfsar.core.utils.DataMapper
import com.arfsar.core.model.Movie
import com.arfsar.core.model.MovieDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepositoryImpl @Inject constructor(private val apiService: ApiService) : MovieRepository {
    override fun getTrendingMovies(): Flow<Result<List<Movie>>> = flow {
        try {
            val response = apiService.getTrendingMovies()
            emit(Result.success(response.results.map { DataMapper.mapMovieResultToMovie(it) }))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getNowPlayingMovies(): Flow<Result<List<Movie>>> = flow {
        try {
            val response = apiService.getNowPlayingMovies()
            emit(Result.success(response.results.map { DataMapper.mapMovieResultToMovie(it) }))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getPopularMovies(): Flow<Result<PagingData<Movie>>> {
        return try {
            Pager(
                config = PagingConfig(pageSize = 20, enablePlaceholders = false),
                pagingSourceFactory = { MoviePagingSource(apiService, "popular") }
            ).flow.map { pagingData -> Result.success(pagingData) }
        } catch (e: Exception) {
            flow { emit(Result.failure(e)) }
        }
    }

    override fun getTopRatedMovies(): Flow<Result<PagingData<Movie>>> {
        return try {
            Pager(
                config = PagingConfig(pageSize = 20, enablePlaceholders = false),
                pagingSourceFactory = { MoviePagingSource(apiService, "top_rated") }
            ).flow.map { pagingData -> Result.success(pagingData) }
        } catch (e: Exception) {
            flow { emit(Result.failure(e)) }
        }
    }

    override fun searchMovies(query: String): Flow<Result<PagingData<Movie>>> {
        return try {
            Pager(
                config = PagingConfig(pageSize = 20, enablePlaceholders = false),
                pagingSourceFactory = { MoviePagingSource(apiService, "search", query) }
            ).flow.map { pagingData -> Result.success(pagingData) }
        } catch (e: Exception) {
            flow { emit(Result.failure(e)) }
        }
    }

    override fun getMovieDetails(movieId: Int): Flow<Result<MovieDetails>> = flow {
        try {
            val response = apiService.getMovieDetails(movieId)
            emit(Result.success(DataMapper.mapMovieDetailsResponseToMovieDetails(response)))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
