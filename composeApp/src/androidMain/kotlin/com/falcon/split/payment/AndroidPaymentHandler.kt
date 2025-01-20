package com.falcon.split.payment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.falcon.split.payment.model.UpiPayment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AndroidPaymentHandler(
    private val context: Context,
    private val activity: Activity
) : PaymentHandler {

    override suspend fun initiatePayment(payment: UpiPayment): Boolean = withContext(Dispatchers.IO) {
        println("Starting payment initiation")
        try {
            println("Building URI with payment details:")
            println("UPI ID: ${payment.upiId}")
            println("Amount: ${payment.amount}")
            println("Note: ${payment.note}")
            println("Name: ${payment.name}")

            val paymentUri = Uri.Builder()
                .scheme("upi")
                .authority("pay")
                .appendQueryParameter("pa", payment.upiId)
                .appendQueryParameter("pn", payment.name.takeIf { it.isNotEmpty() } ?: "Recipient")
                .appendQueryParameter("tn", payment.note.takeIf { it.isNotEmpty() } ?: "Payment")
                .appendQueryParameter("am", payment.amount.toString())
                .appendQueryParameter("cu", "INR")
                .appendQueryParameter("tr", "TXN${System.currentTimeMillis()}")
                .build()

            println("Created URI: $paymentUri")

            val upiPayIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = paymentUri
                // Add these flags
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            println("Created Intent with action: ${upiPayIntent.action}")

            val packageManager = context.packageManager
            println("Getting package manager")

            // List all installed packages that can handle UPI
            packageManager.getInstalledPackages(0).forEach { pkg ->
                println("Package: ${pkg.packageName}")
            }

            // Query for UPI apps explicitly
            val upiApps = packageManager.queryIntentActivities(upiPayIntent, PackageManager.MATCH_DEFAULT_ONLY)
            println("Found ${upiApps.size} UPI apps")
            upiApps.forEach { resolveInfo ->
                println("UPI App found: ${resolveInfo.activityInfo.packageName}")
            }

            // Try querying with specific UPI apps
            val knownUpiPackages = listOf(
                "com.google.android.apps.nbu.paisa.user",  // Google Pay
                "net.one97.paytm",                         // Paytm
                "com.phonepe.app",                         // PhonePe
                "in.org.npci.upiapp",                     // BHIM
                "com.upi.axispay"                         // Axis Pay
            )

            println("Checking for known UPI packages:")
            knownUpiPackages.forEach { packageName ->
                try {
                    val packageInfo = packageManager.getPackageInfo(packageName, 0)
                    println("Found package: $packageName")
                } catch (e: Exception) {
                    println("Package not found: $packageName")
                }
            }

            if (upiApps.isNotEmpty()) {
                println("Creating chooser intent")
                val chooser = Intent.createChooser(upiPayIntent, "Pay using")
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                println("Starting activity for result")
                activity.startActivityForResult(chooser, UPI_PAYMENT_REQUEST)
                true
            } else {
                println("No UPI apps found that can handle the intent")
                // Try direct intent without chooser as fallback
                try {
                    println("Trying direct intent launch")
                    context.startActivity(upiPayIntent)
                    true
                } catch (e: Exception) {
                    println("Direct intent failed: ${e.message}")
                    false
                }
            }
        } catch (e: Exception) {
            println("Exception occurred during payment initiation:")
            println("Message: ${e.message}")
            println("Stack trace: ${e.stackTrace.joinToString("\n")}")
            println("Cause: ${e.cause?.message}")
            false
        }
    }

    override fun handlePaymentResponse(resultCode: Int, data: Any?): PaymentState {
        println("Handling payment response")
        println("Result code: $resultCode")
        println("Data: $data")

        return when (resultCode) {
            Activity.RESULT_OK -> {
                val intent = data as? Intent
                if (intent != null) {
                    val status = intent.getStringExtra("Status") ?: "Status not found"
                    val response = intent.getStringExtra("response") ?: "Response not found"
                    println("Payment successful: $status - $response")
                    PaymentState.Success("$status: $response")
                } else {
                    println("No data received in response")
                    PaymentState.Error("No data received")
                }
            }
            Activity.RESULT_CANCELED -> {
                println("Payment was cancelled")
                PaymentState.Error("Payment cancelled")
            }
            else -> {
                println("Payment failed with result code: $resultCode")
                PaymentState.Error("Payment failed")
            }
        }
    }

    companion object {
        const val UPI_PAYMENT_REQUEST = 1001
    }
}