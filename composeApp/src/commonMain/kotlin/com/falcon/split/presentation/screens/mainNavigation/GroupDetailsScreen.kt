package com.falcon.split.presentation.screens.mainNavigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.falcon.split.contact.ContactManager
import com.falcon.split.presentation.group.GroupState
import com.falcon.split.presentation.group.GroupViewModel
import com.falcon.split.data.network.models_app.Expense
import com.falcon.split.data.network.models_app.Group
import com.falcon.split.data.network.models_app.GroupMember
import com.falcon.split.data.network.models_app.Settlement
import com.falcon.split.data.network.models_app.SettlementState
import com.falcon.split.data.network.models_app.SettlementStatus
import com.falcon.split.presentation.expense.ExpenseState
import com.falcon.split.presentation.theme.lDimens
import com.falcon.split.userManager.UserManager
import com.falcon.split.utils.MemberNameResolver
import io.ktor.http.HttpHeaders.Date
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


sealed class TimelineItem {
    data class ExpenseItem(val expense: Expense) : TimelineItem()
    data class SettlementItem(val settlement: Settlement) : TimelineItem()
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun GroupDetailsScreen(
    groupId: String,
    viewModel: GroupViewModel,
    contactManager: ContactManager?,
    onNavigateBack: () -> Unit,
    navControllerMain: NavHostController,
    userManager: UserManager
) {
    var showOptionsMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState { 3 } // 3 pages: expenses, balances, and requests
    val scope = rememberCoroutineScope()

    // Load data immediately when screen is mounted
    LaunchedEffect(groupId) {
        // Start loading operations
        viewModel.loadGroupDetails(groupId)
        viewModel.loadGroupExpenses(groupId)
        viewModel.loadSettlementHistory(groupId)
        viewModel.loadPendingSettlements()
    }

    val groupState by viewModel.groupState.collectAsState()
    val expenseState by viewModel.expenseState.collectAsState()
    val settlementState by viewModel.settlementState.collectAsState()
    val settlements by viewModel.settlements.collectAsState()
    val pendingSettlements by viewModel.pendingSettlements.collectAsState()

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
                    navControllerMain.navigate("create_expense?groupId=$groupId")
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
            // Once we have group data, show the group details with pager
            when (groupState) {
                is GroupState.GroupDetailSuccess -> {
                    val group = (groupState as GroupState.GroupDetailSuccess).group

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        // Fixed top section (always visible)
                        GroupSummaryCard(
                            totalAmount = group.totalAmount ?: 0.0,
                            expenseCount = group.expenses.size,
                            memberCount = group.members.size,
                            modifier = Modifier.padding(horizontal = lDimens.dp16, vertical = lDimens.dp8)
                        )

                        // Tabs
                        TabRow(
                            selectedTabIndex = pagerState.currentPage
                        ) {
                            Tab(
                                selected = pagerState.currentPage == 0,
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(0)
                                    }
                                },
                                text = { Text("EXPENSES") }
                            )
                            Tab(
                                selected = pagerState.currentPage == 1,
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(1)
                                    }
                                },
                                text = { Text("BALANCES") }
                            )
                            Tab(
                                selected = pagerState.currentPage == 2,
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(2)
                                    }
                                },
                                text = { Text("REQUESTS") }
                            )
                        }

                        // Pager content
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = lDimens.dp8)
                        ) { page ->
                            when (page) {
                                0 -> GroupExpensesPage(
                                    expenseState = expenseState,
                                    group = group,
                                    contactManager = contactManager,
                                    settlements = settlements
                                )
                                1 -> GroupBalancesPage(
                                    group = group,
                                    contactManager = contactManager,
                                    pendingSettlements = pendingSettlements,
                                    settlements = settlements,
                                    onSettleUp = { toUserId, amount ->
                                        viewModel.settleBalance(groupId, toUserId, amount)
                                    },
                                    settlementState = settlementState,
                                    onResetSettlementState = {
                                        viewModel.resetSettlementState()
                                    },
                                    userManager = userManager
                                )
                                2 -> SettlementRequestsPage(
                                    pendingSettlements = pendingSettlements,
                                    contactManager = contactManager,
                                    onApprove = { settlementId ->
                                        viewModel.approveSettlement(settlementId)
                                    },
                                    onDecline = { settlementId ->
                                        viewModel.declineSettlement(settlementId)
                                    }
                                )
                            }
                        }
                    }
                }

                is GroupState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(lDimens.dp16),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text((groupState as GroupState.Error).message)
                        Spacer(modifier = Modifier.height(lDimens.dp16))
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

        // Settlement Success/Error Dialog
        when (settlementState) {
            is SettlementState.Success -> {
                AlertDialog(
                    onDismissRequest = { viewModel.resetSettlementState() },
                    title = { Text("Success") },
                    text = { Text("Operation completed successfully.") },
                    confirmButton = {
                        TextButton(onClick = { viewModel.resetSettlementState() }) {
                            Text("OK")
                        }
                    }
                )
            }
            is SettlementState.Error -> {
                AlertDialog(
                    onDismissRequest = { viewModel.resetSettlementState() },
                    title = { Text("Error") },
                    text = { Text((settlementState as SettlementState.Error).message) },
                    confirmButton = {
                        TextButton(onClick = { viewModel.resetSettlementState() }) {
                            Text("OK")
                        }
                    }
                )
            }
            else -> {}
        }
    }
}


@Composable
fun GroupExpensesPage(
    expenseState: ExpenseState,
    group: Group,
    contactManager: ContactManager?,
    settlements: List<Settlement>
) {
    val nameResolver = remember { MemberNameResolver(contactManager) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = lDimens.dp16),
        verticalArrangement = Arrangement.spacedBy(lDimens.dp8)
    ) {
        item {
            Text(
                "Expenses & Settlements",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = lDimens.dp8)
            )
        }

        when (expenseState) {
            is ExpenseState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            is ExpenseState.Error -> {
                item {
                    Text(
                        "Error loading expenses: ${expenseState.message}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            is ExpenseState.Success -> {
                val expenses = expenseState.expenses
                if (expenses.isEmpty() && settlements.isEmpty()) {
                    item {
                        Text(
                            "No expenses or settlements yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    // Combine expenses and settlements into a timeline, sorted by most recent first
                    val timelineItems = mutableListOf<TimelineItem>()

                    expenses.forEach { expense ->
                        timelineItems.add(TimelineItem.ExpenseItem(expense))
                    }

                    settlements.forEach { settlement ->
                        timelineItems.add(TimelineItem.SettlementItem(settlement))
                    }

                    // Sort by most recent first (assuming there's a timestamp field in both)
                    val sortedItems = timelineItems.sortedByDescending {
                        when (it) {
                            is TimelineItem.ExpenseItem -> Clock.System.now().toEpochMilliseconds()
                            is TimelineItem.SettlementItem -> it.settlement.timestamp
                        }
                    }

                    items(sortedItems) { item ->
                        when (item) {
                            is TimelineItem.ExpenseItem -> {
                                ExpenseCard(
                                    expense = item.expense,
                                    group = group,
                                    nameResolver = nameResolver
                                )
                            }
                            is TimelineItem.SettlementItem -> {
                                SettlementCard(
                                    settlement = item.settlement,
                                    nameResolver = nameResolver
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun GroupBalancesPage(
    group: Group,
    contactManager: ContactManager?,
    pendingSettlements: List<Settlement>,
    settlements: List<Settlement>,
    onSettleUp: (toUserId: String, amount: Double) -> Unit,
    settlementState: SettlementState,
    onResetSettlementState: () -> Unit,
    userManager: UserManager
) {
    val nameResolver = remember { MemberNameResolver(contactManager) }
    val currentUserId = userManager.getCurrentUserId()

    var showSettleDialog by remember { mutableStateOf(false) }
    var selectedMember by remember { mutableStateOf<GroupMember?>(null) }
    var selectedAmount by remember { mutableStateOf(0.0) }

    // Find the current user's member entry
    val currentUserMember = group.members.find { it.userId == currentUserId }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = lDimens.dp16),
        verticalArrangement = Arrangement.spacedBy(lDimens.dp8)
    ) {
        item {
            Text(
                "Individual Balances",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = lDimens.dp8)
            )
        }

        if (currentUserMember == null) {
            item {
                Text(
                    "You're not a member of this group",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else {
            // Show individual balances for each member
            items(group.members) { member ->
                if (member.userId != currentUserId) {
                    // Get the balance between current user and this member
                    val balance = currentUserMember.individualBalances[member.userId] ?: 0.0
                    val displayName = nameResolver.resolveDisplayName(member)

                    // Check for pending settlements - properly check both collections
                    val hasPendingSettlement = pendingSettlements.any {
                        it.fromUserId == currentUserId &&
                                it.toUserId == member.userId &&
                                it.status == SettlementStatus.PENDING
                    }

                    // For approved settlements or zero balance
                    val isSettled = balance >= 0 || settlements.any {
                        it.fromUserId == currentUserId &&
                                it.toUserId == member.userId &&
                                it.status == SettlementStatus.APPROVED
                    }

                    IndividualBalanceCard(
                        memberName = displayName,
                        balance = balance,
                        hasPendingSettlement = hasPendingSettlement,
                        isSettled = isSettled,
                        onSettleUp = {
                            if (balance < 0 && !hasPendingSettlement && !isSettled) {
                                selectedMember = member
                                selectedAmount = -balance
                                showSettleDialog = true
                            }
                        }
                    )
                }
            }
        }
    }

    // Settle Up Dialog
    if (showSettleDialog && selectedMember != null) {
        SettleUpDialog(
            memberName = nameResolver.resolveDisplayName(selectedMember!!),
            initialAmount = selectedAmount,
            onDismiss = { showSettleDialog = false },
            onConfirm = { amount ->
                onSettleUp(selectedMember!!.userId!!, amount)
                showSettleDialog = false
            }
        )
    }
}


@Composable
fun ExpenseCard(
    expense: Expense,
    group: Group,
    nameResolver: MemberNameResolver
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = lDimens.dp4)
    ) {
        Column(
            modifier = Modifier.padding(lDimens.dp16)
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
                    "₹${expense.amount}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(lDimens.dp4))

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

            // Date if available
//            expense.timestamp?.let {
//                Text(
//                    formatDate(it),
//                    style = MaterialTheme.typography.bodySmall,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//            }
        }
    }
}


@Composable
fun SettlementCard(
    settlement: Settlement,
    nameResolver: MemberNameResolver
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = lDimens.dp4),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(lDimens.dp16)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Settlement",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "₹${settlement.amount}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(lDimens.dp4))

            Text(
                "${settlement.fromUserName ?: "Unknown"} paid ${settlement.toUserName ?: "Unknown"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                formatDate(settlement.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Composable
fun IndividualBalanceCard(
    memberName: String,
    balance: Double,
    hasPendingSettlement: Boolean = false,
    isSettled: Boolean = false,
    onSettleUp: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = lDimens.dp4)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(lDimens.dp16),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: Member info and balance
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(lDimens.dp8))
                    Text(
                        memberName,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.height(lDimens.dp4))

                Text(
                    when {
                        balance > 0 -> "Owes you ₹$balance"
                        balance < 0 -> "You owe ₹${-balance}"
                        else -> "Settled"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = when {
                        balance > 0 -> Color.Green
                        balance < 0 -> Color.Red
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            // Right side: Action button or status indicator
            when {
                // Case 1: There's a pending settlement request
                hasPendingSettlement -> {
                    StatusIndicator(
                        text = "Pending",
                        icon = Icons.Default.Lock,
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                }
                // Case 2: The balance is settled
                isSettled -> {
                    StatusIndicator(
                        text = "Settled",
                        icon = Icons.Default.Check,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                        contentColor = MaterialTheme.colorScheme.secondary
                    )
                }
                // Case 3: User needs to settle up
                else -> {
                    Button(
                        onClick = onSettleUp,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text("Settle Up")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusIndicator(
    text: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color
) {
    Surface(
        color = containerColor,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.height(36.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = lDimens.dp12, vertical = lDimens.dp8),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(lDimens.dp4)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(lDimens.dp16),
                tint = contentColor
            )
            Text(
                text,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettleUpDialog(
    memberName: String,
    initialAmount: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var amount by remember { mutableStateOf(initialAmount.toString()) }
    var isValidAmount by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Settle Up with $memberName") },
        text = {
            Column {
                Text(
                    "How much are you settling?",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(lDimens.dp16))

                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it
                        isValidAmount = try {
                            val amountValue = it.toDouble()
                            amountValue > 0 && amountValue <= initialAmount
                        } catch (e: Exception) {
                            false
                        }
                    },
                    label = { Text("Amount") },
                    prefix = { Text("₹") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    isError = !isValidAmount,
                    singleLine = true
                )

                if (!isValidAmount) {
                    Text(
                        "Please enter a valid amount (maximum ₹$initialAmount)",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    try {
                        val amountValue = amount.toDouble()
                        if (amountValue > 0 && amountValue <= initialAmount) {
                            onConfirm(amountValue)
                        }
                    } catch (e: Exception) {
                        // Invalid amount format
                    }
                },
                enabled = isValidAmount && amount.isNotEmpty()
            ) {
                Text("Settle")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


fun formatDate(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    val month = localDateTime.month.name.take(3)
    val day = localDateTime.dayOfMonth.toString().padStart(2, '0')
    val year = localDateTime.year
    val hour = localDateTime.hour.toString().padStart(2, '0')
    val minute = localDateTime.minute.toString().padStart(2, '0')

    return "$month $day, $year - $hour:$minute"
}

@Composable
fun GroupSummaryCard(
    totalAmount: Double,
    expenseCount: Int,
    memberCount: Int,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(lDimens.dp16)
        ) {
            Text(
                "Total Expenses",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                "₹$totalAmount",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = lDimens.dp8),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("$expenseCount expenses")
                Text("$memberCount members")
            }
        }
    }
}


@Composable
fun SettlementRequestsPage(
    pendingSettlements: List<Settlement>,
    contactManager: ContactManager?,
    onApprove: (String) -> Unit,
    onDecline: (String) -> Unit
) {
    val nameResolver = remember { MemberNameResolver(contactManager) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = lDimens.dp16),
        verticalArrangement = Arrangement.spacedBy(lDimens.dp8)
    ) {
        item {
            Text(
                "Pending Settlement Requests",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = lDimens.dp8)
            )
        }

        if (pendingSettlements.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(lDimens.dp32),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No pending requests",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(pendingSettlements) { settlement ->
                SettlementRequestCard(
                    settlement = settlement,
                    contactManager = contactManager,
                    onApprove = { onApprove(settlement.id) },
                    onDecline = { onDecline(settlement.id) }
                )
            }
        }
    }
}


@Composable
fun SettlementRequestCard(
    settlement: Settlement,
    contactManager: ContactManager?,
    onApprove: () -> Unit,
    onDecline: () -> Unit
) {
    val nameResolver = remember { MemberNameResolver(contactManager) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = lDimens.dp4),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(lDimens.dp16)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Payment Request",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "₹${settlement.amount}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(lDimens.dp8))

            Text(
                "${settlement.fromUserName ?: "Someone"} wants to settle ₹${settlement.amount}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                "Requested on ${formatDate(settlement.timestamp)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(lDimens.dp16))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onDecline,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.padding(end = lDimens.dp8)
                ) {
                    Text("Decline")
                }

                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Approve")
                }
            }
        }
    }
}