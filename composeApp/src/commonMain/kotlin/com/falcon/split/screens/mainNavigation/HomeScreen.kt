package com.falcon.split.screens.mainNavigation

import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.DrawableResource
import split.composeapp.generated.resources.Res
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import split.composeapp.generated.resources.HomePic
import split.composeapp.generated.resources.HomePic2
import split.composeapp.generated.resources.check
import split.composeapp.generated.resources.group_icon_outlined


@Composable
fun HomeScreen(
    onNavigate: (rootName: String) -> Unit,
    prefs: DataStore<Preferences>,
    snackBarHostState: SnackbarHostState,
    navControllerBottomNav: NavHostController,
    mainViewModel: MainViewModel,
    navControllerMain: NavHostController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Balance Section
            item {
                Box(){
                    Image(
                        painter = painterResource(Res.drawable.HomePic),
                        contentDescription = "Home illustration",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .graphicsLayer {
                                translationY = -100f
                            }
                            .clip(RectangleShape) // Clip the content to the bounds
                            .padding(0.dp),
                        contentScale = ContentScale.Crop
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 15.dp, start = 16.dp, bottom = 4.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Total Balance",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF64748B),
                            fontSize = 15.sp
                        )
                        Text(
                            text = "₹1000.00",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B),
                            fontSize = 20.sp
                        )

                        // Will Get/Pay Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 0.dp, start = 2.dp, end = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            BalanceItem(
                                amount = "₹475.00",
                                label = "you'll get",
                                color = Color(0xFF22C55E)
                            )
                            BalanceItem(
                                amount = "₹181.67",
                                label = "you'll pay",
                                color = Color(0xFFEF4444)
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Recent Groups",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(15.dp)
                )
            }
            item {
                ExpenseCardList()
            }
        }

        // FAB
        AddExpenseFAB(
            onClick = { navControllerMain.navigate("create_expense") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}

@Composable
private fun BalanceItem(
    amount: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(2.dp)
    ) {
        Text(
            text = amount,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color,
            fontSize = 15.sp
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF64748B),
            fontSize = 10.sp
        )
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
            .wrapContentHeight()
            .padding(2.5.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
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
            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 12.dp),
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = primaryText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isOwed) Color(0xFF22C55E) else Color(0xFFEF4444),
                    modifier = Modifier.padding(vertical = 2.dp)
                )
                Text(
                    text = secondaryText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B)
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
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ExpenseCard(
            title = "E-1302",
            primaryText = "you are owed ₹475.00",
            secondaryText = "Kumar K. owes you ₹475.00",
            imageRes = Res.drawable.group_icon_outlined,
            isOwed = true
        )

        ExpenseCard(
            title = "SIH TRIP KOTA",
            primaryText = "you owe ₹181.67",
            secondaryText = "You owe Ankur C. ₹181.67",
            imageRes = Res.drawable.group_icon_outlined,
            isOwed = false
        )

        ExpenseCard(
            title = "Non-group expenses",
            primaryText = "settled up",
            secondaryText = "",
            imageRes = Res.drawable.group_icon_outlined,
            isOwed = false
        )

        ExpenseCard(
            title = "Non-group expenses",
            primaryText = "settled up",
            secondaryText = "",
            imageRes = Res.drawable.group_icon_outlined,
            isOwed = false
        )
    }
}

@Composable
fun AddExpenseFAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = Color(0xFF8fcb39),
        contentColor = Color.White,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 6.dp,
            pressedElevation = 12.dp
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add expense icon",
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "Add expense",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}