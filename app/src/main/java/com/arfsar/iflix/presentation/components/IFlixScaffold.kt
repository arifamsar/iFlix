package com.arfsar.iflix.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arfsar.iflix.presentation.navigation.Destinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IFlixScaffold(
    modifier: Modifier = Modifier,
    title: String? = null,
    currentRoute: Destinations? = null,
    showBottomBar: Boolean = true,
    showTopBar: Boolean = true,
    showBrandedAppBar: Boolean = false,
    onNavigate: ((Destinations) -> Unit)? = null,
    onBackClick: (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            when {
                showBrandedAppBar -> {
                    IFlixAppBar(title = title ?: "iFlix")
                }
                showTopBar && title != null -> {
                    TopAppBar(
                        title = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        navigationIcon = {
                            if (onBackClick != null) {
                                IconButton(onClick = onBackClick) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            titleContentColor = MaterialTheme.colorScheme.onSurface,
                            navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        },
        bottomBar = {
            if (showBottomBar && onNavigate != null) {
                IFlixBottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = onNavigate
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        content(paddingValues)
    }
}

