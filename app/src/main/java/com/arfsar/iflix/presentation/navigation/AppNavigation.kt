package com.arfsar.iflix.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arfsar.iflix.presentation.details.MovieDetailsScreen
import com.arfsar.iflix.presentation.home.HomeScreen
import com.arfsar.iflix.presentation.search.SearchScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Destinations.Home) {
        composable<Destinations.Home> {
            HomeScreen(navController = navController)
        }
        composable<Destinations.Discover> {
            SearchScreen(navController = navController)
        }
        composable<Destinations.MovieDetails> {
            MovieDetailsScreen()
        }
    }
}
