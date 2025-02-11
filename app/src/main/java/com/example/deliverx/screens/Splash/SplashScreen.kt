package com.example.deliverx.screens.Splash

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.deliverx.R
import com.example.deliverx.navigation.DeliverXScreens

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current
    val preferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val isLoggedIn = preferences.getBoolean("isLoggedIn", false)

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splash))
    val progress by animateLottieCompositionAsState(composition = composition)

    LaunchedEffect(progress) {
        if (progress == 1f) {
            navController.navigate(
                if (isLoggedIn) DeliverXScreens.HomeScreen.name
                else DeliverXScreens.LoginScreen.name
            ) {
                popUpTo(0)
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
        LottieAnimation(composition = composition, speed = 1f)
    }
}