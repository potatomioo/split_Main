package com.falcon.split.data.repository

import com.falcon.split.data.Repository.ExpenseRepository
import com.falcon.split.data.network.models_app.Expense
import com.falcon.split.data.network.models_app.Group
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseExpenseRepository : ExpenseRepository {
    private val db = FirebaseFirestore.getInstance()

    override suspend fun addExpense(groupId: String, expense: Expense): Result<Unit> {
        return try {
            db.runTransaction { transaction ->
                val expenseRef = db.collection("expenses").document()
                val groupRef = db.collection("groups").document(groupId)

                // Add expense
                transaction.set(expenseRef, expense)

                // Update group member balances
                val group = transaction.get(groupRef).toObject(Group::class.java)!!
                val updatedMembers = group.members.map { member ->
                    val split = expense.splits?.find { it.userId == member.userId }
                    if (split != null) {
                        val paidAmount = if (member.userId == expense.paidByUserId) expense.amount else 0.0
                        val owedAmount = split.amount
                        member.copy(balance = member.balance + paidAmount - owedAmount)
                    } else member
                }

                transaction.update(groupRef, "members", updatedMembers)
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getExpensesByGroup(groupId: String): Flow<List<Expense>> = callbackFlow {
        val listener = db.collection("expenses")
            .whereEqualTo("groupId", groupId)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val expenses = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Expense::class.java)?.copy(expenseId = doc.id)
                } ?: emptyList()

                trySend(expenses)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getExpensesByUser(userId: String): Flow<List<Expense>> = callbackFlow {
        val listener = db.collection("expenses")
            .whereArrayContains("splits.userId", userId)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val expenses = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Expense::class.java)?.copy(expenseId = doc.id)
                } ?: emptyList()

                trySend(expenses)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun settleExpense(expenseId: String, userId: String): Result<Unit> {
        return try {
            db.runTransaction { transaction ->
                val expenseRef = db.collection("expenses").document(expenseId)
                val expense = transaction.get(expenseRef).toObject(Expense::class.java)!!

                val updatedSplits = expense.splits?.map { split ->
                    if (split.userId == userId) split.copy(settled = true)
                    else split
                }

                transaction.update(expenseRef, "splits", updatedSplits)
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}