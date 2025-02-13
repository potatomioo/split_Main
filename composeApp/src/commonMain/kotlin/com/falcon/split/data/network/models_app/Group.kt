package com.falcon.split.data.network.models_app

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val id: String = "",
    val name: String,
    val createdBy: String,
    val members: List<GroupMember>,
    val createdAt: Long? = null,
    val updatedAt: Long? = null,
    val totalAmount: Double? = 0.0 ,
    )