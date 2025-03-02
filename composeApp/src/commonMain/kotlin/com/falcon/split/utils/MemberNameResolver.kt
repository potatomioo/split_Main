package com.falcon.split.utils

import com.falcon.split.contact.Contact
import com.falcon.split.contact.ContactManager
import com.falcon.split.data.network.models_app.GroupMember

class MemberNameResolver(private val contactManager: ContactManager?) {

    fun resolveDisplayName(member: GroupMember): String {
        // 1. Try to find in user's contacts
        if (contactManager != null) {
            val contactFromDevice = contactManager.getContactByNumber(member.phoneNumber)
            if (contactFromDevice != null) {
                return contactFromDevice.contactName
            }
        }

        // 2. Use registered name if available
        if (!member.name.isNullOrBlank()) {
            return member.name
        }

        // 3. Fall back to phone number
        return formatPhoneNumber(member.phoneNumber)
    }
    private fun formatPhoneNumber(phoneNumber: String): String {
        // Simple formatting logic - you can enhance this as needed
        return if (phoneNumber.length == 10) {
            "${phoneNumber.substring(0, 3)}-${phoneNumber.substring(3, 6)}-${phoneNumber.substring(6)}"
        } else {
            phoneNumber
        }
    }
}

fun ContactManager.getContactByNumber(phoneNumber: String): Contact? {
    // This would need to be implemented in the platform-specific ContactManager
    // Here's a stub of what it might look like
    val contacts = getAllContacts() ?: return null
    return contacts.find {
        normalizePhoneNumber(it.contactNumber) == normalizePhoneNumber(phoneNumber)
    }
}

private fun normalizePhoneNumber(phoneNumber: String): String {
    // Strip all non-digit characters
    return phoneNumber.replace(Regex("[^0-9]"), "")
}