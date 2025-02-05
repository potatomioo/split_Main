package com.falcon.split

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.falcon.split.contact.AndroidContactManager
import com.falcon.split.data.network.ApiClient
import com.falcon.split.data.network.createHttpClient
import com.falcon.split.presentation.profile.ProfileScreen
import com.falcon.split.presentation.sign_in.GoogleAuthUiClient
import com.falcon.split.presentation.sign_in.SignInViewModel
import com.falcon.split.presentation.sign_in.UserState
import com.falcon.split.screens.mainNavigation.OpenUpiApp
import com.google.android.gms.auth.api.identity.Identity
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.falcon.split.LottieAnimationSpec
import com.falcon.split.LottieAnimationView
import com.falcon.split.SignInProgressPopup
import com.falcon.split.saveUser
import com.mmk.kmpauth.uihelper.google.GoogleSignInButton
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    private lateinit var contactManager : AndroidContactManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactManager = AndroidContactManager(this)
        ClipboardManager.init(applicationContext)
        OpenUpiApp.init(applicationContext)
        installSplashScreen().apply {
            // Perform Some Code During Splash Screen
        }
        setContent {
            App(
                client = remember {
                    ApiClient(createHttpClient(OkHttp.create()))
                },
                prefs = remember {
                    createDataStore(context = this)
                },
                contactManager = contactManager,
                AndroidSignInComposable = { navController ->
                    CallGoogleSignInAndroid(navController)
                },
                AndroidProfileScreenComposable = { navController ->
                    CallProfileScreenInAndroid(navController)
                }
            )
        }
    }


    @Composable
    fun CallGoogleSignInAndroid(navControllerCommon: NavHostController) {
        val viewModel = viewModel<SignInViewModel>()
        val state by viewModel.userDetails.collectAsStateWithLifecycle()

        LaunchedEffect(key1 = Unit) {
            if (googleAuthUiClient.getSignedInUser() != null) {
                navControllerCommon.navigate("app_content")
            }
        }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult(),
            onResult = { result ->
                if (result.resultCode == RESULT_OK) {
                    lifecycleScope.launch {
                        val signInResult = googleAuthUiClient.signInWithIntent(
                            intent = result.data ?: return@launch
                        )
                        viewModel.onSignInResult(signInResult)
                    }
                }
            }
        )

        LaunchedEffect(state) {
            if (state is UserState.Success) {
                Toast.makeText(
                    applicationContext,
                    "Sign in successful",
                    Toast.LENGTH_LONG
                ).show()

                navControllerCommon.navigate("app_content")
                viewModel.resetState()
            }
        }

        SignInScreen(
            state = state,
            viewModel = viewModel,
            navControllerCommon = navControllerCommon,
            onSignInClick = {
                viewModel.makeStateLoading()
                lifecycleScope.launch {
                    val signInIntentSender = googleAuthUiClient.signIn()
                    launcher.launch(
                        IntentSenderRequest.Builder(
                            signInIntentSender ?: return@launch
                        ).build()
                    )
                }
            }
        )
    }

    @Composable
    fun CallProfileScreenInAndroid(navControllerCommon: NavHostController) {
        ProfileScreen(
            userData = googleAuthUiClient.getSignedInUser(),
            onSignOut = {
                lifecycleScope.launch {
                    googleAuthUiClient.signOut()
                    Toast.makeText(
                        applicationContext,
                        "Signed out",
                        Toast.LENGTH_LONG
                    ).show()
                    navControllerCommon.navigate("welcome_page")
                }
            }
        )
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)} passing\n      in a {@link RequestMultiplePermissions} object for the {@link ActivityResultContract} and\n      handling the result in the {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array< String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        contactManager.handlePermissionResult(requestCode, grantResults)
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        contactManager.handleActivityResult(requestCode, resultCode, data)
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.let {
//             TODO: Handle new intent (if app is already running)
            val deepLinkNewsId = handleDeepLink(it)
            // Update your newsId state
        }
    }

    // Handle the deep link intent and extract the newsId
    private fun handleDeepLink(intent: Intent?): String {
        intent?.data?.let { uri ->
            if (uri.pathSegments.isNotEmpty() && uri.pathSegments[0] == "news") {
                return uri.lastPathSegment ?: ""
            }
        }
        return ""
    }
}


@Composable
fun SignInScreen(
    state: UserState,
    viewModel: SignInViewModel,
    navControllerCommon: NavHostController,
    onSignInClick: () -> Unit
) {
    val context = LocalContext.current
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
            navControllerCommon.navigate("app_content")
        }
    }
}

//Contact Handling

//class MainActivity : ComponentActivity() {
//    private lateinit var contactManager: AndroidContactManager
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        contactManager = AndroidContactManager(this)
//
//        setContent {
//            // Your app content
//            YourScreen(contactManager)
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array< String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        contactManager.handlePermissionResult(requestCode, grantResults)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        contactManager.handleActivityResult(requestCode, resultCode, data)
//    }
//}
//
//@Composable
//fun YourScreen(contactManager: ContactManager) {
//    var showContactPicker by remember { mutableStateOf(false) }
//    var selectedContact by remember { mutableStateOf<ContactInfo?>(null) }
//
//    Column {
//        Button(onClick = { showContactPicker = true }) {
//            Text("Select Contact")
//        }
//
//        selectedContact?.let { contact ->
//            Text("Selected: ${contact.name}")
//            Text("Number: ${contact.phoneNumber}")
//        }
//
//        if (showContactPicker) {
//            ContactPicker(
//                contactManager = contactManager
//            ) { contact ->
//                selectedContact = contact
//                showContactPicker = false
//            }
//        }
//    }
//}
