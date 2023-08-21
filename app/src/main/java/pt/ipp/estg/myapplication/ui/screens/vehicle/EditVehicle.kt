package pt.ipp.estg.myapplication.ui.screens.vehicle

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import pt.ipp.estg.myapplication.R
import pt.ipp.estg.myapplication.camera.CameraView
import pt.ipp.estg.myapplication.camera.CameraViewModel
import pt.ipp.estg.myapplication.enumerations.VehiclesTypes
import pt.ipp.estg.myapplication.models.database.vehicle.Vehicle
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.TitleSmall
import java.io.File
import java.net.URI
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


/*
  This composable doesn't have a preview since works as an intermediary of this module and
  doesn't have a graphic interface.
 */

@Composable
fun EditVehicle(
    plate: String,
    navController: NavController
) {
    val vehicleViewModels: VehicleViewModels = viewModel()
    val vehicle = vehicleViewModels.getVehicleByPlate(plate).observeAsState()

    //page control
    val pageToShow = rememberSaveable { mutableStateOf(PageToShow.BASIC_INFO) }
    //alter the back button function
    when (pageToShow.value) {
        PageToShow.BASIC_INFO -> BackHandler(enabled = true) {
            pageToShow.value = PageToShow.BASIC_INFO
        }
        PageToShow.CAMERA -> BackHandler(enabled = true) {
            pageToShow.value = PageToShow.BASIC_INFO
        }
        PageToShow.TECH_INFO -> BackHandler(enabled = true) {
            pageToShow.value = PageToShow.BASIC_INFO
        }
    }

    if (vehicle.value != null) {
        //state control for basic information
        val plate = rememberSaveable { mutableStateOf(plate) }
        val brand = rememberSaveable { mutableStateOf(vehicle.value!!.brand) }
        val model = rememberSaveable { mutableStateOf(vehicle.value!!.model) }
        val specification = rememberSaveable { mutableStateOf(vehicle.value!!.specification) }
        val vehicleType = rememberSaveable { mutableStateOf(vehicle.value!!.vehicleType) }
        val associationVehicle = rememberSaveable { mutableStateOf(vehicle.value!!.associationVehicle) }

        //state control for advanced information
        val oilFilterKm = rememberSaveable { mutableStateOf(vehicle.value!!.oilFilterKm ?: 0) }
        val cabinFilterKm = rememberSaveable { mutableStateOf(vehicle.value!!.cabinFilterKm ?: 0) }
        val frontBrakeShimsKm = rememberSaveable { mutableStateOf(vehicle.value!!.frontBrakeShimsKm ?: 0) }
        val backBrakeShimsKm = rememberSaveable { mutableStateOf(vehicle.value!!.backBrakeShimsKm ?: 0) }
        val frontBrakeDisksKm = rememberSaveable { mutableStateOf(vehicle.value!!.frontBrakeDisksKm ?: 0) }
        val backBrakeDisksKm = rememberSaveable { mutableStateOf(vehicle.value!!.backBrakeDisksKm ?: 0) }

        //state for checkboxes
        // checkers
        val oilFilterCheck = rememberSaveable { mutableStateOf(vehicle.value!!.oilFilterKm != null) }
        val cabinFilterCheck = rememberSaveable { mutableStateOf(vehicle.value!!.cabinFilterKm != null) }
        val backBrakeShimsCheck =
            rememberSaveable { mutableStateOf(vehicle.value!!.backBrakeShimsKm != null) }
        val frontBrakeShimsCheck =
            rememberSaveable { mutableStateOf(vehicle.value!!.frontBrakeShimsKm != null) }
        val backBrakeDisksCheck =
            rememberSaveable { mutableStateOf(vehicle.value!!.backBrakeDisksKm != null) }
        val frontBrakeDisksCheck =
            rememberSaveable { mutableStateOf(vehicle.value!!.frontBrakeDisksKm != null) }

        //camera
        val cameraViewModel: CameraViewModel = viewModel()
        val newPhotoUri = rememberSaveable { mutableStateOf(vehicle.value!!.uriString ?: "") }

        val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

        fun handleImageCapture(uri: Uri) {
            Log.d("img", uri.toString())
            newPhotoUri.value = "${cameraViewModel.imgNameVehicle}.jpg"
            pageToShow.value = PageToShow.BASIC_INFO
        }

        fun cancelCamera() {
            pageToShow.value = PageToShow.BASIC_INFO
        }


        fun editVehicle() {
            val newVehicle = Vehicle(
                plate = plate.value,
                brand = brand.value,
                model = model.value,
                vehicleType = vehicleType.value,
                specification = specification.value,
                associationVehicle = associationVehicle.value,
                uriString = newPhotoUri.value,

                oilFilterKm = if (oilFilterCheck.value) oilFilterKm.value else null,
                cabinFilterKm = if (cabinFilterCheck.value && vehicleType.value == VehiclesTypes.AUTOMOBILE) cabinFilterKm.value else null,

                frontBrakeShimsKm = if (frontBrakeShimsCheck.value) frontBrakeShimsKm.value else null,
                backBrakeShimsKm = if (backBrakeShimsCheck.value) backBrakeShimsKm.value else null,

                frontBrakeDisksKm = if (frontBrakeDisksCheck.value) frontBrakeDisksKm.value else null,
                backBrakeDisksKm = if (backBrakeDisksCheck.value) backBrakeDisksKm.value else null,
            )
            cameraViewModel.updateImgName()
            var file: File? = null
            try {
                file =
                    File(URI("file:///storage/emulated/0/Android/media/pt.ipp.estg.myapplication/imgs/${vehicle.value!!.uriString}"))
                file.delete()
            } catch (_: Exception) {
            }
            vehicleViewModels.insert(newVehicle)
            navController.navigate("vehicles_list")
        }

        if (pageToShow.value != PageToShow.CAMERA) {
            CarTemplate {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    TitleSmall(
                        text = stringResource(id = R.string.manual_registration),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    if (pageToShow.value == PageToShow.BASIC_INFO) {
                        VehicleBasicInfoInsertion(
                            pageToShow = pageToShow,
                            vehicleType = vehicleType,
                            photoUri = newPhotoUri,
                            plate = plate,
                            brand = brand,
                            model = model,
                            specification = specification,
                            associationVehicle = associationVehicle,
                            editMode = true
                        )
                    } else {
                        VehicleAdvancedInfoInsertion(
                            pageToShow = pageToShow,
                            vehicleTypeChosen = vehicleType,
                            oilFilterKm = oilFilterKm,
                            cabinFilterKm = cabinFilterKm,
                            frontBrakeShimsKm = frontBrakeShimsKm,
                            backBrakeShimsKm = backBrakeShimsKm,
                            frontBrakeDisksKm = frontBrakeDisksKm,
                            backBrakeDisksKm = backBrakeDisksKm,
                            oilFilterCheck = oilFilterCheck,
                            cabinFilterCheck = cabinFilterCheck,
                            backBrakeShimsCheck = backBrakeShimsCheck,
                            frontBrakeShimsCheck = frontBrakeShimsCheck,
                            backBrakeDisksCheck = backBrakeDisksCheck,
                            frontBrakeDisksCheck = frontBrakeDisksCheck,
                            submitOnClick = { editVehicle() }
                        )
                    }
                }
            }
        } else {
            CameraView(
                outputDirectory = cameraViewModel.getOutputDirectory(),
                fileName = cameraViewModel.imgNameVehicle,
                executor = cameraExecutor,
                onImageCaptured = ::handleImageCapture,
                onError = { Log.e("img", "View error:", it) },
                cancel = { cancelCamera() }
            )
        }
    }
}
