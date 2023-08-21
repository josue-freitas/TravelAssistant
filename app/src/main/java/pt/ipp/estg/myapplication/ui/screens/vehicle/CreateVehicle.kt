package pt.ipp.estg.myapplication.ui.screens.vehicle

import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import pt.ipp.estg.myapplication.R
import pt.ipp.estg.myapplication.camera.CameraView
import pt.ipp.estg.myapplication.camera.CameraViewModel
import pt.ipp.estg.myapplication.enumerations.VehiclesTypes
import pt.ipp.estg.myapplication.models.database.vehicle.Vehicle
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

enum class PageToShow {
    BASIC_INFO, CAMERA, TECH_INFO
}

@Preview
@Composable
fun PreviewSaveParking() {
    val navController: NavHostController = rememberNavController()
    CreateVehicle(navController = navController)
}

@Composable
fun CreateVehicle(
    navController: NavController,
) {
    val cameraViewModel: CameraViewModel = viewModel()
    val vehicleViewModels: VehicleViewModels = viewModel()

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

    //state control for basic information
    val plate = rememberSaveable { mutableStateOf("") }
    val brand = rememberSaveable { mutableStateOf("") }
    val model = rememberSaveable { mutableStateOf("") }
    val specification = rememberSaveable { mutableStateOf("") }
    val vehicleType = rememberSaveable { mutableStateOf(VehiclesTypes.AUTOMOBILE) }
    val associationVehicle = rememberSaveable { mutableStateOf("") }

    //state control for advanced information
    val oilFilterKm = rememberSaveable { mutableStateOf(0) }
    val cabinFilterKm = rememberSaveable { mutableStateOf((0)) }
    val frontBrakeShimsKm = rememberSaveable { mutableStateOf(0) }
    val backBrakeShimsKm = rememberSaveable { mutableStateOf(0) }
    val frontBrakeDisksKm = rememberSaveable { mutableStateOf(0) }
    val backBrakeDisksKm = rememberSaveable { mutableStateOf(0) }

    //state for checkboxes
    // checkers
    val oilFilterCheck = rememberSaveable { mutableStateOf(false) }
    val cabinFilterCheck = rememberSaveable { mutableStateOf(false) }
    val backBrakeShimsCheck = rememberSaveable { mutableStateOf(false) }
    val frontBrakeShimsCheck = rememberSaveable { mutableStateOf(false) }
    val backBrakeDisksCheck = rememberSaveable { mutableStateOf(false) }
    val frontBrakeDisksCheck = rememberSaveable { mutableStateOf(false) }


    //camera
    val photoUri = rememberSaveable { mutableStateOf("") }
    val isPhotoTaken = rememberSaveable { mutableStateOf(false) }

    val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    fun handleImageCapture(uri: Uri) {
        Log.d("img", uri.toString())
        isPhotoTaken.value = true
        photoUri.value = "${cameraViewModel.imgNameVehicle}.jpg"
        pageToShow.value = PageToShow.BASIC_INFO
    }

    fun cancelCamera() {
        pageToShow.value = PageToShow.BASIC_INFO
    }

    fun addVehicle() {
        val vehicle = Vehicle(
            plate = plate.value,
            brand = brand.value,
            model = model.value,
            vehicleType = vehicleType.value,
            specification = specification.value,
            associationVehicle = associationVehicle.value,
            uriString = photoUri.value,

            oilFilterKm = if (oilFilterCheck.value) oilFilterKm.value else null,
            cabinFilterKm = if (cabinFilterCheck.value && vehicleType.value == VehiclesTypes.AUTOMOBILE) cabinFilterKm.value else null,

            frontBrakeShimsKm = if (frontBrakeShimsCheck.value) frontBrakeShimsKm.value else null,
            backBrakeShimsKm = if (backBrakeShimsCheck.value) backBrakeShimsKm.value else null,

            frontBrakeDisksKm = if (frontBrakeDisksCheck.value) frontBrakeDisksKm.value else null,
            backBrakeDisksKm = if (backBrakeDisksCheck.value) backBrakeDisksKm.value else null,
        )
        cameraViewModel.updateImgName()
        vehicleViewModels.insert(vehicle)
        navController.navigate("vehicles_list")
    }

    if (pageToShow.value != PageToShow.CAMERA) {
        Log.d("camera", "img name: ${cameraViewModel.imgNameVehicle}")
        CarTemplate {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                TitleSmall(
                    text = stringResource(id = R.string.manual_registration),
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(30.dp))
                when (pageToShow.value) {
                    PageToShow.BASIC_INFO ->
                        VehicleBasicInfoInsertion(
                            pageToShow = pageToShow,
                            vehicleType = vehicleType,
                            photoUri = photoUri,
                            plate = plate,
                            brand = brand,
                            model = model,
                            specification = specification,
                            associationVehicle = associationVehicle,
                        )

                    PageToShow.TECH_INFO ->
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
                            submitOnClick = { addVehicle() }
                        )
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
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
