package com.example.deliverx.screens.Settings

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.deliverx.navigation.DeliverXScreens
import com.example.deliverx.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val themeState = LocalThemeState.current
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    val email = currentUser?.email ?: "agent@deliverx.com"
    var displayName by remember {
        mutableStateOf(currentUser?.displayName?.takeIf { it.isNotBlank() } ?: "Delivery Agent")
    }
    val initials = displayName.split(" ").take(2).mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("")

    var showEditNameDialog by remember { mutableStateOf(false) }
    var isSavingName by remember { mutableStateOf(false) }

    var showTraffic by remember { mutableStateOf(true) }
    var pushNotifications by remember { mutableStateOf(true) }
    var routeDeviationAlert by remember { mutableStateOf(true) }
    var voiceGuidance by remember { mutableStateOf(true) }

    var selectedMapType by remember { mutableStateOf("Standard") }
    val mapTypes = listOf("Standard", "Satellite", "Terrain")

    var selectedUnit by remember { mutableStateOf("km") }
    val distanceUnits = listOf("km", "miles")

    var gaGenerations by remember { mutableFloatStateOf(100f) }
    var gaPopulation by remember { mutableFloatStateOf(50f) }

    val isDynamicAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.statusBars)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Driver Profile Hero Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(24.dp))
                .border(
                    1.dp,
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                        )
                    ),
                    RoundedCornerShape(24.dp)
                ),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar with vibrant ring
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary,
                                    MaterialTheme.colorScheme.tertiary,
                                    MaterialTheme.colorScheme.primary
                                )
                            )
                        )
                        .padding(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = displayName,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit name",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable { showEditNameDialog = true }
                    )
                }

                Text(
                    text = email,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tier Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DeliverXAmber.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DeliverXAmber.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = DeliverXAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "⭐ 4.95 Rating • Elite Fleet Agent",
                            color = DeliverXAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Driver Metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    DriverStatItem(label = "Trips", value = "142", color = MaterialTheme.colorScheme.primary)
                    DriverStatItem(label = "On-time", value = "99.2%", color = DeliverXEmerald)
                    DriverStatItem(label = "Distance", value = "1,420 km", color = MaterialTheme.colorScheme.secondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 🎨 MATERIAL YOU & THEME CUSTOMIZER
        VibrantSettingsGroup(
            title = "Appearance & Material You",
            icon = Icons.Outlined.Palette,
            accentColor = MaterialTheme.colorScheme.primary
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Theme Mode Selector (System / Light / Dark)
                Text(
                    text = "THEME MODE",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppThemeMode.entries.forEach { mode ->
                        val isSelected = themeState.themeMode == mode
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { themeState.updateThemeMode(mode) }
                                .border(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    RoundedCornerShape(12.dp)
                                ),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val icon = when (mode) {
                                    AppThemeMode.SYSTEM -> Icons.Outlined.BrightnessAuto
                                    AppThemeMode.LIGHT -> Icons.Outlined.LightMode
                                    AppThemeMode.DARK -> Icons.Outlined.DarkMode
                                }
                                Icon(
                                    imageVector = icon,
                                    contentDescription = mode.title,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = mode.title,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Material You Dynamic Color Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Material You Dynamic Colors",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (isDynamicAvailable) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = DeliverXCyan.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "Android 12+",
                                        color = DeliverXCyan,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = if (isDynamicAvailable) "Match colors with your device wallpaper (Monet)" else "Available on Android 12+",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }

                    Switch(
                        checked = themeState.dynamicColorEnabled,
                        onCheckedChange = { themeState.updateDynamicColor(it) },
                        enabled = isDynamicAvailable,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Vibrant Color Presets
                Text(
                    text = "VIBRANT COLOR PRESETS",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppColorPreset.entries.forEach { preset ->
                        val isSelected = !themeState.dynamicColorEnabled && themeState.colorPreset == preset

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { themeState.updateColorPreset(preset) }
                                .border(
                                    2.dp,
                                    if (isSelected) preset.primaryColor else Color.Transparent,
                                    RoundedCornerShape(14.dp)
                                ),
                            shape = RoundedCornerShape(14.dp),
                            color = preset.primaryColor.copy(alpha = 0.2f)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(preset.primaryColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = preset.title.substringBefore(" "),
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 9.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Routing & Genetic Algorithm Engine Settings
        VibrantSettingsGroup(
            title = "Genetic Algorithm Engine",
            icon = Icons.Outlined.Psychology,
            accentColor = MaterialTheme.colorScheme.primary
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GA Generations (Iterations)",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${gaGenerations.toInt()}",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = gaGenerations,
                    onValueChange = { gaGenerations = it },
                    valueRange = 50f..300f,
                    steps = 4,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Population Size (Routes / Gen)",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${gaPopulation.toInt()}",
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = gaPopulation,
                    onValueChange = { gaPopulation = it },
                    valueRange = 20f..100f,
                    steps = 3,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.secondary,
                        activeTrackColor = MaterialTheme.colorScheme.secondary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Map Preferences
        VibrantSettingsGroup(
            title = "Map & Navigation",
            icon = Icons.Outlined.Map,
            accentColor = DeliverXCyan
        ) {
            VibrantSettingsRow(
                icon = Icons.Default.Layers,
                iconColor = DeliverXCyan,
                title = "Default Layer",
                control = {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        mapTypes.forEach { type ->
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedMapType = type }
                                    .border(
                                        1.dp,
                                        if (selectedMapType == type) DeliverXCyan else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                        RoundedCornerShape(12.dp)
                                    ),
                                shape = RoundedCornerShape(12.dp),
                                color = if (selectedMapType == type) DeliverXCyan.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                            ) {
                                Text(
                                    text = type,
                                    color = if (selectedMapType == type) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            )

            VibrantSettingsToggleRow(
                icon = Icons.Default.Traffic,
                iconColor = DeliverXAmber,
                title = "Live Traffic Overlay",
                checked = showTraffic,
                onCheckedChange = { showTraffic = it }
            )

            VibrantSettingsRow(
                icon = Icons.Default.Straighten,
                iconColor = DeliverXEmerald,
                title = "Distance Units",
                control = {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        distanceUnits.forEach { unit ->
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedUnit = unit }
                                    .border(
                                        1.dp,
                                        if (selectedUnit == unit) DeliverXEmerald else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                        RoundedCornerShape(12.dp)
                                    ),
                                shape = RoundedCornerShape(12.dp),
                                color = if (selectedUnit == unit) DeliverXEmerald.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                            ) {
                                Text(
                                    text = unit,
                                    color = if (selectedUnit == unit) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Notifications & Guidance
        VibrantSettingsGroup(
            title = "Alerts & Audio",
            icon = Icons.Outlined.Notifications,
            accentColor = DeliverXPink
        ) {
            VibrantSettingsToggleRow(
                icon = Icons.Default.NotificationsActive,
                iconColor = DeliverXPink,
                title = "Push Route Updates",
                checked = pushNotifications,
                onCheckedChange = { pushNotifications = it }
            )
            VibrantSettingsToggleRow(
                icon = Icons.Default.WrongLocation,
                iconColor = DeliverXRose,
                title = "Route Deviation Warning",
                checked = routeDeviationAlert,
                onCheckedChange = { routeDeviationAlert = it }
            )
            VibrantSettingsToggleRow(
                icon = Icons.AutoMirrored.Filled.VolumeUp,
                iconColor = DeliverXCyan,
                title = "Voice Navigation Prompts",
                checked = voiceGuidance,
                onCheckedChange = { voiceGuidance = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // App & Support
        VibrantSettingsGroup(
            title = "Support & Legal",
            icon = Icons.Outlined.Info,
            accentColor = DeliverXAmber
        ) {
            VibrantClickableRow(
                icon = Icons.Default.StarRate,
                iconColor = DeliverXAmber,
                title = "Rate DeliverX App",
                onClick = { Toast.makeText(context, "Thank you for rating 5 stars!", Toast.LENGTH_SHORT).show() }
            )
            VibrantClickableRow(
                icon = Icons.Default.Shield,
                iconColor = DeliverXCyan,
                title = "Privacy Policy",
                trailingIcon = Icons.AutoMirrored.Filled.OpenInNew,
                onClick = { Toast.makeText(context, "Privacy Policy loaded", Toast.LENGTH_SHORT).show() }
            )
            VibrantClickableRow(
                icon = Icons.Default.Description,
                iconColor = MaterialTheme.colorScheme.primary,
                title = "Terms of Service",
                trailingIcon = Icons.AutoMirrored.Filled.OpenInNew,
                onClick = { Toast.makeText(context, "Terms of Service loaded", Toast.LENGTH_SHORT).show() }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sign Out Button
        Button(
            onClick = {
                val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                prefs.edit().putBoolean("isLoggedIn", false).apply()
                auth.signOut()
                navController.navigate(DeliverXScreens.LoginScreen.name) {
                    popUpTo(0) { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .shadow(12.dp, RoundedCornerShape(16.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign Out of Account", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = {
                Toast.makeText(context, "Account deletion confirmation requested", Toast.LENGTH_LONG).show()
            }
        ) {
            Text("Delete DeliverX Account", color = DeliverXError, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(80.dp))
    }

    if (showEditNameDialog) {
        var editedName by remember { mutableStateOf(displayName) }

        AlertDialog(
            onDismissRequest = { if (!isSavingName) showEditNameDialog = false },
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            title = {
                Text(
                    text = "Edit Display Name",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    singleLine = true,
                    enabled = !isSavingName,
                    label = { Text("Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            confirmButton = {
                TextButton(
                    enabled = editedName.isNotBlank() && !isSavingName,
                    onClick = {
                        val trimmed = editedName.trim()
                        isSavingName = true
                        val profileUpdate = UserProfileChangeRequest.Builder()
                            .setDisplayName(trimmed)
                            .build()
                        currentUser?.updateProfile(profileUpdate)
                            ?.addOnCompleteListener { task ->
                                isSavingName = false
                                if (task.isSuccessful) {
                                    displayName = trimmed
                                    showEditNameDialog = false
                                    Toast.makeText(context, "Name updated", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Failed to update name: ${task.exception?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                ) {
                    Text(if (isSavingName) "Saving..." else "Save", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !isSavingName,
                    onClick = { showEditNameDialog = false }
                ) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }
}

@Composable
fun DriverStatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = color, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
    }
}

@Composable
fun VibrantSettingsGroup(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 6.dp, bottom = 8.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                color = accentColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }
    }
}

@Composable
fun VibrantSettingsRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    control: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        control()
    }
}

@Composable
fun VibrantSettingsToggleRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    VibrantSettingsRow(
        icon = icon,
        iconColor = iconColor,
        title = title,
        control = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = iconColor,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    )
}

@Composable
fun VibrantClickableRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    onClick: () -> Unit,
    trailingIcon: ImageVector? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        if (trailingIcon != null) {
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
