package com.falcon.split.presentation.sign_in

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignInViewModel: ViewModel() {

    private val _userDetails = MutableStateFlow<UserState>(UserState.Loading)
    val userDetails: StateFlow<UserState> = _userDetails

    fun onSignInResult(result: SignInResult) {
        viewModelScope.launch {
            _userDetails.value = UserState.Loading
            delay(2700) // TODO: Remove If Required || delay to fully animate lottie animation
            if (result.data != null) {
                _userDetails.value = UserState.Success(
                    UserData(
                        result.data.userId.toString(),
                        result.data.username,
                        result.data.profilePictureUrl
                    )
                )
            } else {
                _userDetails.value = UserState.Error(
                    result.errorMessage.toString()
                )
            }
        }
    }

    fun resetState() {
        _userDetails.value = UserState.Success(UserData())
    }
}