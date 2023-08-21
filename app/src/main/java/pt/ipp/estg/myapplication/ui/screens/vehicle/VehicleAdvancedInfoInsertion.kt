package pt.ipp.estg.myapplication.ui.screens.vehicle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shashank.sony.fancytoastlib.FancyToast
import pt.ipp.estg.myapplication.R
import pt.ipp.estg.myapplication.enumerations.VehiclesTypes
import pt.ipp.estg.myapplication.ui.screens.my_coupons.RowToDisplay
import pt.ipp.estg.myapplication.ui.screens.ui_components.IntTextField
import pt.ipp.estg.myapplication.ui.screens.ui_components.NormalCheckBox
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.LabelMedium

@Preview
@Composable
fun PreviewVehicleAdvancedInfoInsertion() {
    val vehicleType = remember { mutableStateOf(VehiclesTypes.AUTOMOBILE) }
    val pageToShow = remember { mutableStateOf(PageToShow.BASIC_INFO) }
    val oilFilterKm = remember { mutableStateOf(0) }
    val cabinFilterKm = remember { mutableStateOf((0)) }
    val frontBrakeShimsKm = remember { mutableStateOf(0) }
    val backBrakeShimsKm = remember { mutableStateOf(0) }
    val frontBrakeDisksKm = remember { mutableStateOf(0) }
    val backBrakeDisksKm = remember { mutableStateOf(0) }
    val oilFilterCheck = remember { mutableStateOf(true) }
    val cabinFilterCheck = remember { mutableStateOf(true) }
    val backBrakeShimsCheck = remember { mutableStateOf(true) }
    val frontBrakeShimsCheck = remember { mutableStateOf(true) }
    val backBrakeDisksCheck = remember { mutableStateOf(true) }
    val frontBrakeDisksCheck = remember { mutableStateOf(true) }

    VehicleAdvancedInfoInsertion(
        pageToShow,
        vehicleType,
        oilFilterKm,
        cabinFilterKm,
        frontBrakeShimsKm,
        backBrakeShimsKm,
        frontBrakeDisksKm,
        backBrakeDisksKm,
        oilFilterCheck,
        cabinFilterCheck,
        backBrakeShimsCheck,
        frontBrakeShimsCheck,
        backBrakeDisksCheck,
        frontBrakeDisksCheck
    ) {}
}

@Composable
fun VehicleAdvancedInfoInsertion(
    pageToShow: MutableState<PageToShow>,
    vehicleTypeChosen: MutableState<VehiclesTypes>,
    oilFilterKm: MutableState<Int>,
    cabinFilterKm: MutableState<Int>,
    frontBrakeShimsKm: MutableState<Int>,
    backBrakeShimsKm: MutableState<Int>,
    frontBrakeDisksKm: MutableState<Int>,
    backBrakeDisksKm: MutableState<Int>,
    oilFilterCheck: MutableState<Boolean>,
    cabinFilterCheck: MutableState<Boolean>,
    backBrakeShimsCheck: MutableState<Boolean>,
    frontBrakeShimsCheck: MutableState<Boolean>,
    backBrakeDisksCheck: MutableState<Boolean>,
    frontBrakeDisksCheck: MutableState<Boolean>,
    submitOnClick: () -> Unit
) {
    val ctx = LocalContext.current
    //get actual size of screen
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp

    //validate
    fun isDataValid(): Boolean {
        var result = true

        if (oilFilterCheck.value) {
            result = (oilFilterKm.value > 0)
        } else if (cabinFilterCheck.value && vehicleTypeChosen.value == VehiclesTypes.AUTOMOBILE) {
            result = (cabinFilterKm.value > 0)

        } else if (backBrakeShimsCheck.value) {
            result = (backBrakeShimsKm.value > 0)
        } else if (frontBrakeShimsCheck.value) {
            result = (frontBrakeShimsKm.value > 0)

        } else if (backBrakeDisksCheck.value) {
            result = (backBrakeDisksKm.value > 0)
        } else if (frontBrakeDisksCheck.value) {
            result = (frontBrakeShimsKm.value > 0)
        }
        return result
    }

    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            LabelMedium(text = stringResource(id = R.string.how_muck_km_to_get))
            Divider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 20.dp)
            )

            // OIL AND CABIN
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                //oil filter
                Column(
                    modifier = if (vehicleTypeChosen.value == VehiclesTypes.MOTORCYCLE) {
                        Modifier.fillMaxWidth()
                    } else {
                        Modifier.width((screenWidth / 2.5).dp)
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        LabelMedium(
                            text = stringResource(id = R.string.oil_filter),
                            color = MaterialTheme.colorScheme.primary
                        )
                        NormalCheckBox(
                            checked = oilFilterCheck.value,
                            onCheckedChange = { oilFilterCheck.value = !oilFilterCheck.value }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    if (oilFilterCheck.value) {
                        IntTextField(
                            numberState = oilFilterKm,
                            modifier = Modifier.fillMaxWidth(),
                            ignoreValue = 0
                        )
                    } else {
                        Spacer(modifier = Modifier.height(56.dp))
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                //cabin air filter
                if (vehicleTypeChosen.value == VehiclesTypes.AUTOMOBILE) {
                    Column(
                        modifier = Modifier.width((screenWidth / 2.5).dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            LabelMedium(
                                text = stringResource(id = R.string.cabin_filter),
                                color = MaterialTheme.colorScheme.primary
                            )
                            NormalCheckBox(
                                checked = cabinFilterCheck.value,
                                onCheckedChange = {
                                    cabinFilterCheck.value = !cabinFilterCheck.value
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        if (cabinFilterCheck.value) {
                            IntTextField(
                                numberState = cabinFilterKm,
                                modifier = Modifier.fillMaxWidth(), ignoreValue = 0
                            )
                        } else {
                            Spacer(modifier = Modifier.height(58.dp))
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }


            //FRONT AND BACK SHIMS
            RowToDisplay(
                leftString = stringResource(id = R.string.back_brake_shims),
                rightString = stringResource(id = R.string.front_brake_shims),
                leftValue = backBrakeShimsKm,
                rightValue = frontBrakeShimsKm,
                leftCheckedValue = backBrakeShimsCheck,
                rightCheckedValue = frontBrakeShimsCheck,
            )

            //FRONT AND BACK DISKS
            RowToDisplay(
                leftString = stringResource(id = R.string.back_brake_disks),
                rightString = stringResource(id = R.string.front_brake_disks),
                leftValue = backBrakeDisksKm,
                rightValue = frontBrakeDisksKm,
                leftCheckedValue = backBrakeDisksCheck,
                rightCheckedValue = frontBrakeDisksCheck
            )
        }

        //PREVIOUS AND SUBMIT BUTTON
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            OutlinedButton(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colorScheme.secondary,
                ),
                shape = RoundedCornerShape(15.dp),
                onClick = {
                    pageToShow.value = PageToShow.BASIC_INFO
                }) {
                LabelMedium(
                    text = stringResource(id = R.string.previous),
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }


            OutlinedButton(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(id = R.color.success_darker),
                ),
                shape = RoundedCornerShape(15.dp),
                onClick = {
                    if (!isDataValid()) {
                        FancyToast.makeText(
                            ctx,
                            ctx.resources.getString(R.string.bad_input),
                            FancyToast.LENGTH_LONG,
                            FancyToast.ERROR,
                            false
                        ).show()
                    } else {
                        submitOnClick()
                    }
                }) {
                LabelMedium(
                    text = stringResource(id = R.string.submit),
                    color = Color.White
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewRowToDisplay() {
    val leftValue = remember { mutableStateOf(0) }
    val rightValue = remember { mutableStateOf(0) }
    val leftCheckedValue = remember { mutableStateOf(true) }
    val rightCheckedValue = remember { mutableStateOf(true) }

    RowToDisplay(
        "Left", "Right", leftValue, rightValue, leftCheckedValue, rightCheckedValue
    )
}

@Composable
fun RowToDisplay(
    leftString: String,
    rightString: String,
    leftValue: MutableState<Int>,
    rightValue: MutableState<Int>,
    leftCheckedValue: MutableState<Boolean>,
    rightCheckedValue: MutableState<Boolean>
) {
    //get actual size of screen
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier.width((screenWidth / 2.5).dp)
        ) {
            //oil filter
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                LabelMedium(
                    text = leftString,
                    color = MaterialTheme.colorScheme.primary
                )
                NormalCheckBox(
                    checked = leftCheckedValue.value,
                    onCheckedChange = { leftCheckedValue.value = !leftCheckedValue.value })
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (leftCheckedValue.value) {
                IntTextField(
                    numberState = leftValue,
                    modifier = Modifier.fillMaxWidth(),
                    ignoreValue = 0
                )
            } else {
                Spacer(modifier = Modifier.height(58.dp))
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        Column(
            Modifier.width((screenWidth / 2.5).dp)
        ) {
            //oil filter
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                LabelMedium(
                    text = rightString,
                    color = MaterialTheme.colorScheme.primary
                )
                NormalCheckBox(
                    checked = rightCheckedValue.value,
                    onCheckedChange = { rightCheckedValue.value = !rightCheckedValue.value })
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (rightCheckedValue.value) {
                IntTextField(
                    numberState = rightValue,
                    modifier = Modifier.fillMaxWidth(),
                    ignoreValue = 0
                )
            } else {
                Spacer(modifier = Modifier.height(56.dp))
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}