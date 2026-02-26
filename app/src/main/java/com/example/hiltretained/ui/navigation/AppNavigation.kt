package com.example.hiltretained.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hiltretained.ui.screen.DetailsScreen
import com.example.hiltretained.ui.screen.HomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(onNavigateToDetails = { navController.navigate("details") })
        }
        composable("details") {
            DetailsScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
