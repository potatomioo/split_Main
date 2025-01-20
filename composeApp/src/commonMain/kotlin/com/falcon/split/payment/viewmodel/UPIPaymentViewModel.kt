package com.falcon.split.payment.viewmodel

import com.falcon.split.payment.PaymentHandler
import com.falcon.split.payment.PaymentState
import com.falcon.split.payment.model.UpiPayment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UPIPaymentViewModel(
    private val paymentHandler: PaymentHandler,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState: StateFlow<PaymentState> = _paymentState

    fun initiatePayment(payment: UpiPayment) {
        scope.launch {
            _paymentState.value = PaymentState.Loading
            val success = paymentHandler.initiatePayment(payment)
            if (!success) {
                _paymentState.value = PaymentState.Error("Failed to initiate payment")
            }
        }
    }

    fun handlePaymentResult(resultCode: Int, data: Any?) {
        _paymentState.value = paymentHandler.handlePaymentResponse(resultCode, data)
    }
}