package com.falcon.split.payment

import com.falcon.split.payment.model.UpiPayment

class IOSPaymentHandler : PaymentHandler {
    override suspend fun initiatePayment(payment: UpiPayment): Boolean {
        // iOS implementation would use different payment methods
        return false
    }

    override fun handlePaymentResponse(resultCode: Int, data: Any?): PaymentState {
        return PaymentState.Error("UPI payments not supported on iOS")
    }
}