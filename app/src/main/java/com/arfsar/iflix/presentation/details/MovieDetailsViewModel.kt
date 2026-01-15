package com.arfsar.iflix.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.arfsar.core.model.MovieDetails
import com.arfsar.core.repository.MovieRepository
import com.arfsar.core.usecase.GetMovieDetailsUseCase
import com.arfsar.iflix.presentation.navigation.Destinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MovieDetailsState(
    val movieDetails: MovieDetails? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val isFavorite: Boolean = false
)

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val movieRepository: MovieRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _movieDetailsState = MutableStateFlow(MovieDetailsState(isLoading = true))
    val movieDetailsState = _movieDetailsState.asStateFlow()

    private val movieId = savedStateHandle.toRoute<Destinations.MovieDetails>().movieId

    init {
        loadMovieDetails()
        checkIfFavorite()
    }

    private fun loadMovieDetails() {
        getMovieDetailsUseCase(movieId).onEach { result ->
            result.onSuccess { movieDetails ->
                _movieDetailsState.value = _movieDetailsState.value.copy(
                    movieDetails = movieDetails,
                    isLoading = false,
                    isRefreshing = false
                )
            }.onFailure { error ->
                _movieDetailsState.value = _movieDetailsState.value.copy(
                    error = error.message,
                    isLoading = false,
                    isRefreshing = false
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun checkIfFavorite() {
        viewModelScope.launch {
            movieRepository.isMovieFavorite(movieId).collectLatest { isFav ->
                _movieDetailsState.value = _movieDetailsState.value.copy(isFavorite = isFav)
            }
        }
    }

    fun toggleFavorite() {
        val currentDetails = _movieDetailsState.value.movieDetails
        if (currentDetails != null) {
            val isFav = _movieDetailsState.value.isFavorite
            viewModelScope.launch {
                val movie = com.arfsar.core.model.Movie(
                    id = currentDetails.id,
                    title = currentDetails.title,
                    posterPath = currentDetails.posterPath,
                    backdropPath = currentDetails.backdropPath,
                    releaseDate = currentDetails.releaseDate,
                    voteAverage = currentDetails.voteAverage
                )
                movieRepository.setFavoriteMovie(movie, !isFav)
            }
        }
    }

    fun refresh() {
        _movieDetailsState.value = _movieDetailsState.value.copy(isRefreshing = true)
        loadMovieDetails()
    }
}
