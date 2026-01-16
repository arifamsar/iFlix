package com.arfsar.core.source.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class GenresListResponse(
    val genres: List<GenreResponse>
)
