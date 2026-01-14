package com.arfsar.iflix.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.arfsar.core.model.Movie
import com.arfsar.core.usecase.SearchMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class SearchState(
    val query: String = "",
    val error: String? = null,
    val isLoading: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMoviesUseCase: SearchMoviesUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    private val _searchState = MutableStateFlow(SearchState())
    val searchState = _searchState.asStateFlow()

    // Unwrap Result and expose only PagingData for paging composables
    val searchResults: Flow<PagingData<Movie>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                _searchState.value = SearchState(query = query)
                flowOf(PagingData.empty())
            } else {
                _searchState.value = SearchState(query = query, isLoading = true)
                searchMoviesUseCase(query).map { result ->
                    result.onSuccess {
                        _searchState.value = SearchState(query = query, isLoading = false)
                    }.onFailure { error ->
                        _searchState.value = SearchState(query = query, error = error.message, isLoading = false)
                    }
                    result.getOrNull() ?: PagingData.empty()
                }
            }
        }
        .cachedIn(viewModelScope)

    fun searchMovies(query: String) {
        _searchQuery.value = query
    }
}
