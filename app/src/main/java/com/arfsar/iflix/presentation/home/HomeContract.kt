package com.arfsar.iflix.presentation.home

import com.arfsar.core.model.Movie

/**
 * Contract for the Home screen following MVI architecture
 */
object HomeContract {
    
    /**
     * Represents the state of the Home screen
     */
    data class State(
        val trendingMoviesLoading: Boolean = false,
        val nowPlayingMoviesLoading: Boolean = false,
        val popularMoviesLoading: Boolean = false,
        val topRatedMoviesLoading: Boolean = false,
        val trendingMoviesError: String? = null,
        val nowPlayingMoviesError: String? = null,
        val popularMoviesError: String? = null,
        val topRatedMoviesError: String? = null,
        val isRefreshing: Boolean = false
    )
    
    /**
     * Represents user actions/intents that can be performed on the Home screen
     */
    sealed interface Action {
        object Refresh : Action
        object LoadTrendingMovies : Action
        object LoadNowPlayingMovies : Action
        object LoadPopularMovies : Action
        object LoadTopRatedMovies : Action
        data class NavigateToMovieDetail(val movieId: Int) : Action
    }
    
    /**
     * Represents events that can be emitted by the Home screen
     */
    sealed interface Event {
        data class NavigateToMovieDetail(val movieId: Int) : Event
        data class ShowError(val message: String) : Event
        object RefreshComplete : Event
    }

    enum class PagingAction {
        RELOAD, REFRESH, RETRY
    }
}