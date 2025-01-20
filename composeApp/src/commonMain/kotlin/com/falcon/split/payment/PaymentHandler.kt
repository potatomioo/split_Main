package com.falcon.split.payment

import com.falcon.split.payment.model.UpiPayment

interface PaymentHandler {
    suspend fun initiatePayment(payment: UpiPayment): Boolean
    fun handlePaymentResponse(resultCode: Int, data: Any?): PaymentState
}