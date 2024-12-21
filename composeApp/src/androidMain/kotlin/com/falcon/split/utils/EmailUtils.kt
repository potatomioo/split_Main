// src/androidMain/kotlin/com/falcon/split/utils/EmailUtils.kt
package com.falcon.split.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

actual class EmailUtils(private val context: Context) {
    actual fun sendEmail(to: String, subject: String, body: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
            }
            context.startActivity(Intent.createChooser(intent, "Send email"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}