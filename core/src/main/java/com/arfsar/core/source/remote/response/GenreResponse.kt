package com.arfsar.core.source.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class GenreResponse(
    val id: Int,
    val name: String
)
