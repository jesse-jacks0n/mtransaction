package com.project.mpesatracker.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.project.mpesatracker.ui.components.BottomNavBar
import com.project.mpesatracker.ui.screens.*
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    Scaffold(
        bottomBar = {
            if (currentDestination?.route !in listOf("splash", "permissions")) {
                BottomNavBar(
                    currentDestination = currentDestination,
                    onNavigateToRoute = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("splash") {
                SplashScreen(
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                )
            }

            composable("permissions") {
                PermissionsScreen(
                    onPermissionGranted = {
                        navController.navigate("dashboard") {
                            popUpTo("permissions") { inclusive = true }
                        }
                    }
                )
            }

            composable("dashboard") {
                DashboardScreen(
                    onTransactionClick = { transactionId ->
                        navController.navigate("transaction_details/$transactionId")
                    }
                )
            }

            composable("transactions") {
                TransactionsScreen(
                    onTransactionClick = { transactionId ->
                        navController.navigate("transaction_details/$transactionId")
                    }
                )
            }

            composable(
                route = "transaction_details/{transactionId}",
                arguments = listOf(
                    navArgument("transactionId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val transactionId = backStackEntry.arguments?.getString("transactionId")
                TransactionDetailsScreen(
                    transactionId = transactionId ?: return@composable,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("settings") {
                SettingsScreen()
            }
        }
    }
} 