package com.example.deliverx.navigation

enum class DeliverXScreens {
    SplashScreen,
    LoginScreen,
    SignUpScreen,
    HomeScreen,
    NavScreen,
    ProfileScreen,
    SettingsScreen,
    SearchScreen;

    companion object {
        fun fromRoute(route: String?): DeliverXScreens = when (route?.substringBefore("/")) {
            SplashScreen.name -> SplashScreen
            LoginScreen.name -> LoginScreen
            SignUpScreen.name -> SignUpScreen
            HomeScreen.name -> HomeScreen
            NavScreen.name -> NavScreen
            ProfileScreen.name -> ProfileScreen
            SettingsScreen.name -> SettingsScreen
            SearchScreen.name -> SearchScreen
            null -> HomeScreen
            else -> throw IllegalArgumentException("Route $route is not recognized")
        }
    }
}