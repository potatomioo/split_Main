package com.falcon.split.payment

sealed class PaymentState {
    object Idle : PaymentState()
    object Loading : PaymentState()
    data class Success(val transactionId: String) : PaymentState()
    data class Error(val message: String) : PaymentState()
}