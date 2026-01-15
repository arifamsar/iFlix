package com.arfsar.core.source.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.arfsar.core.source.remote.network.ApiService
import com.arfsar.core.utils.DataMapper
import com.arfsar.core.model.Movie

class MoviePagingSource(
    private val apiService: ApiService,
    private val source: String,
    private val query: String? = null
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val page = params.key ?: 1
            val response = when (source) {
                "popular" -> apiService.getPopularMovies(page = page)
                "top_rated" -> apiService.getTopRatedMovies(page = page)
                "trending" -> apiService.getTrendingMovies(page = page)
                "now_playing" -> apiService.getNowPlayingMovies(page = page)
                "search" -> apiService.searchMovies(query = query ?: "", page = page)
                else -> throw IllegalArgumentException("Invalid source: $source")
            }
            val movies = response.results.map { DataMapper.mapMovieResultToMovie(it) }
            LoadResult.Page(
                data = movies,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (movies.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
