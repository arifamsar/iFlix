package com.arfsar.iflix.presentation.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalMovies
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.arfsar.iflix.presentation.components.MovieCard
import com.arfsar.iflix.presentation.components.PullToRefreshContainer
import com.arfsar.iflix.presentation.home.HomeContract

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(
    onMovieClick: (Int) -> Unit,
    onBackClick: () -> Unit,
    viewModel: SearchResultViewModel = hiltViewModel()
) {
    val searchResults = viewModel.searchResults.collectAsLazyPagingItems()
    val genres by viewModel.genres.collectAsStateWithLifecycle()
    val selectedGenreId by viewModel.selectedGenreId.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val title by viewModel.title.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    
    var isSearchActive by rememberSaveable { mutableStateOf(false) }
    var currentQuery by rememberSaveable { mutableStateOf("") }

    // Sync current query with viewmodel if needed, but separate edit state
    LaunchedEffect(searchQuery) {
        if (!searchQuery.isNullOrBlank()) {
            currentQuery = searchQuery ?: ""
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
    
    // Back handler for search
    BackHandler(enabled = isSearchActive) {
        isSearchActive = false
    }

    Scaffold(
        topBar = {
            if (isSearchActive) {
                SearchBar(
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = currentQuery,
                            onQueryChange = { currentQuery = it },
                            onSearch = { 
                                viewModel.onSearch(it)
                                isSearchActive = false
                            },
                            expanded = isSearchActive,
                            onExpandedChange = { isSearchActive = it },
                            placeholder = { Text("Search movies...") },
                            leadingIcon = {
                                IconButton(onClick = { isSearchActive = false }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                                }
                            },
                            trailingIcon = {
                                if (currentQuery.isNotEmpty()) {
                                    IconButton(onClick = { currentQuery = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear")
                                    }
                                }
                            }
                        )
                    },
                    expanded = isSearchActive,
                    onExpandedChange = { isSearchActive = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Search suggestions or history could go here
                }
            } else {
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
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        
        val isRefreshing = searchResults.loadState.refresh is LoadState.Loading

        PullToRefreshContainer(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Genre Chips
                if (genres.isNotEmpty() && !isSearchActive) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(genres) { genre ->
                            FilterChip(
                                selected = genre.id.toString() == selectedGenreId,
                                onClick = { viewModel.onGenreSelected(genre) },
                                label = { Text(genre.name) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    labelColor = MaterialTheme.colorScheme.onSurface,
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = RoundedCornerShape(16.dp),
                                border = null
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
}