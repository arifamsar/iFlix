package com.arfsar.core.model

data class MovieDetails(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String,
    val backdropPath: String,
    val releaseDate: String,
    val voteAverage: Double,
    val voteCount: Int,
    val genres: List<Genre>,
    val budget: Long,
    val revenue: Long,
    val runtime: Int,
    val tagline: String,
    val status: String,
    val originalLanguage: String,
    val popularity: Double,
    val productionCompanies: List<ProductionCompany>,
    val productionCountries: List<ProductionCountry>,
    val spokenLanguages: List<SpokenLanguage>,
    val homepage: String,
    val imdbId: String
)

data class ProductionCompany(
    val id: Int,
    val name: String,
    val logoPath: String?,
    val originCountry: String
)

data class ProductionCountry(
    val iso31661: String,
    val name: String
)

data class SpokenLanguage(
    val iso6391: String,
    val name: String,
    val englishName: String
)
