package com.arfsar.iflix.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.arfsar.core.model.MovieDetails
import com.arfsar.core.usecase.GetMovieDetailsUseCase
import com.arfsar.iflix.presentation.navigation.Destinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class MovieDetailsState(
    val movieDetails: MovieDetails? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _movieDetailsState = MutableStateFlow(MovieDetailsState(isLoading = true))
    val movieDetailsState = _movieDetailsState.asStateFlow()

    init {
        val movieId = savedStateHandle.toRoute<Destinations.MovieDetails>().movieId
        getMovieDetailsUseCase(movieId).onEach { result ->
            result.onSuccess { movieDetails ->
                _movieDetailsState.value = MovieDetailsState(movieDetails = movieDetails, isLoading = false)
            }.onFailure { error ->
                _movieDetailsState.value = MovieDetailsState(error = error.message, isLoading = false)
            }
        }.launchIn(viewModelScope)
    }
}
