package com.example.deliverx.screens.Search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController) {
    val gradientColors = listOf(
        Color(0xFF4ECDC4),
        Color(0xFF45B7D1),
        Color(0xFF0077BE)
    )

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    var expanded by remember { mutableStateOf(false) }
    var selectedNumber by remember { mutableStateOf("Select Number") }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Search Screen", color = Color.Black)

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
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    val navController = rememberNavController()
    SearchScreen(navController = navController)
}
