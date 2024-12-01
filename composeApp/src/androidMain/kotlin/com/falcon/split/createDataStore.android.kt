package com.falcon.split

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.falcon.split.utils.DATA_STORE_FILE_NAME

fun createDataStore(context: Context): DataStore<Preferences> {
    return com.falcon.split.utils.createDataStore {
        context.filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath
    }
}