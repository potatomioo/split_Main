@file:OptIn(ExperimentalCoilApi::class)

package com.falcon.split

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.crossfade
import coil3.util.DebugLogger
import com.falcon.split.data.network.ApiClient
import com.falcon.split.screens.WelcomePage
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    client: ApiClient,
    prefs: DataStore<Preferences>
) {
    MaterialTheme {

    }
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val factory = rememberPermissionsControllerFactory()
    val controller = remember(factory) {
        factory.createPermissionsController()
    }
    BindEffect(controller)
    // Request Permission For Notification
    val viewModel = viewModel {
        PermissionsViewModel(controller)
    }
    when(viewModel.notificationPermissionState) {
        PermissionState.Granted -> {
            println("Notification Permission Granted")
        }
        PermissionState.DeniedAlways -> {
            LaunchedEffect(Unit) {
                snackBarHostState.showSnackbar(
                    message = "Notification Permission Denied Permanently",
                    actionLabel = "Settings",
                    duration = SnackbarDuration.Indefinite,  // Make it stay indefinitely
                    withDismissAction = false  // Remove dismiss action
                ).let { result ->
                    when (result) {
                        SnackbarResult.ActionPerformed -> {
                            controller.openAppSettings()
                        }
                        else -> {
                            // Should never reach here since we removed dismiss action
                        }
                    }
                }
            }
        }
        else -> {
            viewModel.provideOrRequestNotificationPermission()
        }
    }
    setSingletonImageLoaderFactory { context ->
        getAsyncImageLoader(context)
    }
    var authReady by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        GoogleAuthProvider.create(
            credentials = GoogleAuthCredentials(
                serverId = "35186900267-7tn7qmjqo7mnc1bv4jl1kb6sudomedrd.apps.googleusercontent.com"
            )
        )
        authReady = true
    }
    val navControllerMain = rememberNavController()
    val newsViewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(client, prefs)
    )
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        }
    ) {
        val startDestination = runBlocking {
            if (getUserAsUserModel(prefs) != null) "app_content" else "welcome_page"
        }
        NavHost(navController = navControllerMain, startDestination = startDestination) {
            composable("welcome_page") {
                WelcomePage(navControllerMain)
            }
        }
    }

}

fun getAsyncImageLoader(context: PlatformContext) = ImageLoader.Builder(context).crossfade(true).logger(
    DebugLogger()
).build()