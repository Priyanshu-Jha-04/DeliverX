package com.example.deliverx.screens.Home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.maps.android.compose.*
import com.google.maps.android.ktx.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val placesClient = remember {
        if (!Places.isInitialized()) {
            Places.initialize(context, "AIzaSyBi4VjsuB3u1DioUj66Z98oX2Seh_2ukpg")
        }
        Places.createClient(context)
    }

    var searchQuery by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }
    var selectedLocation by remember { mutableStateOf(LatLng(28.6139, 77.2090)) }
    var showSuggestions by remember { mutableStateOf(false) }

    val searchQueryFlow = remember { MutableStateFlow("") }
    val coroutineScope = rememberCoroutineScope()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(selectedLocation, 12f)
    }

    LaunchedEffect(searchQueryFlow) {
        searchQueryFlow
            .debounce(300)
            .collect { query ->
                if (query.length >= 2) {
                    val request = FindAutocompletePredictionsRequest.builder()
                        .setQuery(query)
                        .build()

                    placesClient.findAutocompletePredictions(request)
                        .addOnSuccessListener { response ->
                            suggestions = response.autocompletePredictions
                            showSuggestions = true
                        }
                        .addOnFailureListener { exception ->
                            println("Place prediction failed: ${exception.message}")
                        }
                } else {
                    suggestions = emptyList()
                    showSuggestions = false
                }
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(mapType = MapType.SATELLITE)
        ) {
            Marker(
                state = rememberMarkerState(position = selectedLocation),
                title = "Selected Location",
                snippet = "Current selected location"
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { query ->
                    searchQuery = query
                    coroutineScope.launch {
                        searchQueryFlow.emit(query)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = if (showSuggestions) 0.dp else 16.dp),
                placeholder = { Text("Search for a place") }

                )

            if (showSuggestions) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp),
                    tonalElevation = 2.dp
                ) {
                    LazyColumn {
                        items(suggestions.size) { index ->
                            val suggestion = suggestions[index]
                            TextButton(
                                onClick = {
                                    val placeFields = listOf(Place.Field.LAT_LNG)
                                    val request = FetchPlaceRequest.builder(
                                        suggestion.placeId,
                                        placeFields
                                    ).build()

                                    placesClient.fetchPlace(request)
                                        .addOnSuccessListener { response ->
                                            response.place.latLng?.let { latLng ->
                                                selectedLocation = latLng
                                                cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
                                                searchQuery = suggestion.getPrimaryText(null).toString()
                                                showSuggestions = false
                                            }
                                        }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = suggestion.getPrimaryText(null).toString(),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = suggestion.getSecondaryText(null).toString(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}