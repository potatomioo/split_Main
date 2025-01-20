package com.falcon.split.payment.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.falcon.split.payment.PaymentState
import com.falcon.split.payment.model.UpiPayment

@Composable
fun UPIPaymentScreen(
    paymentState: PaymentState,
    onInitiatePayment: (UpiPayment) -> Unit
) {
    var upiId by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = upiId,
            onValueChange = { upiId = it },
            label = { Text("UPI ID") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Note (Optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name (Optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val amountValue = amount.toDoubleOrNull() ?: return@Button
                onInitiatePayment(
                    UpiPayment(
                        upiId = upiId,
                        amount = amountValue,
                        note = note,
                        name = name
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = upiId.isNotEmpty() && amount.isNotEmpty() && 
                     paymentState !is PaymentState.Loading
        ) {
            Text("Pay Now")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (paymentState) {
            is PaymentState.Loading -> {
                CircularProgressIndicator()
            }
            is PaymentState.Error -> {
                Text(
                    paymentState.message,
                    color = MaterialTheme.colorScheme.error
                )
            }
            is PaymentState.Success -> {
                Text(
                    "Payment Successful! ID: ${paymentState.transactionId}",
                    color = MaterialTheme.colorScheme.primary
                )
            }
            is PaymentState.Idle -> {
                // Initial state, nothing to show
            }
        }
    }
}