package com.example.deliverx.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*

import androidx.wear.compose.material.Text

@Composable
fun GradientTextField(
    modifier: Modifier = Modifier,
    value : String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: @Composable () -> Unit,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    focusRequester: FocusRequester = FocusRequester(), // Added FocusRequester
    onNext: (() -> Unit)? = null // Added callback for "Next"
) {
    var text by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }

    val visualTransformation = if (isPassword && !passwordVisibility) {
        PasswordVisualTransformation()
    } else {
        VisualTransformation.None
    }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .focusRequester(focusRequester)
            .width(314.dp)
            .height(52.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.2f))
            .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
        textStyle = TextStyle(fontSize = 16.sp, color = Color.White),
        cursorBrush = SolidColor(Color.White),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = if (onNext != null) ImeAction.Next else ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onNext = { onNext?.invoke() }
        ),
        visualTransformation = visualTransformation,
        decorationBox = @Composable { innerTextField ->
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                leadingIcon()

                Spacer(modifier = Modifier.width(8.dp))

                Box(Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = Color.Gray.copy(alpha = 0.6f)
                        )
                    }
                    innerTextField()
                }

                if (isPassword) {
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                        Icon(
                            imageVector = if (passwordVisibility) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisibility) "Hide password" else "Show password",
                            tint = Color.White
                        )
                    }
                } else {
                    trailingIcon?.invoke()
                }
            }
        }
    )
}



@Composable
fun DropDownTextField(
    options: List<Int>, // List of integer options
    label: String = "Select a number",
    onOptionSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf<Int?>(null) } // Stores selected number

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedOption?.toString() ?: "", // Show selected number or empty
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(start = 20.dp, end = 20.dp),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location Marker",
                    tint = Color.Red // Color for the icon
                )
            },
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Gray,
                focusedBorderColor = Color.Blue
            )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            option.toString(),
                            color = Color.Black
                        )
                    }, // Convert Int to String for display
                    onClick = {
                        selectedOption = option
                        expanded = false
                        onOptionSelected(option) // Return selected integer
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp),
                    colors = MenuDefaults.itemColors(
                        textColor = Color.Black,
                        leadingIconColor = Color.Gray,
                        trailingIconColor = Color.Gray
                    )
                )
            }
        }
    }
}


