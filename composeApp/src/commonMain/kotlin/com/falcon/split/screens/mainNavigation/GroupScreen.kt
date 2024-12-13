package com.falcon.split.screens.mainNavigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.NavHostController
import com.falcon.split.ErrorRed
import com.falcon.split.MainViewModel
import com.falcon.split.SuccessGreen
import com.falcon.split.ThemeGrey
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import split.composeapp.generated.resources.Res
import split.composeapp.generated.resources.group_icon_outlined

@Composable
fun GroupScreen(
    onNavigate: (rootName: String) -> Unit,
    prefs: DataStore<Preferences>,
    newsViewModel: MainViewModel,
    snackBarHostState: SnackbarHostState,
    navControllerMain: NavHostController
) {
    GroupCardList()
}



@Composable
fun GroupCard(
    groupName: String,
    imageRes: ImageVector,
    isSettled: Boolean,
    modifier: Modifier = Modifier
        .padding(top = 10.dp)
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = ThemeGrey,
            disabledContainerColor = Color(0xFFF2E5FF)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Image
                Image(
                    imageVector = imageRes,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(end = 12.dp),
                    contentScale = ContentScale.Fit
                )

                // Text Content
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = groupName,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF1E293B)  // Dark slate for title
                    )
                    if(isSettled == true){
                        Text(
                            text = "Settled",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Normal,
                            color = SuccessGreen
                        )
                    }
                    else{
                        Text(
                            text = "Not Settled",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Normal,
                            color = ErrorRed
                        )
                    }
                }
            }
    }
}



@Composable
fun GroupCardList() {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(5){
            GroupCard(
                groupName = "Flat 13012",
                imageRes = Icons.Filled.Person,
                isSettled = true
            )
            GroupCard(
                groupName = "Goa Trip",
                imageRes = Icons.Filled.Person,
                isSettled = false
            )
            GroupCard(
                groupName = "Mumbai event",
                imageRes = Icons.Filled.Person,
                isSettled = true
            )
        }
        // Owed Money Card
    }
}


