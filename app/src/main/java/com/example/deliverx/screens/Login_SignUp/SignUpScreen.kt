package com.example.deliverx.screens.Login_SignUp

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.deliverx.R
import com.example.deliverx.components.GradientTextField
import com.example.deliverx.navigation.DeliverXScreens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(navController: NavController) {
    // Get screen dimensions for responsive sizing
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val density = LocalDensity.current

    // Calculate responsive dimensions
    val imageScale = screenWidth / 360.dp // Base scale on a standard phone width

    var email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    var FName = rememberSaveable { mutableStateOf("") }
    val LName = rememberSaveable { mutableStateOf("") }
    val MobNo = rememberSaveable { mutableStateOf("") }
    val FNameFocusRequester = remember { FocusRequester() }
    val LNameFocusRequester = remember { FocusRequester() }
    val MobNoFocusRequester = remember { FocusRequester() }
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    val passwordVisibility = rememberSaveable { mutableStateOf(false) }
    val isSignInEnabled =
        email.value.isNotBlank() && password.value.isNotBlank() && FName.value.isNotBlank() && LName.value.isNotBlank() && MobNo.value.isNotBlank()
    val context = LocalContext.current
    var isLoading by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    fun saveLoginState(context: Context, isLoggedIn: Boolean) {
        val preferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        preferences.edit().putBoolean("isLoggedIn", isLoggedIn).apply()
    }

    fun signUp() {
        isLoading = true
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.value, password.value)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid.orEmpty()

                    if (userId.isBlank()) {
                        Log.e("SignUp", "User ID is blank")
                        Toast.makeText(context, "Sign Up Failed: Invalid user ID", Toast.LENGTH_SHORT).show()
                        isLoading = false
                        return@addOnCompleteListener
                    }

                    val user = hashMapOf(
                        "firstName" to FName.value.trim(),
                        "lastName" to LName.value.trim(),
                        "email" to email.value.trim(),
                        "mobile" to MobNo.value.trim()
                    )

                    FirebaseFirestore.getInstance().collection("users").document(userId)
                        .set(user)
                        .addOnSuccessListener {
                            Log.d("SignUp", "User successfully written to Firestore.")
                            Toast.makeText(context, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                            saveLoginState(context, true)
                            navController.navigate(DeliverXScreens.SearchScreen.name) {
                                popUpTo("signup") { inclusive = true }
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("FirestoreError", "Failed to write user data", e)
                            Toast.makeText(context, "Firestore Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                        .addOnCompleteListener {
                            Log.d("SignUp", "Firestore write completed")
                            isLoading = false
                        }


                } else {
                    Log.e("SignUp", "Auth failed", task.exception)
                    Toast.makeText(
                        context,
                        "Sign Up Failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    isLoading = false
                }
            }
    }


    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color.Black
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(R.drawable.signup_animated),
                contentDescription = "Background Image",
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .offset(x = (screenWidth * 0.15f), y = (screenHeight * 0.01f))
                    .height(screenHeight * 0.25f)
                    .zIndex(0f),
                alignment = Alignment.TopEnd,
                contentScale = ContentScale.FillWidth
            )

            Image(
                painter = painterResource(id = R.drawable.signup_shape1),
                contentDescription = "Foreground",
                modifier = Modifier
                    .width(screenWidth * 0.5f)
                    .height(screenHeight * 0.15f)
                    .zIndex(1f)
                    .rotate(33.9F)
                    .graphicsLayer(rotationZ = -33.9f)
                    .offset(x = (screenWidth * -0.07f)),
                contentScale = ContentScale.Fit
            )

            Image(
                painter = painterResource(id = R.drawable.login_ani_top),
                contentDescription = "Foreground",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 0.22f)
                    .offset(y = (screenHeight * -0.05f))
                    .zIndex(1f),
                contentScale = ContentScale.FillWidth
            )

            Image(
                painter = painterResource(id = R.drawable.su_gradient),
                contentDescription = null,
                modifier = Modifier
                    .width(screenWidth * 0.42f)
                    .height(screenHeight * 0.17f)
                    .zIndex(2f)
                    .rotate(18.69F)
                    .graphicsLayer(rotationZ = -18.69f)
                    .offset(x = screenWidth * 0.67f, y = screenHeight * 0.22f),
                contentScale = ContentScale.Fit
            )

            Image(
                painter = painterResource(id = R.drawable.login_bg),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 1.1f)
                    .zIndex(3f)
                    .offset(x = screenWidth * -0.01f, y = screenHeight * 0.08f)
                    .graphicsLayer(
                        scaleX = 1.05f,
                        scaleY = 1.01f
                    ),
                contentScale = ContentScale.FillBounds
            )

            Image(
                painter = painterResource(id = R.drawable.element_01),
                contentDescription = null,
                modifier = Modifier
                    .width(screenWidth * 0.22f)
                    .height(screenHeight * 0.12f)
                    .zIndex(4f)
                    .rotate(18.69F)
                    .graphicsLayer(rotationZ = -18.69f)
                    .offset(x = screenWidth * 0.87f, y = screenHeight * 0.28f),
                contentScale = ContentScale.Fit
            )

            Image(
                painter = painterResource(id = R.drawable.su_g),
                contentDescription = "Foreground",
                modifier = Modifier
                    .width(screenWidth * 0.54f)
                    .height(screenHeight * 0.16f)
                    .zIndex(4f)
                    .offset(x = (screenWidth * -0.125f), y = screenHeight * 0.22f)
                    .rotate(66.69F)
                    .graphicsLayer(rotationZ = -66.69f),
                contentScale = ContentScale.Fit
            )

            Image(
                painter = painterResource(id = R.drawable.book),
                contentDescription = null,
                modifier = Modifier
                    .height(screenHeight * 0.17f)
                    .width(screenWidth * 0.51f)
                    .rotate(33.9F)
                    .graphicsLayer(rotationZ = -33.9f)
                    .zIndex(4f)
                    .align(Alignment.BottomEnd)
                    .offset(x = screenWidth * 0.06f, y = screenHeight * -0.09f),
                contentScale = ContentScale.Fit
            )


            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(4f)
                    .padding(top = screenHeight * 0.28f),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Get On Board!",
                        color = Color(0XFFFFFAEC),
                        fontSize = (screenWidth * 0.11f).value.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Serif,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Text(
                        text = "Join us Now !!",
                        color = Color(0XFFA4A4A4),
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = (screenWidth * 0.04f).value.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(screenWidth * 0.04f)
                            .zIndex(7f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Column(
                            modifier = Modifier.padding(start = 30.dp, end = 30.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                GradientTextField(
                                    placeholder = "First Name",
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.PeopleAlt,
                                            contentDescription = "Person Icon",
                                            tint = Color.White
                                        )
                                    },
                                    trailingIcon = null,
                                    keyboardType = KeyboardType.Text,
                                    focusRequester = FNameFocusRequester,
                                    onNext = { LNameFocusRequester.requestFocus() },
                                    value = FName.value,
                                    onValueChange = { FName.value = it },
                                    isPassword = false,
                                    modifier = Modifier
                                        .height(52.dp)
                                        .weight(1f)
                                        .padding(end = 4.dp)
                                )

                                GradientTextField(
                                    placeholder = "Last Name",
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.PeopleAlt,
                                            contentDescription = "Person Icon",
                                            tint = Color.White
                                        )
                                    },
                                    trailingIcon = null,
                                    keyboardType = KeyboardType.Text,
                                    focusRequester = LNameFocusRequester,
                                    onNext = { emailFocusRequester.requestFocus() },
                                    value = LName.value,
                                    onValueChange = { LName.value = it },
                                    isPassword = false,
                                    modifier = Modifier
                                        .height(52.dp)
                                        .weight(1f)
                                        .padding(start = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.padding(screenHeight * 0.01f))

                            GradientTextField(
                                modifier = Modifier.fillMaxWidth(),
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
                                onNext = { passwordFocusRequester.requestFocus() },
                                value = email.value,
                                onValueChange = { email.value = it },
                                trailingIcon = null,
                                isPassword = false
                            )

                            Spacer(modifier = Modifier.padding(screenHeight * 0.01f))

                            GradientTextField(
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = "Create Password",
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
                                onNext = { MobNoFocusRequester.requestFocus() },
                                value = password.value,
                                onValueChange = { password.value = it }
                            )

                            Spacer(modifier = Modifier.padding(screenHeight * 0.01f))

                            GradientTextField(
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = "Mobile Number",
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = "Phone Icon",
                                        tint = Color.White
                                    )
                                },
                                keyboardType = KeyboardType.Phone,
                                focusRequester = MobNoFocusRequester,
                                value = MobNo.value,
                                onValueChange = { MobNo.value = it },
                                trailingIcon = null,
                                isPassword = false
                            )
                        }



                        Spacer(modifier = Modifier.padding(screenHeight * 0.01f))

                        val coroutineScope = rememberCoroutineScope()

                        Button(
                            onClick = {
                                if (!isSignInEnabled) {
                                    Toast.makeText(
                                        context,
                                        "Fill up all the details!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else if (password.value.length < 8) {
                                    Toast.makeText(
                                        context,
                                        "Password must be at least 8 characters!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    coroutineScope.launch {
                                        navController.navigate(DeliverXScreens.SearchScreen.name) {
                                            popUpTo("signup") { inclusive = true }
                                        }
                                    }
                                }
                            },
                            enabled = isSignInEnabled,
                            modifier = Modifier.alpha(if (isSignInEnabled) 1f else 0.5f)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.signup_button),
                                contentDescription = "Sign Up",
                                modifier = Modifier
                                    .height(screenHeight * 0.055f)
                                    .fillMaxWidth(0.85f),
                                contentScale = ContentScale.FillWidth
                            )
                        }




                        Button(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = {
                                navController.navigate(DeliverXScreens.LoginScreen.name) {
                                    popUpTo(DeliverXScreens.SignUpScreen.name) { inclusive = true }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(Color.Transparent)
                        ) {
                            Row {
                                Text(
                                    "Already have an Account?",
                                    color = Color(0XFFA4A4A4),
                                    fontSize = (screenWidth * 0.04f).value.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = FontFamily.Default
                                )
                                Text(
                                    text = " Sign In",
                                    color = Color.LightGray,
                                    fontSize = (screenWidth * 0.04f).value.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = FontFamily.Default
                                )
                            }
                        }

                        Image(
                            painter = painterResource(id = R.drawable.or_continue_with),
                            contentDescription = "Image Button",
                            modifier = Modifier
                                .padding(top = 1.dp)
                                .height(screenHeight * 0.02f)
                                .fillMaxWidth(0.85f)
                                .align(Alignment.CenterHorizontally),
                            contentScale = ContentScale.FillWidth
                        )

                        Row(
                            modifier = Modifier
                                .padding(top = screenHeight * 0.01f)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.google_login),
                                contentDescription = "Google Login",
                                modifier = Modifier
                                    .height(screenHeight * 0.05f)
                                    .width(screenWidth * 0.16f)
                                    .clickable {
                                        Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT)
                                            .show()
                                    },
                                contentScale = ContentScale.Fit
                            )

                            Image(
                                painter = painterResource(id = R.drawable.apple_login),
                                contentDescription = "Apple Login",
                                modifier = Modifier
                                    .padding(start = screenWidth * 0.09f)
                                    .height(screenHeight * 0.05f)
                                    .width(screenWidth * 0.16f)
                                    .clickable {
                                        Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT)
                                            .show()
                                    },
                                contentScale = ContentScale.Fit
                            )

                            Image(
                                painter = painterResource(id = R.drawable.fb_login),
                                contentDescription = "Facebook Login",
                                modifier = Modifier
                                    .padding(start = screenWidth * 0.09f)
                                    .height(screenHeight * 0.05f)
                                    .width(screenWidth * 0.16f)
                                    .clickable {
                                        Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT)
                                            .show()
                                    },
                                contentScale = ContentScale.Fit
                            )
                        }

                        Spacer(modifier = Modifier.height(screenHeight * 0.05f))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SignUpScreenPreview() {
    val navController = rememberNavController()
    SignUpScreen(navController = navController)
}