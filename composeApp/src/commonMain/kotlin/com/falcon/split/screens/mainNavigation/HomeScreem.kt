package com.falcon.split.screens.mainNavigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.NavHostController
import com.falcon.split.MainViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import split.composeapp.generated.resources.Res
import split.composeapp.generated.resources.refresh
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreem(
    onNavigate: (rootName: String) -> Unit,
    prefs: DataStore<Preferences>,
    snackBarHostState: SnackbarHostState,
    navControllerBottomNav: NavHostController,
    mainViewModel: MainViewModel
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        BalanceCard(100.0, 100.0, 100.0)
        RecentGroupsCard()
    }
}

@Composable
fun RecentGroupsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(800.dp),
        colors = CardDefaults.cardColors(
            // Using a light blue-gray that works well on white backgrounds
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        ExpenseCardList()
    }
}





@Composable
fun ExpenseCard(
    title: String,
    primaryText: String,
    secondaryText: String,
    imageRes: DrawableResource,
    isOwed: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF2E5FF),
            disabledContainerColor = Color(0xFFF2E5FF)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image
            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 12.dp),
                contentScale = ContentScale.Fit
            )

            // Text Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF1E293B)  // Dark slate for title
                )
                Text(
                    text = primaryText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isOwed) Color(0xFF22C55E) else Color(0xFFEF4444),  // Success green or Error red
                    modifier = Modifier.padding(vertical = 2.dp)
                )
                Text(
                    text = secondaryText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B)  // Slate gray for secondary text
                )
            }
        }
    }
}

@Composable
fun ExpenseCardList() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Owed Money Card
        ExpenseCard(
            title = "E-1302",
            primaryText = "you are owed ₹475.00",
            secondaryText = "Kumar K. owes you ₹475.00",
            imageRes = Res.drawable.refresh,
            isOwed = true
        )

        // Owing Money Card
        ExpenseCard(
            title = "SIH TRIP KOTA",
            primaryText = "you owe ₹181.67",
            secondaryText = "You owe Ankur C. ₹181.67",
            imageRes = Res.drawable.refresh,
            isOwed = false
        )

        // Settled Card
        ExpenseCard(
            title = "Non-group expenses",
            primaryText = "settled up",
            secondaryText = "",
            imageRes = Res.drawable.refresh,
            isOwed = false
        )
    }
}


@Composable
fun BalanceCard(
    totalBalance: Double,
    willGet: Double,
    willPay: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            // Using a light blue-gray that works well on white backgrounds
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .wrapContentHeight()
        ) {
            // Total Balance Section
            Text(
                text = "Total Balance",
                color = Color(0xFF64748B),  // Slate gray for labels
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "₹${String.format("%.2f", totalBalance)}",
                color = Color(0xFF1E293B),  // Dark slate for primary text
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Will Get and Will Pay Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Will Get Column
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "₹${willGet.toInt()}",
                        color = Color(0xFF22C55E),  // Success green
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "will get",
                        color = Color(0xFF64748B),  // Slate gray for labels
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Will Pay Column
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "₹${willPay.toInt()}",
                        color = Color(0xFFEF4444),  // Error red
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "will pay",
                        color = Color(0xFF64748B),  // Slate gray for labels
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun BalanceCardPreview() {
    MaterialTheme {
        BalanceCard(
            totalBalance = 100.00,
            willGet = 100.00,
            willPay = 0.00,
            modifier = Modifier.padding(16.dp)
        )
    }
}