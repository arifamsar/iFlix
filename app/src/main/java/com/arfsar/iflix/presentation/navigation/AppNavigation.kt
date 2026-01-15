package com.arfsar.iflix.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.arfsar.iflix.presentation.collections.CollectionsScreen
import com.arfsar.iflix.presentation.components.IFlixScaffold
import com.arfsar.iflix.presentation.details.MovieDetailsScreen
import com.arfsar.iflix.presentation.home.HomeScreen
import com.arfsar.iflix.presentation.search.SearchScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Determine current destination
    val currentDestination = when {
        currentRoute?.contains("Home") == true -> Destinations.Home
        currentRoute?.contains("Discover") == true -> Destinations.Discover
        currentRoute?.contains("Collections") == true -> Destinations.Collections
        currentRoute?.contains("MovieDetails") == true -> null // Hide bottom bar on details
        else -> Destinations.Home
    }

    // Top-level destinations show branded app bar
    val isTopLevelDestination = currentDestination != null
    val showBottomBar = isTopLevelDestination
    val showBrandedAppBar = isTopLevelDestination
    val showDetailTopBar = currentRoute?.contains("MovieDetails") == true

    IFlixScaffold(
        currentRoute = currentDestination,
        showBottomBar = showBottomBar,
        showTopBar = showDetailTopBar,
        showBrandedAppBar = showBrandedAppBar,
        title = if (showDetailTopBar) "Movie Details" else null,
        onNavigate = { destination ->
            navController.navigate(destination) {
                // Pop up to home and save state to avoid building up a large back stack
                popUpTo(Destinations.Home) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        },
        onBackClick = if (showDetailTopBar) {
            { navController.navigateUp() }
        } else null
    ) { paddingValues ->
        NavHost(navController = navController, startDestination = Destinations.Home) {
            composable<Destinations.Home> {
                HomeScreen(
                    navController = navController,
                    paddingValues = paddingValues
                )
            }
            composable<Destinations.Discover> {
                SearchScreen(
                    navController = navController,
                    paddingValues = paddingValues
                )
            }
            composable<Destinations.Collections> {
                CollectionsScreen(
                    paddingValues = paddingValues
                )
            }
            composable<Destinations.MovieDetails> {
                MovieDetailsScreen(
                    paddingValues = paddingValues,
                    onBackClick = { navController.navigateUp() }
                )
            }
        }
    }
}
