package com.arfsar.iflix.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.arfsar.core.model.Movie
import com.arfsar.core.usecase.GetPopularMoviesUseCase
import com.arfsar.core.usecase.GetTopRatedMoviesUseCase
import com.arfsar.core.usecase.GetTrendingMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class TrendingMoviesState(
    val movies: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class PopularMoviesState(
    val isLoading: Boolean = false,
    val error: String? = null
)

data class TopRatedMoviesState(
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    getTrendingMoviesUseCase: GetTrendingMoviesUseCase,
    getPopularMoviesUseCase: GetPopularMoviesUseCase,
    getTopRatedMoviesUseCase: GetTopRatedMoviesUseCase
) : ViewModel() {

    private val _trendingMoviesState = MutableStateFlow(TrendingMoviesState())
    val trendingMoviesState = _trendingMoviesState.asStateFlow()

    private val _popularMoviesState = MutableStateFlow(PopularMoviesState())
    val popularMoviesState = _popularMoviesState.asStateFlow()

    private val _topRatedMoviesState = MutableStateFlow(TopRatedMoviesState())
    val topRatedMoviesState = _topRatedMoviesState.asStateFlow()

    // Unwrap Result and expose only PagingData for paging composables
    val popularMovies: Flow<PagingData<Movie>> = getPopularMoviesUseCase()
        .map { result ->
            result.onSuccess {
                _popularMoviesState.value = PopularMoviesState(isLoading = false)
            }.onFailure { error ->
                _popularMoviesState.value = PopularMoviesState(error = error.message)
            }
            result.getOrNull() ?: PagingData.empty()
        }
        .cachedIn(viewModelScope)

    val topRatedMovies: Flow<PagingData<Movie>> = getTopRatedMoviesUseCase()
        .map { result ->
            result.onSuccess {
                _topRatedMoviesState.value = TopRatedMoviesState(isLoading = false)
            }.onFailure { error ->
                _topRatedMoviesState.value = TopRatedMoviesState(error = error.message)
            }
            result.getOrNull() ?: PagingData.empty()
        }
        .cachedIn(viewModelScope)

    init {
        getTrendingMoviesUseCase().onEach { result ->
            result.onSuccess { movies ->
                _trendingMoviesState.value = TrendingMoviesState(movies = movies, isLoading = false)
            }.onFailure { error ->
                _trendingMoviesState.value = TrendingMoviesState(error = error.message)
            }
        }.launchIn(viewModelScope)
    }
}
