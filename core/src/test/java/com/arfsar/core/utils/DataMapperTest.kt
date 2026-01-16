package com.arfsar.core.utils

import com.arfsar.core.model.Movie
import com.arfsar.core.source.local.entity.MovieEntity
import com.arfsar.core.source.remote.response.MovieResult
import org.junit.Assert.assertEquals
import org.junit.Test

class DataMapperTest {

    @Test
    fun `mapMovieToEntity should map correctly`() {
        val movie = Movie(
            id = 1,
            title = "Test Movie",
            posterPath = "/path",
            backdropPath = "/backdrop",
            releaseDate = "2024-01-01",
            voteAverage = 8.5,
            genreIds = listOf(1, 2)
        )

        val entity = DataMapper.mapMovieToEntity(movie)

        assertEquals(movie.id, entity.id)
        assertEquals(movie.title, entity.title)
        assertEquals(movie.posterPath, entity.posterPath)
        assertEquals(movie.backdropPath, entity.backdropPath)
        assertEquals(movie.releaseDate, entity.releaseDate)
        assertEquals(movie.voteAverage, entity.voteAverage, 0.0)
        assertEquals(false, entity.isFavorite)
    }

    @Test
    fun `mapMovieResultToMovie should map correctly with genreIds`() {
        val movieResult = MovieResult(
            id = 1,
            title = "Test Movie",
            posterPath = "/path",
            backdropPath = "/backdrop",
            releaseDate = "2024-01-01",
            voteAverage = 8.5,
            genreIds = listOf(1, 2)
        )

        val movie = DataMapper.mapMovieResultToMovie(movieResult)

        assertEquals(movieResult.id, movie.id)
        assertEquals(movieResult.title, movie.title)
        assertEquals(movieResult.genreIds, movie.genreIds)
    }

    @Test
    fun `mapMovieResultToMovie should handle null fields`() {
        val movieResult = MovieResult(
            id = 1,
            title = "Test Movie",
            posterPath = null,
            backdropPath = null,
            releaseDate = null,
            voteAverage = null,
            genreIds = null
        )

        val movie = DataMapper.mapMovieResultToMovie(movieResult)

        assertEquals("", movie.posterPath)
        assertEquals("", movie.backdropPath)
        assertEquals("", movie.releaseDate)
        assertEquals(0.0, movie.voteAverage, 0.0)
        assertEquals(emptyList<Int>(), movie.genreIds)
    }
}
