package com.falcon.split.presentation.screens.mainNavigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.falcon.split.presentation.group.GroupState
import com.falcon.split.presentation.group.GroupViewModel
import com.falcon.split.data.network.models_app.Expense
import com.falcon.split.data.network.models_app.GroupMember

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailsScreen(
    groupId: String,
    viewModel: GroupViewModel,
    onNavigateBack: () -> Unit,
    onAddExpense: (String) -> Unit,
    navControllerMain: NavHostController
) {
    var showOptionsMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(groupId) {
        viewModel.loadGroupDetails(groupId)
    }

    val groupState by viewModel.groupState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    when (groupState) {
                        is GroupState.GroupDetailSuccess -> Text((groupState as GroupState.GroupDetailSuccess).group.name)
                        else -> Text("Loading...")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showOptionsMenu = true }) {
                        Icon(Icons.Default.MoreVert, "More options")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navControllerMain.navigate("create_expense/$groupId")
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { padding ->
        when (groupState) {
            is GroupState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is GroupState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text((groupState as GroupState.Error).message)
                        Button(onClick = { viewModel.loadGroupDetails(groupId) }) {
                            Text("Retry")
                        }
                    }
                }
            }

            is GroupState.GroupDetailSuccess -> {
                val group = (groupState as GroupState.GroupDetailSuccess).group

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        GroupSummaryCard(
                            totalAmount = group.totalAmount ?: 0.0,
//                            expenseCount = group.expenses?.size ?: 0,
                            expenseCount = 2,
                            memberCount = group.members.size,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    item {
                        MemberBalancesCard(
                            members = group.members,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    "Recent Expenses",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                if (group.totalAmount == 0.0) {
                                    Text(
                                        "No expenses yet",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

//                    items(group.expenses ?: emptyList()) { expense ->
//                        ExpenseListItem(
//                            expense = expense,
//                            paidByMember = group.members.find { it.userId == expense.paidByUserId },
//                            modifier = Modifier.padding(horizontal = 16.dp)
//                        )
//                    }
                }

                // Options Menu
                DropdownMenu(
                    expanded = showOptionsMenu,
                    onDismissRequest = { showOptionsMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Share Group") },
                        onClick = {
                            showOptionsMenu = false
                            // Implement share functionality
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Share, "Share Group")
                        }
                    )

//                    if (group.createdByUserId == FirebaseAuth.getInstance().currentUser?.uid) {
//                        DropdownMenuItem(
//                            text = { Text("Delete Group", color = MaterialTheme.colorScheme.error) },
//                            onClick = {
//                                showOptionsMenu = false
//                                showDeleteDialog = true
//                            },
//                            leadingIcon = {
//                                Icon(
//                                    Icons.Default.Delete,
//                                    "Delete Group",
//                                    tint = MaterialTheme.colorScheme.error
//                                )
//                            }
//                        )
//                    }
                }

                // Delete Confirmation Dialog
                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        title = { Text("Delete Group") },
                        text = { Text("Are you sure you want to delete this group? This action cannot be undone.") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showDeleteDialog = false
//                                    viewModel.deleteGroup(groupId)
                                    onNavigateBack()
                                }
                            ) {
                                Text("Delete", color = MaterialTheme.colorScheme.error)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }

            GroupState.Empty -> {
                // Handle empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No group data available")
                }
            }

            is GroupState.Success -> {
                // Handle general success state if needed
                // If this state isn't used, you might want to remove it from your GroupState sealed class
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Group loaded successfully")
                }
            }
        }
    }
}

@Composable
private fun GroupSummaryCard(
    totalAmount: Double,
    expenseCount: Int,
    memberCount: Int,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Total Expenses",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                "₹${totalAmount}",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("$expenseCount expenses")
                Text("$memberCount members")
            }
        }
    }
}

@Composable
private fun MemberBalancesCard(
    members: List<GroupMember>,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Member Balances",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            members.forEach { member ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(member.name.toString())
                    }

                    val balance = member.balance ?: 0.0
                    Text(
                        "₹${balance}",
                        color = when {
                            balance > 0 -> Color(0xFF4CAF50)  // Green
                            balance < 0 -> Color(0xFFF44336)  // Red
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpenseListItem(
    expense: Expense,
    paidByMember: GroupMember?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = { /* Navigate to expense details */ }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        expense.description,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Paid by ${paidByMember?.name ?: "Unknown"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    "₹${expense.amount}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (expense.splits?.isNotEmpty() == true) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                expense.splits.forEach { split ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            split.phoneNumber ?: "Unknown",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "₹${split.amount}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
