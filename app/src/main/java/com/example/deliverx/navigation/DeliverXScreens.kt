package com.example.deliverx.navigation

enum class  DeliverXScreens {
    SplashScreen,
    LoginScreen,
    SignUpScreen,
    MainScreen,
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
            MainScreen.name -> MainScreen
            HomeScreen.name -> HomeScreen
            NavScreen.name -> NavScreen
            ProfileScreen.name -> ProfileScreen
            SettingsScreen.name -> SettingsScreen
            SearchScreen.name -> SearchScreen
            null -> MainScreen
            else -> throw IllegalArgumentException("Route $route is not recognized")
        }
    }
}