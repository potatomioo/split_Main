package com.falcon.split.Presentation.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.falcon.split.contact.ContactInfo
import com.falcon.split.data.Repository.GroupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class CreateGroupState {
    data object Initial : CreateGroupState()
    data object Loading : CreateGroupState()
    data class Success(val groupId: String) : CreateGroupState()
    data class Error(val message: String) : CreateGroupState()
}

class CreateGroupViewModel(
    private val groupRepository: GroupRepository
) : ViewModel() {
    private val _state = MutableStateFlow<CreateGroupState>(CreateGroupState.Initial)
    val state = _state.asStateFlow()

    private val _selectedContacts = MutableStateFlow<List<ContactInfo>>(emptyList())
    val selectedContacts = _selectedContacts.asStateFlow()

    fun addContact(contact: ContactInfo) {
        val currentList = _selectedContacts.value.toMutableList()
        if (!currentList.any { it.phoneNumber == contact.phoneNumber }) {
            currentList.add(contact)
            _selectedContacts.value = currentList
        }
    }

    fun removeContact(contact: ContactInfo) {
        val currentList = _selectedContacts.value.toMutableList()
        currentList.removeAll { it.phoneNumber == contact.phoneNumber }
        _selectedContacts.value = currentList
    }

    fun createGroup(name: String) {
        viewModelScope.launch {
            _state.value = CreateGroupState.Loading

            try {
                val phoneNumbers = _selectedContacts.value.map { it.phoneNumber }
                groupRepository.createGroup(name, phoneNumbers)
                    .onSuccess { group ->
                        _state.value = CreateGroupState.Success(group.id)
                    }
                    .onFailure { error ->
                        _state.value = CreateGroupState.Error(error.message ?: "Failed to create group")
                    }
            } catch (e: Exception) {
                _state.value = CreateGroupState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}