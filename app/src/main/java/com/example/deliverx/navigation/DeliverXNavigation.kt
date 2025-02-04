package com.example.deliverx.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.deliverx.screens.Home.HomeScreen
import com.example.deliverx.screens.Login_SignUp.LoginScreen
import com.example.deliverx.screens.Login_SignUp.SignUpScreen
import com.example.deliverx.screens.Nav.NavScreen
import com.example.deliverx.screens.Profile.ProfileScreen
import com.example.deliverx.screens.Search.SearchScreen
import com.example.deliverx.screens.Settings.SettingsScreen
import com.example.deliverx.screens.Splash.SplashScreen


@Composable
fun DeliverXNavigation()  {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = DeliverXScreens.SplashScreen.name) {
        composable(DeliverXScreens.SplashScreen.name) {
            SplashScreen(navController = navController)
        }
        composable(DeliverXScreens.LoginScreen.name) {
            LoginScreen(navController = navController)
        }
        composable(DeliverXScreens.SignUpScreen.name) {
            SignUpScreen(navController = navController)
        }
        composable(DeliverXScreens.HomeScreen.name) {
            HomeScreen(navController = navController)
        }
        composable(DeliverXScreens.NavScreen.name) {
            NavScreen(navController = navController)
        }
        composable(DeliverXScreens.ProfileScreen.name) {
            ProfileScreen(navController = navController)
        }
        composable(DeliverXScreens.SettingsScreen.name) {
            SettingsScreen(navController = navController)
        }
        composable(DeliverXScreens.SearchScreen.name) {
            SearchScreen(navController = navController)
        }
    }
}