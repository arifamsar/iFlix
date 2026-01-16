package com.arfsar.iflix.presentation.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.arfsar.core.model.Genre
import com.arfsar.core.source.local.entity.SearchQueryEntity
import com.arfsar.iflix.presentation.components.AnimatedSearchBar
import com.arfsar.iflix.presentation.components.MovieCard
import com.arfsar.iflix.presentation.components.RecentSearchItem
import com.arfsar.iflix.presentation.components.SkeletonGenreChip
import com.arfsar.iflix.presentation.components.SkeletonRecentSearchItem
import com.arfsar.iflix.presentation.navigation.Destinations
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    snackbarHostState: SnackbarHostState,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchState by viewModel.searchState.collectAsStateWithLifecycle()
    val searchResults = viewModel.searchResults.collectAsLazyPagingItems()
    var query by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // Search Bar
        Box(modifier = Modifier.padding(16.dp)) {
            AnimatedSearchBar(
                query = query,
                onQueryChange = {
                    query = it
                    viewModel.searchMovies(it)
                },
                onSearch = {
                    if (query.isNotBlank()) {
                        viewModel.addToHistory(query)
                        navController.navigate(
                            Destinations.SearchResult(
                                query = query,
                                genreId = null,
                                genreName = null
                            )
                        )
                    }
                },
                onClearClick = {
                    query = ""
                    viewModel.searchMovies("")
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (query.isBlank()) {
            if (searchState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                SearchEmptyState(
                    genres = searchState.genres,
                    isGenresLoading = searchState.isGenresLoading,
                    history = searchState.history,
                    isHistoryLoading = searchState.isHistoryLoading,
                    onGenreClick = { genre ->
                        navController.navigate(
                            Destinations.SearchResult(
                                genreId = genre.id.toString(),
                                genreName = genre.name,
                                query = null
                            )
                        )
                    },
                    onHistoryClick = { historyQuery ->
                        query = historyQuery
                        viewModel.searchMovies(historyQuery)
                        viewModel.addToHistory(historyQuery)
                        navController.navigate(
                            Destinations.SearchResult(
                                query = historyQuery,
                                genreId = null,
                                genreName = null
                            )
                        )
                    },
                    onDeleteHistory = { historyQuery ->
                        viewModel.deleteHistoryItem(historyQuery)
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "Removed \"$historyQuery\" from history",
                                actionLabel = "Undo",
                                duration = SnackbarDuration.Short
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                viewModel.addToHistory(historyQuery)
                            }
                        }
                    })
            }
        } else {
            // Real-time results
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(searchResults.itemCount) { index ->
                    searchResults[index]?.let { movie ->
                        MovieCard(
                            movie = movie,
                            onMovieClick = { movieId ->
                                navController.navigate(Destinations.MovieDetails(movieId))
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchEmptyState(
    genres: List<Genre>,
    isGenresLoading: Boolean,
    history: List<SearchQueryEntity>,
    isHistoryLoading: Boolean,
    onGenreClick: (Genre) -> Unit,
    onHistoryClick: (String) -> Unit,
    onDeleteHistory: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (history.isNotEmpty() || isHistoryLoading) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Searches",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (isHistoryLoading) {
                items(5) {
                    SkeletonRecentSearchItem()
                }
            } else {
                items(history) { item ->
                    RecentSearchItem(
                        query = item.query,
                        onClick = { onHistoryClick(item.query) },
                        onDelete = { onDeleteHistory(item.query) }
                    )
                }
            }
        }

        if (genres.isNotEmpty() || isGenresLoading) {
            item {
                Text(
                    text = "Discover by Genre",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isGenresLoading) {
                        repeat(10) {
                            SkeletonGenreChip()
                        }
                    } else {
                        genres.forEach { genre ->
                            FilterChip(
                                selected = false,
                                onClick = { onGenreClick(genre) },
                                label = { Text(genre.name) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    labelColor = MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(12.dp),
                                border = null
                            )
                        }
                    }
                }
            }
        }
    }
}
