package com.arfsar.iflix.presentation.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.arfsar.core.model.Movie
import com.arfsar.core.usecase.DiscoverMoviesUseCase
import com.arfsar.core.usecase.SearchMoviesUseCase
import com.arfsar.iflix.presentation.navigation.Destinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SearchResultViewModel @Inject constructor(
    private val searchMoviesUseCase: SearchMoviesUseCase,
    private val discoverMoviesUseCase: DiscoverMoviesUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val args = savedStateHandle.toRoute<Destinations.SearchResult>()
    val title: String = args.genreName ?: args.query ?: "Search Results"

    val searchResults: Flow<PagingData<Movie>> = if (args.query != null) {
        flowWrapper(searchMoviesUseCase(args.query))
    } else if (args.genreId != null) {
        flowWrapper(discoverMoviesUseCase(args.genreId))
    } else {
        kotlinx.coroutines.flow.flowOf(PagingData.empty())
    }

    private fun flowWrapper(flow: Flow<Result<PagingData<Movie>>>): Flow<PagingData<Movie>> {
        return flow
            .map { result ->
                result.getOrNull() ?: PagingData.empty()
            }
            .cachedIn(viewModelScope)
    }
}
