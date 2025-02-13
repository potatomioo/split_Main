//package com.falcon.split.presentation.expense
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.falcon.split.Presentation.expense.CreateExpenseViewModel
//import com.falcon.split.data.Repository.ExpenseRepository
//import com.falcon.split.data.Repository.GroupRepository
//import com.google.firebase.auth.FirebaseAuth
//
//
//class CreateExpenseViewModelFactory(
//    private val groupRepository: GroupRepository,
//    private val expenseRepository: ExpenseRepository
//) : ViewModelProvider.Factory {
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(CreateExpenseViewModel::class.java)) {
//            return CreateExpenseViewModel(groupRepository, expenseRepository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}