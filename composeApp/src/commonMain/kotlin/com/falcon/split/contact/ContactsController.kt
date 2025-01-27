package com.falcon.split.contact

interface ContactManager {
    fun hasPermission(): Boolean
    fun requestPermission(onResult: (Boolean) -> Unit)
    fun pickContact(onContactPicked: (ContactInfo?) -> Unit)
}

data class ContactInfo(
    val name: String,
    val phoneNumber: String
)