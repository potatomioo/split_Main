// src/commonMain/kotlin/com/falcon/split/utils/EmailUtils.kt
package com.falcon.split.utils

expect class EmailUtils {
    fun sendEmail(
        to: String,
        subject: String = "",
        body: String = ""
    )
}