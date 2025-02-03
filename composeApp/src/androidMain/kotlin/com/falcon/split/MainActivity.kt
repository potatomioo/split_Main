package com.falcon.split

import ContactPicker
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.falcon.split.contact.AndroidContactManager
import com.falcon.split.contact.ContactInfo
import com.falcon.split.contact.ContactManager
import com.falcon.split.data.network.ApiClient
import com.falcon.split.data.network.createHttpClient
import com.falcon.split.presentation.profile.ProfileScreen
import com.falcon.split.presentation.sign_in.GoogleAuthUiClient
import com.falcon.split.presentation.sign_in.SignInScreen
import com.falcon.split.presentation.sign_in.SignInViewModel
import com.falcon.split.screens.mainNavigation.OpenUpiApp
import com.falcon.split.screens.mainNavigation.PaymentScreen
import com.google.android.gms.auth.api.identity.Identity
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    private lateinit var contactManager : AndroidContactManager

    @SuppressLint("ServiceCast")
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
                contactManager
            )
//            val navController = rememberNavController()
//            NavHost(navController = navController, startDestination = "sign_in") {
//                composable("sign_in") {
//                    val viewModel = viewModel<SignInViewModel>()
//                    val state by viewModel.state.collectAsStateWithLifecycle()
//
//                    LaunchedEffect(key1 = Unit) {
//                        if(googleAuthUiClient.getSignedInUser() != null) {
//                            navController.navigate("profile")
//                        }
//                    }
//
//                    val launcher = rememberLauncherForActivityResult(
//                        contract = ActivityResultContracts.StartIntentSenderForResult(),
//                        onResult = { result ->
//                            if(result.resultCode == RESULT_OK) {
//                                lifecycleScope.launch {
//                                    val signInResult = googleAuthUiClient.signInWithIntent(
//                                        intent = result.data ?: return@launch
//                                    )
//                                    viewModel.onSignInResult(signInResult)
//                                }
//                            }
//                        }
//                    )
//
//                    LaunchedEffect(key1 = state.isSignInSuccessful) {
//                        if(state.isSignInSuccessful) {
//                            Toast.makeText(
//                                applicationContext,
//                                "Sign in successful",
//                                Toast.LENGTH_LONG
//                            ).show()
//
//                            navController.navigate("profile")
//                            viewModel.resetState()
//                        }
//                    }
//
//                    SignInScreen(
//                        state = state,
//                        onSignInClick = {
//                            lifecycleScope.launch {
//                                val signInIntentSender = googleAuthUiClient.signIn()
//                                launcher.launch(
//                                    IntentSenderRequest.Builder(
//                                        signInIntentSender ?: return@launch
//                                    ).build()
//                                )
//                            }
//                        }
//                    )
//                }
//                composable("profile") {
//                    ProfileScreen(
//                        userData = googleAuthUiClient.getSignedInUser(),
//                        onSignOut = {
//                            lifecycleScope.launch {
//                                googleAuthUiClient.signOut()
//                                Toast.makeText(
//                                    applicationContext,
//                                    "Signed out",
//                                    Toast.LENGTH_LONG
//                                ).show()
//
//                                navController.popBackStack()
//                            }
//                        }
//                    )
//                }
//            }




        }
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
