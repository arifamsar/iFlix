package com.arfsar.iflix.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.arfsar.core.model.Movie
import com.arfsar.core.usecase.GetNowPlayingMoviesUseCase
import com.arfsar.core.usecase.GetPopularMoviesUseCase
import com.arfsar.core.usecase.GetTopRatedMoviesUseCase
import com.arfsar.core.usecase.GetTrendingMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTrendingMoviesUseCase: GetTrendingMoviesUseCase,
    private val getNowPlayingMoviesUseCase: GetNowPlayingMoviesUseCase,
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
    private val getTopRatedMoviesUseCase: GetTopRatedMoviesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeContract.State())
    val uiState: StateFlow<HomeContract.State> = _uiState.asStateFlow()

    private val _event = Channel<HomeContract.Event>()
    val event: Flow<HomeContract.Event> = _event.receiveAsFlow()

    // For paging data - all sections now use paging
    val trendingMovies = getTrendingMoviesUseCase()
        .map { result ->
            result.onSuccess {
                updateState { copy(trendingMoviesLoading = false, trendingMoviesError = null) }
            }.onFailure { error ->
                updateState { copy(trendingMoviesLoading = false, trendingMoviesError = error.message) }
            }
            result.getOrNull() ?: PagingData.empty()
        }
        .cachedIn(viewModelScope)

    val nowPlayingMovies = getNowPlayingMoviesUseCase()
        .map { result ->
            result.onSuccess {
                updateState { copy(nowPlayingMoviesLoading = false, nowPlayingMoviesError = null) }
            }.onFailure { error ->
                updateState { copy(nowPlayingMoviesLoading = false, nowPlayingMoviesError = error.message) }
            }
            result.getOrNull() ?: PagingData.empty()
        }
        .cachedIn(viewModelScope)

    val popularMovies = getPopularMoviesUseCase()
        .map { result ->
            result.onSuccess {
                updateState { copy(popularMoviesLoading = false, popularMoviesError = null) }
            }.onFailure { error ->
                updateState { copy(popularMoviesLoading = false, popularMoviesError = error.message) }
            }
            result.getOrNull() ?: PagingData.empty()
        }
        .cachedIn(viewModelScope)

    val topRatedMovies = getTopRatedMoviesUseCase()
        .map { result ->
            result.onSuccess {
                updateState { copy(topRatedMoviesLoading = false, topRatedMoviesError = null) }
            }.onFailure { error ->
                updateState { copy(topRatedMoviesLoading = false, topRatedMoviesError = error.message) }
            }
            result.getOrNull() ?: PagingData.empty()
        }
        .cachedIn(viewModelScope)

    init {
        // Loading states are managed by paging's loadState
    }

    fun accept(action: HomeContract.Action) {
        when (action) {
            is HomeContract.Action.Refresh -> refresh()
            is HomeContract.Action.LoadTrendingMovies -> loadTrendingMovies()
            is HomeContract.Action.LoadNowPlayingMovies -> loadNowPlayingMovies()
            is HomeContract.Action.LoadPopularMovies -> loadPopularMovies()
            is HomeContract.Action.LoadTopRatedMovies -> loadTopRatedMovies()
            is HomeContract.Action.NavigateToMovieDetail -> navigateToMovieDetail(action.movieId)
        }
    }

    private fun updateState(update: HomeContract.State.() -> HomeContract.State) {
        _uiState.value = _uiState.value.update()
    }

    private fun loadTrendingMovies() {
        updateState { copy(trendingMoviesLoading = true, trendingMoviesError = null) }
    }

    private fun loadNowPlayingMovies() {
        updateState { copy(nowPlayingMoviesLoading = true, nowPlayingMoviesError = null) }
    }

    private fun loadPopularMovies() {
        updateState { copy(popularMoviesLoading = true, popularMoviesError = null) }
    }

    private fun loadTopRatedMovies() {
        updateState { copy(topRatedMoviesLoading = true, topRatedMoviesError = null) }
    }

    private fun refresh() {
        // The isRefreshing state is only used as a flag for pull-to-refresh UI
        // The actual loading is managed by paging's loadState
        updateState { copy(isRefreshing = true) }

        viewModelScope.launch {
            // Brief delay to allow the UI to register the refresh gesture
            kotlinx.coroutines.delay(100)
            updateState { copy(isRefreshing = false) }
            _event.trySend(HomeContract.Event.RefreshComplete)
        }
    }

    private fun navigateToMovieDetail(movieId: Int) {
        viewModelScope.launch {
            _event.trySend(HomeContract.Event.NavigateToMovieDetail(movieId))
        }
    }
}
