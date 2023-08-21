package pt.ipp.estg.myapplication.ui.screens.home

import android.app.Activity
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import pt.ipp.estg.myapplication.R
import pt.ipp.estg.myapplication.models.firebase.AuthViewModel
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.LabelLarge
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.LabelSmall
import pt.ipp.estg.myapplication.ui.screens.vehicle.PageToShow

@Preview
@Composable
fun PreviewNavBar() {
    val navController: NavHostController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    NavBar(navController, authViewModel, scope, scaffoldState)
}

@Composable
fun NavBar(
    navController: NavController,
    authViewModel: AuthViewModel,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState
) {
    val isToShowAboutUs = remember { mutableStateOf(false) }

    //alter the back button function
    when (isToShowAboutUs.value) {
        true -> BackHandler(enabled = true) {
            isToShowAboutUs.value = false
        }
        else -> {}
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        if (!isToShowAboutUs.value) {
            NavBarComponent(
                navController = navController,
                scope = scope,
                scaffoldState = scaffoldState,
                isToShowAboutUs = isToShowAboutUs,
                authViewModel = authViewModel
            )
        } else {
            AboutUs(
                isToShowAboutUs = isToShowAboutUs,
                windowWidthSizeClass = WindowWidthSizeClass.Compact
            )
        }
    }
}

@Preview
@Composable
fun PreviewNavBarComponent() {
    val navController: NavHostController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val isToShowAboutUs = remember { mutableStateOf(false) }
    NavBarComponent(navController, scope, scaffoldState, isToShowAboutUs, authViewModel)
}

@Composable
fun NavBarComponent(
    navController: NavController,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    isToShowAboutUs: MutableState<Boolean>,
    authViewModel: AuthViewModel
) {
    val currentUser = authViewModel.currentUser.observeAsState()
    val loginUiState = authViewModel.loginUiState

    Log.d("auth", "user NavBar ${currentUser.value?.email}")
    Log.d("auth", "user fire ${Firebase.auth.currentUser}")
    Log.d("auth", "isLogged NavBar {${loginUiState.isSuccessLogin}}")

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),

            horizontalArrangement = Arrangement.SpaceBetween

        ) {
            //SETTINGS ICON
            IconButton(
                modifier = Modifier
                    .size(50.dp)
                    .padding(top = 20.dp),
                onClick = {
                    navController.navigate("settings");
                    scope.launch { scaffoldState.drawerState.close() }
                }
            ) {
                Image(
                    modifier = Modifier.size(40.dp),
                    painter = painterResource(id = R.drawable.settings),
                    contentDescription = ""
                )
            }

            //USER AVATAR
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(70.dp))
                if (currentUser.value?.email != "") {
                    IconButton(
                        modifier = Modifier
                            .size(64.dp)
                            .clickable {
                                navController.navigate("user_info")
                                scope.launch { scaffoldState.drawerState.close() }
                            },
                        onClick = {
                            navController.navigate("user_info")
                            scope.launch { scaffoldState.drawerState.close() }
                        }
                    ) {
                        Image(
                            modifier = Modifier.size(90.dp),
                            painter = painterResource(
                                R.drawable.avatar_init
                            ),
                            contentDescription = ""
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    LabelSmall(
                        text =
                        stringResource(id = R.string.see_profile),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        modifier = Modifier.width(120.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary),
                        shape = RoundedCornerShape(15.dp),
                        onClick = {
                            authViewModel.signOut()
                        }
                    ) {
                        LabelSmall(
                            text =
                            stringResource(id = R.string.log_out),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                } else {
                    IconButton(
                        modifier = Modifier
                            .size(64.dp),
                        onClick = {
                            navController.navigate("user_login")
                            scope.launch { scaffoldState.drawerState.close() }
                        }
                    ) {
                        Image(
                            modifier = Modifier.size(90.dp),
                            painter = painterResource(
                                R.drawable.ask
                            ),
                            contentDescription = ""
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    LabelSmall(
                        text =
                        stringResource(id = R.string.no_profile),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary),
                        shape = RoundedCornerShape(15.dp),
                        onClick = {
                            navController.navigate("user_login")
                            scope.launch { scaffoldState.drawerState.close() }
                        }
                    ) {
                        LabelSmall(
                            text =
                            stringResource(id = R.string.login_or_register),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            //TURN OFF
            val activity = (LocalContext.current as? Activity)
            IconButton(
                modifier = Modifier.size(64.dp),
                onClick = {
                    activity?.finish()
                }
            ) {
                Image(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = R.drawable.power_off),
                    contentDescription = ""
                )
            }

        }
        Spacer(modifier = Modifier.height(25.dp))
        //BUTTONS
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 50.dp)
        ) {

            NavBarButton(
                image = painterResource(id = R.drawable.map),
                title = stringResource(id = R.string.map),
                navController = navController,
                root = "map",
                scope = scope,
                scaffoldState = scaffoldState
            )
            Spacer(modifier = Modifier.height(10.dp))

            NavBarButton(
                image = painterResource(id = R.drawable.coupon),
                title = stringResource(id = R.string.my_coupons),
                root = "my_coupons_screen",
                navController = navController,
                scope = scope,
                scaffoldState = scaffoldState
            )
            Spacer(modifier = Modifier.height(10.dp))

            NavBarButton(
                image = painterResource(id = R.drawable.park),
                title = stringResource(id = R.string.save_location),
                root = "save_location_screen",
                navController = navController,
                scope = scope,
                scaffoldState = scaffoldState
            )
            Spacer(modifier = Modifier.height(10.dp))

            NavBarButton(
                image = painterResource(id = R.drawable.pin),
                title = stringResource(id = R.string.nav_bar_pin_locations),
                root = "location_home_screen",
                navController = navController,
                scope = scope,
                scaffoldState = scaffoldState
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                isToShowAboutUs.value = true
            }
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 30.dp, vertical = 10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.width(10.dp))
            LabelSmall(
                text = stringResource(id = R.string.about_us),
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun NavBarComponentTablet(
    navController: NavController,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    isToShowAboutUs: MutableState<Boolean>,
    authViewModel: AuthViewModel
) {
    val currentUser = authViewModel.currentUser.observeAsState()
    val loginUiState = authViewModel.loginUiState

    Log.d("auth", "user NavBar ${currentUser.value?.email}")
    Log.d("auth", "user fire ${Firebase.auth.currentUser}")
    Log.d("auth", "isLogged NavBar {${loginUiState.isSuccessLogin}}")

    Column {
        Row(
            modifier = Modifier
                .width(255.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            //SETTINGS ICON
            IconButton(
                modifier = Modifier
                    .size(50.dp)
                    .padding(top = 20.dp),
                onClick = {
                    navController.navigate("settings");
                    scope.launch { scaffoldState.drawerState.close() }
                }
            ) {
                Image(
                    modifier = Modifier.size(40.dp),
                    painter = painterResource(id = R.drawable.settings),
                    contentDescription = ""
                )
            }

            //USER AVATAR
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(70.dp))
                if (currentUser.value?.email != "") {
                    IconButton(
                        modifier = Modifier
                            .size(64.dp)
                            .clickable {
                                navController.navigate("user_info")
                                scope.launch { scaffoldState.drawerState.close() }
                            },
                        onClick = {
                            navController.navigate("user_info")
                            scope.launch { scaffoldState.drawerState.close() }
                        }
                    ) {
                        Image(
                            modifier = Modifier.size(90.dp),
                            painter = painterResource(
                                R.drawable.avatar_init
                            ),
                            contentDescription = ""
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    LabelSmall(
                        text =
                        stringResource(id = R.string.see_profile),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        modifier = Modifier.width(120.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary),
                        shape = RoundedCornerShape(15.dp),
                        onClick = {
                            authViewModel.signOut()
                        }
                    ) {
                        LabelSmall(
                            text =
                            stringResource(id = R.string.log_out),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(30.dp))
                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary),
                        shape = RoundedCornerShape(15.dp),
                        onClick = {
                            navController.navigate("user_login")
                            scope.launch { scaffoldState.drawerState.close() }
                        }
                    ) {
                        LabelSmall(
                            text =
                            stringResource(id = R.string.login_or_register),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            //TURN OFF
            val activity = (LocalContext.current as? Activity)
            IconButton(
                modifier = Modifier.size(64.dp),
                onClick = {
                    activity?.finish()
                }
            ) {
                Image(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = R.drawable.power_off),
                    contentDescription = ""
                )
            }
        }
        Spacer(modifier = Modifier.height(25.dp))

        //BUTTONS
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(255.dp)
                .padding(horizontal = 10.dp, vertical = 50.dp)
        ) {

            NavBarButton(
                image = painterResource(id = R.drawable.map),
                title = stringResource(id = R.string.map),
                navController = navController,
                root = "map",
                scope = scope,
                scaffoldState = scaffoldState
            )
            Spacer(modifier = Modifier.height(10.dp))

            NavBarButton(
                image = painterResource(id = R.drawable.coupon),
                title = stringResource(id = R.string.my_coupons),
                root = "my_coupons_screen",
                navController = navController,
                scope = scope,
                scaffoldState = scaffoldState
            )
            Spacer(modifier = Modifier.height(10.dp))

            NavBarButton(
                image = painterResource(id = R.drawable.park),
                title = stringResource(id = R.string.save_location),
                root = "save_location_screen",
                navController = navController,
                scope = scope,
                scaffoldState = scaffoldState
            )
            Spacer(modifier = Modifier.height(10.dp))

            NavBarButton(
                image = painterResource(id = R.drawable.pin),
                title = stringResource(id = R.string.nav_bar_pin_locations),
                root = "location_home_screen",
                navController = navController,
                scope = scope,
                scaffoldState = scaffoldState
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .width(255.dp)
                    .fillMaxHeight()
                    .clickable {
                        isToShowAboutUs.value = true
                    }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 30.dp, vertical = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    LabelSmall(
                        text = stringResource(id = R.string.about_us),
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewNavBarButton() {
    val navController: NavHostController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    NavBarButton(
        image = painterResource(id = R.drawable.pin),
        title = stringResource(id = R.string.nav_bar_pin_locations),
        root = "location_home_screen",
        navController = navController,
        scope = scope,
        scaffoldState = scaffoldState
    )
}

@Composable
fun NavBarButton(
    image: Painter,
    title: String,
    navController: NavController,
    root: String,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState
) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary),
        shape = RoundedCornerShape(15.dp),
        onClick = {
            navController.navigate(root);
            scope.launch { scaffoldState.drawerState.close() }
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(), horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(30.dp),
                painter = image,
                contentDescription = ""
            )
            Spacer(modifier = Modifier.width(15.dp))
            LabelSmall(
                text = title,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}