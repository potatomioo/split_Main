package com.falcon.split.data.network.models_app

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val groupId: String = "",
    val name: String = "",
    val members: List<String> = listOf(),  // List of userIds who are in this group
    val createdBy: String = "",  // userId of who made the group
    val createdAt: Instant = Clock.System.now()
)