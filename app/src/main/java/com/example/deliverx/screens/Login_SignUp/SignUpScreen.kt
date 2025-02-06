package com.example.deliverx.screens.Login_SignUp

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Person4
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.deliverx.R
import com.example.deliverx.components.GradientTextField
import com.example.deliverx.navigation.DeliverXScreens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun SignUpScreen(navController: NavController) {
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
    val isSignInEnabled = email.value.isNotBlank() && password.value.isNotBlank() && FName.value.isNotBlank() && LName.value.isNotBlank() && MobNo.value.isNotBlank()
    val context = LocalContext.current
    var isLoading by rememberSaveable { mutableStateOf(false) }
    fun saveLoginState(context: Context, isLoggedIn: Boolean) {
        val preferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        preferences.edit().putBoolean("isLoggedIn", isLoggedIn).apply()
    }


    fun signUp() {
        isLoading = true
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.value, password.value)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid ?: ""
                    val user = hashMapOf(
                        "firstName" to FName.value.trim(),
                        "lastName" to LName.value.trim(),
                        "email" to email.value.trim(),
                        "mobile" to MobNo.value.trim()
                    )
                    FirebaseFirestore.getInstance().collection("users").document(userId)
                        .set(user)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                            navController.navigate(DeliverXScreens.HomeScreen.name)
                            saveLoginState(context, true)
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Error saving user data", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Log.d("Failure", "${task.exception}")
                    Toast.makeText(context, "Sign Up Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
                isLoading = false
            }
    }


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(R.drawable.signup_animated),
                contentDescription = "Background Image",
                modifier = Modifier
                    .offset(x = 53.dp, y = 8.dp)
                    .width(340.dp)
                    .height(240.dp)
                    .zIndex(0f),
                alignment = Alignment.TopEnd
            )
            Image(
                painter = painterResource(id = R.drawable.signup_shape1),
                contentDescription = "Foreground",
                modifier = Modifier
                    .width(184.dp)
                    .height(145.dp)
                    .zIndex(1f)
                    .rotate(33.9F)
                    .graphicsLayer(rotationZ = -33.9f)
                    .offset(x = (-25).dp)
            )
            Image(
                painter = painterResource(id = R.drawable.login_ani_top),
                contentDescription = "Foreground",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .offset(y = (-49).dp)
                    .zIndex(1f)
            )
            Image(
                painter = painterResource(id = R.drawable.su_gradient),
                contentDescription = null,
                modifier = Modifier
                    .width(150.dp)
                    .height(159.dp)
                    .zIndex(2f)
                    .rotate(18.69F)
                    .graphicsLayer(rotationZ = -18.69f)
                    .offset(x = 243.dp, y = 215.dp),
                alignment = AbsoluteAlignment.BottomRight
            )
            Image(
                painter = painterResource(id = R.drawable.login_bg),
                contentDescription = null,
                modifier = Modifier
                    .width(954.dp)
                    .height(1047.dp)
                    .zIndex(3f)
                    .offset(x = (-1).dp, y = 75.dp)
                    .graphicsLayer(scaleX = 1.01f, scaleY = 1.01f)
            )
            Image(
                painter = painterResource(id = R.drawable.element_01),
                contentDescription = null,
                modifier = Modifier
                    .width(80.dp)
                    .height(109.dp)
                    .zIndex(4f)
                    .rotate(18.69F)
                    .graphicsLayer(rotationZ = -18.69f)
                    .offset(x = 312.dp, y = 265.dp),
                alignment = AbsoluteAlignment.BottomRight
            )
            Image(
                painter = painterResource(id = R.drawable.su_g),
                contentDescription = "Foreground",
                modifier = Modifier
                    .width(195.dp)
                    .height(155.dp)
                    .zIndex(4f)
                    .offset(x = (-45).dp, y = 205.dp)
                    .rotate(66.69F)
                    .graphicsLayer(rotationZ = -66.69f)
            )
            Image(
                painter = painterResource(id = R.drawable.book),
                contentDescription = null,
                modifier = Modifier
                    .height(160.dp)
                    .width(185.dp)
                    .rotate(33.9F)
                    .graphicsLayer(rotationZ = -33.9f)
                    .zIndex(4f)
                    .offset(x = 215.dp, y = 700.dp),

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
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Get On Board!",
                        color = Color(0XFFFFFAEC),
                        fontSize = 39.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Serif,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "Join us Now !!",
                        color = Color(0XFFA4A4A4),
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(15.dp)
                            .zIndex(7f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Row {
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
                                onNext = {
                                    LNameFocusRequester.requestFocus()
                                },
                                value = FName.value,
                                onValueChange = { FName.value = it },
                                isPassword = false,
                                modifier = Modifier
                                    .height(52.dp)
                                    .width(190.dp)
                                    .padding(start = 25.dp, end = 10.dp)
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
                                onNext = {
                                    emailFocusRequester.requestFocus()
                                },
                                value = LName.value,
                                onValueChange = { LName.value = it },
                                isPassword = false, modifier = Modifier
                                    .height(52.dp)
                                    .width(190.dp)
                                    .padding(start = 2.dp, end = 25.dp)
                            )
                        }

                        Spacer(modifier = Modifier.padding(10.dp))

                        GradientTextField(
                            modifier = Modifier,
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

                        Spacer(modifier = Modifier.padding(10.dp))

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
                            onNext = {
                                MobNoFocusRequester.requestFocus()
                            },
                            value = password.value,
                            onValueChange = { password.value = it }
                        )

                        Spacer(modifier = Modifier.padding(10.dp))

                        GradientTextField(
                            modifier = Modifier,
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

                        Spacer(modifier = Modifier.padding(10.dp))

                        Image(
                            painter = painterResource(id = R.drawable.signup_button),
                            contentDescription = "Image Button",
                            modifier = Modifier
                                .height(50.dp)
                                .width(315.dp)
                                .align(Alignment.CenterHorizontally)
                                .clickable(enabled = isSignInEnabled) {
                                    if (!isSignInEnabled) {
                                        Toast.makeText(
                                            context,
                                            "Fill up all the details!",
                                            Toast.LENGTH_LONG
                                        )
                                            .show()
                                    }
                                    if (isSignInEnabled && password.value.length < 8) {
                                        Toast.makeText(
                                            context,
                                            "Password must of at least 8 characters!",
                                            Toast.LENGTH_LONG
                                        )
                                            .show()
                                    } else if (isSignInEnabled) {
                                        signUp()
                                    }
                                }
                                .alpha(if (isSignInEnabled) 1f else 0.5f)
                        )
                        Button(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = { navController.navigate(DeliverXScreens.LoginScreen.name) },
                            colors = ButtonDefaults.buttonColors(Color.Transparent),

                            ) {
                            Row() {
                                Text("Already have an Account?",
                                    color = Color(0XFFA4A4A4),
                                    fontSize = (14.33).sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = FontFamily.Default)
                                Text(text = " Sign In",
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
                                .padding(top = 1.dp)
                                .height(20.dp)
                                .width(300.dp)
                                .align(Alignment.CenterHorizontally)
                        )

                        Row(modifier = Modifier.padding(top = 10.dp)) {
                            Image(
                                painter = painterResource(id = R.drawable.google_login),
                                contentDescription = "Image Button",
                                modifier = Modifier
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

