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
            Places.initialize(applicationContext, "AIzaSyCF4YkLEcV_lyt53uWOLGIuWL8Bz-9bG6g")
        }
        FirebaseApp.initializeApp(this)
    }
}