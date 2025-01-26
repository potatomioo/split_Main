package com.falcon.split.screens.mainNavigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.Image
import com.falcon.split.ErrorRed
import com.falcon.split.PaymentMethods.listOfPaymentMethods
import com.falcon.split.PaymentMethods.paymentMethod
import org.jetbrains.compose.resources.painterResource
import split.composeapp.generated.resources.Res

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    paymentAmount : Int,
    PersonName : String,
    onNavigateBack : () ->  Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pay Dues") },
                navigationIcon = {
                    IconButton(onClick = {
                        onNavigateBack()
                    } ) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ){padding->
        Column(
            modifier = Modifier.padding(padding)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .padding(18.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    PersonName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(top = 0.dp, bottom = 5.dp, start = 0.dp)
                )
                Text(
                    "Amount to pay : $ ${paymentAmount}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(top = 0.dp, bottom = 0.dp)
                )

            }
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                border = BorderStroke(2.dp,Color.Black),
                modifier = Modifier
                    .padding(8.dp)
            ) {

                Text(
                    "Pay Using Apps",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(top = 8.dp, start = 15.dp)
                )
                LazyColumn(
                    modifier = Modifier
                        .padding(top = 5.dp)
                ) {
                    items(listOfPaymentMethods){
                        upiMethod(it)
                    }
                }
            }
        }
    }
}



@Composable
fun upiMethod(
    paymentMethod: paymentMethod
) {
    Card(
        onClick = paymentMethod.onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier
            .padding(4.dp)
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    paymentMethod.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Text(
                    "Use this payment method",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Open",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}