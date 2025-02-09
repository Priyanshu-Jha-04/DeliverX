package com.example.deliverx.screens.Home

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.*
import com.google.maps.android.ktx.BuildConfig
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun HomeScreen(navController: NavController) {
    SimpleMapWithSearch()
}

@Composable
fun SimpleMapWithSearch() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    val cameraPositionState = rememberCameraPositionState()
    var predictions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }
    val focusManager = LocalFocusManager.current

    // Initialize Places
    val placesClient = remember {
        if (!Places.isInitialized()) {
            Places.initialize(context, "AIzaSyBi4VjsuB3u1DioUj66Z98oX2Seh_2ukpg")
        }
        Places.createClient(context)
    }

    // Initialize location client
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // Get user's location when component is first created
    LaunchedEffect(Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                try {
                    val location = fusedLocationClient.lastLocation.await()
                    location?.let {
                        val latLng = LatLng(it.latitude, it.longitude)
                        cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    }
                } catch (e: Exception) {
                    // Handle location error
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Map
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            )
        )

        // Search Box and Predictions
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Search TextField
            TextField(
                value = searchQuery,
                onValueChange = { query ->
                    searchQuery = query
                    scope.launch {
                        getPlacePredictions(placesClient, query) { newPredictions ->
                            predictions = newPredictions
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(8.dp)),
                placeholder = { Text("Search location...") },
                trailingIcon = {
                    IconButton(onClick = {
                        focusManager.clearFocus()
                        predictions = emptyList()
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )

            // Predictions list
            if (predictions.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column {
                        predictions.forEach { prediction ->
                            Text(
                                text = prediction.getFullText(null).toString(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        scope.launch {
                                            getPlaceLatLng(placesClient, prediction.placeId) { latLng ->
                                                latLng?.let {
                                                    cameraPositionState.move(
                                                        CameraUpdateFactory.newLatLngZoom(it, 15f)
                                                    )
                                                }
                                            }
                                        }
                                        searchQuery = prediction.getFullText(null).toString()
                                        predictions = emptyList()
                                        focusManager.clearFocus()
                                    }
                                    .padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

suspend fun getPlacePredictions(
    placesClient: PlacesClient,
    query: String,
    onResult: (List<AutocompletePrediction>) -> Unit
) {
    try {
        if (query.length < 3) {
            onResult(emptyList())
            return
        }

        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .build()

        val response = placesClient.findAutocompletePredictions(request).await()
        onResult(response.autocompletePredictions)
    } catch (e: Exception) {
        onResult(emptyList())
    }
}

suspend fun getPlaceLatLng(
    placesClient: PlacesClient,
    placeId: String,
    onResult: (LatLng?) -> Unit
) {
    try {
        val request = FetchPlaceRequest.builder(
            placeId,
            listOf(Place.Field.LAT_LNG)
        ).build()

        val response = placesClient.fetchPlace(request).await()
        onResult(response.place.latLng)
    } catch (e: Exception) {
        onResult(null)
    }
}