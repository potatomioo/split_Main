package com.falcon.split.data.repository

import com.falcon.split.data.Repository.ExpenseRepository
import com.falcon.split.data.network.models_app.Expense
import com.falcon.split.data.network.models_app.Group
import com.falcon.split.domain.logics.expense.ExpenseCalculator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseExpenseRepository : ExpenseRepository {
    private val db = FirebaseFirestore.getInstance()
    private val expenseCalculator = ExpenseCalculator()

    override suspend fun addExpense(
        groupId: String,
        description: String,
        amount: Double,
    ): Result<Unit> {
        val currentUser = FirebaseAuth.getInstance().currentUser
            ?: return Result.failure(Exception("User not logged in"))

        val paidByUserId: String = currentUser.uid
        val paidByUserName: String = currentUser.displayName ?: "Unknown"

        return try {
            db.runTransaction { transaction ->
                // 1. Get the group to access its members
                val groupRef = db.collection("groups").document(groupId)
                val group = transaction.get(groupRef).toObject(Group::class.java)
                    ?: throw Exception("Group not found")

                // 2. Calculate splits
                val splits = expenseCalculator.calculateEqualSplits(
                    totalAmount = amount,
                    paidByUserId = paidByUserId,
                    groupMembers = group.members
                )

                // 3. Create the expense document
                val expenseRef = db.collection("expenses").document()
                val expense = Expense(
                    expenseId = expenseRef.id,
                    groupId = groupId,
                    description = description,
                    amount = amount,
                    paidByUserId = paidByUserId,
                    paidByUserName = paidByUserName,
                    splits = splits
                )

                // 4. Update member balances in the group
                val equalSplitAmount = amount / group.members.size
                val updatedMembers = group.members.map { member ->
                    when (member.userId) {
                        // For the person who paid
                        paidByUserId -> {
                            val currentBalance = member.balance ?: 0.0
                            // They paid full amount but owe their share
                            // amount = what they paid
                            // -equalSplitAmount = what they owe
                            member.copy(
                                balance = currentBalance + (amount - equalSplitAmount)
                            )
                        }
                        // For everyone else
                        else -> {
                            val currentBalance = member.balance ?: 0.0
                            // They owe their share
                            member.copy(
                                balance = currentBalance - equalSplitAmount
                            )
                        }
                    }
                }

                // 5. Perform the transaction
                transaction.set(expenseRef, expense)
                transaction.update(groupRef, "members", updatedMembers)

                // 6. Update group's total amount
                val currentTotal = group.totalAmount ?: 0.0
                transaction.update(groupRef, "totalAmount", currentTotal + amount)
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