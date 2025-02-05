package com.example.deliverx.screens.Login_SignUp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.deliverx.R

@Composable
fun SignUpScreen(navController: NavController) {
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
                    .offset(x = 215.dp,y = 700.dp),

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