package com.falcon.split.domain.logics.expense

import com.falcon.split.data.network.models_app.ExpenseSplit
import com.falcon.split.data.network.models_app.GroupMember

class ExpenseCalculator {
    fun calculateEqualSplits(
        totalAmount: Double,
        paidByUserId: String,
        groupMembers: List<GroupMember>
    ): List<ExpenseSplit> {
        val numberOfMembers = groupMembers.size
        val amountPerPerson = totalAmount / numberOfMembers

        return groupMembers.map { member ->
            ExpenseSplit(
                userId = member.userId ?: "",
                amount = amountPerPerson,
                settled = member.userId == paidByUserId, // Auto settle for the person who paid
                phoneNumber = member.phoneNumber
            )
        }
    }
}