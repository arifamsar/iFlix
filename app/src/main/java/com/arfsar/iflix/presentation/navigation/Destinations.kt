package com.arfsar.iflix.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed class Destinations {
    @Serializable
    data object Home : Destinations()

    @Serializable
    data object Discover : Destinations()

    @Serializable
    data object Collections : Destinations()

    @Serializable
    data class MovieDetails(val movieId: Int) : Destinations()
}
