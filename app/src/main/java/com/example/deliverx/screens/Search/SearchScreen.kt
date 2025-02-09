package com.example.deliverx.screens.Search

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

@Composable
fun SearchScreen(navController: NavController) {
    var optimizedRoute by remember { mutableStateOf<List<LatLng>>(emptyList()) }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchScreen1 { route ->
            optimizedRoute = route // Updates the optimized route
        }
        ShowOptimizedRoute(optimizedRoute) // Displays the route on the map
    }
}

@Composable
fun SearchScreen1(onCalculateRoute: (List<LatLng>) -> Unit) {
    val context = LocalContext.current
    var numPlaces by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedPlaces by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var predictions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    val placesClient = Places.createClient(context)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
    ) {
        OutlinedTextField(
            value = numPlaces,
            onValueChange = { numPlaces = it },
            label = { Text("Number of Places") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                getPlaceSuggestions(placesClient, it) { suggestions ->
                    predictions = suggestions
                }
            },
            label = { Text("Search Place") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        predictions.forEach { prediction ->
            Text(
                text = prediction.getPrimaryText(null).toString(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        getLocationFromPlace(context, prediction.getPrimaryText(null).toString()) { newLocation ->
                            if (newLocation != null) {
                                selectedPlaces = selectedPlaces + newLocation
                            }
                        }
                        searchQuery = ""
                        predictions = emptyList()
                    }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        selectedPlaces.forEachIndexed { index, place ->
            Text(
                text = "Place ${index + 1}: Lat ${place.latitude}, Lng ${place.longitude}",
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    val optimizedRoute = findShortestRoute(selectedPlaces)
                    onCalculateRoute(optimizedRoute) // Updates the map immediately
                }
            },
            enabled = selectedPlaces.size == numPlaces.toIntOrNull()
        ) {
            Icon(Icons.Default.Add, contentDescription = "Calculate Route")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Find Shortest Path")
        }
    }
}

@Composable
fun ShowOptimizedRoute(optimizedRoute: List<LatLng>) {
    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(optimizedRoute) {
        if (optimizedRoute.isNotEmpty()) {
            cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(optimizedRoute.first(), 12f))
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(mapType = MapType.NORMAL)
    ) {
        if (optimizedRoute.isNotEmpty()) {
            Polyline(
                points = optimizedRoute,
                color = Color.Blue,
                width = 8f
            )

            optimizedRoute.forEach { location ->
                Marker(
                    state = rememberMarkerState(position = location),
                    title = "Stop",
                    snippet = "Optimized Route"
                )
            }
        }
    }
}

// 📌 Function to Fetch Place Suggestions
fun getPlaceSuggestions(placesClient: PlacesClient, query: String, onResult: (List<AutocompletePrediction>) -> Unit) {
    if (query.isEmpty()) {
        onResult(emptyList())
        return
    }

    val request = FindAutocompletePredictionsRequest.builder()
        .setQuery(query)
        .build()

    placesClient.findAutocompletePredictions(request)
        .addOnSuccessListener { response ->
            onResult(response.autocompletePredictions)
        }
        .addOnFailureListener { exception ->
            Log.e("PlacesAPI", "Error fetching suggestions: ${exception.localizedMessage}")
            onResult(emptyList())
        }
}

// 📌 Function to Get LatLng from Place Name
fun getLocationFromPlace(context: Context, placeName: String, onResult: (LatLng?) -> Unit) {
    val placesClient = Places.createClient(context)
    val request = FindAutocompletePredictionsRequest.builder()
        .setQuery(placeName)
        .build()

    placesClient.findAutocompletePredictions(request)
        .addOnSuccessListener { response ->
            if (response.autocompletePredictions.isNotEmpty()) {
                // Get the place's ID
                val placeId = response.autocompletePredictions[0].placeId
                getPlaceDetails(placeId, context, onResult)
            } else {
                onResult(null)
            }
        }
        .addOnFailureListener { exception ->
            Log.e("PlacesAPI", "Error fetching suggestions: ${exception.localizedMessage}")
            onResult(null)
        }
}

// Fetch the details of a place using its ID
fun getPlaceDetails(placeId: String, context: Context, onResult: (LatLng?) -> Unit) {
    val placesClient = Places.createClient(context)
    val placeFields = listOf(Place.Field.LAT_LNG)
    val request = FetchPlaceRequest.builder(placeId, placeFields).build()

    placesClient.fetchPlace(request)
        .addOnSuccessListener { response ->
            val place = response.place
            onResult(place.latLng) // Return the LatLng of the place
        }
        .addOnFailureListener { exception ->
            Log.e("PlacesAPI", "Error fetching place details: ${exception.localizedMessage}")
            onResult(null)
        }
}


// 📌 Genetic Algorithm for Shortest Route Optimization
data class Route(val locations: List<LatLng>, val distance: Double)

suspend fun findShortestRoute(locations: List<LatLng>, generations: Int = 300, populationSize: Int = 100): List<LatLng> {
    var population = generateInitialPopulation(locations, populationSize)

    repeat(generations) {
        population = evolvePopulation(population)
    }

    return population.minByOrNull { it.distance }?.locations ?: locations
}

// 1️⃣ Generate Random Population
fun generateInitialPopulation(locations: List<LatLng>, size: Int): List<Route> {
    return List(size) {
        val shuffled = locations.shuffled()
        Route(shuffled, calculateTotalDistance(shuffled))
    }
}

// 2️⃣ Calculate Total Distance
fun calculateTotalDistance(route: List<LatLng>): Double {
    return route.zipWithNext { a, b -> calculateDistance(a, b) }.sum()
}

// 3️⃣ Distance Calculation (Haversine Approximation)
fun calculateDistance(a: LatLng, b: LatLng): Double {
    val dx = a.latitude - b.latitude
    val dy = a.longitude - b.longitude
    return sqrt(dx.pow(2) + dy.pow(2)) * 111 // Convert degrees to km
}

// 4️⃣ Selection, Crossover, and Mutation
fun evolvePopulation(population: List<Route>): List<Route> {
    val selected = population.sortedBy { it.distance }.take(population.size / 2)

    val newPopulation = mutableListOf<Route>()

    while (newPopulation.size < population.size) {
        val parent1 = selected.random()
        val parent2 = selected.random()

        val childLocations = crossover(parent1.locations, parent2.locations)
        val mutatedLocations = mutate(childLocations)

        newPopulation.add(Route(mutatedLocations, calculateTotalDistance(mutatedLocations)))
    }

    return newPopulation
}

// 5️⃣ Crossover (Merge Parent Routes)
fun crossover(parent1: List<LatLng>, parent2: List<LatLng>): List<LatLng> {
    return parent1.shuffled()
}

// 6️⃣ Mutation (Swap Two Locations)
fun mutate(route: List<LatLng>): List<LatLng> {
    if (Random.nextDouble() > 0.2) return route
    return route.shuffled()
}
