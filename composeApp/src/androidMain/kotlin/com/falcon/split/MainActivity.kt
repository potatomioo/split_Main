package com.falcon.split

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.falcon.split.data.network.ApiClient
import com.falcon.split.data.network.createHttpClient
import io.ktor.client.engine.okhttp.OkHttp



class MainActivity : ComponentActivity() {
    companion object {
        const val NEWS_PATH_SEGMENT = "Split"
    }

    @SuppressLint("ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            )
        }
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