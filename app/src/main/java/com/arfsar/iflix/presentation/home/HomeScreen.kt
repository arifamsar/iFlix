package com.arfsar.iflix.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.arfsar.iflix.presentation.components.MovieCarousel
import com.arfsar.iflix.presentation.components.NowPlayingBanner
import com.arfsar.iflix.presentation.components.PullToRefreshContainer
import com.arfsar.iflix.presentation.navigation.Destinations

@Composable
fun HomeScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val trendingMoviesState by viewModel.trendingMoviesState.collectAsStateWithLifecycle()
    val nowPlayingMoviesState by viewModel.nowPlayingMoviesState.collectAsStateWithLifecycle()
    val popularMoviesState by viewModel.popularMoviesState.collectAsStateWithLifecycle()
    val topRatedMoviesState by viewModel.topRatedMoviesState.collectAsStateWithLifecycle()

    val popularMovies = viewModel.popularMovies.collectAsLazyPagingItems()
    val topRatedMovies = viewModel.topRatedMovies.collectAsLazyPagingItems()

    PullToRefreshContainer(
        isRefreshing = trendingMoviesState.isRefreshing,
        onRefresh = { viewModel.refresh() },
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Now Playing Movies Section
            when {
                nowPlayingMoviesState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                nowPlayingMoviesState.error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error loading now playing: ${nowPlayingMoviesState.error}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                else -> {
                    NowPlayingBanner(
                        movies = nowPlayingMoviesState.movies,
                        onMovieClick = { movieId ->
                            navController.navigate(Destinations.MovieDetails(movieId))
                        }
                    )
                }
            }

            // Trending Movies Section
            when {
                trendingMoviesState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                trendingMoviesState.error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error loading trending: ${trendingMoviesState.error}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                else -> {
                    MovieCarousel(
                        title = "Trending",
                        movies = trendingMoviesState.movies,
                        onMovieClick = { movieId ->
                            navController.navigate(Destinations.MovieDetails(movieId))
                        }
                    )
                }
            }

            // Popular Movies Section
            when {
                popularMoviesState.error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error loading popular: ${popularMoviesState.error}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                else -> {
                    MovieCarousel(
                        title = "Popular",
                        movies = popularMovies,
                        onMovieClick = { movieId ->
                            navController.navigate(Destinations.MovieDetails(movieId))
                        }
                    )
                }
            }

            // Top Rated Movies Section
            when {
                topRatedMoviesState.error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error loading top rated: ${topRatedMoviesState.error}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                else -> {
                    MovieCarousel(
                        title = "Top Rated",
                        movies = topRatedMovies,
                        onMovieClick = { movieId ->
                            navController.navigate(Destinations.MovieDetails(movieId))
                        }
                    )
                }
            }
        }
    }
}
