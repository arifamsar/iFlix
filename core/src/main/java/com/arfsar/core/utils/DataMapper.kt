package com.arfsar.core.utils

import com.arfsar.core.source.remote.response.MovieDetailsResponse
import com.arfsar.core.source.remote.response.MovieResult
import com.arfsar.core.source.remote.response.ProductionCompanyResponse
import com.arfsar.core.source.remote.response.ProductionCountryResponse
import com.arfsar.core.source.remote.response.SpokenLanguageResponse
import com.arfsar.core.model.Genre
import com.arfsar.core.model.Movie
import com.arfsar.core.model.MovieDetails
import com.arfsar.core.model.ProductionCompany
import com.arfsar.core.model.ProductionCountry
import com.arfsar.core.model.SpokenLanguage
import com.arfsar.core.source.local.entity.MovieEntity

object DataMapper {
    fun mapMovieToEntity(movie: Movie): MovieEntity {
        return MovieEntity(
            id = movie.id,
            title = movie.title,
            posterPath = movie.posterPath,
            backdropPath = movie.backdropPath,
            releaseDate = movie.releaseDate,
            voteAverage = movie.voteAverage,
            isFavorite = false
        )
    }

    fun mapEntityToMovie(entity: MovieEntity): Movie {
        return Movie(
            id = entity.id,
            title = entity.title,
            posterPath = entity.posterPath,
            backdropPath = entity.backdropPath,
            releaseDate = entity.releaseDate,
            voteAverage = entity.voteAverage
        )
    }

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
            backdropPath = movieDetailsResponse.backdropPath ?: "",
            releaseDate = movieDetailsResponse.releaseDate ?: "",
            voteAverage = movieDetailsResponse.voteAverage ?: 0.0,
            voteCount = movieDetailsResponse.voteCount,
            genres = movieDetailsResponse.genres.map { Genre(it.id, it.name) },
            budget = movieDetailsResponse.budget,
            revenue = movieDetailsResponse.revenue,
            runtime = movieDetailsResponse.runtime,
            tagline = movieDetailsResponse.tagline,
            status = movieDetailsResponse.status,
            originalLanguage = movieDetailsResponse.originalLanguage,
            popularity = movieDetailsResponse.popularity,
            productionCompanies = movieDetailsResponse.productionCompanies.map {
                mapProductionCompanyResponseToProductionCompany(it)
            },
            productionCountries = movieDetailsResponse.productionCountries.map {
                mapProductionCountryResponseToProductionCountry(it)
            },
            spokenLanguages = movieDetailsResponse.spokenLanguages.map {
                mapSpokenLanguageResponseToSpokenLanguage(it)
            },
            homepage = movieDetailsResponse.homepage ?: "",
            imdbId = movieDetailsResponse.imdbId ?: ""
        )
    }

    private fun mapProductionCompanyResponseToProductionCompany(response: ProductionCompanyResponse): ProductionCompany {
        return ProductionCompany(
            id = response.id,
            name = response.name,
            logoPath = response.logoPath,
            originCountry = response.originCountry
        )
    }

    private fun mapProductionCountryResponseToProductionCountry(response: ProductionCountryResponse): ProductionCountry {
        return ProductionCountry(
            iso31661 = response.iso31661,
            name = response.name
        )
    }

    private fun mapSpokenLanguageResponseToSpokenLanguage(response: SpokenLanguageResponse): SpokenLanguage {
        return SpokenLanguage(
            iso6391 = response.iso6391,
            name = response.name,
            englishName = response.englishName
        )
    }
}

