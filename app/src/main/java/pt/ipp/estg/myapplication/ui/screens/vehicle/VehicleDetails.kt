package pt.ipp.estg.myapplication.ui.screens.vehicle

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import pt.ipp.estg.myapplication.R
import pt.ipp.estg.myapplication.enumerations.*
import pt.ipp.estg.myapplication.models.database.vehicle.Vehicle
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.*
import java.io.File
import java.io.FileInputStream
import java.net.URI

data class MaintenanceInfo(
    val icon: Int,
    val iconMaintenance: Int? = null,
    val title: String,
    val subTitle: String
)

@Preview
@Composable
fun PreviewVehicleDetails() {
    val navController: NavHostController = rememberNavController()
    VehicleDetails(plate = "00-AA-00", navController = navController)
}

@Composable
fun VehicleDetails(
    plate: String,
    navController: NavController,
) {
    val vehicleViewModels: VehicleViewModels = viewModel()

    val vehicleToDetail = vehicleViewModels.getVehicleByPlate(plate).observeAsState()

    val dialogIsOpen = remember { mutableStateOf(false) }

    val context = LocalContext.current

    CarTemplate {
        if (vehicleToDetail.value != null) {

            if (dialogIsOpen.value) {
                RemoveDialog(navController, vehicleToDetail.value!!, dialogIsOpen)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                TitleSmall(
                    text = stringResource(id = R.string.information_about_vehicle),
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.heightIn(20.dp))

                //BUTTONS
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    IconButton(onClick = { navController.navigate("vehicle_edit/$plate") }) {
                        Icon(
                            Icons.Filled.Edit, contentDescription = "Localized description",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }


                    IconButton(onClick = { dialogIsOpen.value = true }) {
                        Icon(
                            Icons.Filled.Delete, contentDescription = "Localized description",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                Divider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 15.dp)
                )

                //IMAGE
                var file: File? = null
                var b: Bitmap? = null
                try {
                    file =
                        File(URI("file:///storage/emulated/0/Android/media/pt.ipp.estg.myapplication/imgs/${vehicleToDetail.value!!.uriString}"))
                    b = BitmapFactory.decodeStream(FileInputStream(file))
                } catch (_: Exception) {
                    file = null
                    b = null
                }
                if (file != null && b != null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            bitmap = b.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(120.dp)
                                .border(
                                    2.dp,
                                    Color.Gray,
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // BASIC INFORMATION
                Column(
                    horizontalAlignment = Alignment.Start
                ) {

                    TitleSmall(
                        text = stringResource(id = R.string.basic_information),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.height(20.dp))


                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        InfoCard(R.drawable.license_plate, vehicleToDetail.value!!.plate)

                        val img: Int =
                            if (vehicleToDetail.value!!.vehicleType == VehiclesTypes.AUTOMOBILE) {
                                R.drawable.car
                            } else {
                                R.drawable.motorbike
                            }
                        InfoCardTwoText(
                            img,
                            vehicleToDetail.value!!.brand,
                            vehicleToDetail.value!!.model
                        )

                        InfoCard(R.drawable.car_engine, vehicleToDetail.value!!.specification)

                        InfoCard(R.drawable.car_key, vehicleToDetail.value!!.associationVehicle)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                //MAINTENANCE
                Spacer(modifier = Modifier.height(35.dp))
                TitleSmall(
                    text = stringResource(id = R.string.maintenance),
                    color = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.heightIn(20.dp))

                MaintenanceBlock(context, vehicleToDetail.value!!)
            }
        }
    }
}

@Preview
@Composable
fun PreviewRemoveDialog() {
    val navController: NavHostController = rememberNavController()
    val vehicle = Vehicle(
        "00-AA-00",
        "Brand",
        "Model",
        VehiclesTypes.AUTOMOBILE,
        "Specification",
        "Association",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        MaintenanceStatus.OK
    )
    val dialogIsOpen = remember { mutableStateOf(true) }
    RemoveDialog(navController = navController, vehicle = vehicle, dialogIsOpen = dialogIsOpen)
}

@Composable
fun RemoveDialog(
    navController: NavController,
    vehicle: Vehicle,
    dialogIsOpen: MutableState<Boolean>
) {
    val vehicleViewModels: VehicleViewModels = viewModel()
    Dialog(
        onDismissRequest = { dialogIsOpen.value = false },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        AlertDialog(
            onDismissRequest = {
                dialogIsOpen.value = false
            },
            confirmButton = {
                TextButton(onClick = {
                    vehicleViewModels.delete(vehicle.plate)

                    //delete img
                    var fileToDelete: File? = null
                    try {
                        fileToDelete =
                            File(URI("file:///storage/emulated/0/Android/media/pt.ipp.estg.myapplication/imgs/${vehicle.uriString}"))
                        fileToDelete.delete()
                    } catch (_: Exception) {

                    }
                    dialogIsOpen.value = false
                    navController.navigate("vehicles_list")
                }) {
                    LabelMedium(
                        text = stringResource(id = R.string.remove),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    dialogIsOpen.value = false
                }) {
                    LabelMedium(
                        text = stringResource(id = R.string.cancel),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            },
            title = {
                LabelLarge(
                    text = stringResource(id = R.string.remove_vehicle),
                    color = MaterialTheme.colorScheme.onBackground
                )

            },
            text = {
                LabelSmall(
                    text = stringResource(id = R.string.remove_msg),
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            modifier = Modifier // Set the width and padding
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(5.dp),
            backgroundColor = MaterialTheme.colorScheme.background,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        )
    }
}

@Preview
@Composable
fun PreviewInfoCard() {
    InfoCard(
        R.drawable.car,
        "Car"
    )
}

@Composable
fun InfoCard(image: Int, text: String) {
    Card(
        backgroundColor = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(15.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.padding(10.dp)
        ) {
            Image(
                painter = painterResource(id = image),
                contentDescription = "image",
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.height(15.dp))
            LabelSmall(
                text = text,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Preview
@Composable
fun PreviewInfoCardTwoText() {
    InfoCardTwoText(
        R.drawable.car,
        "Car",
        "Car 2"
    )
}

@Composable
fun InfoCardTwoText(image: Int, text1: String, text2: String) {
    Card(
        backgroundColor = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(15.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Image(
                painter = painterResource(id = image),
                contentDescription = "image",
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.height(5.dp))
            LabelSmall(
                text = text1,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(3.dp))
            LabelSmall(
                text = text2,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

//given a value, medium and min point gives the text that fits(km left or km passed9
fun getText(context: Context, value: Int?): String {
    var str: Int = R.string.km_left

    if (value != null) {
        if (value < 0) {
            str = R.string.km_passed
        }
    }

    return context.resources.getString(str)
}

@Preview
@Composable
fun PreviewMaintenanceInfo() {
    val maintence = MaintenanceInfo(
        R.drawable.car_oil,
        R.drawable.danger,
        "Title",
        "Subtitle"
    )
    MaintenanceInfo(
        maintence
    )
}

@Composable
fun MaintenanceInfo(info: MaintenanceInfo) {
    Card(
        shape = RoundedCornerShape(15.dp),
        contentColor = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(10.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.primary)
                .padding(15.dp)
        ) {

            BadgedBox(
                badge = {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Badge(
                            modifier = Modifier.padding(horizontal = 5.dp),
                            backgroundColor = Color.Transparent
                        ) {
                            info.iconMaintenance?.let {
                                Image(
                                    painter = painterResource(id = it),
                                    contentDescription = "image",
                                    modifier = Modifier.size(25.dp)
                                )
                            }
                        }
                    }
                })
            {
                Image(
                    painter = painterResource(id = info.icon),
                    contentDescription = "image",
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.width(45.dp))
            Column() {
                LabelMedium(
                    text = info.title,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                LabelMedium(
                    text = info.subTitle,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewMaintenanceBlock() {
    val vehicle = Vehicle(
        "00-AA-00",
        "Brand",
        "Model",
        VehiclesTypes.AUTOMOBILE,
        "Specification",
        "Association",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        MaintenanceStatus.OK
    )
    val context = LocalContext.current
    MaintenanceBlock(
        context, vehicle
    )
}

@Composable
fun MaintenanceBlock(context: Context, vehicleToDetail: Vehicle) {
    val list = getMaintenanceList(context, vehicleToDetail)

    if (list.isEmpty()) {
        LabelMedium(text = stringResource(id = R.string.without_maintanence_info))
    } else {
        LazyRow {
            items(list) { it ->
                MaintenanceInfo(info = it)
            }
        }
    }
}

/*
  This composable doesn't have a preview since works as an intermediary of this module and
  doesn't have a graphic interface.
 */
fun getMaintenanceList(context: Context, vehicle: Vehicle): List<MaintenanceInfo> {
    val list = mutableListOf<MaintenanceInfo>()

    if (vehicle.oilFilterKm != null) {
        list.add(
            MaintenanceInfo(
                R.drawable.car_oil,
                if (vehicle.oilFilterKm < MIN_KM_OIL) R.drawable.danger else if (vehicle.oilFilterKm < MEDIUM_KM_OIL) R.drawable.warning else R.drawable.checked,
                context.resources.getString(R.string.oil_filter),
                getText(
                    context,
                    vehicle.oilFilterKm,
                ) + " ${vehicle.oilFilterKm}"
            )
        )
    }
    if (vehicle.cabinFilterKm != null) {
        list.add(
            MaintenanceInfo(
                R.drawable.cabin_filter,
                if (vehicle.cabinFilterKm < MIN_KM_CABIN) R.drawable.danger else if (vehicle.cabinFilterKm < MEDIUM_KM_CABIN) R.drawable.warning else R.drawable.checked,
                context.resources.getString(R.string.cabin_filter),
                getText(
                    context,
                    vehicle.cabinFilterKm,
                ) + " ${vehicle.cabinFilterKm}"
            )
        )
    }
    if (vehicle.frontBrakeDisksKm != null) {
        list.add(
            MaintenanceInfo(
                R.drawable.car_brake_disks,
                if (vehicle.frontBrakeDisksKm < MIN_KM_DISKS) R.drawable.danger else if (vehicle.frontBrakeDisksKm < MEDIUM_KM_DISKS) R.drawable.warning else R.drawable.checked,
                context.resources.getString(R.string.front_brake_disks),
                getText(
                    context,
                    vehicle.frontBrakeDisksKm,
                ) + " ${vehicle.frontBrakeDisksKm}"
            )
        )
    }
    if (vehicle.backBrakeDisksKm != null) {
        list.add(
            MaintenanceInfo(
                R.drawable.car_brake_disks,
                if (vehicle.backBrakeDisksKm < MIN_KM_DISKS) R.drawable.danger else if (vehicle.backBrakeDisksKm < MEDIUM_KM_DISKS) R.drawable.warning else R.drawable.checked,
                context.resources.getString(R.string.back_brake_disks),
                getText(
                    context,
                    vehicle.backBrakeDisksKm,
                ) + " ${vehicle.backBrakeDisksKm}"
            )
        )
    }

    if (vehicle.frontBrakeShimsKm != null) {
        list.add(
            MaintenanceInfo(
                R.drawable.car_shims,
                if (vehicle.frontBrakeShimsKm < MIN_KM_SHIMS) R.drawable.danger else if (vehicle.frontBrakeShimsKm < MEDIUM_KM_SHIMS) R.drawable.warning else R.drawable.checked,
                context.resources.getString(R.string.front_brake_shims),
                getText(
                    context,
                    vehicle.frontBrakeShimsKm,
                ) + " ${vehicle.frontBrakeShimsKm}"
            )
        )
    }

    if (vehicle.backBrakeShimsKm != null) {
        list.add(
            MaintenanceInfo(
                R.drawable.car_shims,
                if (vehicle.backBrakeShimsKm < MIN_KM_SHIMS) R.drawable.danger else if (vehicle.backBrakeShimsKm < MEDIUM_KM_SHIMS) R.drawable.warning else R.drawable.checked,
                context.resources.getString(R.string.back_brake_shims),
                getText(
                    context,
                    vehicle.backBrakeShimsKm,
                ) + " ${vehicle.backBrakeShimsKm}"
            )
        )
    }

    return list
}