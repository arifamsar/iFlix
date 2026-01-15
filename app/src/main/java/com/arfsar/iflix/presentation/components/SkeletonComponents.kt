package com.arfsar.iflix.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.arfsar.iflix.ui.theme.CardBackground
import com.arfsar.iflix.ui.theme.DarkSurface

@Composable
fun SkeletonEffect(): Color {
    val infiniteTransition = rememberInfiniteTransition(label = "skeleton_transition")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha_animation"
    )
    
    return DarkSurface.copy(alpha = alpha)
}

@Composable
fun SkeletonMovieCard(
    modifier: Modifier = Modifier
) {
    val backgroundColor = SkeletonEffect()
    
    Card(
        modifier = modifier
            .width(150.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        )
    ) {
        Column {
            // Poster placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(backgroundColor)
            )
            
            // Title placeholder
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(16.dp)
                    .padding(horizontal = 8.dp)
                    .background(backgroundColor, shape = RoundedCornerShape(8.dp))
            )
            
            // Rating placeholder
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(backgroundColor, shape = RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(14.dp)
                        .background(backgroundColor, shape = RoundedCornerShape(7.dp))
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun SkeletonBannerItem(
    modifier: Modifier = Modifier
) {
    val backgroundColor = SkeletonEffect()
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        )
    ) {
        Box {
            // Backdrop placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(backgroundColor)
            )
            
            // Bottom info placeholder
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                // Title placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(24.dp)
                        .background(backgroundColor, shape = RoundedCornerShape(4.dp))
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Rating and year placeholder
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(backgroundColor, shape = RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(14.dp)
                            .background(backgroundColor, shape = RoundedCornerShape(7.dp))
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(14.dp)
                            .background(backgroundColor, shape = RoundedCornerShape(7.dp))
                    )
                }
            }
        }
    }
}

@Composable
fun SkeletonMovieCarousel(
    title: String,
    modifier: Modifier = Modifier
) {
    val backgroundColor = SkeletonEffect()
    
    Column(modifier = modifier) {
        // Title placeholder
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(0.4f)
                .height(24.dp)
                .background(backgroundColor, shape = RoundedCornerShape(8.dp))
        )
        
        // Cards row placeholder
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(5) {
                SkeletonMovieCard()
            }
        }
    }
}

@Composable
fun SkeletonNowPlayingBanner(
    modifier: Modifier = Modifier
) {
    val backgroundColor = SkeletonEffect()
    
    Column(modifier = modifier) {
        Box {
            SkeletonBannerItem()
            
            // Page indicator placeholder
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(backgroundColor)
                    )
                }
            }
        }
    }
}