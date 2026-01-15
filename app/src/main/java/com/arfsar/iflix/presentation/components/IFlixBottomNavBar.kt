package com.arfsar.iflix.presentation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.arfsar.iflix.presentation.navigation.Destinations
import com.arfsar.iflix.presentation.navigation.TopLevelDestination
import com.arfsar.iflix.ui.theme.BottomNavGradientStart

/**
 * Material Design 3 bottom navigation bar
 */
@Composable
fun IFlixBottomNavBar(
    currentRoute: Destinations?,
    onNavigate: (Destinations) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 8.dp
    ) {
        TopLevelDestination.entries.forEach { destination ->
            val isSelected = currentRoute?.let {
                it::class == destination.route::class
            } ?: false

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(destination.route) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                        contentDescription = destination.label
                    )
                },
                label = {
                    Text(text = destination.label)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
