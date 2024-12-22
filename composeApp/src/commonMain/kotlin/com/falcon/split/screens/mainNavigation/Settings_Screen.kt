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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.falcon.split.ErrorRed
import com.falcon.split.ThemePurple
import com.falcon.split.utils.EmailUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navController: NavHostController,
    onNavigateBack:() -> Unit,
    emailUtils: EmailUtils
) {
    //For delete Account
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(1){
                settingType("General")
                SettingOption(
                    "Contact Us",
                    "Contact our team",
                    {
                        emailUtils.sendEmail(
                            to = "deeptanshushuklaji@gmail.com",
                            subject = "Support Request - Split App",
                            body = "Hello, I need assistance with..."
                        )
                    }
                )
                SettingOption("Theme","Change the theme of app",{navController.navigate("ThemeChangeScreen")})
                SettingOption("Payment Account","Change your current payment account",{})
                SettingOption(
                    "Delete Account",
                    "Delete your account",
                    {showDeleteDialog = true}
                )
                settingType("Developer")
                SettingOption("Resource Used","Resources used for app",{})
                SettingOption("Bug Report","Report bugs here",{})
                SettingOption("Terms & Condition","Terms and Condition for using",{})
                SettingOption("Privacy Poicy","All the privacy policies",{})
            }
        }
        DeleteAccountDialog(
            showDeleteDialog,
            onDismiss = {showDeleteDialog = false},
            onConfirmDelete = {}
        )
    }
}

@Composable
fun settingType(
    title : String
) {
    Text(
        title,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = ThemePurple,
        modifier = Modifier
            .padding(15.dp)
    )
}

@Composable
fun SettingOption(
//    modifier: Modifier = Modifier,
    settingText : String,
    description : String,
    onClick:()->Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    settingText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color =
                    if(settingText == "Delete Account"){
                        ErrorRed
                    }
                    else{
                        Color.Black
                    }
                )
                Text(
                    description,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Open",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}


@Composable
fun DeleteAccountDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    "Delete Account",
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
            },
            text = {
                Text("Are you sure you want to delete your account? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirmDelete()
                        onDismiss()
                    }
                ) {
                    Text("Delete", color = ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = Color.Black)
                }
            }
        )
    }
}