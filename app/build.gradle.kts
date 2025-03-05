plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // Google services Gradle plugin
    id("com.google.gms.google-services")

    //Dagger-Hilt
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    //alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.deliverx"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.deliverx"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        manifestPlaceholders["MAPS_API_KEY"] = project.findProperty("MAPS_API_KEY") ?: ""
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.play.services.maps)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.material)
    implementation(libs.androidx.annotation)
    implementation(libs.places)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Firebase BoM
    implementation(platform(libs.firebase.bom))

    // Firebase Authentication and Cloud Fire-store

    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(platform(libs.firebase.bom))

    // Dagger-Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.runtime.rxjava2)
    implementation(libs.androidx.hilt.navigation)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android.v173)
    implementation(libs.kotlinx.coroutines.core.v173)
    implementation (libs.kotlinx.coroutines.play.services.v173)

    // ViewModel and LiveData
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Coil for loading Images
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Lottie Animation
    implementation (libs.lottie)
    implementation(libs.lottie.compose)

    // For Material Icons
    implementation(libs.androidx.material.icons.extended)

    // Wear
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.navigation)
    implementation(libs.androidx.compose.ui.tooling)

    // Google Maps Dependency
    implementation(libs.maps.compose)
    implementation (libs.play.services.location)
    implementation(libs.androidx.navigation.compose)
    implementation (libs.android.maps.utils)

    // Animated Navigation Bar
    implementation(libs.animated.navigation.bar)
    implementation(libs.androidx.navigation.compose.v276)

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    implementation ("com.google.android.libraries.places:places:4.1.0")
    implementation ("com.google.android.gms:play-services-maps:19.0.0")

    implementation ("org.slf4j:slf4j-simple:2.0.5")
    implementation ("com.google.maps:google-maps-services:2.1.2")


    // Google Places

    // Kotlin Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")


}

kapt {
    correctErrorTypes = true
}


