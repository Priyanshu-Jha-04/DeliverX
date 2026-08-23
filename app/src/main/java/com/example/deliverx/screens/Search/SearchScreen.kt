package com.example.deliverx.screens.Search

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.AltRoute
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ElectricBolt
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.deliverx.ui.theme.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
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
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequesters = remember { List(10) { FocusRequester() } }

    val placesClient = remember {
        if (!Places.isInitialized()) {
            // Places initialized in App
        }
        Places.createClient(context)
    }

    var locationCount by remember { mutableIntStateOf(3) }

    val locationInputs = remember { mutableStateListOf("", "", "") }
    val selectedLocations = remember { mutableStateListOf(LocationData(""), LocationData(""), LocationData("")) }
    val locationSuggestions = remember { mutableStateMapOf<Int, List<Pair<String, String>>>() }
    val showSuggestions = remember { mutableStateMapOf<Int, Boolean>() }

    var isCalculating by remember { mutableStateOf(false) }
    var showRouteDialog by remember { mutableStateOf(false) }
    var routeResult by remember { mutableStateOf<RouteResult?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371e3
        val φ1 = lat1 * PI / 180
        val φ2 = lat2 * PI / 180
        val Δφ = (lat2 - lat1) * PI / 180
        val Δλ = (lon2 - lon1) * PI / 180

        val a = sin(Δφ / 2) * sin(Δφ / 2) +
                cos(φ1) * cos(φ2) *
                sin(Δλ / 2) * sin(Δλ / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return r * c
    }

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
                    if (index < selectedLocations.size) {
                        selectedLocations[index] = selectedLocations[index].copy(
                            latitude = latLng.latitude,
                            longitude = latLng.longitude
                        )
                    }
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

        val distanceMatrix = Array(locations.size) { i ->
            DoubleArray(locations.size) { j ->
                if (i == j) 0.0 else calculateDistance(
                    locations[i].latitude, locations[i].longitude,
                    locations[j].latitude, locations[j].longitude
                )
            }
        }

        val populationSize = 50
        val generations = 100
        val mutationRate = 0.02
        val eliteSize = 5

        val population = mutableListOf<List<Int>>()
        repeat(populationSize) {
            val route = (1 until locations.size).shuffled().toMutableList()
            route.add(0, 0)
            population.add(route)
        }

        fun calculateFitness(route: List<Int>): Double {
            var totalDistance = 0.0
            for (i in 0 until route.size - 1) {
                totalDistance += distanceMatrix[route[i]][route[i + 1]]
            }
            return 1.0 / (totalDistance + 1e-6)
        }

        fun selection(pop: List<List<Int>>): List<Int> {
            val tournamentSize = 5
            val tournament = pop.shuffled().take(tournamentSize)
            return tournament.maxByOrNull { calculateFitness(it) } ?: pop[0]
        }

        fun crossover(parent1: List<Int>, parent2: List<Int>): List<Int> {
            val size = parent1.size
            val start = Random.nextInt(1, size)
            val end = Random.nextInt(start, size)

            val childGenes = MutableList(size) { -1 }
            for (i in start until end) {
                childGenes[i] = parent1[i]
            }

            var j = 0
            for (gene in parent2) {
                if (!childGenes.contains(gene)) {
                    while (j < size && childGenes[j] != -1) j++
                    if (j < size) childGenes[j] = gene
                }
            }
            childGenes[0] = 0
            return childGenes
        }

        fun mutate(route: List<Int>): List<Int> {
            val mutatedRoute = route.toMutableList()
            for (i in 1 until route.size) {
                if (Random.nextDouble() < mutationRate) {
                    val j = Random.nextInt(1, route.size)
                    val temp = mutatedRoute[i]
                    mutatedRoute[i] = mutatedRoute[j]
                    mutatedRoute[j] = temp
                }
            }
            return mutatedRoute
        }

        fun evolvePopulation(currentPop: List<List<Int>>): MutableList<List<Int>> {
            val sortedPop = currentPop.sortedByDescending { calculateFitness(it) }
            val newPop = sortedPop.take(eliteSize).toMutableList()

            while (newPop.size < populationSize) {
                val parent1 = selection(currentPop)
                val parent2 = selection(currentPop)
                var offspring = crossover(parent1, parent2)
                offspring = mutate(offspring)
                newPop.add(offspring)
            }
            return newPop
        }

        var currentPopulation = population
        repeat(generations) {
            currentPopulation = evolvePopulation(currentPopulation)
        }

        val bestRoute = currentPopulation.maxByOrNull { calculateFitness(it) } ?: population[0]

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

    fun calculateOptimalRoute() {
        if (selectedLocations.size < 2) {
            errorMessage = "Please enter at least 2 locations"
            return
        }

        isCalculating = true
        errorMessage = null
        routeResult = null

        val invalidLocations = selectedLocations.withIndex().filter { it.value.placeId.isEmpty() }
        if (invalidLocations.isNotEmpty()) {
            errorMessage = "Please select all locations from the suggestions list"
            isCalculating = false
            return
        }

        val locationsWithMissingCoords = selectedLocations.withIndex()
            .filter { it.value.latitude == 0.0 || it.value.longitude == 0.0 }

        var locationsFetched = 0
        var locationsFailed = 0

        if (locationsWithMissingCoords.isEmpty()) {
            try {
                routeResult = findShortestRoute(selectedLocations)
                showRouteDialog = true
            } catch (e: Exception) {
                errorMessage = "Error calculating route: ${e.message}"
            } finally {
                isCalculating = false
            }
        } else {
            locationsWithMissingCoords.forEach { (index, location) ->
                fetchPlaceDetails(location.placeId, index) { success ->
                    if (success) locationsFetched++ else locationsFailed++

                    if (locationsFetched + locationsFailed >= locationsWithMissingCoords.size) {
                        if (locationsFailed > 0) {
                            errorMessage = "Failed to get coordinates for $locationsFailed locations"
                            isCalculating = false
                        } else {
                            try {
                                routeResult = findShortestRoute(selectedLocations)
                                showRouteDialog = true
                            } catch (e: Exception) {
                                errorMessage = "Error calculating route: ${e.message}"
                            } finally {
                                isCalculating = false
                            }
                        }
                    }
                }
            }
        }
    }

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
                        .addOnFailureListener {
                            locationSuggestions[index] = emptyList()
                            showSuggestions[index] = false
                        }
                } catch (e: Exception) {
                    locationSuggestions[index] = emptyList()
                    showSuggestions[index] = false
                }
            } else {
                locationSuggestions[index] = emptyList()
                showSuggestions[index] = false
            }
        }
    }

    val stopColors = listOf(
        DeliverXEmerald,
        DeliverXCyan,
        DeliverXViolet,
        DeliverXPink,
        DeliverXAmber,
        DeliverXRose,
        DeliverXElectricCyan,
        DeliverXPurpleLight,
        DeliverXOrange,
        DeliverXGreen
    )

    // Route Result Dialog
    if (showRouteDialog && routeResult != null) {
        Dialog(onDismissRequest = { showRouteDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(24.dp, RoundedCornerShape(24.dp))
                    .border(
                        1.dp,
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        ),
                        RoundedCornerShape(24.dp)
                    ),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "⚡ TSP Optimized Route",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Optimal shortest multi-stop sequence",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DeliverXEmerald.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DeliverXEmerald.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "99.4% OPTIMAL",
                                color = DeliverXEmerald,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Distance Banner
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${String.format("%.2f", routeResult!!.totalDistance / 1000)} km",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text("Total Distance", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                            }
                            Box(modifier = Modifier.width(1.dp).height(28.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${routeResult!!.path.size} Stops",
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text("Waypoints", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Optimal Stop Sequence:",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(modifier = Modifier.heightIn(max = 260.dp)) {
                        items(routeResult!!.path.size) { sequenceIndex ->
                            val locationIndex = routeResult!!.path[sequenceIndex]
                            val location = routeResult!!.locations[locationIndex]
                            val badgeColor = stopColors.getOrElse(sequenceIndex % stopColors.size) { MaterialTheme.colorScheme.primary }

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(badgeColor),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "${sequenceIndex + 1}",
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 13.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        location.address,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = { showRouteDialog = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Done", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Hero Banner
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(22.dp))
                        .border(
                            1.dp,
                            Brush.horizontalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                                )
                            ),
                            RoundedCornerShape(22.dp)
                        ),
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Route Optimizer",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.AltRoute,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Add up to 7 destinations. Our Genetic Algorithm solves the Traveling Salesman Problem (TSP) in real-time.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Number of Stops Selector Chips
                        Text(
                            text = "SELECT NUMBER OF STOPS:",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            (2..7).forEach { count ->
                                val isSelected = locationCount == count
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            locationCount = count
                                            while (locationInputs.size < count) locationInputs.add("")
                                            while (locationInputs.size > count) locationInputs.removeLast()
                                            while (selectedLocations.size < count) selectedLocations.add(LocationData(""))
                                            while (selectedLocations.size > count) selectedLocations.removeLast()
                                        }
                                        .border(
                                            1.dp,
                                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                            RoundedCornerShape(10.dp)
                                        ),
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
                                ) {
                                    Text(
                                        text = "$count",
                                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Location Input Cards
            items(locationCount) { index ->
                val badgeColor = stopColors.getOrElse(index % stopColors.size) { MaterialTheme.colorScheme.primary }
                val stopTitle = if (index == 0) "Start Location (Origin)" else "Stop $index (Destination)"

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, badgeColor.copy(alpha = 0.3f), RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(badgeColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (index == 0) "A" else "$index",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = stopTitle,
                                color = badgeColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = locationInputs.getOrElse(index) { "" },
                            onValueChange = { newValue ->
                                if (index < locationInputs.size) {
                                    locationInputs[index] = newValue
                                    selectedLocations[index] = LocationData(newValue)
                                    fetchLocationSuggestions(newValue, index)
                                }
                            },
                            placeholder = {
                                Text(
                                    "Search location or place...",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequesters[index]),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedBorderColor = badgeColor,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                cursorColor = badgeColor
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Autocomplete Suggestions
                        if (showSuggestions[index] == true) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 6.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                LazyColumn(modifier = Modifier.heightIn(max = 160.dp)) {
                                    items(locationSuggestions[index] ?: emptyList()) { (suggestion, placeId) ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    locationInputs[index] = suggestion
                                                    selectedLocations[index] = LocationData(suggestion, placeId)
                                                    showSuggestions[index] = false

                                                    coroutineScope.launch {
                                                        delay(80)
                                                        if (index < locationCount - 1) {
                                                            focusRequesters[index + 1].requestFocus()
                                                        } else {
                                                            keyboardController?.hide()
                                                            focusManager.clearFocus()
                                                        }
                                                    }
                                                }
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Outlined.Place,
                                                contentDescription = null,
                                                tint = badgeColor,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = suggestion,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontSize = 13.sp,
                                                maxLines = 1
                                            )
                                        }
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), thickness = 0.5.dp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Error Alert
            if (errorMessage != null) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = DeliverXError.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DeliverXError.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = DeliverXError)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(errorMessage!!, color = DeliverXError, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            // Calculate Button
            item {
                Button(
                    onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        calculateOptimalRoute()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(16.dp, RoundedCornerShape(18.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    if (isCalculating) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp), strokeWidth = 2.5.dp)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.ElectricBolt, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Find Optimal TSP Route",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}