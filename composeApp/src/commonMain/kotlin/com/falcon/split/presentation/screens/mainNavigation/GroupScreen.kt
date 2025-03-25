import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.falcon.split.presentation.theme.LocalSplitColors
import com.falcon.split.presentation.theme.getAppTypography
import com.falcon.split.presentation.group.GroupState
import com.falcon.split.presentation.group.GroupViewModel
import com.falcon.split.presentation.screens.AnimationComponents.UpwardFlipHeaderImage
import com.falcon.split.data.network.models_app.Group
import com.falcon.split.presentation.theme.lDimens
import org.jetbrains.compose.resources.painterResource
import split.composeapp.generated.resources.GroupPic
import split.composeapp.generated.resources.Res
import split.composeapp.generated.resources.group_icon_filled

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    onCreateGroupClick: () -> Unit,
    onGroupClick: (Group) -> Unit,
    navControllerMain: NavHostController,
    viewModel: GroupViewModel = viewModel()
) {
    val colors = LocalSplitColors.current
    val isDarkTheme = isSystemInDarkTheme()
    val groupsState by viewModel.groupState.collectAsState()
    println("Screen: Current state is: ${groupsState::class.simpleName}")
    val lazyState = rememberLazyListState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navControllerMain.navigate("create_group") },
                containerColor = Color(0xFF8fcb39)
            ) {
                Icon(Icons.Default.Add, "Create Group", tint = Color.White)
            }
        },
        containerColor = colors.backgroundPrimary
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.backgroundPrimary)
                .padding(padding)
        ) {
            when (groupsState) {
                is GroupState.Loading -> {
                    println("Screen: Showing loading state")
                    LoadingIndicator()
                }
                is GroupState.Empty -> {
                    println("Screen: Showing empty state")
                    EmptyGroupsView(
                        onCreateGroupClick = onCreateGroupClick,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is GroupState.Error -> {
                    ErrorView(
                        error = (groupsState as GroupState.Error).message,
                        onRetry = { viewModel.retryLoading() }
                    )
                }
                is GroupState.Success -> {
                    val groups = (groupsState as GroupState.Success).groups
                    println("Screen: Showing success state with ${groups.size} groups")
                    GroupList(
                        groups = groups,
                        lazyState = lazyState,
                        onGroupClick = onGroupClick
                    )
                }
                is GroupState.GroupDetailSuccess -> {
                    // This state is handled in a different screen
                }
            }
        }
    }
}

@Composable
private fun LoadingIndicator() {
    val colors = LocalSplitColors.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.backgroundPrimary),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = colors.primary)
    }
}

@Composable
private fun ErrorView(
    error: String,
    onRetry: () -> Unit
) {
    val colors = LocalSplitColors.current
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.backgroundPrimary)
            .padding(lDimens.dp16),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = error,
            color = colors.error,
            style = getAppTypography(isDarkTheme).bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(lDimens.dp16))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.primary
            )
        ) {
            Text("Retry", color = Color.White)
        }
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
            .height(lDimens.dp56)
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
private fun GroupList(
    groups: List<Group>,
    lazyState: LazyListState,
    onGroupClick: (Group) -> Unit
) {
    val colors = LocalSplitColors.current
    val isDarkTheme = isSystemInDarkTheme()

    // Add search state
    var searchQuery by remember { mutableStateOf("") }
    val filteredGroups = remember(searchQuery, groups) {
        if (searchQuery.isEmpty()) {
            groups
        } else {
            groups.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LazyColumn(
        state = lazyState,
        modifier = Modifier
            .fillMaxSize()
            .background(colors.backgroundPrimary),
        contentPadding = PaddingValues(lDimens.dp0),
        verticalArrangement = Arrangement.spacedBy(lDimens.dp0)
    ) {
        // Header with image
        item {
            Box {
                UpwardFlipHeaderImage(
                    Res.drawable.GroupPic,
                    lazyState
                )
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(lDimens.dp16),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Number of Groups",
                        style = getAppTypography(isDarkTheme).titleMedium,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "${groups.size}",
                        style = getAppTypography(isDarkTheme).titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                }
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
                    .padding(horizontal = lDimens.dp16, vertical = lDimens.dp8)
            )
        }

        // No results message
        if (filteredGroups.isEmpty() && searchQuery.isNotEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(lDimens.dp32),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No groups found matching \"$searchQuery\"",
                        style = getAppTypography(isDarkTheme).bodyMedium,
                        color = colors.textSecondary
                    )
                }
            }
        }
        // Group items
        else {
            items(filteredGroups) { group ->
                GroupCard(
                    group = group,
                    onClick = { onGroupClick(group) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GroupCard(
    group: Group,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalSplitColors.current
    val isDarkTheme = isSystemInDarkTheme()

    OutlinedCard(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = lDimens.dp0, bottom = lDimens.dp10, start = lDimens.dp10, end = lDimens.dp10),
        colors = CardDefaults.outlinedCardColors(
            containerColor = colors.cardBackground,
            contentColor = colors.textPrimary
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(lDimens.dp10)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(lDimens.dp12),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = colors.primary.copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.group_icon_filled),
                            contentDescription = null,
                            modifier = Modifier
                                .size(lDimens.dp48)
                                .padding(horizontal = lDimens.dp12),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Column {
                        Text(
                            text = group.name,
                            style = getAppTypography(isDarkTheme).titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "${group.members.size} members",
                            style = getAppTypography(isDarkTheme).titleSmall,
                            color = colors.textSecondary
                        )
                    }
                }

                IconButton(onClick = onClick) {
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = "View Group",
                        tint = colors.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyGroupsView(
    onCreateGroupClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalSplitColors.current
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = modifier
            .padding(24.dp)
            .background(colors.backgroundPrimary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Group icon with background for better visibility in dark theme
        Surface(
            color = colors.primary.copy(alpha = 0.2f),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.size(70.dp)
        ) {
            Image(
                painter = painterResource(Res.drawable.group_icon_filled),
                contentDescription = null,
                modifier = Modifier
                    .padding(lDimens.dp12)
                    .size(lDimens.dp48),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(lDimens.dp16))

        Text(
            "No Groups Yet",
            style = getAppTypography(isDarkTheme).titleLarge.copy(
                fontSize = MaterialTheme.typography.titleLarge.fontSize
            ),
            color = colors.textPrimary
        )

        Spacer(modifier = Modifier.height(lDimens.dp8))

        Text(
            "Create a group to start splitting expenses with friends",
            style = getAppTypography(isDarkTheme).bodyMedium,
            color = colors.textSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onCreateGroupClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isDarkTheme)
                    colors.primary
                else
                    Color(0xFFDAD1EC)
            )
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.padding(end = lDimens.dp8),
                tint = if (isDarkTheme) Color.White else Color.Black
            )
            Text(
                "Create Group",
                color = if (isDarkTheme) Color.White else Color.Black
            )
        }
    }
}