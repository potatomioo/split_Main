package com.falcon.split.payment.model

data class UpiPayment(
    val upiId: String,
    val amount: Double,
    val note: String = "",
    val name: String = ""
)
