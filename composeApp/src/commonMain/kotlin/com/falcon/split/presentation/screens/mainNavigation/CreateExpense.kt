package com.falcon.split.presentation.screens.mainNavigation

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.falcon.split.data.network.models_app.Expense
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.falcon.split.presentation.expense.CreateExpenseState
import com.falcon.split.presentation.expense.CreateExpenseViewModel
import com.falcon.split.data.network.models_app.ExpenseSplit
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.painterResource
import split.composeapp.generated.resources.Res
import split.composeapp.generated.resources.description_icon
import split.composeapp.generated.resources.group_icon_outlined

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExpense(
    navControllerMain: NavHostController,
    viewModel: CreateExpenseViewModel,
    onNavigateBack: () -> Unit
) {
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedGroup by remember { mutableStateOf<String?>(null) }
    var selectedPayer by remember { mutableStateOf<String?>(null) }
    var showGroupDropdown by remember { mutableStateOf(false) }
    var showPayerDropdown by remember { mutableStateOf(false) }

    // Collect state from ViewModel
    val state by viewModel.state.collectAsState()
    val selectedGroupDetails by viewModel.selectedGroup.collectAsState()

    // Effect to update selected group details when a group is selected
    LaunchedEffect(selectedGroup) {
        selectedGroup?.let { groupId ->
            viewModel.selectGroup(groupId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Expense") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        when (state) {
            is CreateExpenseState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is CreateExpenseState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text((state as CreateExpenseState.Error).message)
                }
            }
            is CreateExpenseState.Success -> {
                val groups = (state as CreateExpenseState.Success).groups

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Amount Input with Currency Symbol
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Enter Amount",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "₹",
                                    style = MaterialTheme.typography.headlineMedium,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                OutlinedTextField(
                                    value = amount,
                                    onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) amount = it },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    placeholder = { Text("0.00") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                            }
                        }
                    }

                    // Description Input
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        placeholder = { Text("What's this expense for?") },
                        leadingIcon = {
                            Image(
                                painter = painterResource(Res.drawable.description_icon),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(horizontal = 12.dp),
                                contentScale = ContentScale.Fit
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Group Selection
                    ExposedDropdownMenuBox(
                        expanded = showGroupDropdown,
                        onExpandedChange = { showGroupDropdown = it }
                    ) {
                        OutlinedTextField(
                            value = groups.find { it.id == selectedGroup }?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Group") },
                            leadingIcon = {
                                Image(
                                    painter = painterResource(Res.drawable.group_icon_outlined),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .padding(horizontal = 12.dp),
                                    contentScale = ContentScale.Fit
                                )
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showGroupDropdown) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = showGroupDropdown,
                            onDismissRequest = { showGroupDropdown = false }
                        ) {
                            groups.forEach { group ->
                                DropdownMenuItem(
                                    text = { Text(group.name) },
                                    onClick = {
                                        selectedGroup = group.id
                                        showGroupDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    // Paid By Selection (showing only group members)
                    selectedGroupDetails?.let { group ->
                        ExposedDropdownMenuBox(
                            expanded = showPayerDropdown,
                            onExpandedChange = { showPayerDropdown = it }
                        ) {
                            OutlinedTextField(
                                value = group.members.find { it.userId == selectedPayer }?.name ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Paid By") },
                                leadingIcon = { Icon(Icons.Default.Person, null) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showPayerDropdown) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = showPayerDropdown,
                                onDismissRequest = { showPayerDropdown = false }
                            ) {
                                group.members.forEach { member ->
                                    DropdownMenuItem(
                                        text = { member.name?.let { Text(it) } },
                                        onClick = {
                                            selectedPayer = member.userId
                                            showPayerDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        // Split Options Card
                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    "Split Equally Between",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                group.members.forEach { member ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        member.name?.let { Text(it) }
                                        if (amount.isNotEmpty()) {
                                            val splitAmount = amount.toDoubleOrNull()?.div(group.members.size) ?: 0.0
                                            Text("₹${splitAmount.toInt()}")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Add Expense Button
                    Button(
                        onClick = {
                            if (amount.isNotEmpty() && description.isNotEmpty() && selectedGroup != null) {
                                viewModel.createExpense(
                                    description = description,
                                    amount = amount.toDoubleOrNull() ?: 0.0,
                                    selectedGroupId = selectedGroup!!
                                )
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = amount.isNotEmpty() && description.isNotEmpty() && selectedGroup != null
                    ) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.padding(end = 8.dp))
                        Text("Add Expense")
                    }
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExpenseFromAGroup(
    navControllerMain: NavHostController,
    onExpenseAdded: (Expense) -> Unit,
    onNavigateBack: () -> Unit
) {
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedGroup by remember { mutableStateOf<String?>(null) }
    var selectedPayer by remember { mutableStateOf<String?>(null) }
    var showGroupDropdown by remember { mutableStateOf(false) }
    var showPayerDropdown by remember { mutableStateOf(false) }

    // Dummy data - Replace with actual data from your ViewModel
    val dummyGroups = listOf(
        "Weekend Trip" to "group1",
        "House Expenses" to "group2",
        "Movie Night" to "group3"
    )

    val dummyUsers = listOf(
        "John Doe" to "user1",
        "Jane Smith" to "user2",
        "Mike Johnson" to "user3"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Expense") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Amount Input with Currency Symbol
            OutlinedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Enter Amount",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "₹",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) amount = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            placeholder = { Text("0.00") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }

            // Description Input
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                placeholder = { Text("What's this expense for?") },
                leadingIcon = {
                    Image(
                        painter = painterResource(Res.drawable.description_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(horizontal = 12.dp),
                        contentScale = ContentScale.Fit
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            // Paid By Selection
            ExposedDropdownMenuBox(
                expanded = showPayerDropdown,
                onExpandedChange = { showPayerDropdown = it }
            ) {
                ExposedDropdownMenu(
                    expanded = showPayerDropdown,
                    onDismissRequest = { showPayerDropdown = false }
                ) {
                    dummyUsers.forEach { (name, id) ->
                        DropdownMenuItem(
                            text = { Text(name) },
                            onClick = {
                                selectedPayer = id
                                showPayerDropdown = false
                            }
                        )
                    }
                }
            }

            // Split Options Card
            OutlinedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Split Equally Between",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    dummyUsers.forEach { (name, _) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(name)
                            if (!amount.isNullOrEmpty()) {
                                Text("₹${(amount.toDoubleOrNull()?.div(dummyUsers.size) ?: 0.0).toString().take(5)}")
                            }
                        }
                    }
                }
            }

            // Add Expense Button
            Button(
                onClick = {
                    if (amount.isNotEmpty() && description.isNotEmpty() && selectedGroup != null && selectedPayer != null) {
                        val expenseAmount = amount.toDoubleOrNull() ?: 0.0
                        val splitAmount = expenseAmount / dummyUsers.size

                        val expense = Expense(
                            expenseId = Clock.System.now().toEpochMilliseconds().toString(),
                            groupId = selectedGroup!!,
                            description = description,
                            amount = expenseAmount,
                            paidByUserId = selectedPayer!!,
//                            createdAt = Clock.System.now(),
                            splits = dummyUsers.map { (_, userId) ->
                                ExpenseSplit(
                                    userId = userId,
                                    amount = splitAmount,
                                    settled = false,
                                    phoneNumber = ""
                                )
                            }
                        )
                        onExpenseAdded(expense)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = amount.isNotEmpty() && description.isNotEmpty() &&
                        selectedGroup != null && selectedPayer != null
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.padding(end = 8.dp))
                Text("Add Expense")
            }
        }
    }
}