package com.falcon.split

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.RequestCanceledException
import kotlinx.coroutines.launch

class PermissionsViewModel(
    private val controller: PermissionsController
): ViewModel() {

    var notificationPermissionState by mutableStateOf(PermissionState.NotDetermined)
        private set

    init {
        viewModelScope.launch {
            notificationPermissionState = controller.getPermissionState(Permission.REMOTE_NOTIFICATION)
        }
    }

    fun provideOrRequestNotificationPermission() {
        viewModelScope.launch {
            try {
                controller.providePermission(Permission.REMOTE_NOTIFICATION)
                notificationPermissionState = PermissionState.Granted
            } catch (e: DeniedAlwaysException) {
                notificationPermissionState = PermissionState.DeniedAlways
            } catch (e: DeniedException) {
                notificationPermissionState = PermissionState.Denied
            } catch (e: RequestCanceledException) {
                notificationPermissionState = PermissionState.NotGranted
                e.printStackTrace()
            }
        }
    }

}