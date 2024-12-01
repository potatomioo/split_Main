package com.falcon.split

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.falcon.split.data.network.ApiClient
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    client: ApiClient,
    prefs: DataStore<Preferences>
) {
    MaterialTheme {
        Text("Compose:")
    }
}