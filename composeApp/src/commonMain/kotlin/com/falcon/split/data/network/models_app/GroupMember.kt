package com.falcon.split.data.network.models_app

import kotlinx.serialization.Serializable

@Serializable
data class GroupMember (
    val userId: String?,
    val phoneNumber: String,
    val name: String?,
    val balance: Double = 0.0
)