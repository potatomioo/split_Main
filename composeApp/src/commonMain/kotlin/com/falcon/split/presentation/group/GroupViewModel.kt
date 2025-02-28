package com.falcon.split.presentation.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.falcon.split.data.Repository.GroupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GroupViewModel(
    private val groupRepository: GroupRepository,
) : ViewModel() {
    private val _groupState = MutableStateFlow<GroupState>(GroupState.Loading)
    val groupState = _groupState.asStateFlow()

    init {
        loadGroups()
    }

    fun loadGroups() {
        viewModelScope.launch {
            try {
                println("ViewModel: Starting to load groups...")
                _groupState.value = GroupState.Loading

                groupRepository.getCurrentUserGroups()
                    .collect { groups ->
                        println("ViewModel: Collected groups, size: ${groups.size}")
                        groups.forEach { group ->
                            println("ViewModel: Group details - Name: ${group.name}, CreatedBy: ${group.createdBy}")
                        }

                        _groupState.value = if (groups.isEmpty()) {
                            println("ViewModel: No groups found")
                            GroupState.Empty
                        } else {
                            println("ViewModel: Found ${groups.size} groups")
                            GroupState.Success(groups)
                        }
                    }
            } catch (e: Exception) {
                println("ViewModel Error: ${e.message}")
                e.printStackTrace() // Print full stack trace
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

    fun retryLoading() {
        loadGroups()
    }
}