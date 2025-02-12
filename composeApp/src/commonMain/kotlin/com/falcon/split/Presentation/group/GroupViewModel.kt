package com.falcon.split.Presentation.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.falcon.split.data.Repository.GroupRepository
import com.falcon.split.domain.useCase.group.CreateGroupUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GroupViewModel(
    private val groupRepository: GroupRepository
) : ViewModel() {
    private val _groupState = MutableStateFlow<GroupState>(GroupState.Loading)
    val groupState = _groupState.asStateFlow()

    fun loadGroups(userId: String) {
        viewModelScope.launch {
            try {
                groupRepository.getGroupsByUser(userId)
                    .collect { groups ->
                        _groupState.value = GroupState.Success(groups)
                    }
            } catch (e: Exception) {
                _groupState.value = GroupState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun createGroup(name: String, members: List<String>) {
        viewModelScope.launch {
            try {
                groupRepository.createGroup(name, members)
                    .onSuccess { /* Handle success */ }
                    .onFailure { error ->
                        _groupState.value = GroupState.Error(error.message ?: "Failed to create group")
                    }
            } catch (e: Exception) {
                _groupState.value = GroupState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun addMembersToGroup(groupId: String, newMembers: List<String>) {
        viewModelScope.launch {
            try {
                groupRepository.addMembersToGroup(groupId, newMembers)
                    .onFailure { error ->
                        _groupState.value = GroupState.Error(error.message ?: "Failed to add members")
                    }
            } catch (e: Exception) {
                _groupState.value = GroupState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun loadGroupDetails(groupId: String) {
        viewModelScope.launch {
            try {
                groupRepository.getGroupDetails(groupId)
                    .collect { group ->
                        group?.let {
                            _groupState.value = GroupState.GroupDetailSuccess(it)
                        }
                    }
            } catch (e: Exception) {
                _groupState.value = GroupState.Error(e.message ?: "Unknown error")
            }
        }
    }
}