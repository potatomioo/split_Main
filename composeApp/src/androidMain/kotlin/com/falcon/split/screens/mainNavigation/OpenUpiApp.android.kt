package com.falcon.split.screens.mainNavigation

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
actual object OpenUpiApp {
    private lateinit var context: Context

    fun init(context: Context) {
        this.context = context
    }
    actual fun openPaytm() {
        val intent = context.packageManager.getLaunchIntentForPackage("net.one97.paytm")
        context.startActivity(intent)
    }
    actual fun openGooglePay() {
        val intent = context.packageManager.getLaunchIntentForPackage("com.google.android.apps.nbu.paisa.user")
        context.startActivity(intent)
    }

    actual fun openPhonePe() {
        val intent = context.packageManager.getLaunchIntentForPackage("com.phonepe.app")
        context.startActivity(intent)
    }
}