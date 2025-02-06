// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    kotlin("jvm") version "1.9.22"


    // Google services Gradle plugin
    id("com.google.gms.google-services") version "4.4.2" apply false


    //Dagger-Hilt
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin) apply false


}
