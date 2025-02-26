@file:OptIn(ExperimentalCoilApi::class)

package com.falcon.split

import SettingScreen
import ThemeChangeSwitcher
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.crossfade
import coil3.util.DebugLogger
import com.falcon.split.Presentation.LocalSplitColors
import com.falcon.split.Presentation.expense.CreateExpenseViewModel
import com.falcon.split.Presentation.group.CreateGroupViewModel
import com.falcon.split.Presentation.group.GroupViewModel
import com.falcon.split.contact.ContactManager
import com.falcon.split.data.network.ApiClient
import com.falcon.split.data.network.models.UserState
import com.falcon.split.Presentation.screens.WelcomePage
import com.falcon.split.Presentation.screens.mainNavigation.CreateExpense
import com.falcon.split.Presentation.screens.mainNavigation.CreateGroupScreen
import com.falcon.split.Presentation.screens.mainNavigation.GroupDetailsScreen
import com.falcon.split.Presentation.screens.mainNavigation.NavHostMain
import com.falcon.split.Presentation.screens.mainNavigation.ProfileScreen
import com.falcon.split.Presentation.screens.mainNavigation.Routes
import com.falcon.split.Presentation.screens.mainNavigation.navigateTo
import com.falcon.split.data.Repository.ExpenseRepository
import com.falcon.split.data.Repository.GroupRepository
import com.falcon.split.screens.mainNavigation.PaymentScreen
import com.falcon.split.utils.rememberEmailUtils
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import com.mmk.kmpauth.google.GoogleButtonUiContainer
import com.mmk.kmpauth.uihelper.google.GoogleSignInButton
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import split.composeapp.generated.resources.Res
import split.composeapp.generated.resources.nunito_regular_1
import split.composeapp.generated.resources.profile_icon
import split.composeapp.generated.resources.settings_icon

@Composable
@Preview
fun App(
    client: ApiClient,
    prefs: DataStore<Preferences>,
    contactManager: ContactManager? = null,
    onSignOut: (() -> Unit)? = null,
    AndroidProfileScreenComposable: @Composable() ((navController: NavHostController) -> Unit)? = null,
    AndroidSignInComposable: @Composable() ((navController: NavHostController) -> Unit)? = null,
    groupRepository: GroupRepository? = null,
    expenseRepository: ExpenseRepository? = null,
    darkTheme: MutableState<Boolean>?
) {
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val factory = rememberPermissionsControllerFactory()
    val controller = remember(factory) {
        factory.createPermissionsController()
    }
    BindEffect(controller)
    // Request Permission For Notification
    val viewModel = viewModel {
        PermissionsViewModel(controller)
    }

    when(viewModel.notificationPermissionState) {
        PermissionState.Granted -> {
            println("Notification Permission Granted")
        }
        PermissionState.DeniedAlways -> {
            LaunchedEffect(Unit) {
                snackBarHostState.showSnackbar(
                    message = "Notification Permission Denied Permanently",
                    actionLabel = "Settings",
                    duration = SnackbarDuration.Indefinite,  // Make it stay indefinitely
                    withDismissAction = false  // Remove dismiss action
                ).let { result ->
                    when (result) {
                        SnackbarResult.ActionPerformed -> {
                            controller.openAppSettings()
                        }
                        else -> {
                            // Should never reach here since we removed dismiss action
                        }
                    }
                }
            }
        }
        else -> {
            viewModel.provideOrRequestNotificationPermission()
        }
    }
    setSingletonImageLoaderFactory { context ->
        getAsyncImageLoader(context)
    }
    var authReady by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        GoogleAuthProvider.create(
            credentials = GoogleAuthCredentials(
                serverId = "280065041634-08vjbkjqvjmpl24nuplrl47dicvl92v3.apps.googleusercontent.com"
            )
        )
        authReady = true
    }
    val navControllerMain = rememberNavController()
    val newsViewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(client, prefs)
    )

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        }
    ) {
        var startDestination = runBlocking {
            if (getFirebaseUserAsUserModel(prefs) != null) Routes.APP_CONTENT.name else Routes.WELCOME_PAGE.name
        }
//        startDestination = Routes.SETTINGS.name // TODO: Remove Later
        NavHost(navController = navControllerMain, startDestination = startDestination) {
            composable(Routes.PAYMENT_SCREEN.name) {
                PaymentScreen(
                    paymentAmount = 1000,
                    personName = "John Doe",
                    paymentUpiId = "john@okhdfcbank",
                    snackBarHostState = snackBarHostState,
                ) {
                    navControllerMain.popBackStack()
                }
            }
            composable(Routes.WELCOME_PAGE.name) {
                WelcomePage(navControllerMain)
            }
            composable(Routes.SIGN_IN.name) {
                AndroidSignInComposable?.invoke(navControllerMain) // Firebase Based Google Sign-In Android Specific Only
//                GoogleCloudBasedGoogleSignInForKMM(prefs, navControllerMain, authReady, newsViewModel, scope) // Google Cloud Based Google Sign In For KMM, Works In KMM but need to setup separate server for JWT Token Conversion As Google Auth Id Provided By It is Temporary.
            }
            composable(Routes.APP_CONTENT.name) {
                val openUserOptionsMenu = remember { mutableStateOf(false) } // In Future Replace It With Bottom - Sheet
                val groupViewModel = remember { GroupViewModel(groupRepository!!) }
                NavHostMain(
                    client = client,
                    onNavigate = { routeName ->
                        navigateTo(routeName, navControllerMain)
                    },
                    prefs = prefs,
                    openUserOptionsMenu = openUserOptionsMenu,
                    snackBarHostState = snackBarHostState,
                    navControllerMain = navControllerMain,
                    viewModel = groupViewModel
                )
                if (openUserOptionsMenu.value) {
                    OptionMenuPopup(
                        openUserOptionsMenu,
                        navControllerMain
                    )
                }
            }
            composable(Routes.CREATE_GROUP.name) {
                val createGroupViewModel = remember { CreateGroupViewModel(groupRepository!!) }
                CreateGroupScreen(
                    onGroupCreated = { groupId ->
                        scope.launch {
                            snackBarHostState.showSnackbar(
                                message = "Group created successfully",
                                duration = SnackbarDuration.Short
                            )
                        }
                        // Just navigate back to the previous screen instead of popping
                        navControllerMain.navigate(Routes.APP_CONTENT.name) {
                            popUpTo(Routes.APP_CONTENT.name) { inclusive = false }
                        }
                    },
                    onNavigateBack = {
                        navControllerMain.popBackStack()
                    },
                    contactManager = contactManager!!,
                    viewModel = createGroupViewModel!!
                )
            }
            composable(Routes.CREATE_EXPENSE.name) {
                val createExpenseViewModel = remember{ CreateExpenseViewModel(groupRepository!!,expenseRepository!!) }
                CreateExpense(
                    navControllerMain = navControllerMain,
                    onNavigateBack = { navControllerMain.popBackStack() },
                    viewModel = createExpenseViewModel
                )
            }
            composable(
                "create_expense_in_a_group/{groupId}",
                arguments = listOf(navArgument("groupId") { type = NavType.StringType })
            ) {
                val createExpenseViewModel = remember{ CreateExpenseViewModel(groupRepository!!,expenseRepository!!) }
                CreateExpense(
                    navControllerMain = navControllerMain,
                    onNavigateBack = { navControllerMain.popBackStack() },
                    viewModel = createExpenseViewModel
                )
            }
            composable(Routes.PROFILE.name) {
                ProfileScreen(
                    navControllerMain,
                    prefs
                ) {
                    scope.launch {
                        deleteUser(prefs)
                        onSignOut?.invoke()
                        navControllerMain.navigate(Routes.WELCOME_PAGE.name)
                    }
                }
            }
            composable(Routes.SETTINGS.name){
                val emailUtils = rememberEmailUtils()
                SettingScreen(
                    navController = navControllerMain,
                    onNavigateBack = {navControllerMain.popBackStack()},
                    emailUtils = emailUtils,
                    prefs = prefs,
                    darkTheme = darkTheme!!
                )
            }
            composable(Routes.THEME_CHANGE_SCREEN.name){
                var HowIsTheme = remember{ mutableStateOf(false) }
                ThemeChangeSwitcher(isDarkMode = false,{},{})
            }
            composable(
                route = "group_details/{groupId}",
                arguments = listOf(navArgument("groupId") { type = NavType.StringType })
            ) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId") ?: return@composable
                val groupViewModel = remember { GroupViewModel(groupRepository!!) }
                GroupDetailsScreen(
                    groupId = groupId,
                    onNavigateBack = { navControllerMain.popBackStack() },
                    onAddExpense = { groupId ->
                        navControllerMain.navigate("add_expense/$groupId")
                    },
                    navControllerMain = navControllerMain,
                    viewModel = groupViewModel
                )
            }
        }
    }

}

@Deprecated("This method has been deprecated in favor of using Firebase Based Google-SignIn")
@Composable
private fun GoogleCloudBasedGoogleSignInForKMM( // Don't Remove This, More Mentioned At Line 184
    prefs: DataStore<Preferences>,
    navControllerMain: NavHostController,
    authReady: Boolean,
    newsViewModel: MainViewModel,
    scope: CoroutineScope
) {
    LaunchedEffect(Unit) {
        val user = getUserAsUserModel(prefs)
        if (user != null) {
            navControllerMain.navigate(Routes.APP_CONTENT.name)
        }
    }
    val requestSendForGetUserData = remember { mutableStateOf(false) }
    if (authReady) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LottieAnimationView(LottieAnimationSpec("login_animation.json"))
            Spacer(
                modifier = Modifier.height(60.dp)
            )
            GoogleButtonUiContainer(
                onGoogleSignInResult = { googleUser ->
                    newsViewModel.getUserDetailsFromGoogleAuthToken(googleUser?.idToken.toString())
                    println("Google Token:")
                    println(googleUser?.idToken)
                    requestSendForGetUserData.value = true
                }
            ) {
                GoogleSignInButton(
                    onClick = {
                        this.onClick()
                    }
                )
            }
            Spacer(
                modifier = Modifier.height(35.dp)
            )
        }
    }
    if (requestSendForGetUserData.value) {
        val userState by newsViewModel.userDetails.collectAsState()
        when (userState) {
            is UserState.Error -> {
                val error = (userState as UserState.Error).error
                println("ERROR_TAG" + error.name)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    Text(
                        text = "Error loading user: ${error.name}",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            is UserState.Loading -> {
                SignInProgressPopup()
            }

            is UserState.Success -> {
                // Saving the User Details and navigate further
                val user = (userState as UserState.Success).user
                scope.launch {
                    saveUser(prefs, user)
                    navControllerMain.navigate(Routes.APP_CONTENT.name)
                }
            }
        }
    }
}
@Composable
fun OptionMenuPopup(
    openUserOptionsMenu: MutableState<Boolean>,
    navController: NavHostController
) {
    val splitColors = LocalSplitColors.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 25.dp, end = 15.dp),
        contentAlignment = Alignment.TopEnd,
    ) {
        Popup(
            alignment = Alignment.TopEnd,
            onDismissRequest = {
                openUserOptionsMenu.value = false
            }) {
            Card(
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = splitColors.cardBackground
                )
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable {
                                openUserOptionsMenu.value = false
                                navController.navigate(Routes.PROFILE.name)
                            }
                            .padding(vertical = 16.dp, horizontal = 24.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.profile_icon),
                            contentDescription = "Profile Icon",
                            modifier = Modifier
                                .size(24.dp),
                            tint = splitColors.textPrimary
                        )
                        Text(
                            text = "Profile",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = splitColors.textPrimary,
                            modifier = Modifier
                                .padding(start = 8.dp)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable {
                                openUserOptionsMenu.value = false
                                navController.navigate(Routes.SETTINGS.name)
                            }
                            .padding(vertical = 16.dp, horizontal = 24.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.settings_icon),
                            contentDescription = "Settings Icon",
                            modifier = Modifier
                                .size(24.dp),
                            tint = splitColors.textPrimary
                        )
                        Text(
                            text = "Settings",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = splitColors.textPrimary,
                            modifier = Modifier
                                .padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LineWithText() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 64.dp, end = 64.dp)
    ) {
        Divider(
            modifier = Modifier
                .height(1.dp)
                .weight(1f),
            color = Color.LightGray
        )
        Text(
            text = "OR",
            color = Color.LightGray,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Divider(
            modifier = Modifier
                .height(1.dp)
                .weight(1f),
            color = Color.LightGray
        )
    }
}

@Composable
fun SignInProgressPopup(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color.White)
            .fillMaxSize()
            .padding(bottom = 40.dp),
        contentAlignment = Alignment.Center,
    ) {
        Popup(
            alignment = Alignment.Center,
        ) {
            Card(
                Modifier
                    .background(Color.White)
                    .padding(horizontal = 18.dp)
                    .padding(top = 4.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    Modifier
                        .background(Color.White)
                        .padding(36.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.size(180.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        LottieAnimationView(LottieAnimationSpec("google_loading.json"))
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                    Text(
                        text = "Please Wait Via We Fetch Details Linked To Your Account..",
                        fontSize = 20.sp,
                        fontFamily = FontFamily(Font(Res.font.nunito_regular_1, weight = FontWeight.Normal, style = FontStyle.Normal)),
                    )
                }
            }
        }
    }
}

fun getAsyncImageLoader(context: PlatformContext) = ImageLoader.Builder(context).crossfade(true).logger(
    DebugLogger()
).build()