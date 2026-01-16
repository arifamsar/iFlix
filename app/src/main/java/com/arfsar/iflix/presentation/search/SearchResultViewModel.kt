package com.arfsar.iflix.presentation.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.arfsar.core.model.Genre
import com.arfsar.core.model.Movie
import com.arfsar.core.source.local.entity.SearchQueryEntity
import com.arfsar.core.usecase.AddSearchQueryUseCase
import com.arfsar.core.usecase.ClearSearchHistoryUseCase
import com.arfsar.core.usecase.DeleteSearchQueryUseCase
import com.arfsar.core.usecase.DiscoverMoviesUseCase
import com.arfsar.core.usecase.GetMovieGenresUseCase
import com.arfsar.core.usecase.GetSearchHistoryUseCase
import com.arfsar.core.usecase.SearchMoviesUseCase
import com.arfsar.iflix.presentation.home.HomeContract
import com.arfsar.iflix.presentation.navigation.Destinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchResultViewModel @Inject constructor(
    private val searchMoviesUseCase: SearchMoviesUseCase,
    private val discoverMoviesUseCase: DiscoverMoviesUseCase,
    private val getMovieGenresUseCase: GetMovieGenresUseCase,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val addSearchQueryUseCase: AddSearchQueryUseCase,
    private val deleteSearchQueryUseCase: DeleteSearchQueryUseCase,
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val args = savedStateHandle.toRoute<Destinations.SearchResult>()
    
    // States
    private val _selectedGenreIds = MutableStateFlow<Set<String>>(
        args.genreId?.let { setOf(it) } ?: emptySet()
    )
    val selectedGenreIds: StateFlow<Set<String>> = _selectedGenreIds.asStateFlow()

    private val _searchQuery = MutableStateFlow<String?>(args.query)
    val searchQuery: StateFlow<String?> = _searchQuery.asStateFlow()

    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres: StateFlow<List<Genre>> = _genres.asStateFlow()
    
    val searchHistory: StateFlow<List<SearchQueryEntity>> = getSearchHistoryUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Paging Action Mediator
    private val _pagingAction = MutableSharedFlow<HomeContract.PagingAction>()
    val pagingAction: SharedFlow<HomeContract.PagingAction> = _pagingAction.asSharedFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults: Flow<PagingData<Movie>> = combine(_selectedGenreIds, _searchQuery) { genreIds, query ->
        Pair(genreIds, query)
    }.flatMapLatest { (genreIds, query) ->
        if (!query.isNullOrBlank()) {
            searchMoviesUseCase(query).map { result ->
                val pagingData = result.getOrNull() ?: PagingData.empty()
                if (genreIds.isNotEmpty()) {
                    pagingData.filter { movie ->
                        // Client-side filtering: Check if movie has ALL selected genres
                        val selectedIdsInt = genreIds.mapNotNull { it.toIntOrNull() }
                        movie.genreIds.containsAll(selectedIdsInt)
                    }
                } else {
                    pagingData
                }
            }
        } else if (genreIds.isNotEmpty()) {
            // Discover with multiple genres (comma separated for AND logic)
            val genreQuery = genreIds.joinToString(",")
            discoverMoviesUseCase(genreQuery).map { result ->
                result.getOrNull() ?: PagingData.empty()
            }
        } else {
            flowOf(PagingData.empty())
        }
    }.cachedIn(viewModelScope)

    val title: StateFlow<String> = combine(_selectedGenreIds, _searchQuery, _genres) { genreIds, query, genreList ->
        when {
            !query.isNullOrBlank() -> "Results for \"$query\""
            genreIds.isNotEmpty() -> {
                val names = genreIds.mapNotNull { id -> 
                    genreList.find { it.id.toString() == id }?.name 
                }
                if (names.isNotEmpty()) names.joinToString(", ") else "Movies"
            }
            else -> "Movies"
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Movies")

    init {
        fetchGenres()
    }

    private fun fetchGenres() {
        viewModelScope.launch {
            getMovieGenresUseCase()
                .collect { result ->
                    result.onSuccess { genreList ->
                        _genres.value = genreList
                    }
                }
        }
    }

    fun onGenreSelected(genre: Genre) {
        val current = _selectedGenreIds.value.toMutableSet()
        val id = genre.id.toString()
        if (current.contains(id)) {
            current.remove(id)
        } else {
            current.add(id)
        }
        _selectedGenreIds.value = current
    }

    fun onSearch(query: String) {
        if (query.isNotBlank()) {
            // Don't clear genres, we support combined search
            _searchQuery.value = query
            viewModelScope.launch {
                addSearchQueryUseCase(query)
            }
        }
    }
    
    fun deleteHistoryItem(query: String) {
        viewModelScope.launch {
            deleteSearchQueryUseCase(query)
        }
    }
    
    fun clearHistory() {
        viewModelScope.launch {
            clearSearchHistoryUseCase()
        }
    }
    
    fun clearSearch() {
        _searchQuery.value = null
    }
    
    fun refresh() {
        viewModelScope.launch {
            _pagingAction.emit(HomeContract.PagingAction.REFRESH)
        }
    }
}
