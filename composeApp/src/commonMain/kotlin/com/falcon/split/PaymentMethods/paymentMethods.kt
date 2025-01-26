package com.falcon.split.PaymentMethods

import androidx.compose.ui.input.key.Key.Companion.R
import org.jetbrains.compose.resources.DrawableResource
import split.composeapp.generated.resources.Res

data class paymentMethod (

    val name : String,
//    val img : DrawableResource ,
    val onClick : () -> Unit

)

val listOfPaymentMethods = listOf(
    paymentMethod("Paytm",{}),
    paymentMethod("PhonePe",{}),
    paymentMethod("GooglePe",{})
)