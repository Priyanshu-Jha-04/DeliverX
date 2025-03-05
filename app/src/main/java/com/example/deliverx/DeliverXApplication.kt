package com.example.deliverx

import android.app.Application
import com.google.android.libraries.places.api.Places
import com.google.firebase.FirebaseApp
import com.google.maps.android.ktx.BuildConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DeliverXApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyBi4VjsuB3u1DioUj66Z98oX2Seh_2ukpg")
        }
        FirebaseApp.initializeApp(this)
    }
}