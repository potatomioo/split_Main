package com.falcon.split.Presentation.screens.mainNavigation

//import ContactPicker
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.unit.dp
//import com.falcon.split.contact.ContactInfo
//import com.falcon.split.contact.ContactManager
//import com.falcon.split.data.network.models_app.Group
//import kotlinx.datetime.Clock
//import org.jetbrains.compose.resources.painterResource
//import split.composeapp.generated.resources.Res
//import split.composeapp.generated.resources.group_icon_filled
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun CreateGroupScreen(
//    onGroupCreated: (Group) -> Unit,
//    onNavigateBack: () -> Unit,
//    contactManager: ContactManager
//) {
//    var groupName by remember { mutableStateOf("") }
//    var showMemberSelection by remember { mutableStateOf(false) }
//    var selectedMembers by remember { mutableStateOf(setOf<String>()) }
//
//    var showContactPicker by remember { mutableStateOf(false) }
//    var selectedContact by remember { mutableStateOf<ContactInfo?>(null) }
//
//    // Dummy data for users - Replace with actual data from your ViewModel
//    val dummyUsers = remember {
//        mutableListOf(
//            "John Doe" to "user1",
//            "Jane Smith" to "user2"
//        )
//    }
//
//    if (showContactPicker) {
//        ContactPicker(
//            contactManager = contactManager
//        ) { contact ->
//            selectedContact = contact
//            showContactPicker = false
//        }
//    }
//    LaunchedEffect(selectedContact){
//        selectedContact?.let { contact ->
//            dummyUsers.add(contact.name to "user ${dummyUsers.size + 1} ")
//            println("Hey printing this $dummyUsers")
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Create New Group") },
//                navigationIcon = {
//                    IconButton(onClick = onNavigateBack) {
//                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
//                    }
//                }
//            )
//        }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding)
//                .padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            // Group Icon and Name Section
//            OutlinedCard(
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Column(
//                    modifier = Modifier
//                        .padding(16.dp)
//                        .fillMaxWidth(),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Image(
//                        painter = painterResource(Res.drawable.group_icon_filled),
//                        contentDescription = null,
//                        modifier = Modifier
//                            .size(48.dp)
//                            .padding(horizontal = 12.dp),
//                        contentScale = ContentScale.Fit
//                    )
//                    Spacer(modifier = Modifier.height(16.dp))
//                    OutlinedTextField(
//                        value = groupName,
//                        onValueChange = { groupName = it },
//                        label = { Text("Group Name") },
//                        placeholder = { Text("Enter group name") },
//                        singleLine = true,
//                        modifier = Modifier.fillMaxWidth()
//                    )
//                }
//            }
//
//            // Member Selection Section
//            OutlinedCard(
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Column(
//                    modifier = Modifier
//                        .padding(16.dp)
//                        .fillMaxWidth()
//                ) {
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(
//                            "Select Members",
//                            style = MaterialTheme.typography.titleMedium
//                        )
//                        IconButton(onClick = {
//                            //showMemberSelection = !showMemberSelection
//                            //Changes this, and opening Contact on the click
//                            showContactPicker = true
//                        }) {
//                            Icon(
//                                if (showMemberSelection) Icons.Default.KeyboardArrowDown
//                                else Icons.Default.KeyboardArrowDown,
//                                contentDescription = "Toggle selection"
//                            )
//                            Image(
//                                painter = painterResource(Res.drawable.group_icon_filled),
//                                contentDescription = null,
//                                modifier = Modifier
//                                    .size(48.dp)
//                                    .padding(horizontal = 12.dp),
//                                contentScale = ContentScale.Fit
//                            )
//                        }
//                    }
////        }
//
//                    AnimatedVisibility(visible = showMemberSelection) {
//                        LazyColumn(
//                            modifier = Modifier.heightIn(max = 200.dp),
//                            verticalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            items(dummyUsers) { (name, id) ->
//                                Row(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    horizontalArrangement = Arrangement.SpaceBetween,
//                                    verticalAlignment = Alignment.CenterVertically
//                                ) {
//                                    Text(name)
//                                    Checkbox(
//                                        checked = selectedMembers.contains(id),
//                                        onCheckedChange = { checked ->
//                                            selectedMembers = if (checked) {
//                                                selectedMembers + id
//                                            } else {
//                                                selectedMembers - id
//                                            }
//                                        }
//                                    )
//                                }
//                            }
//                        }
//                    }
//
//                    // Selected Members Summary
//                    if (selectedMembers.isNotEmpty()) {
//                        Spacer(modifier = Modifier.height(8.dp))
//                        Text(
//                            "${selectedMembers.size} members selected",
//                            style = MaterialTheme.typography.bodyMedium,
//                            color = MaterialTheme.colorScheme.primary
//                        )
//                    }
//                }
//            }
//
//            // Selected Members Preview
//            if (selectedMembers.isNotEmpty()) {
//                OutlinedCard(
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Column(
//                        modifier = Modifier.padding(16.dp)
//                    ) {
//                        Text(
//                            "Selected Members",
//                            style = MaterialTheme.typography.titleMedium
//                        )
//                        Spacer(modifier = Modifier.height(8.dp))
//                        selectedMembers.forEach { memberId ->
//                            val memberName = dummyUsers.find { it.second == memberId }?.first ?: ""
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(vertical = 4.dp),
//                                horizontalArrangement = Arrangement.SpaceBetween
//                            ) {
//                                Row(
//                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
//                                    verticalAlignment = Alignment.CenterVertically
//                                ) {
//                                    Icon(Icons.Default.Person, null,
//                                         modifier = Modifier.size(16.dp))
//                                    Text(memberName)
//                                }
//                                IconButton(
//                                    onClick = {
//                                        selectedMembers = selectedMembers - memberId
//                                    }
//                                ) {
//                                    Icon(
//                                        Icons.Default.Close,
//                                        contentDescription = "Remove member",
//                                        modifier = Modifier.size(16.dp)
//                                    )
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.weight(1f))
//
//            // Create Group Button
//            Button(
//                onClick = {
//                    val group = Group(
//                        id = Clock.System.now().toEpochMilliseconds().toString(),
//                        name = groupName,
//                        members = selectedMembers.toList(),
//                        createdBy = "currentUserId", // Replace with actual current user ID
//                        createdAt = Clock.System.now(),
//                        updatedAt = null
//                    )
//                    onGroupCreated(group)
//                },
//                modifier = Modifier.fillMaxWidth(),
//                enabled = groupName.isNotEmpty() && selectedMembers.isNotEmpty()
//            ) {
//                Icon(
//                    Icons.Default.AddCircle,
//                    contentDescription = null,
//                    modifier = Modifier.padding(end = 8.dp)
//                )
//                Text("Create Group")
//            }
//        }
//    }
//}


import ContactPicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.falcon.split.Presentation.group.CreateGroupState
import com.falcon.split.Presentation.group.CreateGroupViewModel
import com.falcon.split.contact.ContactManager
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import split.composeapp.generated.resources.Res
import split.composeapp.generated.resources.group_icon_filled

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupScreen(
    onGroupCreated: (String) -> Unit,
    onNavigateBack: () -> Unit,
    contactManager: ContactManager,
    viewModel: CreateGroupViewModel = viewModel()
) {
    var groupName by remember { mutableStateOf("") }
    var showContactPicker by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val selectedContacts by viewModel.selectedContacts.collectAsState()
    val state by viewModel.state.collectAsState()

    // Handle state changes
    LaunchedEffect(state) {
        when (state) {
            is CreateGroupState.Success -> {
                onGroupCreated((state as CreateGroupState.Success).groupId)
            }
            is CreateGroupState.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = (state as CreateGroupState.Error).message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            else -> {}
        }
    }

    if (showContactPicker) {
        ContactPicker(
            contactManager = contactManager
        ) { contact ->
            // Ensure the contact is not null before adding
            contact?.let { nonNullContact ->
                viewModel.addContact(nonNullContact)
            }
            showContactPicker = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Create New Group") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Group Name Section
            OutlinedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(Res.drawable.group_icon_filled),
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(horizontal = 12.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = groupName,
                        onValueChange = { groupName = it },
                        label = { Text("Group Name") },
                        placeholder = { Text("Enter group name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Member Selection Section
            OutlinedCard(
                modifier = Modifier.fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Select Members",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Button(
                            onClick = { showContactPicker = true },
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add member",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Member")
                        }
                    }

                    if (selectedContacts.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "${selectedContacts.size} members selected",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(selectedContacts) { contact ->
                                Card(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.Person,
                                                contentDescription = null,
                                                modifier = Modifier.size(24.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                            Column {
                                                Text(
                                                    text = contact.name,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = contact.phoneNumber,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        IconButton(
                                            onClick = { viewModel.removeContact(contact) }
                                        ) {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Remove member",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Add members to your group",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Create Group Button
            Button(
                onClick = { viewModel.createGroup(groupName) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = groupName.isNotEmpty() && selectedContacts.isNotEmpty() &&
                        state !is CreateGroupState.Loading
            ) {
                if (state is CreateGroupState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Create Group",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}