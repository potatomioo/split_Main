package com.falcon.split.presentation.screens.mainNavigation

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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.falcon.split.contact.ContactManager
import com.falcon.split.presentation.group.GroupState
import com.falcon.split.presentation.group.GroupViewModel
import com.falcon.split.data.network.models_app.Expense
import com.falcon.split.data.network.models_app.Group
import com.falcon.split.data.network.models_app.GroupMember
import com.falcon.split.presentation.expense.ExpenseState
import com.falcon.split.utils.MemberNameResolver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailsScreen(
    groupId: String,
    viewModel: GroupViewModel,
    contactManager: ContactManager?,
    onNavigateBack: () -> Unit,
    onAddExpense: (String) -> Unit,
    navControllerMain: NavHostController
) {
    var showOptionsMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Load data immediately when screen is mounted
    LaunchedEffect(groupId) {
        // Start both loading operations in parallel
        viewModel.loadGroupDetails(groupId)
        viewModel.loadGroupExpenses(groupId)
    }

    val groupState by viewModel.groupState.collectAsState()
    val expenseState by viewModel.expenseState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    when (groupState) {
                        is GroupState.GroupDetailSuccess -> Text((groupState as GroupState.GroupDetailSuccess).group.name)
                        else -> Text("Group Details")
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
        // Show a fullscreen loading indicator if we don't have any group data yet
        if (groupState is GroupState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Once we have group data, show the group details with section-specific loading states
            when (groupState) {
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
                                expenseCount = group.expenses.size,
                                memberCount = group.members.size,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        item {
                            MemberBalancesCard(
                                members = group.members,
                                contactManager = contactManager,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        item {
                            ExpensesCard(
                                expenseState = expenseState,
                                group = group,
                                contactManager = contactManager,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }

                is GroupState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text((groupState as GroupState.Error).message)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadGroupDetails(groupId) }) {
                            Text("Retry")
                        }
                    }
                }

                else -> {
                    // This branch should rarely be hit since we check for Loading earlier
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
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
                    println("Sharing group: $groupId")
                },
                leadingIcon = {
                    Icon(Icons.Default.Share, "Share Group")
                }
            )

            DropdownMenuItem(
                text = { Text("Delete Group", color = MaterialTheme.colorScheme.error) },
                onClick = {
                    showOptionsMenu = false
                    showDeleteDialog = true
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Delete,
                        "Delete Group",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            )
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
                            viewModel.deleteGroup(groupId)
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
}

@Composable
private fun ExpensesCard(
    expenseState: ExpenseState,
    group: Group,
    contactManager: ContactManager?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Recent Expenses",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            when (expenseState) {
                is ExpenseState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Use a custom loading indicator that will animate smoothly
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                is ExpenseState.Error -> {
                    Text(
                        "Error loading expenses: ${expenseState.message}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is ExpenseState.Success -> {
                    val expenses = expenseState.expenses
                    if (expenses.isEmpty()) {
                        Text(
                            "No expenses yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        // Create nameResolver here once
                        val nameResolver = remember { MemberNameResolver(contactManager) }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            expenses.forEach { expense ->
                                // Add divider between expenses
                                if (expense != expenses.first()) {
                                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                                }

                                // Inline expense item - keep it lightweight
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

                                        // Find the member who paid
                                        val paidByMember = group.members.find { it.userId == expense.paidByUserId }
                                        val payerName = if (paidByMember != null) {
                                            nameResolver.resolveDisplayName(paidByMember)
                                        } else {
                                            expense.paidByUserName ?: "Unknown"
                                        }

                                        Text(
                                            "Paid by $payerName",
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
                            }
                        }
                    }
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
    contactManager: ContactManager?,
    modifier: Modifier = Modifier
) {
    var isContactsLoading by remember { mutableStateOf(true) }
    var resolvedMembers by remember { mutableStateOf<List<Pair<GroupMember, String>>>(emptyList()) }
    val nameResolver = remember { MemberNameResolver(contactManager) }

    // Use a key based on members list to ensure proper recomposition
    val memberKey = members.hashCode()

    // Load contact names in a coroutine
    LaunchedEffect(memberKey) {
        withContext(Dispatchers.Default) {
            val resolved = members.map { member ->
                member to nameResolver.resolveDisplayName(member)
            }
            resolvedMembers = resolved
            isContactsLoading = false
        }
    }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Member Balances",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (isContactsLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Custom loading indicator that will animate smoothly
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(32.dp)
                    )
                }
            } else {
                // Show resolved member list
                resolvedMembers.forEach { (member, displayName) ->
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

                            Text(displayName)
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
}