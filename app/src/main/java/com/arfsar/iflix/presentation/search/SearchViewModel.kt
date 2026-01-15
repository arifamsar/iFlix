package com.arfsar.iflix.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.arfsar.core.model.Genre
import com.arfsar.core.model.Movie
import com.arfsar.core.usecase.DiscoverMoviesUseCase
import com.arfsar.core.usecase.GetMovieGenresUseCase
import com.arfsar.core.usecase.SearchMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class SearchState(
    val query: String = "",
    val error: String? = null,
    val isLoading: Boolean = false,
    val genres: List<Genre> = emptyList()
)

sealed class SearchIntent {
    data class TextSearch(val query: String) : SearchIntent()
    data class GenreSearch(val genreName: String, val genreId: String) : SearchIntent()
    object None : SearchIntent()
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMoviesUseCase: SearchMoviesUseCase,
    private val discoverMoviesUseCase: DiscoverMoviesUseCase,
    private val getMovieGenresUseCase: GetMovieGenresUseCase
) : ViewModel() {

    private val _searchIntent = MutableStateFlow<SearchIntent>(SearchIntent.None)

    private val _searchState = MutableStateFlow(SearchState())
    val searchState = _searchState.asStateFlow()

    init {
        loadGenres()
    }

    private fun loadGenres() {
        getMovieGenresUseCase().onEach { result ->
            result.onSuccess { genres ->
                _searchState.value = _searchState.value.copy(genres = genres)
            }
        }.launchIn(viewModelScope)
    }

    // Unwrap Result and expose only PagingData for paging composables
    @OptIn(FlowPreview::class)
    val searchResults: Flow<PagingData<Movie>> = _searchIntent
        .debounce { intent -> 
             if (intent is SearchIntent.TextSearch) 500L else 0L 
        }
        .flatMapLatest { intent ->
            when (intent) {
                is SearchIntent.None -> {
                     _searchState.value = _searchState.value.copy(query = "", error = null, isLoading = false)
                    flowOf(PagingData.empty())
                }
                is SearchIntent.TextSearch -> {
                    val query = intent.query
                    if (query.isBlank()) {
                        _searchState.value = _searchState.value.copy(query = query, error = null, isLoading = false)
                        flowOf(PagingData.empty())
                    } else {
                        _searchState.value = _searchState.value.copy(query = query, isLoading = true, error = null)
                        searchMoviesUseCase(query).map { result ->
                            handleResult(result, query)
                        }
                    }
                }
                is SearchIntent.GenreSearch -> {
                    val genreName = intent.genreName
                    _searchState.value = _searchState.value.copy(query = genreName, isLoading = true, error = null)
                    discoverMoviesUseCase(intent.genreId).map { result ->
                        handleResult(result, genreName)
                    }
                }
            }
        }
        .cachedIn(viewModelScope)

    private fun handleResult(result: Result<PagingData<Movie>>, queryContext: String): PagingData<Movie> {
        result.onSuccess {
            _searchState.value = _searchState.value.copy(query = queryContext, isLoading = false, error = null)
        }.onFailure { error ->
            _searchState.value = _searchState.value.copy(query = queryContext, error = error.message, isLoading = false)
        }
        return result.getOrNull() ?: PagingData.empty()
    }

    fun searchMovies(query: String) {
        _searchIntent.value = SearchIntent.TextSearch(query)
    }
    
    fun searchByGenre(genre: Genre) {
        _searchIntent.value = SearchIntent.GenreSearch(genre.name, genre.id.toString())
    }
}
