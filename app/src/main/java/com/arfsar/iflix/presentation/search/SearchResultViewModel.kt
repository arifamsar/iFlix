package com.arfsar.iflix.presentation.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.arfsar.core.model.Genre
import com.arfsar.core.model.Movie
import com.arfsar.core.usecase.DiscoverMoviesUseCase
import com.arfsar.core.usecase.GetMovieGenresUseCase
import com.arfsar.core.usecase.SearchMoviesUseCase
import com.arfsar.iflix.presentation.home.HomeContract
import com.arfsar.iflix.presentation.navigation.Destinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
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
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val args = savedStateHandle.toRoute<Destinations.SearchResult>()
    
    // States
    private val _selectedGenreId = MutableStateFlow<String?>(args.genreId)
    val selectedGenreId: StateFlow<String?> = _selectedGenreId.asStateFlow()

    private val _searchQuery = MutableStateFlow<String?>(args.query)
    val searchQuery: StateFlow<String?> = _searchQuery.asStateFlow()

    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres: StateFlow<List<Genre>> = _genres.asStateFlow()
    
    // Paging Action Mediator
    private val _pagingAction = MutableSharedFlow<HomeContract.PagingAction>()
    val pagingAction: SharedFlow<HomeContract.PagingAction> = _pagingAction.asSharedFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults: Flow<PagingData<Movie>> = combine(_selectedGenreId, _searchQuery) { genreId, query ->
        Pair(genreId, query)
    }.flatMapLatest { (genreId, query) ->
        if (!query.isNullOrBlank()) {
            searchMoviesUseCase(query)
        } else if (genreId != null) {
            discoverMoviesUseCase(genreId)
        } else {
            flowOf(Result.success(PagingData.empty()))
        }
    }.map { result ->
        result.getOrNull() ?: PagingData.empty()
    }.cachedIn(viewModelScope)

    val title: StateFlow<String> = combine(_selectedGenreId, _searchQuery, _genres) { genreId, query, genreList ->
        when {
            !query.isNullOrBlank() -> "Results for \"$query\""
            genreId != null -> genreList.find { it.id.toString() == genreId }?.name ?: args.genreName ?: "Movies"
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
        _searchQuery.value = null
        _selectedGenreId.value = genre.id.toString()
    }

    fun onSearch(query: String) {
        if (query.isNotBlank()) {
            _selectedGenreId.value = null
            _searchQuery.value = query
        }
    }
    
    fun refresh() {
        viewModelScope.launch {
            _pagingAction.emit(HomeContract.PagingAction.REFRESH)
        }
    }
}
