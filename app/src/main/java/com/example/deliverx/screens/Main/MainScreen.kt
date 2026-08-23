package com.example.deliverx.screens.Main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.deliverx.screens.Home.HomeScreen
import com.example.deliverx.screens.Map.MapScreen
import com.example.deliverx.screens.Search.SearchScreen
import com.example.deliverx.screens.Settings.SettingsScreen
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.CupertinoMaterials
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi

data class VibrantNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val getAccentColor: @Composable () -> Color
)

@OptIn(ExperimentalHazeMaterialsApi::class)
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun MainScreen(navController: NavController) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val hazeState = remember { HazeState() }

    val navItems = listOf(
        VibrantNavItem("Home", Icons.Filled.Home, Icons.Outlined.Home) { MaterialTheme.colorScheme.primary },
        VibrantNavItem("Map", Icons.Filled.Map, Icons.Outlined.Map) { MaterialTheme.colorScheme.secondary },
        VibrantNavItem("Optimizer", Icons.Filled.Route, Icons.Outlined.Route) { MaterialTheme.colorScheme.tertiary },
        VibrantNavItem("Settings", Icons.Filled.Settings, Icons.Outlined.Settings) { MaterialTheme.colorScheme.primary }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Full-bleed screen content — flows edge to edge, including underneath the floating dock,
        // so the dock has real pixels behind it to blur.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(state = hazeState)
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(250)) togetherWith
                            fadeOut(animationSpec = tween(200))
                },
                label = "tab_transition",
                modifier = Modifier.fillMaxSize()
            ) { tab ->
                when (tab) {
                    0 -> HomeScreen(
                        navController = navController,
                        onNavigateToOptimizer = { selectedTab = 2 },
                        onNavigateToMap = { selectedTab = 1 }
                    )
                    1 -> MapScreen(navController = navController)
                    2 -> SearchScreen(navController = navController)
                    3 -> SettingsScreen(navController = navController)
                }
            }
        }

        // Floating iOS-style translucent glass dock — every dimension is a fraction of the
        // available width/height rather than a fixed dp, so it scales with the device.
        BoxWithConstraints(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            val horizontalMargin = (maxWidth * 0.06f).coerceIn(12.dp, 32.dp)
            val bottomMargin = (maxWidth * 0.035f).coerceIn(8.dp, 20.dp)
            val dockHeight = (maxWidth * 0.16f).coerceIn(58.dp, 78.dp)
            val cornerRadius = dockHeight / 2.15f
            val iconSize = dockHeight * 0.32f

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalMargin, vertical = bottomMargin)
                    .height(dockHeight)
                    .shadow(elevation = 24.dp, shape = RoundedCornerShape(cornerRadius), clip = false)
                    .clip(RoundedCornerShape(cornerRadius))
                    .hazeEffect(
                        state = hazeState,
                        style = CupertinoMaterials.thin(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.45f),
                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.45f)
                            )
                        ),
                        shape = RoundedCornerShape(cornerRadius)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = cornerRadius * 0.35f),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val selectedItemWeight = 0.40f
                    val unselectedItemWeight = (1f - selectedItemWeight) / (navItems.size - 1)

                    navItems.forEachIndexed { index, item ->
                        val isSelected = selectedTab == index
                        val accent = item.getAccentColor()
                        val scale by animateFloatAsState(
                            targetValue = if (isSelected) 1.08f else 1.0f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "nav_scale"
                        )

                        val animatedWeight by animateFloatAsState(
                            targetValue = if (isSelected) selectedItemWeight else unselectedItemWeight,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "nav_item_weight"
                        )

                        val contentColor by animateColorAsState(
                            targetValue = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                            animationSpec = tween(200),
                            label = "nav_color"
                        )

                        Box(
                            modifier = Modifier
                                .weight(animatedWeight)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(cornerRadius * 0.7f))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { selectedTab = index },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.9f)
                                        .fillMaxHeight(0.65f)
                                        .clip(RoundedCornerShape(cornerRadius * 0.7f))
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(
                                                    accent.copy(alpha = 0.35f),
                                                    accent.copy(alpha = 0.15f)
                                                )
                                            )
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = accent.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(cornerRadius * 0.7f)
                                        )
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .scale(scale)
                                    .padding(horizontal = 8.dp)
                            ) {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label,
                                    tint = if (isSelected) accent else contentColor,
                                    modifier = Modifier.size(iconSize)
                                )

                                if (isSelected) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = item.label,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
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
