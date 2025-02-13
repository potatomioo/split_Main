package com.falcon.split.contact

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import android.Manifest
import android.annotation.SuppressLint

class AndroidContactManager(
    private val activity: Activity
) : ContactManager {

    override fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun requestPermission(onResult: (Boolean) -> Unit) {
        permissionCallback = onResult
        activity.requestPermissions(
            arrayOf(Manifest.permission.READ_CONTACTS),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun pickContact(onContactPicked: (Contact?) -> Unit) {
        if (!hasPermission()) {
            onContactPicked(null)
            return
        }

        contactPickedCallback = onContactPicked
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        activity.startActivityForResult(intent, CONTACT_PICK_CODE)
    }

    fun handlePermissionResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val granted = grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
            permissionCallback?.invoke(granted)
            permissionCallback = null
        }
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CONTACT_PICK_CODE && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                val contactInfo = getContactDetails(uri)
                contactPickedCallback?.invoke(contactInfo)
            } else {
                contactPickedCallback?.invoke(null)
            }
            contactPickedCallback = null
        }
    }

    @SuppressLint("Range")
    private fun getContactDetails(uri: android.net.Uri): Contact? {
        var contactName = ""
        var contactNumber = ""

        activity.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                if (nameIndex != -1) {
                    contactName = cursor.getString(nameIndex)
                }

                val idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID)
                val contactId = cursor.getString(idIndex)

                val hasPhoneNumber = cursor.getInt(
                    cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                ) > 0

                if (hasPhoneNumber) {
                    activity.contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                        arrayOf(contactId),
                        null
                    )?.use { phoneCursor ->
                        if (phoneCursor.moveToFirst()) {
                            contactNumber = phoneCursor.getString(
                                phoneCursor.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER
                                )
                            )
                        }
                    }
                }
            }
        }

        return if (contactName.isNotEmpty() && contactNumber.isNotEmpty()) {
            Contact(contactName, getFormattedPhoneNumber(contactNumber))
        } else null
    }

    private fun getFormattedPhoneNumber(number: String): String {
        val digitsOnly = number.filter { it.isDigit() }
        return when {
            digitsOnly.length == 12 -> digitsOnly.substring(2)
            else -> digitsOnly
        }
    }

    companion object {
        const val PERMISSION_REQUEST_CODE = 123
        const val CONTACT_PICK_CODE = 124
        private var permissionCallback: ((Boolean) -> Unit)? = null
        private var contactPickedCallback: ((Contact?) -> Unit)? = null
    }
}