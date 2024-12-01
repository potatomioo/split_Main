package com.falcon.split

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.falcon.split.data.network.ApiClient
import com.falcon.split.data.network.createHttpClient

fun MainViewController() = ComposeUIViewController { App(remember {
    ApiClient(createHttpClient(io.ktor.client.engine.okhttp.OkHttp.create()))
}, remember {
    com.falcon.split.createDataStore(context = this)
})
}