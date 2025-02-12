package com.falcon.split.data.Repository

import com.falcon.split.data.network.models_app.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    suspend fun addExpense(groupId: String, expense: Expense): Result<Unit>
    suspend fun getExpensesByGroup(groupId: String): Flow<List<Expense>>
    suspend fun settleExpense(expenseId: String, userId: String): Result<Unit>
    suspend fun getExpensesByUser(userId: String): Flow<List<Expense>>
}