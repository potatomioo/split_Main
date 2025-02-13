package com.falcon.split.Presentation.screens.mainNavigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.falcon.split.Presentation.ErrorRed
import com.falcon.split.data.Repository.ExpenseRepository
import com.falcon.split.data.Repository.GroupRepository
import com.falcon.split.data.network.models_app.Expense
import com.falcon.split.data.network.models_app.Group
import com.falcon.split.data.network.models_app.GroupMember
import kotlinx.datetime.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailsScreen(
    groupId: String,
    onNavigateBack: () -> Unit,
    onAddExpense: (String) -> Unit,
    navControllerMain: NavHostController,
    groupRepository: GroupRepository,
    expenseRepository: ExpenseRepository
) {
    // Dummy data for the group
    val group = remember {
        Group(
            id = groupId,
            name = "Weekend Trip to Goa",
            members = emptyList(),
            createdBy = "user1",
            createdAt = Clock.System.now().toEpochMilliseconds(),
            updatedAt = null,
            totalAmount = null
        )
    }

    // Dummy data for members
    val memberNames = remember {
        mapOf(
            "user1" to "John Doe",
            "user2" to "Jane Smith",
            "user3" to "Mike Johnson",
            "user4" to "Sarah Wilson"
        )
    }

    // Dummy expenses
    val expenses = remember {
        listOf(
            Expense(
                expenseId = "1",
                groupId = groupId,
                description = "Hotel Booking",
                amount = 12000.0,
                paidByUserId = "user1",
//                createdAt = Clock.System.now(),
                splits = emptyList()
            )
        )
    }

    //ShowOptionsMenu State
    var showOptionsMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(group.name) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showOptionsMenu = true }) {
                        Icon(Icons.Default.MoreVert, "More options")
                    }
                    GroupOptionsMenu(
                        showMenu = showOptionsMenu,
                        onDismiss = { showOptionsMenu = false },
                        onDeleteClick = {
                            // Handle delete group
                        },
                        onRemoveMemberClick = {
                            // Handle remove member
                        }
                    )
                }
            )
        },
        floatingActionButton = {
            AddExpenseFAB(
                onClick = {
                    navControllerMain.navigate("create_expense_in_a_group")
                },
                modifier = Modifier
                    .padding(end = 16.dp, bottom = 16.dp)  // Adds spacing from screen edges
            )
        }
    ) { padding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        GroupSummaryCard(
                            totalAmount = expenses.sumOf { it.amount },
                            expenseCount = expenses.size,
                    memberCount = group.members.size
                        )
                    }

                    item {
                        MembersCard(
                    members = group.members,
                    memberNames = memberNames
                        )
                    }

                    items(expenses) { expense ->
                        ExpenseCard(
                            expense = expense,
                    paidByName = memberNames[expense.paidByUserId] ?: "Unknown"
            )
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
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Total Expenses",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                "₹${((totalAmount * 100).toInt() / 100.0)}",
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
private fun MembersCard(
    members: List<GroupMember>,
    memberNames: Map<String, String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Members",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            members.forEach { memberId ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
//                    Text(memberNames[memberId] ?: "Unknown")
                }
            }
        }
    }
}

@Composable
private fun ExpenseCard(
    expense: Expense,
    paidByName: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    expense.description,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "₹${((expense.amount * 100).toInt() / 100.0)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Paid by $paidByName",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}




@Composable
fun GroupOptionsMenu(
    showMenu: Boolean,
    onDismiss: () -> Unit,
    onDeleteClick: () -> Unit,
    onRemoveMemberClick: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = onDismiss,
        offset = DpOffset(-10.dp,1.dp)
    ) {
        DropdownMenuItem(
            text = { Text("Remove Member") },
            onClick = {
                onDismiss()
                onRemoveMemberClick()
            },
            leadingIcon = {
                Icon(Icons.Default.Person, "Remove Member")
            }
        )

        DropdownMenuItem(
            text = { Text("Delete Group", color = ErrorRed) },
            onClick = {
                onDismiss()
                showDeleteDialog = true
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Delete,
                    "Delete Group",
                    tint = ErrorRed
                )
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "Delete Group",
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
            },
            text = {
                Text("Are you sure you want to delete this group?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteClick()
                    }
                ) {
                    Text("Delete", color = ErrorRed)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel", color = Color.Black)
                }
            }
        )
    }
}