package com.arfsar.iflix.presentation.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arfsar.core.model.Movie
import com.arfsar.core.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class CollectionsState(
    val favoriteMovies: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CollectionsViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CollectionsState(isLoading = true))
    val state = _state.asStateFlow()

    init {
        getFavorites()
    }

    private fun getFavorites() {
        movieRepository.getFavoriteMovies()
            .onEach { movies ->
                _state.value = _state.value.copy(
                    favoriteMovies = movies,
                    isLoading = false
                )
            }
            .launchIn(viewModelScope)
    }
}
