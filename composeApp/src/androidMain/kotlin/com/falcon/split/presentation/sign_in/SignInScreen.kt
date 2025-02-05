package com.falcon.split.presentation.sign_in

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.falcon.split.LottieAnimationSpec
import com.falcon.split.LottieAnimationView
import com.falcon.split.SignInProgressPopup
import com.falcon.split.saveUser
import com.mmk.kmpauth.uihelper.google.GoogleSignInButton
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun SignInScreen(
    state: UserState,
    viewModel: SignInViewModel,
    navControllerCommon: NavHostController,
    onSignInClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = state) {
        if (state is UserState.Error) {
            Toast.makeText(
                context,
                state.error,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimationView(LottieAnimationSpec("login_animation.json"))
        Spacer(
            modifier = Modifier.height(60.dp)
        )
        GoogleSignInButton(
            onClick = {
                onSignInClick()
            }
        )
        Spacer(
            modifier = Modifier.height(35.dp)
        )
    }

    val userState by viewModel.userDetails.collectAsState()
    when (userState) {
        is UserState.Error -> {
            val error = (userState as UserState.Error).error
            println("ERROR_TAG$error")
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                Text(
                    text = "Error loading user: $error",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        UserState.Loading -> {
            SignInProgressPopup()
        }
        is UserState.Success -> {
            val user = (userState as UserState.Success).user // TODO
            scope.launch {
                navControllerCommon.navigate("app_content")
            }
        }
    }

}