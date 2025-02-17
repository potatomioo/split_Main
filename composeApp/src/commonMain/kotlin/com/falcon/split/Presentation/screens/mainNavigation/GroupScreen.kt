import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.falcon.split.Presentation.getAppTypography
import com.falcon.split.Presentation.group.GroupState
import com.falcon.split.Presentation.group.GroupViewModel
import com.falcon.split.Presentation.screens.AnimationComponents.UpwardFlipHeaderImage
import com.falcon.split.data.network.models_app.Group
import kotlinx.coroutines.delay
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
    val groupsState by viewModel.groupState.collectAsState()
    println("Screen: Current state is: ${groupsState::class.simpleName}")
    val lazyState = rememberLazyListState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navControllerMain.navigate("create_group") },
                containerColor = Color(0xFF8fcb39)
            ) {
                Icon(Icons.Default.Add, "Create Group")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
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
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorView(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
private fun GroupList(
    groups: List<Group>,
    lazyState: LazyListState,
    onGroupClick: (Group) -> Unit
) {
    LazyColumn(
        state = lazyState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item {
            Box {
                UpwardFlipHeaderImage(
                    Res.drawable.GroupPic,
                    lazyState
                )
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Number of Groups",
                        style = getAppTypography().titleMedium,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = "${groups.size}",
                        style = getAppTypography().titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B)
                    )
                }
            }
        }

        items(groups) { group ->
            GroupCard(
                group = group,
                onClick = { onGroupClick(group) }
            )
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
    OutlinedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
            .padding(top = 0.dp, bottom = 10.dp, start = 10.dp, end = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.group_icon_filled),
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .padding(horizontal = 12.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Column {
                        Text(
                            text = group.name,
                            style = getAppTypography().titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${group.members.size} members",
                            style = getAppTypography().titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onClick) {
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = "View Group",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
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
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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
        Text(
            "No Groups Yet",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Create a group to start splitting expenses with friends",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        FilledTonalButton(
            onClick = onCreateGroupClick
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Create Group")
        }
    }
}
