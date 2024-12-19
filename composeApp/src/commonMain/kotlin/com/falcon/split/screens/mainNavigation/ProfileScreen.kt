package com.falcon.split.screens.mainNavigation


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.falcon.split.ClipboardManager
import com.falcon.split.data.network.models.UserModel
import com.falcon.split.getUserAsUserModel
import org.jetbrains.compose.resources.painterResource
import split.composeapp.generated.resources.Res
import split.composeapp.generated.resources.copy_icon
import split.composeapp.generated.resources.nunito_bold_1
import split.composeapp.generated.resources.picture_preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    prefs: DataStore<Preferences>,
    onSignOut: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier

                .fillMaxWidth()
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.width(10.dp))
            androidx.compose.material3.Text(
                text = "Info",
                fontSize = 21.sp,
                fontFamily = FontFamily(org.jetbrains.compose.resources.Font(Res.font.nunito_bold_1, weight = FontWeight.Normal, style = FontStyle.Normal)),
            )
        }
        TextWithBorder(headingValue = "Name", descriptionValue = userModel?.name?: "INVALID USER")
        TextWithBorder(headingValue = "Email", descriptionValue = userModel?.email?: "INVALID USER")
        TextWithBorderEditable(headingValue = "UPI ID", descriptionValue = userModel?.upiId?: "billi@paytm")
        TextWithBorderAndCopyIcon("User ID", userModel?.token ?: "INVALID USER ID")
        Spacer(modifier = Modifier.height(16.dp))
        androidx.compose.material3.Button(
            onClick = onSignOut,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            // State to hold the fetched UserModel
            var userModel by remember { mutableStateOf<UserModel?>(null) }

            // LaunchedEffect to fetch UserModel once when the composable is first composed
            LaunchedEffect(Unit) {
                userModel = getUserAsUserModel(prefs)
            }
            // Check if the profile image is available, otherwise show a placeholder
            AsyncImage(
                model = userModel?.profileImageUrl ?: Res.drawable.picture_preview, // Show placeholder if no image URL
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.width(10.dp))
                androidx.compose.material3.Text(
                    text = "Info",
                    fontSize = 21.sp,
                    fontFamily = FontFamily(org.jetbrains.compose.resources.Font(Res.font.nunito_bold_1, weight = FontWeight.Normal, style = FontStyle.Normal)),
                )
            }
            TextWithBorder(headingValue = "Name", descriptionValue = userModel?.name?: "INVALID USER")
            TextWithBorder(headingValue = "Email", descriptionValue = userModel?.email?: "INVALID USER")
            TextWithBorderAndCopyIcon("User ID", userModel?.token ?: "INVALID USER ID")
            Spacer(modifier = Modifier.height(16.dp))
            androidx.compose.material3.Button(
                onClick = onSignOut,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
            ) {
                androidx.compose.material3.Text(
                    text = "Sign Out",
                )
            }
        }
    }
}


@Composable
fun TextWithBorder(headingValue: String, descriptionValue: String){
    var mSelectedText by remember(descriptionValue) { mutableStateOf(descriptionValue) }
    var mExpanded by remember { mutableStateOf(false) }
    var mTextFieldSize by remember { mutableStateOf(Size.Zero)}
    Column(
        Modifier
            .padding(10.dp, 5.dp)
    ) {
        OutlinedTextField(
            readOnly = true,
            value = mSelectedText.toString(),
            onValueChange = {
                mSelectedText = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    // This value is used to assign to
                    // the DropDown the same width
                    mTextFieldSize = coordinates.size.toSize()
                }
            ,
            label = {Text(headingValue, modifier = Modifier
                .clickable {
                    mExpanded = !mExpanded
                })}
        )
    }
}

@Composable
fun TextWithBorderEditable(headingValue: String, descriptionValue: String){
    var mSelectedText by remember(descriptionValue) { mutableStateOf(descriptionValue) }
    var mExpanded by remember { mutableStateOf(false) }
    var mTextFieldSize by remember { mutableStateOf(Size.Zero)}
    Column(
        Modifier
            .padding(10.dp, 5.dp)
    ) {
        OutlinedTextField(
            readOnly = false,
            value = mSelectedText.toString(),
            onValueChange = {
                mSelectedText = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    // This value is used to assign to
                    // the DropDown the same width
                    mTextFieldSize = coordinates.size.toSize()
                }
            ,
            trailingIcon = {
                Icon(
                    painter = painterResource(Res.drawable.copy_icon),
                    contentDescription = "Copy",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            ClipboardManager.copyToClipboard(descriptionValue)
//                            clipboardManager.copyText(descriptionValue)
                            // TODO: Add A Toast Or Something Here To Notify That Text Is Copies, maybe consider using snackbar
                        },
                )
            },
            label = {Text(headingValue, modifier = Modifier
                .clickable {
                    mExpanded = !mExpanded
                })}
        )
    }
}

@Composable
fun TextWithBorderAndCopyIcon(
    headingValue: String,
    descriptionValue: String,
){
    var mSelectedText by remember(descriptionValue) { mutableStateOf(descriptionValue) }
    var mExpanded by remember { mutableStateOf(false) }
    var mTextFieldSize by remember { mutableStateOf(Size.Zero)}
    Column(
        Modifier
            .padding(10.dp, 5.dp)
    ) {
        OutlinedTextField(
            readOnly = true,
            value = mSelectedText.toString(),
            onValueChange = {
                mSelectedText = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    // This value is used to assign to
                    // the DropDown the same width
                    mTextFieldSize = coordinates.size.toSize()
                }
            ,
            trailingIcon = {
                Icon(
                    painter = painterResource(Res.drawable.copy_icon),
                    contentDescription = "Copy",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            ClipboardManager.copyToClipboard(descriptionValue)
//                            clipboardManager.copyText(descriptionValue)
                            // TODO: Add A Toast Or Something Here To Notify That Text Is Copies, maybe consider using snackbar
                        },
                )
            },
            label = {Text(headingValue, modifier = Modifier
                .clickable {
                    mExpanded = !mExpanded
                })}
        )
    }
}