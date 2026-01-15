package com.arfsar.iflix.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.arfsar.iflix.presentation.components.MovieCarousel
import com.arfsar.iflix.presentation.components.NowPlayingBanner
import com.arfsar.iflix.presentation.components.NowPlayingBannerShimmer
import com.arfsar.iflix.presentation.components.PullToRefreshContainer
import com.arfsar.iflix.presentation.components.SkeletonMovieCarousel
import com.arfsar.iflix.presentation.navigation.Destinations

@Composable
fun HomeScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val trendingMovies = viewModel.trendingMovies.collectAsLazyPagingItems()
    val nowPlayingMovies = viewModel.nowPlayingMovies.collectAsLazyPagingItems()
    val popularMovies = viewModel.popularMovies.collectAsLazyPagingItems()
    val topRatedMovies = viewModel.topRatedMovies.collectAsLazyPagingItems()

    // Handle navigation events
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is HomeContract.Event.NavigateToMovieDetail -> {
                    navController.navigate(Destinations.MovieDetails(event.movieId))
                }
                is HomeContract.Event.ShowError -> {
                    // Handle error display if needed
                }
                HomeContract.Event.RefreshComplete -> {
                    // Handle refresh completion if needed
                }
            }
        }
    }

    PullToRefreshContainer(
        isRefreshing = uiState.isRefreshing,
        onRefresh = { viewModel.accept(HomeContract.Action.Refresh) },
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
                nowPlayingMovies.loadState.refresh is LoadState.Loading -> {
                    NowPlayingBannerShimmer()
                }

                nowPlayingMovies.loadState.refresh is LoadState.Error -> {
                    val error = (nowPlayingMovies.loadState.refresh as LoadState.Error).error
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error loading now playing: ${error.message}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                else -> {
                    NowPlayingBanner(
                        movies = nowPlayingMovies,
                        onMovieClick = { movieId ->
                            viewModel.accept(HomeContract.Action.NavigateToMovieDetail(movieId))
                        }
                    )
                }
            }

            // Trending Movies Section
            when {
                trendingMovies.loadState.refresh is LoadState.Loading -> {
                    SkeletonMovieCarousel(title = "Trending")
                }

                trendingMovies.loadState.refresh is LoadState.Error -> {
                    val error = (trendingMovies.loadState.refresh as LoadState.Error).error
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error loading trending: ${error.message}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                else -> {
                    MovieCarousel(
                        title = "Trending",
                        movies = trendingMovies,
                        onMovieClick = { movieId ->
                            viewModel.accept(HomeContract.Action.NavigateToMovieDetail(movieId))
                        }
                    )
                }
            }

            // Popular Movies Section
            when {
                popularMovies.loadState.refresh is LoadState.Loading -> {
                    SkeletonMovieCarousel(title = "Popular")
                }

                popularMovies.loadState.refresh is LoadState.Error -> {
                    val error = (popularMovies.loadState.refresh as LoadState.Error).error
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error loading popular: ${error.message}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                else -> {
                    MovieCarousel(
                        title = "Popular",
                        movies = popularMovies,
                        onMovieClick = { movieId ->
                            viewModel.accept(HomeContract.Action.NavigateToMovieDetail(movieId))
                        }
                    )
                }
            }

            // Top Rated Movies Section
            when {
                topRatedMovies.loadState.refresh is LoadState.Loading -> {
                    SkeletonMovieCarousel(title = "Top Rated")
                }

                topRatedMovies.loadState.refresh is LoadState.Error -> {
                    val error = (topRatedMovies.loadState.refresh as LoadState.Error).error
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error loading top rated: ${error.message}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                else -> {
                    MovieCarousel(
                        title = "Top Rated",
                        movies = topRatedMovies,
                        onMovieClick = { movieId ->
                            viewModel.accept(HomeContract.Action.NavigateToMovieDetail(movieId))
                        }
                    )
                }
            }
        }
    }
}
