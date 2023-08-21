package pt.ipp.estg.myapplication.ui.screens.user_info

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.shashank.sony.fancytoastlib.FancyToast
import pt.ipp.estg.myapplication.R
import pt.ipp.estg.myapplication.connection.ConnectivityObserver
import pt.ipp.estg.myapplication.models.GeneralViewModels
import pt.ipp.estg.myapplication.models.database.coupon.Coupon
import pt.ipp.estg.myapplication.models.database.coupon.CouponViewModels
import pt.ipp.estg.myapplication.models.database.vehicle.Vehicle
import pt.ipp.estg.myapplication.ui.screens.locations.PreferencesViewModel
import pt.ipp.estg.myapplication.ui.screens.ui_components.NormalCheckBox
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.LabelSmall
import pt.ipp.estg.myapplication.ui.screens.vehicle.VehicleViewModels
import pt.ipp.estg.myapplication.ui.theme.AppTypography

enum class DataDialog {
    UPLOAD, GET, DELETE
}

@Composable
fun FirebaseBackupDialog(
    dialogIsOpen: MutableState<Boolean>,
    operationType: DataDialog,

    context: Context,
    userEmail: String,
    vehicleViewModels: VehicleViewModels,
    couponViewModel: CouponViewModels,
    preferencesViewModel: PreferencesViewModel,
    generalViewModels : GeneralViewModels,

    allVehicles: State<List<Vehicle>?>,
    allCoupons: State<List<Coupon>?>,
) {
    val vehicleChoice = remember { mutableStateOf(true) }
    val couponsChoice = remember { mutableStateOf(true) }
    val locationsChoice = remember { mutableStateOf(true) }

    val homeLocation = preferencesViewModel.preferencesHome.observeAsState()
    val workLocation = preferencesViewModel.preferencesWork.observeAsState()

    val dialogWarningUploadIsOpen = remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = { dialogIsOpen.value = false },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .padding(10.dp, 5.dp, 5.dp, 10.dp)
                .fillMaxWidth(),
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Image(
                    painter = if (operationType == DataDialog.DELETE) {
                        painterResource(id = R.drawable.danger)
                    } else {
                        painterResource(id = R.drawable.cloud)
                    },
                    contentDescription = "",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .padding(vertical = 35.dp)
                        .height(60.dp)
                        .fillMaxWidth(),
                )

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(
                            id =
                            if (operationType == DataDialog.GET) {
                                R.string.get_title
                            } else if (operationType == DataDialog.UPLOAD) {
                                R.string.upload_title
                            } else {
                                R.string.remove
                            }
                        ),
                        style = AppTypography.headlineSmall.copy(color = MaterialTheme.colorScheme.onBackground),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text =
                        stringResource(
                            id =
                            if (operationType == DataDialog.GET) {
                                R.string.get_details
                            } else if (operationType == DataDialog.UPLOAD) {
                                R.string.upload_details
                            } else {
                                R.string.remove_details
                            }
                        ),
                        style = AppTypography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Normal
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 10.dp, bottom = 35.dp)
                    )

                    RowContentGetUploadPopUp(
                        title = stringResource(id = R.string.my_vehicles),
                        vehicleChoice
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    RowContentGetUploadPopUp(
                        title = stringResource(id = R.string.my_coupons),
                        couponsChoice
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    RowContentGetUploadPopUp(
                        title = stringResource(id = R.string.my_locations),
                        locationsChoice
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                //.......................................................................
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 15.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {

                    TextButton(onClick = {
                        dialogIsOpen.value = false
                    }) {
                        Text(
                            stringResource(id = R.string.cancel),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                        )
                    }
                    TextButton(onClick = {
                        if (generalViewModels.status.value != ConnectivityObserver.Status.Available) {
                            FancyToast.makeText(
                                context,
                                context.resources.getString(R.string.no_internet_connection),
                                FancyToast.LENGTH_LONG,
                                FancyToast.ERROR,
                                false
                            ).show()
                        }
                        else {
                            if (operationType == DataDialog.UPLOAD) {
                                if (allVehicles.value?.isEmpty() == true || allCoupons.value?.isEmpty() == true) {
                                    dialogWarningUploadIsOpen.value = true;
                                } else {
                                    uploadItems(
                                        context = context,
                                        vehicleUploadChoice = vehicleChoice.value,
                                        couponsUploadChoice = couponsChoice.value,
                                        locationsUploadChoice = locationsChoice.value,
                                        userEmail = userEmail,
                                        vehicleViewModels = vehicleViewModels,
                                        couponViewModel = couponViewModel,
                                        preferencesViewModel = preferencesViewModel,
                                        allVehicles = allVehicles,
                                        allCoupons = allCoupons
                                    )
                                    dialogIsOpen.value = false;
                                }
                            } else if (operationType == DataDialog.GET) {
                                getItems(
                                    context = context,
                                    vehicleGetChoice = vehicleChoice.value,
                                    couponsGetChoice = couponsChoice.value,
                                    locationsGetChoice = locationsChoice.value,
                                    userEmail = userEmail,
                                    vehicleViewModels = vehicleViewModels,
                                    couponViewModel = couponViewModel,
                                    preferencesViewModel = preferencesViewModel,
                                )
                                dialogIsOpen.value = false;
                            } else {
                                removeItems(
                                    context = context,
                                    vehicleRemoveChoice = vehicleChoice.value,
                                    couponsRemoveChoice = couponsChoice.value,
                                    locationsRemoveChoice = locationsChoice.value,
                                    userEmail = userEmail,
                                    vehicleViewModels = vehicleViewModels,
                                    couponViewModel = couponViewModel,
                                    preferencesViewModel = preferencesViewModel,
                                )
                                dialogIsOpen.value = false;
                            }
                        }
                    }
                    ) {
                        Text(
                            stringResource(id = R.string.submit),
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                        )
                    }
                }
            }
        }
    }

    if (dialogWarningUploadIsOpen.value) {
        if (allVehicles.value != null && allCoupons.value != null) {
            UploadWarningDialog(
                vehicleEmpty = allVehicles.value!!.isEmpty(),
                couponsEmpty = allCoupons.value!!.isEmpty(),
                homeLocationEmpty = homeLocation.value?.district == " ",
                workLocationEmpty = workLocation.value?.district == " ",
                dialogIsOpen = dialogWarningUploadIsOpen,
            ) {
                uploadItems(
                    context = context,
                    vehicleUploadChoice = vehicleChoice.value,
                    couponsUploadChoice = couponsChoice.value,
                    locationsUploadChoice = locationsChoice.value,
                    userEmail = userEmail,
                    vehicleViewModels = vehicleViewModels,
                    couponViewModel = couponViewModel,
                    preferencesViewModel = preferencesViewModel,
                    allVehicles = allVehicles,
                    allCoupons = allCoupons
                )
                dialogIsOpen.value = false
            }
        }
    }
}

@Composable
fun RowContentGetUploadPopUp(title: String, checkBox: MutableState<Boolean>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LabelSmall(
            text = title,
            color = MaterialTheme.colorScheme.onBackground
        )
        NormalCheckBox(
            checked = checkBox.value,
            onCheckedChange = { checkBox.value = !checkBox.value }
        )
    }
}

fun getItems(
    context: Context,
    vehicleGetChoice: Boolean,
    couponsGetChoice: Boolean,
    locationsGetChoice: Boolean,
    userEmail: String,
    vehicleViewModels: VehicleViewModels,
    couponViewModel: CouponViewModels,
    preferencesViewModel: PreferencesViewModel
) {
    if (vehicleGetChoice) {
        vehicleViewModels.getCloudVehicles(
            email = userEmail,
            context = context,
        )
    }
    if (couponsGetChoice) {
        couponViewModel.getCloudCoupons(
            email = userEmail,
            context = context,
        )
    }

    if (locationsGetChoice) {
        preferencesViewModel.getCloudLocations(
            context = context,
            email = userEmail,
        )
    }
}

fun uploadItems(
    context: Context,
    vehicleUploadChoice: Boolean,
    couponsUploadChoice: Boolean,
    locationsUploadChoice: Boolean,
    userEmail: String,
    vehicleViewModels: VehicleViewModels,
    couponViewModel: CouponViewModels,
    allVehicles: State<List<Vehicle>?>,
    allCoupons: State<List<Coupon>?>,
    preferencesViewModel: PreferencesViewModel
) {
    if (vehicleUploadChoice) {
        vehicleViewModels.uploadVehiclesToCloudByUser(
            email = userEmail,
            context = context,
            vehicles = allVehicles
        )
    }
    if (couponsUploadChoice) {
        couponViewModel.uploadCouponsToCloudByUser(
            email = userEmail,
            context = context,
            coupons = allCoupons
        )
    }

    if (locationsUploadChoice) {
        preferencesViewModel.uploadLocationsToCloudByUser(
            context = context,
            email = userEmail,
        )
    }
}

fun removeItems(
    context: Context,
    vehicleRemoveChoice: Boolean,
    couponsRemoveChoice: Boolean,
    locationsRemoveChoice: Boolean,
    userEmail: String,
    vehicleViewModels: VehicleViewModels,
    couponViewModel: CouponViewModels,
    preferencesViewModel: PreferencesViewModel
) {
    if (vehicleRemoveChoice) {
        vehicleViewModels.removeVehiclesInCloudByUser(
            email = userEmail,
            context = context,
        )
    }
    if (couponsRemoveChoice) {
        couponViewModel.removeVehicleCloudByUser(
            email = userEmail,
            context = context,
        )
    }

    if (locationsRemoveChoice) {
        preferencesViewModel.removeLocationsCloudByUser(
            context = context,
            email = userEmail,
        )
    }
}


@Composable
fun UploadWarningDialog(
    vehicleEmpty: Boolean,
    couponsEmpty: Boolean,
    homeLocationEmpty: Boolean,
    workLocationEmpty: Boolean,
    dialogIsOpen: MutableState<Boolean>,
    onSubmit: () -> Unit
) {
    Dialog(
        onDismissRequest = { dialogIsOpen.value = false },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .padding(10.dp, 5.dp, 5.dp, 10.dp)
                .fillMaxWidth(),
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 10.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.warning),
                    contentDescription = "",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .padding(vertical = 35.dp)
                        .height(60.dp)
                        .fillMaxWidth(),
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.empty_entities),
                        style = AppTypography.headlineSmall.copy(color = MaterialTheme.colorScheme.onBackground),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    val stringBuilder = StringBuilder()
                    if (vehicleEmpty) {
                        stringBuilder.append("- ")
                            .append(stringResource(id = R.string.my_vehicles))
                            .append("\n")
                    }
                    if (couponsEmpty) {
                        stringBuilder.append("- ")
                            .append(stringResource(id = R.string.my_coupons))
                            .append("\n")
                    }
                    if (homeLocationEmpty) {
                        stringBuilder.append("- ")
                            .append(stringResource(id = R.string.home_location))
                            .append("\n")
                    }
                    if (workLocationEmpty) {
                        stringBuilder.append("- ")
                            .append(stringResource(id = R.string.work_location))
                            .append("\n")
                    }


                    Text(
                        text = stringBuilder.toString(),
                        style = AppTypography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Normal
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 10.dp, bottom = 15.dp)
                    )
                }

                Text(
                    stringResource(id = R.string.empty_upload_msg),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 5.dp, bottom = 15.dp)
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 15.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {

                    TextButton(onClick = {
                        dialogIsOpen.value = false
                    }) {
                        Text(
                            stringResource(id = R.string.cancel),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                        )
                    }
                    TextButton(onClick = {
                        onSubmit()
                        dialogIsOpen.value = false
                    }) {
                        Text(
                            stringResource(id = R.string.submit),
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                        )
                    }
                }
            }
        }
    }
}