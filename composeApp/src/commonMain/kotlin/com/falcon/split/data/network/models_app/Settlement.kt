package com.falcon.split.data.network.models_app

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Settlement(
    val settlementId: String = "",
    val paidBy: String = "",  // userId of person paying their debt
    val paidTo: String = "",  // userId of person receiving payment
    val amount: Double = 0.0,
    val groupId: String = "", // Which group this settlement is for
    val createdAt: Instant = Clock.System.now(),
)