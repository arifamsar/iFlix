package com.arfsar.core.source.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDetailsResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("title")
    val title: String,
    @SerialName("overview")
    val overview: String?,
    @SerialName("poster_path")
    val posterPath: String?,
    @SerialName("backdrop_path")
    val backdropPath: String?,
    @SerialName("release_date")
    val releaseDate: String?,
    @SerialName("vote_average")
    val voteAverage: Double?,
    @SerialName("vote_count")
    val voteCount: Int = 0,
    @SerialName("genres")
    val genres: List<GenreResponse> = emptyList(),
    @SerialName("budget")
    val budget: Long = 0,
    @SerialName("revenue")
    val revenue: Long = 0,
    @SerialName("runtime")
    val runtime: Int = 0,
    @SerialName("tagline")
    val tagline: String = "",
    @SerialName("status")
    val status: String = "",
    @SerialName("original_language")
    val originalLanguage: String = "",
    @SerialName("popularity")
    val popularity: Double = 0.0,
    @SerialName("production_companies")
    val productionCompanies: List<ProductionCompanyResponse> = emptyList(),
    @SerialName("production_countries")
    val productionCountries: List<ProductionCountryResponse> = emptyList(),
    @SerialName("spoken_languages")
    val spokenLanguages: List<SpokenLanguageResponse> = emptyList(),
    @SerialName("homepage")
    val homepage: String = "",
    @SerialName("imdb_id")
    val imdbId: String = ""
)

@Serializable
data class ProductionCompanyResponse(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("name")
    val name: String = "",
    @SerialName("logo_path")
    val logoPath: String? = null,
    @SerialName("origin_country")
    val originCountry: String = ""
)

@Serializable
data class ProductionCountryResponse(
    @SerialName("iso_3166_1")
    val iso31661: String = "",
    @SerialName("name")
    val name: String = ""
)

@Serializable
data class SpokenLanguageResponse(
    @SerialName("iso_639_1")
    val iso6391: String = "",
    @SerialName("name")
    val name: String = "",
    @SerialName("english_name")
    val englishName: String = ""
)
