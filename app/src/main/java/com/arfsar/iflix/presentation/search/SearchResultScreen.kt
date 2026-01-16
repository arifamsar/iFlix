package com.arfsar.iflix.presentation.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalMovies
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.arfsar.core.model.Genre
import com.arfsar.iflix.presentation.components.CustomSearchBar
import com.arfsar.iflix.presentation.components.MovieCard
import com.arfsar.iflix.presentation.components.PullToRefreshContainer
import com.arfsar.iflix.presentation.home.HomeContract
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(
    onMovieClick: (Int) -> Unit,
    onBackClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
    viewModel: SearchResultViewModel = hiltViewModel()
) {
    val searchResults = viewModel.searchResults.collectAsLazyPagingItems()
    val genres by viewModel.genres.collectAsStateWithLifecycle()
    val selectedGenreIds by viewModel.selectedGenreIds.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val title by viewModel.title.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    
    var isSearchActive by rememberSaveable { mutableStateOf(false) }
    var currentQuery by rememberSaveable { mutableStateOf("") }

    // Sync current query with viewmodel if needed, but separate edit state
    LaunchedEffect(searchQuery) {
        if (!searchQuery.isNullOrBlank()) {
            currentQuery = searchQuery ?: ""
        } else {
            currentQuery = ""
        }
    }

    // Handle paging actions from ViewModel
    LaunchedEffect(Unit) {
        viewModel.pagingAction.collect { action ->
            when (action) {
                HomeContract.PagingAction.REFRESH, HomeContract.PagingAction.RELOAD -> {
                    searchResults.refresh()
                }
                HomeContract.PagingAction.RETRY -> {
                    searchResults.retry()
                }
            }
        }
    }

    // Handle LoadState errors with Snackbar
    val errorState = (searchResults.loadState.refresh as? LoadState.Error)
        ?: (searchResults.loadState.append as? LoadState.Error)

    LaunchedEffect(errorState) {
        if (errorState != null) {
            val result = snackbarHostState.showSnackbar(
                message = errorState.error.message ?: "An error occurred",
                actionLabel = "Retry",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.refresh()
            }
        }
    }
    
    val searchHistory by viewModel.searchHistory.collectAsStateWithLifecycle()

    // Back handler for search
    BackHandler(enabled = isSearchActive) {
        isSearchActive = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // TopAppBar
            TopAppBar(
                title = { 
                    Text(
                        text = title, 
                        maxLines = 1, 
                        overflow = TextOverflow.Ellipsis 
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isSearchActive = true }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )

            val isRefreshing = searchResults.loadState.refresh is LoadState.Loading

            PullToRefreshContainer(
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.refresh() },
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Genre Chips
                    if (genres.isNotEmpty()) {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(genres) { genre ->
                                val isSelected = selectedGenreIds.contains(genre.id.toString())
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.onGenreSelected(genre) },
                                    label = { Text(genre.name) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                        labelColor = MaterialTheme.colorScheme.onSurface,
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    border = null,
                                    leadingIcon = if (isSelected) {
                                        {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    } else null
                                )
                            }
                        }
                    }
                    
                    // Content
                    if (searchResults.loadState.refresh is LoadState.Loading && searchResults.itemCount == 0) {
                         Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (searchResults.itemCount == 0 && searchResults.loadState.refresh !is LoadState.Loading) {
                         Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalMovies,
                                    contentDescription = "No results",
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No movies found",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    } else {
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
                                        onMovieClick = onMovieClick
                                    )
                                }
                            }
                            
                            if (searchResults.loadState.append is LoadState.Loading) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(100.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Custom Search Bar Overlay
        CustomSearchBar(
            query = currentQuery,
            onQueryChange = { 
                currentQuery = it
                viewModel.onQueryChange(it)
            },
            onSearch = { 
                viewModel.onSearch(it)
                isSearchActive = false
            },
            active = isSearchActive,
            onActiveChange = { isSearchActive = it },
            placeholder = { 
                Text(
                    text = "Search movies...", 
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                ) 
            },
            leadingIcon = {
                IconButton(onClick = { isSearchActive = false }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            trailingIcon = {
                if (currentQuery.isNotEmpty()) {
                    IconButton(onClick = { 
                        currentQuery = ""
                        viewModel.clearSearch()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (currentQuery.isEmpty()) {
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
                            if (searchHistory.isNotEmpty()) {
                                Text(
                                    text = "Clear all",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.clickable { viewModel.clearHistory() }
                                )
                            }
                        }
                    }
                    
                    items(searchHistory) { historyItem ->
                        RecentSearchItem(
                            query = historyItem.query,
                            onClick = {
                                currentQuery = historyItem.query
                                viewModel.onSearch(historyItem.query)
                                isSearchActive = false
                            },
                            onDelete = {
                                viewModel.deleteHistoryItem(historyItem.query)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Removed \"${historyItem.query}\" from history")
                                }
                            }
                        )
                    }
                } else {
                    items(searchResults.itemCount) { index ->
                        val movie = searchResults[index]
                        if (movie != null) {
                            ListItem(
                                headlineContent = { 
                                    Text(
                                        text = movie.title, 
                                        maxLines = 1, 
                                        overflow = TextOverflow.Ellipsis
                                    ) 
                                },
                                leadingContent = { 
                                    Icon(
                                        Icons.Default.LocalMovies, 
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    ) 
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        viewModel.onSearch(movie.title) // Add to history
                                        onMovieClick(movie.id)
                                        isSearchActive = false
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}
