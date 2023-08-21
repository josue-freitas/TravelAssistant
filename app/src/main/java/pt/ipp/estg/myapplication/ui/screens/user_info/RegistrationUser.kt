package pt.ipp.estg.myapplication.ui.screens.user_info

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.ipp.estg.myapplication.R
import pt.ipp.estg.myapplication.helper.Validations
import pt.ipp.estg.myapplication.ui.screens.ui_components.NormalTextField
import android.app.DatePickerDialog
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.shashank.sony.fancytoastlib.FancyToast
import pt.ipp.estg.myapplication.connection.ConnectivityObserver
import pt.ipp.estg.myapplication.models.GeneralViewModels
import pt.ipp.estg.myapplication.models.database.user.User
import pt.ipp.estg.myapplication.models.firebase.AuthViewModel
import pt.ipp.estg.myapplication.ui.screens.ui_components.IntTextField
import pt.ipp.estg.myapplication.ui.screens.ui_components.PasswordTextField
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.*
import java.util.*

@Preview
@Composable
fun PreviewRegistrationUser() {
    val navController: NavHostController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val generalViewModels: GeneralViewModels = viewModel()
    RegistrationUser(navController, authViewModel, generalViewModels)
}

@Composable
fun RegistrationUser(
    navController: NavController,
    authViewModel: AuthViewModel,
    generalViewModels: GeneralViewModels,
    isEditing: Boolean = false
) {
    val ctx = LocalContext.current

    val currentUser = authViewModel.currentUser.observeAsState()

    val name =
        rememberSaveable { mutableStateOf(if (isEditing && currentUser.value != null) currentUser.value!!.name else "") }
    val password = rememberSaveable { mutableStateOf("") }
    val birthDate =
        rememberSaveable { mutableStateOf(if (isEditing && currentUser.value != null) currentUser.value!!.name else "") }
    val email = rememberSaveable { mutableStateOf("") }
    val contact = rememberSaveable { mutableStateOf(0) }

    val loginUiState = authViewModel.loginUiState

    Log.d("auth", "user register  ${currentUser.value?.email}")
    Log.d("auth", "isLogged register {${loginUiState.isSuccessLogin}}")

    fun isDataValid(): Boolean {
        val flag = true
        if (!Validations.validateName(name.value) || name.value == "") {
            return false
        }
        if (!Validations.validatePassword(password.value) || password.value == "") {
            return false
        }
        if (!Validations.validateBirthDate(birthDate.value) || birthDate.value == "") {
            return false
        }
        if (!Validations.validateEmail(email.value) || email.value == "") {
            return false
        }
        if (!Validations.validateContact(contact.value)) {
            return false
        }
        return flag
    }

    LaunchedEffect(loginUiState.isSuccessLogin) {
        if (loginUiState.isSuccessLogin && !isEditing) {
            navController.navigate("map")
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onPrimary)
            .verticalScroll(rememberScrollState())
    )
    {
        Spacer(modifier = Modifier.height(25.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.splash_logo),
                contentDescription = "App Logo",
                alignment = Alignment.CenterStart,
                modifier = Modifier
                    .weight(1f)
                    .width(60.dp)
                    .size(60.dp),
            )
            IconButton(onClick = {
                navController.navigate("user_login") {
                    popUpTo("user_login") {
                        inclusive = true
                    }
                }
            }) {
                Icon(
                    Icons.Filled.ArrowBack, contentDescription = "Cancel",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        Column(
            Modifier
                .weight(2f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .fillMaxSize()
                .clip(RoundedCornerShape(30.dp))
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp, vertical = 20.dp)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Spacer(modifier = Modifier.height(5.dp))
                //REGISTRATION TITLE
                TitleMedium(
                    text = stringResource(id = R.string.registration),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(15.dp))
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(10.dp))

                    //EMAIL
                    LabelMedium(
                        text = stringResource(id = R.string.email),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    NormalTextField(
                        stringState = email,
                        enabled = !isEditing,
                        modifier = Modifier.fillMaxWidth(),
                        errorMsg = stringResource(id = R.string.bad_email),
                        validation = { str -> Validations.validateEmail(str) },
                        colorBackground = MaterialTheme.colorScheme.onPrimary,
                        textColor = MaterialTheme.colorScheme.primary,
                    )

                    Spacer(modifier = Modifier.height(10.dp))

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
                        textColor = MaterialTheme.colorScheme.primary,
                        errorMsg = stringResource(id = R.string.bad_password),
                        validation = { str -> Validations.validatePassword(str) },
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    //USER NAME
                    LabelMedium(
                        text = stringResource(id = R.string.name),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    NormalTextField(
                        stringState = name,
                        modifier = Modifier.fillMaxWidth(),
                        errorMsg = stringResource(id = R.string.bad_user),
                        validation = { str -> Validations.validateName(str) },
                        colorBackground = MaterialTheme.colorScheme.onPrimary,
                        textColor = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    //CONTACT
                    LabelMedium(
                        text = stringResource(id = R.string.contact),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    IntTextField(
                        numberState = contact,
                        ignoreValue = 0,
                        modifier = Modifier.fillMaxWidth(),
                        errorMsg = stringResource(id = R.string.bad_contact),
                        validation = { str -> Validations.validateContact(str) },
                        colorBackground = MaterialTheme.colorScheme.onPrimary,
                        textColor = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    //BIRTH DATE
                    LabelMedium(
                        text = stringResource(id = R.string.birth_date),
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    val mYear: Int
                    val mMonth: Int
                    val mDay: Int
                    val mCalendar = Calendar.getInstance()

                    mYear = mCalendar.get(Calendar.YEAR)
                    mMonth = mCalendar.get(Calendar.MONTH)
                    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

                    mCalendar.time = Date()
                    val mDatePickerDialog = DatePickerDialog(
                        ctx,
                        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
                            birthDate.value = "$mDayOfMonth/${mMonth + 1}/$mYear"
                        },
                        mYear - 12, mMonth, mDay
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Column(modifier = Modifier.clickable {
                        mDatePickerDialog.show()
                    }) {
                        NormalTextField(
                            stringState = birthDate,
                            enabled = false,
                            modifier = Modifier.fillMaxWidth(),
                            errorMsg = stringResource(id = R.string.bad_birth_date),
                            validation = { str ->
                                if (birthDate.value == "") true else Validations.validateBirthDate(
                                    str
                                )
                            },
                            colorBackground = MaterialTheme.colorScheme.onPrimary,
                            textColor = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))

                //REGISTER BUTTON
                if (loginUiState.isLoading) {
                    Column(
                        Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                    }
                } else {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colorScheme.onPrimary,
                            contentColor = MaterialTheme.colorScheme.primary
                        ), onClick = {
                            if (generalViewModels.status.value == ConnectivityObserver.Status.Available) {
                                if (!isDataValid()) {
                                    FancyToast.makeText(
                                        ctx,
                                        ctx.resources.getString(R.string.bad_input),
                                        FancyToast.LENGTH_LONG,
                                        FancyToast.ERROR,
                                        false
                                    ).show()
                                } else {
                                    val user = User(
                                        email = email.value,
                                        contact = contact.value,
                                        birthDate = birthDate.value,
                                        name = name.value,
                                    )
                                    authViewModel.createUser(ctx, user, password.value)
                                }
                            } else {
                                FancyToast.makeText(
                                    ctx,
                                    ctx.resources.getString(R.string.no_internet_connection),
                                    FancyToast.LENGTH_LONG,
                                    FancyToast.ERROR,
                                    false
                                ).show()
                            }
                        }
                    ) {
                        LabelMedium(
                            text = stringResource(id = R.string.registrate),
                            color = MaterialTheme.colorScheme.primary,
                            weight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}