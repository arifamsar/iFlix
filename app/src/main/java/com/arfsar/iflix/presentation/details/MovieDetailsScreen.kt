package com.arfsar.iflix.presentation.details

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.arfsar.iflix.presentation.components.IFlixButton
import com.arfsar.iflix.presentation.components.IFlixButtonVariant
import com.arfsar.iflix.presentation.components.PullToRefreshContainer
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    paddingValues: PaddingValues,
    onBackClick: () -> Unit,
    viewModel: MovieDetailsViewModel = hiltViewModel()
) {
    val movieDetailsState by viewModel.movieDetailsState.collectAsStateWithLifecycle()

    when {
        movieDetailsState.isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        movieDetailsState.error != null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Error: ${movieDetailsState.error}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    IFlixButton(
                        text = "Retry",
                        onClick = { viewModel.refresh() },
                        variant = IFlixButtonVariant.PRIMARY
                    )
                }
            }
        }

        movieDetailsState.movieDetails != null -> {
            val movie = movieDetailsState.movieDetails!!
            val scrollState = rememberScrollState()

            CollapsibleImageScaffold(
                title = movie.title,
                backdropPath = movie.backdropPath,
                onBackClick = onBackClick,
                scrollState = scrollState
            ) { innerPadding ->
                PullToRefreshContainer(
                    isRefreshing = movieDetailsState.isRefreshing,
                    onRefresh = { viewModel.refresh() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        // Spacer for the header
                        Spacer(modifier = Modifier.height(300.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            MovieTagline(tagline = movie.tagline)

                            MovieStats(
                                voteAverage = movie.voteAverage,
                                voteCount = movie.voteCount,
                                runtime = movie.runtime,
                                releaseDate = movie.releaseDate
                            )

                            MovieGenres(genres = movie.genres)

                            MovieOverview(overview = movie.overview)

                            MovieFinance(
                                budget = movie.budget,
                                revenue = movie.revenue
                            )

                            MovieProductionCompanies(companies = movie.productionCompanies)

                            MovieAdditionalDetails(
                                status = movie.status,
                                productionCountries = movie.productionCountries,
                                spokenLanguages = movie.spokenLanguages,
                                imdbId = movie.imdbId
                            )

                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollapsibleImageScaffold(
    title: String,
    backdropPath: String,
    onBackClick: () -> Unit,
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit
) {
    val density = LocalDensity.current
    val headerMaxHeight = 300.dp
    val headerMinHeight = 64.dp + WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val headerMaxHeightPx = with(density) { headerMaxHeight.toPx() }
    val headerMinHeightPx = with(density) { headerMinHeight.toPx() }

    val scrollOffset by remember { derivedStateOf { scrollState.value } }
    
    // Calculate header height based on scroll
    val headerHeightPx by remember {
        derivedStateOf {
            max(headerMinHeightPx, headerMaxHeightPx - scrollOffset)
        }
    }
    
    // Calculate progress (0f = expanded, 1f = collapsed)
    val collapseProgress by remember {
        derivedStateOf {
            val progress = 1f - (headerHeightPx - headerMinHeightPx) / (headerMaxHeightPx - headerMinHeightPx)
            progress.coerceIn(0f, 1f)
        }
    }

    // Calculate background color opacity (fade in as we scroll up)
    val topBarContainerColor = MaterialTheme.colorScheme.surface.copy(
        alpha = collapseProgress
    )

    // Define curved top shape for the gradient overlay (inverse curve)
    val curveDepth = with(density) { 48.dp.toPx() } // Deeper curve
    val overlayShape = remember(curveDepth) {
        GenericShape { size, _ ->
            val width = size.width
            val height = size.height
            // Start at top-left, curve down to middle, then up to top-right
            moveTo(0f, 0f)
            quadraticTo(width / 2, curveDepth, width, 0f)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        content(PaddingValues(0.dp))

        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(with(density) { headerHeightPx.toDp() })
                .graphicsLayer {
                    translationY = 0f // Pinned to top
                    shadowElevation = if (collapseProgress > 0.9f) 4.dp.toPx() else 0f
                }
                .background(topBarContainerColor)
        ) {
            // Background Image
            if (collapseProgress < 1f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = 1f - collapseProgress
                        }
                ) {
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/original$backdropPath",
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    
                    // Standard smooth gradient for general readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
                                    ),
                                    startY = 0f,
                                    endY = Float.POSITIVE_INFINITY
                                )
                            )
                    )

                    // Curved Gradient Overlay at the bottom
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp) // Height of the curve blending area
                            .align(Alignment.BottomCenter)
                            .clip(overlayShape)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                                        MaterialTheme.colorScheme.background
                                    )
                                )
                            )
                    )
                }
            }
            
            // Large Title (Visible when expanded)
            if (collapseProgress < 0.8f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .padding(bottom = 24.dp), 
                    contentAlignment = Alignment.BottomStart
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface, 
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.graphicsLayer {
                            alpha = 1f - (collapseProgress * 1.5f).coerceAtMost(1f)
                            translationY = collapseProgress * 50f // Parallax effect
                        }
                    )
                }
            }

            // Top App Bar (Title + Back Button)
            TopAppBar(
                title = {
                    // Show small title only when nearly collapsed
                    if (collapseProgress > 0.7f) {
                        Text(
                            text = title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.graphicsLayer {
                                alpha = ((collapseProgress - 0.7f) / 0.3f).coerceIn(0f, 1f)
                            }
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f * (1f - collapseProgress)),
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                ),
                windowInsets = WindowInsets.statusBars
            )
        }
    }
}