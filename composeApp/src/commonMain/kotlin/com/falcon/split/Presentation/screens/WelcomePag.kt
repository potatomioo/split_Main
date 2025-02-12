package com.falcon.split.Presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.falcon.split.LottieAnimationSpec
import com.falcon.split.LottieAnimationView
import kotlinx.coroutines.launch
import split.composeapp.generated.resources.Res
import split.composeapp.generated.resources.nunito_bold_1

@Composable
fun WelcomePage(navController: NavHostController) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.padding(30.dp))
        LottieAnimationView(LottieAnimationSpec("welcome.json"))
        Text(
            text = "WELCOME TO SPLIT!",
            fontSize = 18.sp,
            fontFamily = FontFamily(org.jetbrains.compose.resources.Font(Res.font.nunito_bold_1, weight = FontWeight.Normal)),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier
                .padding(10.dp, 0.dp, 10.dp, 0.dp)
        )
        Spacer(modifier = Modifier.padding(40.dp))
        FabWelcomePage(navController)
    }
}

@Composable
private fun FabWelcomePage(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    FloatingActionButton(
        onClick = {
            scope.launch {
                navController.navigate("signin") // TODO: CHANGE IT
            }
        },
        containerColor = Color.Black,
        contentColor = Color.White,
        modifier = Modifier
            .padding(4.dp)
            .size(56.dp),
        shape = RoundedCornerShape(percent = 30),
    ) {
        Icon(
            imageVector = Icons.Filled.KeyboardArrowRight,
            contentDescription = "Go",
            tint = Color.White,
        )
    }
}