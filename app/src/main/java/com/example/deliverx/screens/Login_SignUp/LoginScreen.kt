package com.example.deliverx.screens.Login_SignUp

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
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
import androidx.wear.compose.material.MaterialTheme
import com.example.deliverx.R
import com.example.deliverx.components.GradientTextField
import com.example.deliverx.navigation.DeliverXScreens
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    var email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val passwordVisibility = rememberSaveable { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    val isSignInEnabled = email.value.isNotBlank() && password.value.length >= 8
    var isLoading by rememberSaveable { mutableStateOf(false) }

    fun signIn() {
        isLoading = true
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.value, password.value)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                    navController.navigate(DeliverXScreens.HomeScreen.name)
                } else {
                    Toast.makeText(context, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
    fun saveLoginState(context: Context, isLoggedIn: Boolean) {
        val preferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        preferences.edit().putBoolean("isLoggedIn", isLoggedIn).apply()
    }

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
                    .offset(x = (-1).dp, y = 55.dp)
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
                        text = "Together we Travel !!",
                        color = Color(0XFFA4A4A4),
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "Email ID",
                        color = Color(0XFFA4A4A4),
                        fontSize = (14.33).sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Serif,
                        modifier = Modifier.padding(top = 40.dp, start = 49.dp)
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(end = 40.dp)
                    ) {
                        GradientTextField(
                            modifier = Modifier.padding(start = 40.dp),
                            placeholder = "Enter Email",
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email Icon",
                                    tint = Color.White
                                )
                            },
                            keyboardType = KeyboardType.Email,
                            focusRequester = emailFocusRequester,
                            onNext = {
                                passwordFocusRequester.requestFocus()
                            },
                            value = email.value,
                            onValueChange = { email.value = it },
                            trailingIcon = null,
                            isPassword = false
                        )

                        Spacer(modifier = Modifier.height(16.dp))


                        Column(
                            modifier = Modifier.padding(start = 40.dp)
                        ) {
                            Text(
                                text = "Password",
                                color = Color(0XFFA4A4A4),
                                fontSize = (14.33).sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Serif,
                                modifier = Modifier.padding(top = 1.dp, start = 9.dp)
                            )

                            GradientTextField(
                                placeholder = "Enter Password",
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Lock Icon",
                                        tint = Color.White
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = {
                                        passwordVisibility.value = !passwordVisibility.value
                                    }) {
                                        Icon(
                                            imageVector = if (passwordVisibility.value) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Toggle Password Visibility",
                                            tint = Color.White
                                        )
                                    }
                                },
                                keyboardType = KeyboardType.Password,
                                isPassword = true,
                                focusRequester = passwordFocusRequester,
                                value = password.value,
                                onValueChange = { password.value = it }
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                    Image(
                        painter = painterResource(id = R.drawable.signin_button),
                        contentDescription = "Image Button",
                        modifier = Modifier
                            .height(50.dp)
                            .width(315.dp)
                            .align(Alignment.CenterHorizontally)
                            .clickable(enabled = isSignInEnabled) {
                                if (isSignInEnabled) {
                                    signIn()
                                    saveLoginState(context, true)
                                }
                            }
                            .alpha(if (isSignInEnabled) 1f else 0.5f)
                    )

                    Button(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = { navController.navigate(DeliverXScreens.SignUpScreen.name) },
                        colors = ButtonDefaults.buttonColors(Color.Transparent),

                    ) {
                        Row() {
                            Text("Don't have an Account?",
                                color = Color(0XFFA4A4A4),
                                fontSize = (14.33).sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Default)
                            Text(text = " Sign Up",
                                color = Color.LightGray,
                                fontSize = (14.33).sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Default,)
                        }
                    }
                    Image(
                        painter = painterResource(id = R.drawable.or_continue_with),
                        contentDescription = "Image Button",
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .height(20.dp)
                            .width(300.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    Row(modifier = Modifier.padding(20.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.google_login),
                            contentDescription = "Image Button",
                            modifier = Modifier
                                .padding(start = 50.dp)
                                .height(44.dp)
                                .width(58.dp)
                                .clickable {
                                    Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT)
                                        .show()
                                }
                        )
                        Image(
                            painter = painterResource(id = R.drawable.apple_login),
                            contentDescription = "Image Button",
                            modifier = Modifier
                                .padding(start = 33.dp)
                                .height(44.dp)
                                .width(58.dp)
                                .clickable {
                                    Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT)
                                        .show()
                                }
                        )
                        Image(
                            painter = painterResource(id = R.drawable.fb_login),
                            contentDescription = "Image Button",
                            modifier = Modifier
                                .padding(start = 33.dp)
                                .height(44.dp)
                                .width(58.dp)
                                .clickable {
                                    Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT)
                                        .show()
                                }
                        )
                    }

                    Image(
                        painter = painterResource(id = R.drawable.book),
                        contentDescription = null,
                        modifier = Modifier
                            .height(160.dp)
                            .width(185.dp)
                            .rotate(33.9F)
                            .graphicsLayer(rotationZ = -33.9f)
                            .zIndex(5f)
                            .offset(x = 235.dp, y = 8.dp)

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





