package pt.ipp.estg.myapplication.ui.screens.vehicle

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shashank.sony.fancytoastlib.FancyToast
import pt.ipp.estg.myapplication.R
import pt.ipp.estg.myapplication.enumerations.VehiclesTypes
import pt.ipp.estg.myapplication.helper.Validations
import pt.ipp.estg.myapplication.helper.VehicleListInfo
import pt.ipp.estg.myapplication.ui.screens.ui_components.DropMenu
import pt.ipp.estg.myapplication.ui.screens.ui_components.NormalTextField
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.LabelMedium
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.LabelSmall
import pt.ipp.estg.myapplication.ui.theme.AppTypography
import java.io.File
import java.io.FileInputStream
import java.net.URI
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

enum class MsgReturn { EMPTY_FIELDS, BAD_INPUT }

@Preview
@Composable
fun PreviewVehicleBasicInfoInsertion() {
    val pageToShow = rememberSaveable { mutableStateOf(PageToShow.BASIC_INFO) }
    val plate = rememberSaveable { mutableStateOf("") }
    val brand = rememberSaveable { mutableStateOf("") }
    val model = rememberSaveable { mutableStateOf("") }
    val specification = rememberSaveable { mutableStateOf("") }
    val vehicleType = rememberSaveable { mutableStateOf(VehiclesTypes.AUTOMOBILE) }
    val associationVehicle = rememberSaveable { mutableStateOf("") }

    val photoUri = rememberSaveable { mutableStateOf("") }

    VehicleBasicInfoInsertion(
        pageToShow,
        vehicleType,
        photoUri,
        plate,
        brand,
        model,
        specification,
        associationVehicle
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VehicleBasicInfoInsertion(
    pageToShow: MutableState<PageToShow>,
    vehicleType: MutableState<VehiclesTypes>,
    photoUri: MutableState<String>,
    plate: MutableState<String>,
    brand: MutableState<String>,
    model: MutableState<String>,
    specification: MutableState<String>,
    associationVehicle: MutableState<String>,
    editMode: Boolean = false
) {
    val ctx = LocalContext.current

    var tabRowStatus by rememberSaveable { mutableStateOf(0) }
    var userClickedOnNext by rememberSaveable { mutableStateOf(false) }

    //text fields
    val listVehicleType =
        listOf(stringResource(id = R.string.vehicle), stringResource(id = R.string.motorcycle))
    val listBrand =
        if (vehicleType.value == VehiclesTypes.AUTOMOBILE) VehicleListInfo.getBrandListCar(ctx)
        else VehicleListInfo.getBrandListMotorcycle(ctx)

    val listModels =
        if (vehicleType.value == VehiclesTypes.AUTOMOBILE) VehicleListInfo.getModelListCar(
            context = ctx,
            brand = brand.value
        )
        else VehicleListInfo.getModelListMotorcycle(context = ctx, brand = brand.value)

    val listSpecification =
        if (vehicleType.value == VehiclesTypes.AUTOMOBILE)
            VehicleListInfo.getSpecificationCar(ctx, model = model.value)
        else VehicleListInfo.getSpecificationMotorcycle(ctx, model = model.value)


    val listAssociationVehicles =
        listOf(stringResource(id = R.string.personal), stringResource(id = R.string.profissional))

    //validate
    data class IsDataValidReturnValue(val msg: MsgReturn, val result: Boolean)

    fun isDataValid(): IsDataValidReturnValue {
        var result = true
        var msg: MsgReturn = MsgReturn.BAD_INPUT


        if (!Validations.validatePlate(plate.value)) {
            msg = MsgReturn.BAD_INPUT
            result = false
        }

        if (plate.value == "" || brand.value == "" ||
            model.value == "" || specification.value == "" ||
            associationVehicle.value == ""
        ) {
            msg = MsgReturn.EMPTY_FIELDS
            result = false
        }
        return IsDataValidReturnValue(msg, result)
    }


    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        //type of vehicle
        LabelMedium(
            text = stringResource(id = R.string.what_is_vehicle_type),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(20.dp))
        TabRow(
            selectedTabIndex =
            when (vehicleType.value) {
                VehiclesTypes.AUTOMOBILE -> 0
                VehiclesTypes.MOTORCYCLE -> 1
            },
            backgroundColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.secondary
        ) {
            listVehicleType.forEachIndexed { index, title ->
                Tab(
                    selected = tabRowStatus == index,
                    onClick = {
                        tabRowStatus = index;
                        brand.value = ""
                        model.value = ""
                        specification.value = ""
                        when (index) {
                            0 -> vehicleType.value = VehiclesTypes.AUTOMOBILE
                            1 -> vehicleType.value = VehiclesTypes.MOTORCYCLE
                        }
                    }) {
                    Column(
                        verticalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.padding(vertical = 10.dp)
                    ) {
                        LabelSmall(
                            text = title, color =
                            if (tabRowStatus == index) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        //plate
        LabelMedium(
            text = stringResource(id = R.string.plate),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(10.dp))
        if (editMode) {
            OutlinedTextField(
                value = plate.value,
                onValueChange = { plate.value = it },
                enabled = false,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("plate_input"),
                textStyle = AppTypography.labelLarge,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = MaterialTheme.colorScheme.background,
                    textColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary,
                ),
                singleLine = true,
            )
        } else {
            NormalTextField(
                stringState = plate,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("plate_input"),
                errorMsg = stringResource(id = R.string.bad_plate_input),
                validation = { str -> Validations.validatePlate(str) },
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        //brand
        LabelMedium(
            text = stringResource(id = R.string.brand),
            color = MaterialTheme.colorScheme.primary
        )
        DropMenu(
            listBrand,
            brand,
            showError = userClickedOnNext && brand.value == "",
            onValueChange = {
                specification.value = ""
                model.value = ""
            },
            testTag = "brand_input"
        )
        Spacer(modifier = Modifier.height(20.dp))

        //model
        LabelMedium(
            text = stringResource(id = R.string.model),
            color = MaterialTheme.colorScheme.primary
        )
        Log.d("dropmenu", model.value)
        DropMenu(
            listModels,
            model,
            showError = userClickedOnNext && model.value == "",
            testTag = "model_input"
        )
        Spacer(modifier = Modifier.height(20.dp))

        //specification
        LabelMedium(
            text = stringResource(id = R.string.specification),
            color = MaterialTheme.colorScheme.primary
        )
        DropMenu(
            listSpecification,
            specification,
            showError = userClickedOnNext && specification.value == ""
        )
        Spacer(modifier = Modifier.height(20.dp))

        //association with vehicle
        LabelMedium(
            text = stringResource(id = R.string.association_with_vehicle),
            color = MaterialTheme.colorScheme.primary
        )
        DropMenu(
            listAssociationVehicles,
            associationVehicle,
            showError = userClickedOnNext && associationVehicle.value == ""
        )
        Spacer(modifier = Modifier.height(20.dp))

        //camera
        val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                LabelMedium(
                    text = stringResource(id = R.string.take_pic),
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = {
                    if (cameraPermissionState.status.isGranted) {
                        pageToShow.value = PageToShow.CAMERA
                    } else {
                        cameraPermissionState.launchPermissionRequest()
                        if (!cameraPermissionState.status.shouldShowRationale) {
                            FancyToast.makeText(
                                ctx,
                                ctx.resources.getString(R.string.camera_denied),
                                FancyToast.LENGTH_LONG,
                                FancyToast.ERROR,
                                false
                            ).show();
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.Camera,
                        tint = MaterialTheme.colorScheme.primary, contentDescription = "camera"
                    )
                }
            }

            //IMAGE
            if (photoUri.value != "") {
                var file: File?
                var b: Bitmap?
                try {
                    file =
                        File(URI("file:///storage/emulated/0/Android/media/pt.ipp.estg.myapplication/imgs/${photoUri.value}"))
                    b = BitmapFactory.decodeStream(FileInputStream(file))
                } catch (_: Exception) {
                    file = null
                    b = null
                }
                if (file != null && b != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    LabelMedium(
                        text = stringResource(id = R.string.preview),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.height(5.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
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

                            IconButton(onClick = {
                                photoUri.value = ""
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    tint = MaterialTheme.colorScheme.primary,
                                    contentDescription = "camera"
                                )
                            }

                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        //next button
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor =
                    MaterialTheme.colorScheme.secondary
                ),
                shape = RoundedCornerShape(15.dp),
                onClick = {
                    userClickedOnNext = true;
                    val (msg, result) = isDataValid()
                    if (result) {
                        pageToShow.value = PageToShow.TECH_INFO
                    } else {
                        val msgToSend: String = if (msg == MsgReturn.EMPTY_FIELDS)
                            ctx.resources.getString(R.string.empty_fields)
                        else
                            ctx.resources.getString(R.string.bad_input)

                        FancyToast.makeText(
                            ctx,
                            msgToSend,
                            FancyToast.LENGTH_LONG,
                            FancyToast.ERROR,
                            false
                        ).show();
                    }
                }) {
                LabelMedium(
                    text = stringResource(id = R.string.next),
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}
