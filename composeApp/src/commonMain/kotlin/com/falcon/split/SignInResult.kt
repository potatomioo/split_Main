package com.falcon.split

import kotlinx.serialization.Serializable

@Serializable
data class SignInResult(
    val data: UserModelGoogleFirebaseBased?,
    val errorMessage: String?
)

@Serializable
data class UserModelGoogleFirebaseBased(
    val userId: String? = null,
    val username: String? = null,
    val profilePictureUrl: String? = null,

    val email: String? = null, // TODO: See how to get email from Google Firebase Sign-In
    val upiId: String? = null,
)