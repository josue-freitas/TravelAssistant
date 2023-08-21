package pt.ipp.estg.myapplication.ui.screens.user_info

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.util.Log
import android.widget.DatePicker
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.shashank.sony.fancytoastlib.FancyToast
import pt.ipp.estg.myapplication.R
import pt.ipp.estg.myapplication.connection.ConnectivityObserver
import pt.ipp.estg.myapplication.helper.Validations
import pt.ipp.estg.myapplication.models.GeneralViewModels
import pt.ipp.estg.myapplication.models.database.coupon.CouponViewModels
import pt.ipp.estg.myapplication.models.database.user.User
import pt.ipp.estg.myapplication.ui.screens.vehicle.VehicleViewModels
import pt.ipp.estg.myapplication.models.firebase.AuthViewModel
import pt.ipp.estg.myapplication.ui.screens.locations.PreferencesViewModel
import pt.ipp.estg.myapplication.ui.screens.ui_components.IntTextField
import pt.ipp.estg.myapplication.ui.screens.ui_components.NormalTextField
import pt.ipp.estg.myapplication.ui.screens.ui_components.PasswordTextField
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.LabelLarge
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.LabelMedium
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.LabelSmall
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.TitleLarge
import pt.ipp.estg.myapplication.ui.screens.vehicle.PageToShow
import pt.ipp.estg.myapplication.ui.theme.AppTypography
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

data class InfoCardClass(val icon: Painter, val title: String, val content: String) {
}

@Preview
@Composable
fun PreviewUserInfo() {
    val authViewModel: AuthViewModel = viewModel()
    val vehicleViewModels: VehicleViewModels = viewModel()
    val couponViewModels: CouponViewModels = viewModel()
    val preferencesViewModel: PreferencesViewModel = viewModel()
    val generalViewModels: GeneralViewModels = viewModel()
    UserInfo(
        authViewModel,
        rememberNavController(),
        vehicleViewModels,
        preferencesViewModel,
        couponViewModels,
        generalViewModels
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserInfo(
    authViewModel: AuthViewModel,
    navController: NavController,
    vehicleViewModels: VehicleViewModels,
    preferencesViewModel: PreferencesViewModel,
    couponViewModel: CouponViewModels,
    generalViewModels: GeneralViewModels
) {
    val currentUser = authViewModel.currentUser.observeAsState()

    val dialogCloudOperation = remember { mutableStateOf(false) }

    val context = LocalContext.current

    var isEditing by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val editingName = remember { mutableStateOf("") }
    val editingBirth = remember { mutableStateOf("") }
    val editingEmail = remember { mutableStateOf("") }
    val editingContact = remember { mutableStateOf(0) }

    val allVehicles = vehicleViewModels.allVehicles.observeAsState()
    val allCoupons = couponViewModel.allCoupons.observeAsState()

    val operationType = remember { mutableStateOf(DataDialog.GET) }

    //page control
    //alter the back button function
    when (isEditing) {
        true -> BackHandler(enabled = true) {
            isEditing = false
        }
    }

    fun isDataValid(): Boolean {
        val flag = true
        if (!Validations.validateName(editingName.value) || editingName.value == "") {
            return false
        }
        if (!Validations.validateBirthDate(editingBirth.value) || editingBirth.value == "") {
            return false
        }
        return flag
    }

    if (currentUser.value != null) {

        editingBirth.value = currentUser.value!!.birthDate
        editingName.value = currentUser.value!!.name
        editingEmail.value = currentUser.value!!.email
        editingContact.value = currentUser.value!!.contact

        if (dialogCloudOperation.value) {
            FirebaseBackupDialog(
                dialogIsOpen = dialogCloudOperation,
                context = context,
                userEmail = currentUser.value!!.email,
                vehicleViewModels = vehicleViewModels,
                couponViewModel = couponViewModel,
                preferencesViewModel = preferencesViewModel,
                generalViewModels = generalViewModels,
                operationType = operationType.value,
                allCoupons = allCoupons,
                allVehicles = allVehicles
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(15.dp),
        ) {
            TitleLarge(text = stringResource(id = R.string.my_profile))

            Spacer(modifier = Modifier.height(40.dp))

            //if page is open and the user logged out
            if (currentUser.value!!.email == "") {
                Box(modifier = Modifier.fillMaxSize(), Alignment.Center) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LabelMedium(
                            text = stringResource(id = R.string.not_logged),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(30.dp))
                        Button(
                            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary),
                            shape = RoundedCornerShape(15.dp),
                            onClick = {
                                navController.navigate("user_login")
                            }
                        ) {
                            LabelSmall(
                                text = stringResource(id = R.string.login_or_register),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            } else {
                if (!isEditing) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            //HEADER
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {

                                //image + name
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        modifier = Modifier.size(60.dp),
                                        painter = painterResource(id = R.drawable.avatar_init),
                                        contentDescription = ""
                                    )
                                    Spacer(modifier = Modifier.width(15.dp))
                                    Column() {
                                        LabelSmall(text = stringResource(id = R.string.hello))
                                        LabelLarge(
                                            text = currentUser.value!!.name,
                                            weight = FontWeight.Bold
                                        )
                                    }
                                }
                                IconButton(onClick = {
                                    isEditing = true
                                }) {
                                    Icon(
                                        Icons.Filled.Edit,
                                        contentDescription = "Localized description",
                                        tint = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(25.dp))
                            Divider(
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(25.dp))

                            val listItems = listOf(
                                InfoCardClass(
                                    painterResource(id = R.drawable.agreement),
                                    stringResource(id = R.string.membership_time),
                                    currentUser.value!!.registeredDate
                                ),
                                InfoCardClass(
                                    painterResource(id = R.drawable.cake),
                                    stringResource(id = R.string.birth_date),
                                    currentUser.value!!.birthDate
                                ),
                                InfoCardClass(
                                    painterResource(id = R.drawable.contact),
                                    stringResource(id = R.string.contact),
                                    currentUser.value!!.contact.toString()
                                ),
                                InfoCardClass(
                                    painterResource(id = R.drawable.email),
                                    stringResource(id = R.string.email),
                                    currentUser.value!!.email,
                                )
                            )

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            )
                            {
                                items(listItems) { info ->
                                    InfoCard(
                                        icon = info.icon,
                                        title = info.title,
                                        content = info.content
                                    )
                                }
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Button(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary),
                                    border = BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.onPrimary
                                    ),
                                    shape = RoundedCornerShape(15.dp),
                                    onClick = {
                                        operationType.value = DataDialog.GET
                                        dialogCloudOperation.value = true
                                    }
                                ) {
                                    LabelSmall(
                                        text =
                                        stringResource(id = R.string.download_data),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary),
                                    border = BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.onPrimary
                                    ),
                                    shape = RoundedCornerShape(15.dp),
                                    onClick = {
                                        operationType.value = DataDialog.UPLOAD
                                        dialogCloudOperation.value = true
                                    }
                                ) {
                                    LabelSmall(
                                        text = stringResource(id = R.string.upload_data),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.error),
                                    border = BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.onPrimary
                                    ),
                                    shape = RoundedCornerShape(15.dp),
                                    onClick = {
                                        operationType.value = DataDialog.DELETE
                                        dialogCloudOperation.value = true
                                    }
                                ) {
                                    LabelSmall(
                                        text =
                                        stringResource(id = R.string.delete_from_cloud),
                                        color = MaterialTheme.colorScheme.onError
                                    )
                                }
                            }
                        }
                    }
                } else { //EDITING

                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Column() {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(onClick = {
                                    isEditing = false
                                }) {
                                    Icon(
                                        Icons.Filled.ArrowBack,
                                        contentDescription = "Localized description",
                                        tint = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }

                            Column(modifier = Modifier.fillMaxWidth()) {
                                //EMAIL
                                LabelMedium(
                                    text = stringResource(id = R.string.email),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(5.dp))
                                OutlinedTextField(
                                    value = editingEmail.value,
                                    onValueChange = { },
                                    enabled = false,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = AppTypography.labelLarge,
                                    colors = TextFieldDefaults.textFieldColors(
                                        backgroundColor = MaterialTheme.colorScheme.background,
                                        textColor = MaterialTheme.colorScheme.primary,
                                        cursorColor = MaterialTheme.colorScheme.primary,
                                    ),
                                    singleLine = true,
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                //USER NAME
                                LabelMedium(
                                    text = stringResource(id = R.string.name),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(5.dp))
                                NormalTextField(
                                    stringState = editingName,
                                    modifier = Modifier.fillMaxWidth(),
                                    errorMsg = stringResource(id = R.string.bad_user),
                                    validation = { str -> Validations.validateName(str) },
                                    colorBackground = MaterialTheme.colorScheme.primary,
                                    textColor = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                //CONTACT
                                LabelMedium(
                                    text = stringResource(id = R.string.contact),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(5.dp))
                                IntTextField(
                                    numberState = editingContact,
                                    ignoreValue = 0,
                                    modifier = Modifier.fillMaxWidth(),
                                    errorMsg = stringResource(id = R.string.bad_contact),
                                    validation = { str -> Validations.validateContact(str) },
                                    colorBackground = MaterialTheme.colorScheme.primary,
                                    textColor = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                //BIRTH DATE
                                LabelMedium(
                                    text = stringResource(id = R.string.birth_date),
                                    color = MaterialTheme.colorScheme.onBackground
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
                                    context,
                                    { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
                                        editingBirth.value = "$mDayOfMonth/${mMonth + 1}/$mYear"
                                    },
                                    mYear - 12, mMonth, mDay
                                )
                                Spacer(modifier = Modifier.height(5.dp))
                                Column(modifier = Modifier.clickable {
                                    mDatePickerDialog.show()
                                }) {
                                    NormalTextField(
                                        stringState = editingBirth,
                                        enabled = false,
                                        modifier = Modifier.fillMaxWidth(),
                                        errorMsg = stringResource(id = R.string.bad_birth_date),
                                        validation = { str ->
                                            if (editingBirth.value == "") true else Validations.validateBirthDate(
                                                str
                                            )
                                        },
                                        colorBackground = MaterialTheme.colorScheme.primary,
                                        textColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary),
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = RoundedCornerShape(15.dp),
                                onClick = {
                                    if (generalViewModels.status.value == ConnectivityObserver.Status.Available) {
                                        isLoading = true
                                        if (!isDataValid()) {
                                            FancyToast.makeText(
                                                context,
                                                context.resources.getString(R.string.bad_input),
                                                FancyToast.LENGTH_LONG,
                                                FancyToast.ERROR,
                                                false
                                            ).show()
                                        } else {
                                            val user = User(
                                                email = currentUser.value!!.email,
                                                contact = editingContact.value,
                                                birthDate = editingBirth.value,
                                                registeredDate = currentUser.value!!.registeredDate,
                                                name = editingName.value,
                                            )
                                            authViewModel.updateUser(context, user)
                                            isLoading = false
                                            navController.navigate("map")
                                        }
                                    } else {
                                        FancyToast.makeText(
                                            context,
                                            context.resources.getString(R.string.no_internet_connection),
                                            FancyToast.LENGTH_LONG,
                                            FancyToast.ERROR,
                                            false
                                        ).show()
                                    }
                                }
                            ) {
                                if(isLoading){
                                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(15.dp))
                                } else {
                                LabelSmall(
                                    text =
                                    stringResource(id = R.string.update),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewInfoCard() {
    InfoCard(
        painterResource(id = R.drawable.kmh),
        stringResource(id = R.string.total_kms),
        "321km"
    )
}

@SuppressLint("SimpleDateFormat")
fun getMembership(date: Date): String {
    val formatter2 = SimpleDateFormat("dd/MM/yyyy")
    formatter2.timeZone = TimeZone.getTimeZone("Europe/Lisbon");

    //member date
    val formatMemberDate = SimpleDateFormat("dd/MM/yyy")
    val memberDateString = formatMemberDate.format(date)
    val memberDate = formatter2.parse(memberDateString)

    //actual date
    val formatterActual = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val actualDateString = LocalDateTime.now().toLocalDate().format(formatterActual)
    val currentDate = formatter2.parse(actualDateString)

    val total = currentDate!!.date.minus(memberDate!!.time)

    Log.d("date", "member $memberDate")
    Log.d("date", "current $currentDate")
    Log.d("date", "received $total")

    return Date(total).toString()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InfoCard(icon: Painter, title: String, content: String) {
    val config = LocalConfiguration.current
    val widthDp = config.screenWidthDp

    Card(
        shape = RoundedCornerShape(15.dp),
        backgroundColor = MaterialTheme.colorScheme.primary,
        onClick = {}
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .width((widthDp / 2.2).dp)
                .padding(vertical = 10.dp, horizontal = 10.dp)
        ) {
            Image(
                modifier = Modifier.size(30.dp),
                painter = icon,
                contentDescription = ""
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column() {
                LabelSmall(text = title, color = MaterialTheme.colorScheme.onPrimary)
                Text(
                    content,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = AppTypography.labelMedium.copy(MaterialTheme.colorScheme.onPrimary),
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}