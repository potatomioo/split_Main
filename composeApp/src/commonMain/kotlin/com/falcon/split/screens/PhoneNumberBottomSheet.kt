package com.falcon.split.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.falcon.split.ThemePurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneNumberBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onPhoneNumberSubmit: (String) -> Unit
) {
    if (isVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() },
            contentAlignment = Alignment.BottomCenter
        ) {
            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color.White)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* Prevent click through */ }
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Handle bar
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFFE5E7EB))
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Enter Phone Number",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(16.dp))

                var phoneNumber by remember { mutableStateOf("") }

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {
                        if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                            phoneNumber = it
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    label = { Text("Phone Number") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (phoneNumber.length == 10) {
                            onPhoneNumberSubmit(phoneNumber)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = phoneNumber.length == 10,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8fcb39),
                        disabledContainerColor = Color(0xFF5DC095).copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Done",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}