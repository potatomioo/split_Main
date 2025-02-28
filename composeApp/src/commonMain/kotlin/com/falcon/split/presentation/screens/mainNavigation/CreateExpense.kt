package com.falcon.split.presentation.screens.mainNavigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.backhandler.BackHandler
import com.falcon.split.data.network.models_app.Expense
import com.falcon.split.data.network.models_app.ExpenseSplit
import com.falcon.split.presentation.LocalSplitColors
import com.falcon.split.presentation.expense.CreateExpenseState
import com.falcon.split.presentation.expense.CreateExpenseViewModel
import com.falcon.split.presentation.getAppTypography
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
    onNavigateBack: () -> Unit,
    backHandler: BackHandler // Add backHandler as a parameter
) {
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedGroup by remember { mutableStateOf<String?>(null) }
    var selectedPayer by remember { mutableStateOf<String?>(null) }
    var showPayerDropdown by remember { mutableStateOf(false) }
    val colors = LocalSplitColors.current
    val isDarkTheme = isSystemInDarkTheme()

    // Collect state from ViewModel
    val state by viewModel.state.collectAsState()
    val selectedGroupDetails by viewModel.selectedGroup.collectAsState()

    // For dropdown search
    var isExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Create a back callback for the dropdown
    val dropdownBackCallback = remember {
        BackCallback {
            if (isExpanded) {
                isExpanded = false
                searchQuery = ""
            }
        }
    }

    // Register/unregister back callback based on dropdown state
    DisposableEffect(isExpanded) {
        if (isExpanded) {
            backHandler.register(dropdownBackCallback)
        } else {
            backHandler.unregister(dropdownBackCallback)
        }

        onDispose {
            backHandler.unregister(dropdownBackCallback)
        }
    }

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

                    // Group Selection with Searchable Dropdown
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Select Group",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                        )

                        // Filter the groups based on search query
                        val filteredGroups = remember(searchQuery, groups) {
                            if (searchQuery.isEmpty()) {
                                groups
                            } else {
                                groups.filter { group ->
                                    group.name.contains(searchQuery, ignoreCase = true)
                                }
                            }
                        }

                        // Selected group display with search functionality
                        OutlinedTextField(
                            value = if (isExpanded) searchQuery else groups.find { it.id == selectedGroup }?.name ?: "",
                            onValueChange = {
                                if (isExpanded) {
                                    searchQuery = it
                                }
                            },
                            placeholder = {
                                Text(
                                    text = "Search or select a group",
                                    style = getAppTypography(isDarkTheme).bodyMedium,
                                    color = colors.textSecondary
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { if (it.isFocused) isExpanded = true },
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
                            trailingIcon = {
                                IconButton(onClick = { isExpanded = !isExpanded }) {
                                    Icon(
                                        if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Toggle dropdown"
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = colors.cardBackground,
                                unfocusedContainerColor = colors.cardBackground
                            ),
                            singleLine = true
                        )

                        // Dropdown list shown below the field
                        AnimatedVisibility(visible = isExpanded) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 200.dp)
                                ) {
                                    if (filteredGroups.isEmpty() && searchQuery.isNotEmpty()) {
                                        // No matching groups
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "No groups found",
                                                style = getAppTypography(isDarkTheme).bodyMedium,
                                                color = colors.textSecondary
                                            )
                                        }
                                    } else {
                                        // Using LazyColumn for better performance with many items
                                        LazyColumn(
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            items(filteredGroups) { group ->
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            selectedGroup = group.id
                                                            searchQuery = ""
                                                            isExpanded = false
                                                        }
                                                        .padding(16.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = group.name,
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                }
                                                if (filteredGroups.indexOf(group) < filteredGroups.size - 1) {
                                                    HorizontalDivider(
                                                        color = colors.textSecondary.copy(alpha = 0.1f),
                                                        modifier = Modifier.padding(horizontal = 8.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Close button at bottom
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(colors.backgroundSecondary.copy(alpha = 0.1f))
                                    ) {
                                        TextButton(
                                            onClick = {
                                                isExpanded = false
                                                searchQuery = ""
                                            },
                                            modifier = Modifier.align(Alignment.Center)
                                        ) {
                                            Text("Close")
                                        }
                                    }
                                }
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
    onNavigateBack: () -> Unit,
    backHandler: BackHandler // Add backHandler as a parameter
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