import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.NavHostController
import com.falcon.split.Presentation.ErrorRed
import com.falcon.split.Presentation.ThemePurple
import com.falcon.split.Presentation.ThemeSwitcher
import com.falcon.split.Presentation.getAppTypography
import com.falcon.split.Presentation.screens.mainNavigation.Routes
import com.falcon.split.isDarkThemeEnabled
import com.falcon.split.toggleDarkTheme
import com.falcon.split.utils.EmailUtils
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navController: NavHostController,
    onNavigateBack: () -> Unit,
    emailUtils: EmailUtils,
    prefs: DataStore<Preferences>,
) {
    //For delete Account
    var showDeleteDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    "Settings",
                    style = getAppTypography().titleLarge
                ) },
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
                // TODO: FIX THIS ThemeSwitcher
                ThemeSwitcher(
                    size = 50.dp,
                    padding = 5.dp,
                    onClick = {
                        scope.launch {
                            toggleDarkTheme(prefs)
                        }
                    },
                    darkTheme = runBlocking {
                        isDarkThemeEnabled(prefs)
                    }
                )
                SettingType("General")
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
                SettingOption("Theme","Change the theme of app",{navController.navigate(Routes.THEME_CHANGE_SCREEN.name)})
                SettingOption("Payment Account","Change your current payment account",{})
                SettingOption(
                    "Delete Account",
                    "Delete your account",
                    {showDeleteDialog = true}
                )
                SettingType("Developer")
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
fun SettingType(
    title : String
) {
    Text(
        title,
        fontSize = 12.sp,
        style = getAppTypography().titleSmall,
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
                    style = getAppTypography().titleMedium,
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
                    style = getAppTypography().titleSmall,
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
                    style = getAppTypography().titleMedium
                )
            },
            text = {
                Text(
                    "Are you sure you want to delete your account? This action cannot be undone.",
                    style = getAppTypography().titleSmall
                    )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirmDelete()
                        onDismiss()
                    }
                ) {
                    Text(
                        "Delete",
                        color = ErrorRed,
                        style = getAppTypography().titleSmall
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(
                        "Cancel",
                        color = Color.Black,
                        style = getAppTypography().titleSmall
                    )
                }
            }
        )
    }
}