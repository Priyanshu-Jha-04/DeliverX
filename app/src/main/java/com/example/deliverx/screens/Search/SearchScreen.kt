package com.example.deliverx.screens.Search

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*
import kotlin.random.Random

// Data class to store location details
data class LocationData(
    val address: String,
    val placeId: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

// Data class for route results
data class RouteResult(
    val path: List<Int>,
    val totalDistance: Double,
    val locations: List<LocationData>
)

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchScreen(navController: NavController) {
    val gradientColors = listOf(
        Color(0xFF640D6B),
        Color(0xFF640D6B)
    )

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Keyboard and focus management
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    // Create a list of focus requesters for each location input
    val focusRequesters = remember { List(10) { FocusRequester() } }

    // Places API client
    val placesClient = remember {
        if (!Places.isInitialized()) {
            // Replace with your actual API key
            Places.initialize(context, "YOUR_API_KEY_HERE")
        }
        Places.createClient(context)
    }

    var expanded by remember { mutableStateOf(false) }
    var selectedNumber by remember { mutableStateOf("Select Number") }
    var locationCount by remember { mutableStateOf(0) }

    // State for location inputs and suggestions
    val locationInputs = remember { mutableStateListOf<String>() }
    val selectedLocations = remember { mutableStateListOf<LocationData>() }
    val locationSuggestions =
        remember { mutableStateMapOf<Int, List<Pair<String, String>>>() } // Pair of display text and place ID
    val showSuggestions = remember { mutableStateMapOf<Int, Boolean>() }

    // State for route calculation and dialog
    var isCalculating by remember { mutableStateOf(false) }
    var showRouteDialog by remember { mutableStateOf(false) }
    var routeResult by remember { mutableStateOf<RouteResult?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Function to calculate distance between two points using Haversine formula
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371e3 // Earth radius in meters
        val φ1 = lat1 * PI / 180
        val φ2 = lat2 * PI / 180
        val Δφ = (lat2 - lat1) * PI / 180
        val Δλ = (lon2 - lon1) * PI / 180

        val a = sin(Δφ / 2) * sin(Δφ / 2) +
                cos(φ1) * cos(φ2) *
                sin(Δλ / 2) * sin(Δλ / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return r * c // Distance in meters
    }

    // Function to fetch full place details (lat/lng) from placeId
    fun fetchPlaceDetails(placeId: String, index: Int, onComplete: (success: Boolean) -> Unit) {
        if (placeId.isEmpty()) {
            onComplete(false)
            return
        }

        val placeFields = listOf(Place.Field.LAT_LNG, Place.Field.ADDRESS)
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                val place = response.place
                val latLng = place.latLng

                if (latLng != null) {
                    selectedLocations[index] = selectedLocations[index].copy(
                        latitude = latLng.latitude,
                        longitude = latLng.longitude
                    )
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            }
            .addOnFailureListener { e ->
                Log.e("PlaceDetails", "Error fetching details: ${e.message}", e)
                onComplete(false)
            }
    }

    // Genetic Algorithm implementation
    fun findShortestRoute(locations: List<LocationData>): RouteResult {
        if (locations.size <= 2) {
            return RouteResult(
                path = (0 until locations.size).toList(),
                totalDistance = if (locations.size == 2) {
                    calculateDistance(
                        locations[0].latitude, locations[0].longitude,
                        locations[1].latitude, locations[1].longitude
                    )
                } else 0.0,
                locations = locations
            )
        }

        // Calculate distance matrix
        val distanceMatrix = Array(locations.size) { i ->
            DoubleArray(locations.size) { j ->
                if (i == j) 0.0 else calculateDistance(
                    locations[i].latitude, locations[i].longitude,
                    locations[j].latitude, locations[j].longitude
                )
            }
        }

        // GA parameters
        val populationSize = 50
        val generations = 100
        val mutationRate = 0.01
        val eliteSize = 5

        // Create initial population
        val population = mutableListOf<List<Int>>()
        repeat(populationSize) {
            val route = (1 until locations.size).shuffled().toMutableList()
            route.add(0, 0) // Always start from the first location
            population.add(route)
        }

        // Calculate fitness (inverse of total distance)
        fun calculateFitness(route: List<Int>): Double {
            var totalDistance = 0.0
            for (i in 0 until route.size - 1) {
                totalDistance += distanceMatrix[route[i]][route[i + 1]]
            }
            return 1.0 / totalDistance
        }

        // Tournament selection
        fun selection(population: List<List<Int>>): List<Int> {
            val tournamentSize = 5
            val tournament = population.shuffled().take(tournamentSize)
            return tournament.maxByOrNull { calculateFitness(it) } ?: population[0]
        }

        // Crossover (Ordered Crossover)
        fun crossover(parent1: List<Int>, parent2: List<Int>): List<Int> {
            val size = parent1.size
            val start = Random.nextInt(1, size) // Skip the first city (start point)
            val end = Random.nextInt(start, size)

            val childGenes = MutableList(size) { -1 }
            // Copy segment from parent1
            for (i in start until end) {
                childGenes[i] = parent1[i]
            }

            // Fill remaining positions with genes from parent2 in order
            var j = 0
            for (gene in parent2) {
                if (!childGenes.contains(gene)) {
                    while (j < size && childGenes[j] != -1) j++
                    if (j < size) childGenes[j] = gene
                }
            }

            // Ensure first city is always 0
            childGenes[0] = 0

            return childGenes
        }

        // Mutation (swap mutation)
        fun mutate(route: List<Int>): List<Int> {
            val mutatedRoute = route.toMutableList()
            for (i in 1 until route.size) { // Skip the first city (start point)
                if (Random.nextDouble() < mutationRate) {
                    val j = Random.nextInt(1, route.size) // Skip the first city (start point)
                    val temp = mutatedRoute[i]
                    mutatedRoute[i] = mutatedRoute[j]
                    mutatedRoute[j] = temp
                }
            }
            return mutatedRoute
        }

        // Evolve population
        fun evolvePopulation(currentPopulation: List<List<Int>>): MutableList<List<Int>> {
            // Sort by fitness
            val sortedPopulation = currentPopulation.sortedByDescending { calculateFitness(it) }

            // Keep elite individuals
            val newPopulation = sortedPopulation.take(eliteSize).toMutableList()

            // Add offspring
            while (newPopulation.size < populationSize) {
                val parent1 = selection(currentPopulation)
                val parent2 = selection(currentPopulation)
                var offspring = crossover(parent1, parent2)
                offspring = mutate(offspring)
                newPopulation.add(offspring)
            }

            return newPopulation
        }

        // Run the GA
        var currentPopulation = population
        repeat(generations) {
            currentPopulation = evolvePopulation(currentPopulation)
        }

        // Get best route
        val bestRoute = currentPopulation.maxByOrNull { calculateFitness(it) } ?: population[0]

        // Calculate total distance
        var totalDistance = 0.0
        for (i in 0 until bestRoute.size - 1) {
            totalDistance += distanceMatrix[bestRoute[i]][bestRoute[i + 1]]
        }

        return RouteResult(
            path = bestRoute,
            totalDistance = totalDistance,
            locations = locations
        )
    }

    // Function to prepare and execute route calculation
    fun calculateOptimalRoute() {
        if (selectedLocations.size < 2) {
            errorMessage = "Please select at least 2 locations"
            return
        }

        // Reset states
        isCalculating = true
        errorMessage = null
        routeResult = null

        // Check for valid locations (need placeId)
        val invalidLocations = selectedLocations.withIndex()
            .filter { it.value.placeId.isEmpty() }

        if (invalidLocations.isNotEmpty()) {
            errorMessage = "Please select valid locations from the suggestions"
            isCalculating = false
            return
        }

        // Check if we have coordinates for all locations
        val locationsWithMissingCoords = selectedLocations.withIndex()
            .filter { it.value.latitude == 0.0 || it.value.longitude == 0.0 }

        // Track progress of location detail fetching
        var locationsFetched = 0
        var locationsFailed = 0

        if (locationsWithMissingCoords.isEmpty()) {
            // All coordinates are available, run GA
            try {
                routeResult = findShortestRoute(selectedLocations)
                showRouteDialog = true
            } catch (e: Exception) {
                errorMessage = "Error calculating route: ${e.message}"
                Log.e("RouteCalculation", "Error", e)
            } finally {
                isCalculating = false
            }
        } else {
            // Fetch missing coordinates first
            locationsWithMissingCoords.forEach { (index, location) ->
                fetchPlaceDetails(location.placeId, index) { success ->
                    if (success) {
                        locationsFetched++
                    } else {
                        locationsFailed++
                    }

                    // Check if all location fetching attempts are complete
                    if (locationsFetched + locationsFailed >= locationsWithMissingCoords.size) {
                        if (locationsFailed > 0) {
                            errorMessage = "Failed to get details for $locationsFailed locations"
                            isCalculating = false
                        } else {
                            // All locations fetched, now calculate route
                            try {
                                routeResult = findShortestRoute(selectedLocations)
                                showRouteDialog = true
                            } catch (e: Exception) {
                                errorMessage = "Error calculating route: ${e.message}"
                                Log.e("RouteCalculation", "Error", e)
                            } finally {
                                isCalculating = false
                            }
                        }
                    }
                }
            }
        }
    }

    // Function to fetch location suggestions using Google Places API
    fun fetchLocationSuggestions(query: String, index: Int) {
        coroutineScope.launch {
            if (query.length >= 2) {
                val token = AutocompleteSessionToken.newInstance()
                val request = FindAutocompletePredictionsRequest.builder()
                    .setSessionToken(token)
                    .setQuery(query)
                    .build()

                try {
                    placesClient.findAutocompletePredictions(request)
                        .addOnSuccessListener { response ->
                            val suggestions = response.autocompletePredictions.map {
                                Pair(it.getFullText(null).toString(), it.placeId)
                            }
                            locationSuggestions[index] = suggestions
                            showSuggestions[index] = suggestions.isNotEmpty()
                        }
                        .addOnFailureListener { e ->
                            locationSuggestions[index] = emptyList()
                            showSuggestions[index] = false
                            Log.e("FetchSuggestions", "Error: ${e.message}", e)
                        }
                } catch (e: Exception) {
                    Log.e("FetchSuggestions", "Exception: ${e.message}", e)
                }
            } else {
                locationSuggestions[index] = emptyList()
                showSuggestions[index] = false
            }
        }
    }

    // Initialize or resize the selected locations list
    LaunchedEffect(locationCount) {
        while (selectedLocations.size < locationCount) {
            selectedLocations.add(LocationData(""))
        }
        while (selectedLocations.size > locationCount) {
            selectedLocations.removeLast()
        }
    }

    // Route dialog
    if (showRouteDialog && routeResult != null) {
        Dialog(onDismissRequest = { showRouteDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF8A2BE2)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Optimal Route",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Total Distance: ${
                            String.format(
                                "%.2f",
                                routeResult!!.totalDistance / 1000
                            )
                        } km",
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Visit locations in this order:",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp)
                    ) {
                        items(routeResult!!.path) { locationIndex ->
                            val location = routeResult!!.locations[locationIndex]
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF4B0082)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "${routeResult!!.path.indexOf(locationIndex) + 1}.",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text(
                                        location.address,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showRouteDialog = false },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .height(48.dp)
                            .width(120.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF640D6B)
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                    ) {
                        Text("Close", color = Color.White)
                    }
                }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFB9AD1)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.padding(screenHeight * 0.03f))

                // Dropdown Menu with Gradient Background
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier
                        .height(56.dp)
                        .width(280.dp)
                ) {
                    // Dropdown TextField with Gradient Background
                    TextField(
                        value = selectedNumber,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Number of Locations", color = Color.White) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .menuAnchor()
                            .background(
                                brush = Brush.horizontalGradient(colors = gradientColors),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                            )
                    )

                    // Dropdown Menu
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .width(280.dp)
                            .background(
                                brush = Brush.horizontalGradient(colors = gradientColors)
                            )
                    ) {
                        // Add numbers 1 to 10 as dropdown items
                        (1..10).forEach { number ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = number.toString(),
                                        color = Color.White
                                    )
                                },
                                onClick = {
                                    selectedNumber = number.toString()
                                    locationCount = number

                                    // Initialize or resize the location inputs list
                                    while (locationInputs.size < number) {
                                        locationInputs.add("")
                                    }
                                    while (locationInputs.size > number) {
                                        locationInputs.removeLast()
                                    }

                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Display location input fields based on selected number
            items(locationCount) { index ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        TextField(
                            value = locationInputs.getOrElse(index) { "" },
                            onValueChange = { newValue ->
                                if (index < locationInputs.size) {
                                    locationInputs[index] = newValue
                                    selectedLocations[index] =
                                        LocationData(newValue) // Update with basic address
                                    fetchLocationSuggestions(newValue, index)
                                }
                            },
                            label = { Text("Location ${index + 1}", color = Color.White) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .background(
                                    brush = Brush.horizontalGradient(colors = gradientColors),
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                                )
                                .focusRequester(focusRequesters[index])
                        )
                        if (showSuggestions[index] == true) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF8A2BE2) // A lighter purple for suggestions
                                )
                            ) {
                                LazyColumn(
                                    modifier = Modifier.heightIn(max = 200.dp)
                                ) {
                                    items(
                                        locationSuggestions[index] ?: emptyList()
                                    ) { (suggestion, placeId) ->
                                        Text(
                                            text = suggestion,
                                            color = Color.White,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    locationInputs[index] = suggestion
                                                    // Store the selected location with its place ID
                                                    selectedLocations[index] =
                                                        LocationData(suggestion, placeId)
                                                    showSuggestions[index] = false

                                                    // Move focus to next field or hide keyboard if it's the last field
                                                    coroutineScope.launch {
                                                        delay(100) // Small delay to ensure UI updates
                                                        if (index < locationCount - 1) {
                                                            // Focus on next field
                                                            focusRequesters[index + 1].requestFocus()
                                                        } else {
                                                            // Hide keyboard if it's the last field
                                                            keyboardController?.hide()
                                                            focusManager.clearFocus()
                                                        }
                                                    }
                                                }
                                                .padding(16.dp)
                                        )
                                        Divider(color = Color(0xFFD8BFD8))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (locationCount > 0) {
                item {
                    Button(
                        onClick = {
                            // Hide keyboard when calculating route
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            calculateOptimalRoute()
                        },
                        modifier = Modifier
                            .padding(16.dp)
                            .height(56.dp)
                            .width(280.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF640D6B)
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                    ) {
                        Text("Find Shortest Route", color = Color.White)
                    }

                    // Show loading indicator while calculating
                    if (isCalculating) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(16.dp),
                            color = Color.White
                        )
                    }

                    // Show error message if any
                    if (errorMessage != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFF6B6B)
                            )
                        ) {
                            Text(
                                errorMessage!!,
                                color = Color.White,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        val navController = rememberNavController()
        SearchScreen(navController = navController)
    }
}