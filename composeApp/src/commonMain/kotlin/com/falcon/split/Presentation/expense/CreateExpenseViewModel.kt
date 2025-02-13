//package com.falcon.split.Presentation.expense
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.falcon.split.data.Repository.ExpenseRepository
//import com.falcon.split.data.Repository.GroupRepository
//import com.falcon.split.data.network.models_app.Expense
//import com.falcon.split.data.network.models_app.ExpenseSplit
//import com.falcon.split.data.network.models_app.Group
//import dev.gitlive.firebase.auth.FirebaseAuth
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//
//
//class CreateExpenseViewModel(
//    private val groupRepository: GroupRepository,
//    private val expenseRepository: ExpenseRepository
//) : ViewModel() {
//    private val _groups = MutableStateFlow<List<Group>>(emptyList())
//    val groups = _groups.asStateFlow()
//
//    private val _currentGroup = MutableStateFlow<Group?>(null)
//    val currentGroup = _currentGroup.asStateFlow()
//
//    private val _state = MutableStateFlow<CreateExpenseState>(CreateExpenseState.Initial)
//    val state = _state.asStateFlow()
//
//    init {
//        loadUserGroups()
//    }
//
//    private fun loadUserGroups() {
//        viewModelScope.launch {
//            try {
//                // Assuming we have current user ID
//                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
//                groupRepository.getGroupsByUser(userId)
//                    .collect { groupsList ->
//                        _groups.value = groupsList
//                    }
//            } catch (e: Exception) {
//                _state.value = CreateExpenseState.Error(e.message ?: "Failed to load groups")
//            }
//        }
//    }
//
//    fun loadGroupDetails(groupId: String) {
//        viewModelScope.launch {
//            groupRepository.getGroupDetails(groupId)
//                .collect { group ->
//                    _currentGroup.value = group
//                }
//        }
//    }
//
//    fun createExpense(
//        description: String,
//        amount: Double,
//        groupId: String,
//        paidByUserId: String,
//        splits: List<ExpenseSplit>
//    ) {
//        viewModelScope.launch {
//            _state.value = CreateExpenseState.Loading
//            try {
//                val expense = Expense(
//                    groupId = groupId,
//                    description = description,
//                    amount = amount,
//                    paidByUserId = paidByUserId,
//                    splits = splits
//                )
//
//                expenseRepository.addExpense(groupId, expense)
//                    .onSuccess {
//                        _state.value = CreateExpenseState.Success
//                    }
//                    .onFailure { error ->
//                        _state.value = CreateExpenseState.Error(error.message ?: "Failed to create expense")
//                    }
//            } catch (e: Exception) {
//                _state.value = CreateExpenseState.Error(e.message ?: "Failed to create expense")
//            }
//        }
//    }
//}
//
//sealed class CreateExpenseState {
//    data object Initial : CreateExpenseState()
//    data object Loading : CreateExpenseState()
//    data object Success : CreateExpenseState()
//    data class Error(val message: String) : CreateExpenseState()
//}