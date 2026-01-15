package com.arfsar.core.utils

import com.arfsar.core.source.remote.response.MovieDetailsResponse
import com.arfsar.core.source.remote.response.MovieResult
import com.arfsar.core.model.Genre
import com.arfsar.core.model.Movie
import com.arfsar.core.model.MovieDetails

object DataMapper {
    fun mapMovieResultToMovie(movieResult: MovieResult): Movie {
        return Movie(
            id = movieResult.id,
            title = movieResult.title,
            posterPath = movieResult.posterPath ?: "",
            backdropPath = movieResult.backdropPath ?: "",
            releaseDate = movieResult.releaseDate ?: "",
            voteAverage = movieResult.voteAverage ?: 0.0
        )
    }

    fun mapMovieDetailsResponseToMovieDetails(movieDetailsResponse: MovieDetailsResponse): MovieDetails {
        return MovieDetails(
            id = movieDetailsResponse.id,
            title = movieDetailsResponse.title,
            overview = movieDetailsResponse.overview ?: "",
            posterPath = movieDetailsResponse.posterPath ?: "",
            releaseDate = movieDetailsResponse.releaseDate ?: "",
            voteAverage = movieDetailsResponse.voteAverage ?: 0.0,
            genres = movieDetailsResponse.genres.map { Genre(it.id, it.name) }
        )
    }
}
