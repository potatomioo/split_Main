package com.falcon.split.presentation.screens.mainNavigation.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.falcon.split.presentation.theme.LocalSplitColors
import com.falcon.split.presentation.theme.getAppTypography
import com.falcon.split.presentation.screens.AnimationComponents.UpwardFlipHeaderImage
import org.jetbrains.compose.resources.painterResource
import split.composeapp.generated.resources.HistoryPic
import split.composeapp.generated.resources.Res
import split.composeapp.generated.resources.menu_icon_sec
import split.composeapp.generated.resources.nunito_semibold_1


@Composable
fun MyRewardsUpperComposable() {
    val colors = LocalSplitColors.current
    val isDarkTheme = isSystemInDarkTheme()
    val lazyListState = rememberLazyListState()

    // Add search state
    var searchQuery by remember { mutableStateOf("") }

    val rewardList = remember {
        listOf(
            Reward("1 January 2024", "$8.19"),
            Reward("2 January 2024", "$8.19"),
            Reward("3 January 2024", "$8.19"),
            Reward("4 January 2024", "$8.19"),
            Reward("5 January 2024", "$8.19"),
            Reward("6 January 2024", "$8.19"),
            Reward("7 January 2024", "$8.19"),
            Reward("2 January 2024", "$8.19"),
            Reward("3 January 2024", "$8.19"),
            Reward("4 January 2024", "$8.19"),
            Reward("5 January 2024", "$8.19"),
            Reward("6 January 2024", "$8.19"),
            Reward("7 January 2024", "$8.19")
        )
    }

    // Filter based on search query (both date and amount)
    val filteredRewards = remember(searchQuery, rewardList) {
        if (searchQuery.isEmpty()) {
            rewardList
        } else {
            rewardList.filter { reward ->
                reward.date?.contains(searchQuery, ignoreCase = true) == true ||
                        reward.amount?.contains(searchQuery, ignoreCase = true) == true
            }
        }
    }

    LazyColumn(
        state = lazyListState,
        content = {
            // Header image
            item {
                UpwardFlipHeaderImage(
                    Res.drawable.HistoryPic,
                    lazyListState
                )
            }

            // Search bar
            item {
                RewardSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onClearQuery = { searchQuery = "" },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Show no results message if needed
            if (filteredRewards.isEmpty() && searchQuery.isNotEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No rewards found matching \"$searchQuery\"",
                            style = getAppTypography(isDarkTheme).bodyMedium,
                            color = colors.textSecondary
                        )
                    }
                }
            }

            // Reward items
            items(filteredRewards) { content ->
                RewardComposable(
                    date = content.date.toString(),
                    amount = content.amount.toString()
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = colors.textSecondary.copy(alpha = 0.2f)
                )
            }
        }
    )
}

// SearchBar component
@Composable
fun RewardSearchBar(
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
                text = "Search by date or amount",
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
private fun RewardComposable(date: String, amount: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(Res.drawable.menu_icon_sec),
                contentDescription = "menu icon",
                modifier = Modifier
                    .size(25.dp)
            )
            Column(
                modifier = Modifier
                    .padding(12.dp)
            ) {
                Text(
                    text = date,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(org.jetbrains.compose.resources.Font(Res.font.nunito_semibold_1, weight = FontWeight.Normal)),
                )
                Text(
                    text = "Reward",
                    fontFamily = FontFamily(org.jetbrains.compose.resources.Font(Res.font.nunito_semibold_1, weight = FontWeight.Normal)),
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                onPayClickOnHistoryItem(
                    upiId = "avishisht@paytm",
                    amount = 100,
                    currency = "INR"
                )
            }) {
                Icon(Icons.Default.ArrowBack, "Back")
            }
            Text(
                text = amount,
                fontSize = 16.sp,
                fontFamily = FontFamily(org.jetbrains.compose.resources.Font(Res.font.nunito_semibold_1, weight = FontWeight.Normal)),
                color = Color(0xFF008030)
            )
        }
    }
}

fun onPayClickOnHistoryItem(upiId: String, amount: Int, currency: String) {
    TODO("Not yet implemented")
}

data class Reward (
    val date: String? = null,
    val amount: String? = null
)