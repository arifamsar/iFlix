package com.arfsar.iflix.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.arfsar.core.model.Movie
import com.arfsar.iflix.presentation.components.MovieCardShimmer
import com.arfsar.iflix.presentation.components.SkeletonMovieCarousel

@Composable
fun MovieCarousel(
    title: String,
    movies: LazyPagingItems<Movie>,
    modifier: Modifier = Modifier,
    onMovieClick: (Int) -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(movies.itemCount) { index ->
                val movie = movies[index]
                if (movie != null) {
                    MovieCard(
                        movie = movie,
                        onMovieClick = onMovieClick
                    )
                }
            }

            if (movies.loadState.append is LoadState.Loading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun MovieCarousel(
    title: String,
    movies: List<Movie>,
    modifier: Modifier = Modifier,
    onMovieClick: (Int) -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(movies) { movie ->
                MovieCard(
                    movie = movie,
                    onMovieClick = onMovieClick
                )
            }
        }
    }
}
