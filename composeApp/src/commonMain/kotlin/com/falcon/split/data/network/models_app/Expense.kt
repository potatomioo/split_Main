package com.falcon.split.data.network.models_app

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Expense(
    val expenseId: String = "",
    val groupId: String = "",  // Which group this expense belongs to
    val description: String = "",
    val amount: Double = 0.0,
    val paidBy: String = "",  // userId of person who paid
    val createdAt: Instant = Clock.System.now(),
    val splitBetween: List<ExpenseSplit> = listOf()
)

data class ExpenseSplit(
    val userId: String = "",  // Who needs to pay
    val amount: Double = 0.0  // How much they owe
)