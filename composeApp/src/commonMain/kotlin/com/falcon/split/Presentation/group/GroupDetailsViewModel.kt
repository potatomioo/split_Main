//package com.falcon.split.Presentation.group
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.falcon.split.data.Repository.ExpenseRepository
//import com.falcon.split.data.Repository.GroupRepository
//import com.falcon.split.data.network.models_app.Expense
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//
//class GroupDetailsViewModel(
//    private val groupRepository: GroupRepository,
//    private val expenseRepository: ExpenseRepository
//) : ViewModel() {
//    private val _groupState = MutableStateFlow<GroupState>(GroupState.Loading)
//    val groupState = _groupState.asStateFlow()
//
//    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
//    val expenses = _expenses.asStateFlow()
//
//    fun loadGroupDetails(groupId: String) {
//        viewModelScope.launch {
//            groupRepository.getGroupDetails(groupId)
//                .collect { group ->
//                    _groupState.value = group?.let { GroupState.GroupDetailSuccess(it) }
//                        ?: GroupState.Error("Group not found")
//                }
//
//            expenseRepository.getExpensesByGroup(groupId)
//                .collect { expensesList ->
//                    _expenses.value = expensesList
//                }
//        }
//    }
//}