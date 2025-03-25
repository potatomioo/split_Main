package com.falcon.split.presentation.screens.mainNavigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.NavHostController
import com.falcon.split.MainViewModel
import com.falcon.split.presentation.theme.LocalSplitColors
import com.falcon.split.presentation.theme.getAppTypography
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import split.composeapp.generated.resources.Res
import split.composeapp.generated.resources.HomePic
import split.composeapp.generated.resources.group_icon_outlined

@Composable
fun HomeScreen(
    onNavigate: (rootName: String) -> Unit,
    prefs: DataStore<Preferences>,
    snackBarHostState: SnackbarHostState,
    navControllerBottomNav: NavHostController,
    mainViewModel: MainViewModel,
    navControllerMain: NavHostController,
    topPadding: Dp
) {
    val colors = LocalSplitColors.current
    val isDarkTheme = isSystemInDarkTheme()

    // Add search state
    var searchQuery by remember { mutableStateOf("") }
    val expenses = remember { getExpensesList() }
    val filteredExpenses = remember(searchQuery, expenses) {
        if (searchQuery.isEmpty()) {
            expenses
        } else {
            expenses.filter {
                it.title.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.backgroundPrimary)
                .padding(paddingValues)
                .offset(y = (-5).dp)
        ) {
            // Use a single LazyColumn for all content
            LazyColumn(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxSize()
            ) {
                // Top Balance Section
                item {
                    Box(
                        modifier = Modifier
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.HomePic),
                            contentDescription = "Home illustration",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                                .clip(RectangleShape)
                                .padding(0.dp),
                            contentScale = ContentScale.FillWidth
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp, start = 15.dp, bottom = 0.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Total Balance",
                                style = getAppTypography(isDarkTheme).titleMedium,
                                fontSize = 15.sp,
                                color = if (isDarkTheme) colors.textSecondary else Color(0xFF64748B)
                            )
                            Text(
                                text = "₹1000.00",
                                style = getAppTypography(isDarkTheme).titleLarge,
                                fontSize = 20.sp,
                                color = if (isDarkTheme) colors.textPrimary else Color(0xFF1E293B)
                            )

                            // Will Get/Pay Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 0.dp, start = 0.dp, end = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                BalanceItem(
                                    amount = "₹475.00",
                                    label = "you'll get",
                                    color = colors.success
                                )
                                BalanceItem(
                                    amount = "₹181.67",
                                    label = "you'll pay",
                                    color = colors.error
                                )
                            }
                        }
                    }
                }

                // Recent Groups header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Recent Groups",
                            style = getAppTypography(isDarkTheme).titleLarge,
                            color = colors.textPrimary
                        )
                    }
                }

                // Search bar
                item {
                    GroupSearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onClearQuery = { searchQuery = "" },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Show no results message if needed
                if (filteredExpenses.isEmpty() && searchQuery.isNotEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No groups found matching \"$searchQuery\"",
                                style = getAppTypography(isDarkTheme).bodyMedium,
                                color = colors.textSecondary
                            )
                        }
                    }
                } else {
                    // Expense cards - directly in the main LazyColumn
                    items(filteredExpenses) { expense ->
                        ExpenseCard(
                            title = expense.title,
                            primaryText = expense.primaryText,
                            secondaryText = expense.secondaryText,
                            imageRes = expense.imageRes,
                            isOwed = expense.isOwed,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
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
}

@Composable
private fun BalanceItem(
    amount: String,
    label: String,
    color: Color
) {
    val colors = LocalSplitColors.current
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 1.dp)
    ) {
        Text(
            text = amount,
            style = getAppTypography(isDarkTheme).bodyMedium,
            color = color,
            fontSize = 15.sp
        )
        Text(
            text = label,
            style = getAppTypography(isDarkTheme).bodySmall,
            color = if (isDarkTheme) colors.textSecondary else Color(0xFF64748B),
            fontSize = 10.sp
        )
    }
}

// SearchBar component
@Composable
fun GroupSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalSplitColors.current
    val isDarkTheme = isSystemInDarkTheme()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .focusRequester(focusRequester),
        placeholder = {
            Text(
                text = "Search groups",
                style = getAppTypography(isDarkTheme).bodyMedium,
                color = colors.textSecondary
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = colors.cardBackground,
            unfocusedContainerColor = colors.cardBackground,
            focusedBorderColor = colors.primary,
            unfocusedBorderColor = colors.textSecondary.copy(alpha = 0.2f),
            cursorColor = colors.primary,
            focusedTextColor = colors.textPrimary,
            unfocusedTextColor = colors.textPrimary
        ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search icon",
                tint = colors.textSecondary
            )
        },
        trailingIcon = {
            AnimatedVisibility(visible = query.isNotEmpty()) {
                IconButton(onClick = {
                    onClearQuery()
                    focusManager.clearFocus()
                }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search query",
                        tint = colors.textSecondary
                    )
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search,
            keyboardType = KeyboardType.Text
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                focusManager.clearFocus()
            }
        ),
        shape = RoundedCornerShape(28.dp),
        textStyle = getAppTypography(isDarkTheme).bodyMedium
    )
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
    val colors = LocalSplitColors.current
    val isDarkTheme = isSystemInDarkTheme()

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = colors.cardBackground
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
                    .size(30.dp)
                    .padding(end = 5.dp),
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = getAppTypography(isDarkTheme).titleLarge,
                    color = colors.textPrimary
                )
                Text(
                    text = primaryText,
                    style = getAppTypography(isDarkTheme).titleMedium,
                    color = if (isOwed) colors.success else colors.error,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
                Text(
                    text = secondaryText,
                    style = getAppTypography(isDarkTheme).titleMedium,
                    color = colors.textSecondary
                )
            }
        }
    }
}

data class ExpenseItem(
    val title: String,
    val primaryText: String,
    val secondaryText: String,
    val imageRes: DrawableResource,
    val isOwed: Boolean
)

// Helper function to get the expense list
private fun getExpensesList(): List<ExpenseItem> {
    return listOf(
        ExpenseItem(
            title = "E-1302",
            primaryText = "you are owed ₹475.00",
            secondaryText = "Kumar K. owes you ₹475.00",
            imageRes = Res.drawable.group_icon_outlined,
            isOwed = true
        ),
        ExpenseItem(
            title = "SIH TRIP KOTA",
            primaryText = "you owe ₹181.67",
            secondaryText = "You owe Ankur C. ₹181.67",
            imageRes = Res.drawable.group_icon_outlined,
            isOwed = false
        ),
        ExpenseItem(
            title = "Non-group expenses",
            primaryText = "settled up",
            secondaryText = "",
            imageRes = Res.drawable.group_icon_outlined,
            isOwed = false
        ),
        ExpenseItem(
            title = "Non-group expenses",
            primaryText = "settled up",
            secondaryText = "",
            imageRes = Res.drawable.group_icon_outlined,
            isOwed = false
        )
    )
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
                style = getAppTypography().titleLarge,
                color = Color.White
            )
        }
    }
}