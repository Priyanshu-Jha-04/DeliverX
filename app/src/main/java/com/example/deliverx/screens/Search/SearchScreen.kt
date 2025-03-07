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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.launch

// Data class to store location details
data class LocationData(
    val address: String,
    val placeId: String = "", 
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalMaterial3Api::class)
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
    val locationSuggestions = remember { mutableStateMapOf<Int, List<Pair<String, String>>>() } // Pair of display text and place ID
    val showSuggestions = remember { mutableStateMapOf<Int, Boolean>() }

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

    Surface(modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFB9AD1)) {
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
                                    selectedLocations[index] = LocationData(newValue) // Update with basic address
                                    fetchLocationSuggestions(newValue, index)
                                }
                            },
                            label = { Text("Location ${index + 1}", color = Color.White) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .background(
                                    brush = Brush.horizontalGradient(colors = gradientColors),
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                                )
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
                                    items(locationSuggestions[index] ?: emptyList()) { (suggestion, placeId) ->
                                        Text(
                                            text = suggestion,
                                            color = Color.White,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    locationInputs[index] = suggestion
                                                    // Store the selected location with its place ID
                                                    selectedLocations[index] = LocationData(suggestion, placeId)
                                                    showSuggestions[index] = false
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
                            Log.d("LocationData", "Selected locations: ${selectedLocations.joinToString()}")
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
                        Text("Submit Locations", color = Color.White)
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