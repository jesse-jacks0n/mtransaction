package com.project.mpesatracker.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.project.mpesatracker.R

@Composable
fun BottomNavBar(
    currentDestination: NavDestination?,
    onNavigateToRoute: (String) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Dashboard") },
            selected = currentDestination?.hierarchy?.any { it.route == "dashboard" } == true,
            onClick = { onNavigateToRoute("dashboard") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.List, contentDescription = null) },
            label = { Text("Transactions") },
            selected = currentDestination?.hierarchy?.any { it.route == "transactions" } == true,
            onClick = { onNavigateToRoute("transactions") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            label = { Text("Settings") },
            selected = currentDestination?.hierarchy?.any { it.route == "settings" } == true,
            onClick = { onNavigateToRoute("settings") }
        )
    }
} 