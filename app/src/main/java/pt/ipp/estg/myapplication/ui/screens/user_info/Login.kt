package pt.ipp.estg.myapplication.ui.screens.user_info

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.shashank.sony.fancytoastlib.FancyToast
import pt.ipp.estg.myapplication.R
import pt.ipp.estg.myapplication.connection.ConnectivityObserver
import pt.ipp.estg.myapplication.helper.Validations
import pt.ipp.estg.myapplication.models.GeneralViewModels
import pt.ipp.estg.myapplication.models.database.coupon.CouponViewModels
import pt.ipp.estg.myapplication.ui.screens.vehicle.VehicleViewModels
import pt.ipp.estg.myapplication.models.firebase.AuthViewModel
import pt.ipp.estg.myapplication.ui.screens.locations.PreferencesViewModel
import pt.ipp.estg.myapplication.ui.screens.ui_components.DataTypes
import pt.ipp.estg.myapplication.ui.screens.ui_components.LoginPopUp
import pt.ipp.estg.myapplication.ui.screens.ui_components.NormalTextField
import pt.ipp.estg.myapplication.ui.screens.ui_components.PasswordTextField
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.*

@Preview
@Composable
fun PreviewLogin() {
    val navController: NavHostController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val generalViewModels: GeneralViewModels = viewModel()
    val vehicleViewModels: VehicleViewModels = viewModel()
    val couponViewModel: CouponViewModels = viewModel()
    val preferencesViewModel: PreferencesViewModel = viewModel()
    Login(
        navController,
        authViewModel,
        generalViewModels,
        vehicleViewModels,
        couponViewModel,
        preferencesViewModel
    )
}

@Composable
fun Login(
    navController: NavController,
    authViewModel: AuthViewModel,
    generalViewModels: GeneralViewModels,
    vehicleViewModels: VehicleViewModels,
    couponViewModel: CouponViewModels,
    preferencesViewModel: PreferencesViewModel
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val dialogIsOpen = remember { mutableStateOf(false) }
    val vehicleChoice = remember { mutableStateOf(DataTypes.CLOUD) }
    val couponsChoice = remember { mutableStateOf(DataTypes.CLOUD) }
    val locationsChoice = remember { mutableStateOf(DataTypes.CLOUD) }


    val ctx = LocalContext.current

    val loginUiState = authViewModel.loginUiState

    val currentUser = authViewModel.currentUser.observeAsState()

    LaunchedEffect(loginUiState.isSuccessLogin) {
        if (loginUiState.isSuccessLogin) {
            dialogIsOpen.value = true
        }
    }

    Log.d("auth", "user login ${currentUser.value?.email}")
    Log.d("auth", "isLogged login {${loginUiState.isSuccessLogin}}")


    if (dialogIsOpen.value) {
        LoginPopUp(
            dialogIsOpen = dialogIsOpen,
            vehicleChoice = vehicleChoice,
            couponsChoice = couponsChoice,
            locationsChoice = locationsChoice,
            onCancel = { navController.navigate("map") }
        ) {
            navController.navigate("map")
            if (generalViewModels.status.value != ConnectivityObserver.Status.Available) {
                FancyToast.makeText(
                    ctx,
                    ctx.resources.getString(R.string.no_internet_connection),
                    FancyToast.LENGTH_LONG,
                    FancyToast.ERROR,
                    false
                ).show()
            } else {
                if (vehicleChoice.value == DataTypes.CLOUD) {
                    vehicleViewModels.getCloudVehicles(
                        email = currentUser.value!!.email,
                        context = ctx,
                    )
                }
                if (couponsChoice.value == DataTypes.CLOUD) {
                    couponViewModel.getCloudCoupons(
                        email = currentUser.value!!.email,
                        context = ctx,
                    )
                }
                if (locationsChoice.value == DataTypes.CLOUD) {
                    preferencesViewModel.getCloudLocations(
                        email = currentUser.value!!.email,
                        context = ctx,
                    )
                }
            }
        }
    }

    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onPrimary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp)
                .height(80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.splash_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .weight(1f)
                    .size(150.dp)
            )
        }
        Card(
            Modifier
                .weight(2f)
                .padding(16.dp)
                .fillMaxWidth(),
            backgroundColor = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(30.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 25.dp, vertical = 25.dp)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                TitleSmall(
                    text = stringResource(id = R.string.app_name),
                    color = MaterialTheme.colorScheme.background
                )
                Spacer(modifier = Modifier.height(15.dp))
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    //EMAIL
                    LabelMedium(
                        text = stringResource(id = R.string.email),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    NormalTextField(
                        stringState = email,
                        modifier = Modifier.fillMaxWidth(),
                        errorMsg = stringResource(id = R.string.bad_email),
                        validation = { str -> Validations.validateEmail(str) },
                        colorBackground = MaterialTheme.colorScheme.onPrimary,
                        textColor = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    //PASSWORD
                    LabelMedium(
                        text = stringResource(id = R.string.password),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    PasswordTextField(
                        stringState = password,
                        modifier = Modifier.fillMaxWidth(),
                        colorBackground = MaterialTheme.colorScheme.onPrimary,
                        textColor = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.padding(20.dp))

                    //LOGIN BUTTON
                    if (loginUiState.isLoading) {
                        Column(
                            Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                        }
                    } else {
                        Button(
                            onClick = {
                                if (!Validations.validateEmail(email.value)) {
                                    FancyToast.makeText(
                                        ctx,
                                        ctx.resources.getString(R.string.bad_email),
                                        FancyToast.LENGTH_LONG,
                                        FancyToast.ERROR,
                                        false
                                    ).show()
                                } else {
                                    if (generalViewModels.status.value != ConnectivityObserver.Status.Available) {
                                        FancyToast.makeText(
                                            ctx,
                                            ctx.resources.getString(R.string.no_internet_connection),
                                            FancyToast.LENGTH_LONG,
                                            FancyToast.ERROR,
                                            false
                                        ).show()
                                    } else {
                                        authViewModel.loginUser(
                                            ctx,
                                            email.value,
                                            password.value
                                        ) {
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .height(40.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colorScheme.onPrimary,
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            LabelMedium(
                                text = stringResource(id = R.string.login),
                                color = MaterialTheme.colorScheme.primary,
                                weight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.padding(5.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LabelMedium(
                            text = stringResource(id = R.string.without_account),
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                        TextButton(onClick = { navController.navigate("user_registration") }) {
                            LabelMedium(
                                text = stringResource(id = R.string.create_account),
                                color = MaterialTheme.colorScheme.primaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}