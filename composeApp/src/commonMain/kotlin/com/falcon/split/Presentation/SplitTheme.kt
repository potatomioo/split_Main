package com.falcon.split.Presentation

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun getAppTypography(): Typography {
    return Typography(
        bodyLarge = TextStyle(
            fontFamily = AppFontFamily.nunitoFamily(),
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = AppFontFamily.nunitoFamily(),
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        titleLarge = TextStyle(
            fontFamily = AppFontFamily.nunitoFamily(),
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.1.sp
        ),
        titleSmall = TextStyle(
            fontFamily = AppFontFamily.nunitoFamily(),
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp,
            lineHeight = 25.sp,
            letterSpacing = 0.1.sp
        ),
        titleMedium = TextStyle(
            fontFamily = AppFontFamily.nunitoFamily(),
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.1.sp
        )
    )
}

