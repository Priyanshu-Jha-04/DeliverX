package com.example.deliverx.screens.Search

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.deliverx.components.DropDownTextField
import com.example.deliverx.components.GradientTextField
import com.example.deliverx.screens.Login_SignUp.SignUpScreen

@Composable
fun SearchScreen(navController: NavController) {
    val noOfPlaces = rememberSaveable { mutableStateOf("") }
    val noOfPlacesFocusRequester = remember { FocusRequester() }


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            val numberOptions = (1..5).toList()
            DropDownTextField(
                options = numberOptions,
                label = "Choose a number",
                onOptionSelected = { selectedNumber ->
                    Log.d("Dropdown", "User selected: $selectedNumber")
                }
            )
        }
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    val navController = rememberNavController()
    SearchScreen(navController = navController)
}
