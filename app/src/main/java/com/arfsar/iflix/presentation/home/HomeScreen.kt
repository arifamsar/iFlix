package com.arfsar.iflix.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.arfsar.iflix.presentation.components.MovieCarousel
import com.arfsar.iflix.presentation.components.NowPlayingBanner
import com.arfsar.iflix.presentation.components.NowPlayingBannerShimmer
import com.arfsar.iflix.presentation.components.PullToRefreshContainer
import com.arfsar.iflix.presentation.components.SectionErrorState
import com.arfsar.iflix.presentation.components.SkeletonMovieCarousel
import com.arfsar.iflix.presentation.navigation.Destinations

@Composable
fun HomeScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val trendingMovies = viewModel.trendingMovies.collectAsLazyPagingItems()
    val nowPlayingMovies = viewModel.nowPlayingMovies.collectAsLazyPagingItems()
    val popularMovies = viewModel.popularMovies.collectAsLazyPagingItems()
    val topRatedMovies = viewModel.topRatedMovies.collectAsLazyPagingItems()
    
    val snackbarHostState = remember { SnackbarHostState() }

    // Derive isRefreshing from load states
    val isRefreshing = trendingMovies.loadState.refresh is LoadState.Loading ||
            nowPlayingMovies.loadState.refresh is LoadState.Loading ||
            popularMovies.loadState.refresh is LoadState.Loading ||
            topRatedMovies.loadState.refresh is LoadState.Loading

    // Handle paging actions from ViewModel
    LaunchedEffect(Unit) {
        viewModel.pagingAction.collect { action ->
            when (action) {
                HomeContract.PagingAction.REFRESH, HomeContract.PagingAction.RELOAD -> {
                    trendingMovies.refresh()
                    nowPlayingMovies.refresh()
                    popularMovies.refresh()
                    topRatedMovies.refresh()
                }
                HomeContract.PagingAction.RETRY -> {
                    trendingMovies.retry()
                    nowPlayingMovies.retry()
                    popularMovies.retry()
                    topRatedMovies.retry()
                }
            }
        }
    }

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

    // Handle LoadState errors with Snackbar
    val errorState = (trendingMovies.loadState.refresh as? LoadState.Error)
        ?: (nowPlayingMovies.loadState.refresh as? LoadState.Error)
        ?: (popularMovies.loadState.refresh as? LoadState.Error)
        ?: (topRatedMovies.loadState.refresh as? LoadState.Error)
        ?: (trendingMovies.loadState.append as? LoadState.Error)
        ?: (nowPlayingMovies.loadState.append as? LoadState.Error)
        ?: (popularMovies.loadState.append as? LoadState.Error)
        ?: (topRatedMovies.loadState.append as? LoadState.Error)

    LaunchedEffect(errorState) {
        if (errorState != null) {
            val result = snackbarHostState.showSnackbar(
                message = errorState.error.message ?: "An error occurred",
                actionLabel = "Retry",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.accept(HomeContract.Action.Refresh)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        PullToRefreshContainer(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.accept(HomeContract.Action.Refresh) },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Now Playing Movies Section
                item {
                    when {
                        nowPlayingMovies.loadState.refresh is LoadState.Loading && nowPlayingMovies.itemCount == 0 -> {
                            NowPlayingBannerShimmer()
                        }

                        nowPlayingMovies.loadState.refresh is LoadState.Error && nowPlayingMovies.itemCount == 0 -> {
                            val error = (nowPlayingMovies.loadState.refresh as LoadState.Error).error
                            SectionErrorState(
                                errorMessage = "Error loading now playing: ${error.message}",
                                onRetry = { nowPlayingMovies.retry() }
                            )
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
                }

                // Trending Movies Section
                item {
                    when {
                        trendingMovies.loadState.refresh is LoadState.Loading && trendingMovies.itemCount == 0 -> {
                            SkeletonMovieCarousel(title = "Trending")
                        }

                        trendingMovies.loadState.refresh is LoadState.Error && trendingMovies.itemCount == 0 -> {
                            val error = (trendingMovies.loadState.refresh as LoadState.Error).error
                            SectionErrorState(
                                errorMessage = "Error loading trending: ${error.message}",
                                onRetry = { trendingMovies.retry() }
                            )
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
                }

                // Popular Movies Section
                item {
                    when {
                        popularMovies.loadState.refresh is LoadState.Loading && popularMovies.itemCount == 0 -> {
                            SkeletonMovieCarousel(title = "Popular")
                        }

                        popularMovies.loadState.refresh is LoadState.Error && popularMovies.itemCount == 0 -> {
                            val error = (popularMovies.loadState.refresh as LoadState.Error).error
                            SectionErrorState(
                                errorMessage = "Error loading popular: ${error.message}",
                                onRetry = { popularMovies.retry() }
                            )
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
                }

                // Top Rated Movies Section
                item {
                    when {
                        topRatedMovies.loadState.refresh is LoadState.Loading && topRatedMovies.itemCount == 0 -> {
                            SkeletonMovieCarousel(title = "Top Rated")
                        }

                        topRatedMovies.loadState.refresh is LoadState.Error && topRatedMovies.itemCount == 0 -> {
                            val error = (topRatedMovies.loadState.refresh as LoadState.Error).error
                            SectionErrorState(
                                errorMessage = "Error loading top rated: ${error.message}",
                                onRetry = { topRatedMovies.retry() }
                            )
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

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = paddingValues.calculateBottomPadding() + 16.dp)
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                actionColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}