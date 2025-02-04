package com.example.deliverx.screens.Login_SignUp

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.deliverx.R

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) } // Simulate loading state

    val usernameFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(R.drawable.login_animated),
                contentDescription = "Background Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(393.dp)
                    .zIndex(0f),
                alignment = Alignment.TopStart
            )
            Image(
                painter = painterResource(id = R.drawable.login_ani_top),
                contentDescription = "Foreground",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(197.dp)
                    .offset(y = (-49).dp)
                    .zIndex(1f)
            )
            Image(
                painter = painterResource(id = R.drawable.element_01),
                contentDescription = null,
                modifier = Modifier
                    .width(130.dp)
                    .height(139.dp)
                    .zIndex(4f)
                    .rotate(18.69F)
                    .graphicsLayer(rotationZ = -18.69f)
                    .offset(x = 262.dp, y = 205.dp),
                alignment = AbsoluteAlignment.BottomRight
            )
            Image(
                painter = painterResource(id = R.drawable.login_bg),
                contentDescription = null,
                modifier = Modifier
                    .width(944.dp)
                    .height(1037.dp)
                    .zIndex(3f)
                    .offset(x=(-1).dp, y = 60.dp)
                    .graphicsLayer(scaleX = 1.01f, scaleY = 1.01f)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(4f)
                    .padding(top = 265.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(5.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = "Welcome Back!",
                        color = Color(0XFFFFFAEC),
                        fontSize = 39.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Serif,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "Together we Travel!!!",
                        color = Color(0XFFA4A4A4),
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "Username",
                        color = Color(0XFFA4A4A4),
                        fontSize = (14.33).sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Serif,
                        modifier = Modifier.padding(top = 40.dp, start = 49.dp)
                    )

                }
            }
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    val navController = rememberNavController()
    LoginScreen(navController = navController)
}




