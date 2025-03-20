import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import com.falcon.split.presentation.DarkErrorRed
import com.falcon.split.presentation.DarkPrimary
import com.falcon.split.presentation.ErrorRed
import com.falcon.split.presentation.LocalSplitColors
import com.falcon.split.presentation.ThemePurple
import com.falcon.split.presentation.ThemeSwitcher
import com.falcon.split.presentation.getAppTypography
import com.falcon.split.toggleDarkTheme
import com.falcon.split.utils.EmailUtils
import com.falcon.split.utils.OpenLink
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navController: NavHostController,
    onNavigateBack: () -> Unit,
    emailUtils: EmailUtils,
    prefs: DataStore<Preferences>,
    darkTheme: MutableState<Boolean>,
) {
    val colors = LocalSplitColors.current
    val isDarkTheme = isSystemInDarkTheme()

    //For delete Account
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
                        style = getAppTypography(isDarkTheme).titleLarge,
                        color = colors.textPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            "Back",
                            tint = colors.textPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.backgroundSecondary,
                    titleContentColor = colors.textPrimary
                )
            )
        },
        containerColor = colors.backgroundPrimary
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(colors.backgroundPrimary)
        ) {
            items(1){
                SettingType("General")
                ThemeOption(
                    prefs = prefs,
                    darkTheme = darkTheme
                )
                SettingOption(
                    "Contact Us",
                    "Contact our team",
                    {
                        emailUtils.sendEmail(
                            to = "deeptanshushuklaji@gmail.com",
                            subject = "Regarding App Split",
                        )
                    }
                )
                SettingOption("Payment Account","Change your current payment account",{})
                SettingOption(
                    "Delete Account",
                    "Delete your account",
                    {showDeleteDialog = true},
                    isDeleteOption = true
                )
                SettingType("Developer")
                SettingOption("Resource Used","Resources used for app",{
                    OpenLink.openLink("https://sites.google.com/view/ggsipu-notices/resources-used")
                })
                SettingOption("Bug Report","Report bugs here",{
                    emailUtils.sendEmail(
                        to = "deeptanshushuklaji@gmail.com",
                        subject = "Bug Report For Split App",
                    )
                })
                SettingOption("Terms & Condition","Terms and Condition for using",{
                    OpenLink.openLink("https://sites.google.com/view/split-app/terms-conditions")
                })
                SettingOption("Privacy Policy","All the privacy policies",{
                    OpenLink.openLink("https://sites.google.com/view/split-app/home")
                })
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
    title: String
) {
    val colors = LocalSplitColors.current
    val accentColor = if (isSystemInDarkTheme()) DarkPrimary else ThemePurple

    Text(
        title,
        fontSize = 12.sp,
        style = getAppTypography().titleSmall,
        color = accentColor,
        modifier = Modifier
            .padding(15.dp)
    )
}

@Composable
fun ThemeOption(
    modifier: Modifier = Modifier,
    prefs: DataStore<Preferences>,
    darkTheme: MutableState<Boolean>,
) {
    val colors = LocalSplitColors.current
    val isDarkTheme = isSystemInDarkTheme()
    val scope = rememberCoroutineScope()

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier.background(colors.backgroundPrimary)
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
                    "Theme",
                    style = getAppTypography(isDarkTheme).titleMedium,
                    color = colors.textPrimary
                )
                Text(
                    "Toggle Theme",
                    style = getAppTypography(isDarkTheme).titleSmall,
                    color = colors.textSecondary
                )
            }
            ThemeSwitcher(
                size = 50.dp,
                padding = 5.dp,
                onClick = {
                    scope.launch {
                        darkTheme.value = !darkTheme.value
                        toggleDarkTheme(prefs)
                    }
                },
                darkTheme = darkTheme.value
            )
        }
    }
}

@Composable
fun SettingOption(
    settingText: String,
    description: String,
    onClick: () -> Unit,
    isDeleteOption: Boolean = false
) {
    val colors = LocalSplitColors.current
    val isDarkTheme = isSystemInDarkTheme()

    // Determine text color for the main setting text
    val textColor = when {
        isDeleteOption -> if (isDarkTheme) DarkErrorRed else ErrorRed
        else -> colors.textPrimary
    }

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier.background(colors.backgroundPrimary)
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
                    style = getAppTypography(isDarkTheme).titleMedium,
                    color = textColor
                )
                Text(
                    description,
                    style = getAppTypography(isDarkTheme).titleSmall,
                    color = colors.textSecondary
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Open",
                modifier = Modifier.size(24.dp),
                tint = colors.textSecondary
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
    val colors = LocalSplitColors.current
    val isDarkTheme = isSystemInDarkTheme()
    val deleteColor = if (isDarkTheme) DarkErrorRed else ErrorRed

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = colors.cardBackground,
            title = {
                Text(
                    "Delete Account",
                    color = colors.textPrimary,
                    style = getAppTypography(isDarkTheme).titleMedium
                )
            },
            text = {
                Text(
                    "Are you sure you want to delete your account? This action cannot be undone.",
                    style = getAppTypography(isDarkTheme).titleSmall,
                    color = colors.textSecondary
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
                        color = deleteColor,
                        style = getAppTypography(isDarkTheme).titleSmall
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(
                        "Cancel",
                        color = colors.textPrimary,
                        style = getAppTypography(isDarkTheme).titleSmall
                    )
                }
            }
        )
    }
}