package pt.ipp.estg.myapplication.ui.screens.locations

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.maps.model.LatLng
import com.shashank.sony.fancytoastlib.FancyToast
import pt.ipp.estg.myapplication.R
import pt.ipp.estg.myapplication.camera.CameraView
import pt.ipp.estg.myapplication.camera.CameraViewModel
import pt.ipp.estg.myapplication.enumerations.MapMode
import pt.ipp.estg.myapplication.enumerations.TypeLocation
import pt.ipp.estg.myapplication.models.GeneralViewModels
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.*
import pt.ipp.estg.myapplication.ui.theme.AppTypography
import java.io.File
import java.io.FileInputStream
import java.net.URI
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Preview
@Composable
fun PreviewSaveParking() {
    val generalViewModels: GeneralViewModels = viewModel()
    val cameraViewModel: CameraViewModel = viewModel()
    val preferencesViewModel: PreferencesViewModel = viewModel()
    val navController: NavHostController = rememberNavController()
    SaveParking(
        generalViewModels = generalViewModels,
        cameraViewModel = cameraViewModel,
        navController = navController,
        preferencesViewModel = preferencesViewModel
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SaveParking(
    generalViewModels: GeneralViewModels,
    cameraViewModel: CameraViewModel,
    navController: NavController,
    preferencesViewModel: PreferencesViewModel
) {
    val preferencesPark = preferencesViewModel.preferencesPark.observeAsState()

    val parkLocationString = stringResource(id = R.string.park_location)

    val isLocationImgSaved = cameraViewModel.isLocationImgSaved.observeAsState()

    val showCamera = remember { mutableStateOf(false) }
    val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    val longClickToSaveString = stringResource(id = R.string.long_click_on_location_to_save)

    val context = LocalContext.current

    fun handleImageCapture(uri: Uri) {
        cameraViewModel.saveLocationImg()
        showCamera.value = false
    }
    Log.d("IMG", "location status $isLocationImgSaved")

    fun cancelCamera() {
        showCamera.value = false
    }

    val ctx = LocalContext.current

    if (!showCamera.value) {
        Box(
            Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(15.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            ) {
                TitleLarge(text = stringResource(id = R.string.park_location))

                Spacer(modifier = Modifier.height(10.dp))

                Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.secondary)

                Spacer(modifier = Modifier.height(10.dp))

                //LOCATION IN MAPS
                if (preferencesPark.value?.district == " ") {
                    Card(
                        shape = RoundedCornerShape(15.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Row(
                            Modifier
                                .background(MaterialTheme.colorScheme.primary)
                                .border(1.dp, color = MaterialTheme.colorScheme.primary)
                                .padding(top = 30.dp, bottom = 30.dp),
                            horizontalArrangement = Arrangement.Center,

                            ) {
                            Column(
                                modifier = Modifier.padding(top = 15.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                LabelLarge(
                                    text = stringResource(id = R.string.park_location_not_defined),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                OutlinedButton(
                                    modifier = Modifier.padding(top = 15.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = MaterialTheme.colorScheme.secondary,
                                    ),
                                    shape = RoundedCornerShape(15.dp),
                                    onClick = {
                                        preferencesViewModel.setTypeLocation(TypeLocation.VEHICLE)
                                        generalViewModels.setTextMarker(parkLocationString)
                                        generalViewModels.setMapMode(MapMode.ADD_LOCATION)
                                        FancyToast.makeText(
                                            context,
                                            longClickToSaveString,
                                            FancyToast.LENGTH_SHORT,
                                            FancyToast.INFO,
                                            false
                                        ).show()
                                        navController.navigate("map")
                                    }) {
                                    LabelMedium(
                                        text = stringResource(id = R.string.add_location),
                                        color = MaterialTheme.colorScheme.onSecondary
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Card(
                        shape = RoundedCornerShape(15.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primary)
                        ) {
                            Row(
                                Modifier
                                    .background(MaterialTheme.colorScheme.primary)
                                    .border(1.dp, color = MaterialTheme.colorScheme.primary)
                                    .padding(15.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.park),
                                    contentDescription = "Park icon",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(5.dp)
                                )
                                Column(Modifier.width(200.dp)) {
                                    Text(
                                        text = preferencesPark.value?.street.toString(),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontWeight = FontWeight.Bold,
                                        style = AppTypography.titleMedium
                                    )
                                    Text(
                                        text = preferencesPark.value?.district.toString() + ", " + preferencesPark.value?.country.toString(),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        style = AppTypography.titleSmall
                                    )
                                    LabelTiny(
                                        text = stringResource(id = R.string.last_update) + ": " + preferencesPark.value?.lastUpdate.toString(),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    OutlinedButton(
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = MaterialTheme.colorScheme.secondary,
                                        ),
                                        shape = RoundedCornerShape(15.dp),
                                        onClick = {
                                            preferencesViewModel.setTypeLocation(TypeLocation.VEHICLE)
                                            generalViewModels.setTextMarker(parkLocationString)
                                            generalViewModels.setMapMode(MapMode.ADD_LOCATION)
                                            FancyToast.makeText(
                                                context,
                                                longClickToSaveString,
                                                FancyToast.LENGTH_SHORT,
                                                FancyToast.INFO,
                                                false
                                            ).show()
                                            navController.navigate("map")
                                        }) {
                                        LabelSmall(
                                            text = stringResource(id = R.string.edit),
                                            color = MaterialTheme.colorScheme.onSecondary
                                        )
                                    }
                                    IconButton(onClick = {
                                        preferencesViewModel.updatePreference(
                                            LatLng(0.0, 0.0),
                                            TypeLocation.VEHICLE
                                        )
                                    }) {
                                        Icon(Icons.Filled.Close, contentDescription = "")
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                //LOCATION PHOTO
                val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
                Card(
                    shape = RoundedCornerShape(15.dp),
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(vertical = 20.dp)
                    ) {
                        Spacer(modifier = Modifier.height(15.dp))

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp)
                        ) {
                            Spacer(modifier = Modifier.height(10.dp))
                            LabelLarge(
                                text = stringResource(id = R.string.park_image),
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        }

                        Spacer(modifier = Modifier.height(5.dp))

                        if (isLocationImgSaved.value == false) {
                            OutlinedButton(
                                modifier = Modifier.padding(vertical = 10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = MaterialTheme.colorScheme.secondary,
                                ),
                                shape = RoundedCornerShape(15.dp),
                                onClick = {
                                    if (cameraPermissionState.status.isGranted) {
                                        showCamera.value = true
                                    } else {
                                        cameraPermissionState.launchPermissionRequest()
                                        if (!cameraPermissionState.status.shouldShowRationale) {
                                            FancyToast.makeText(
                                                ctx,
                                                ctx.resources.getString(R.string.camera_denied),
                                                FancyToast.LENGTH_LONG,
                                                FancyToast.ERROR,
                                                false
                                            ).show()
                                        }
                                    }
                                }) {
                                LabelSmall(
                                    text = stringResource(id = R.string.take_pic),
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        }

                        //IMAGE
                        if (isLocationImgSaved.value == true) {
                            var file: File?
                            var b: Bitmap?
                            try {
                                file =
                                    File(
                                        URI("file:///storage/emulated/0/Android/media/pt.ipp.estg.myapplication/imgs/location.jpg")
                                    )
                                b = BitmapFactory.decodeStream(FileInputStream(file))
                            } catch (_: Exception) {
                                file = null
                                b = null
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(onClick = {
                                    cameraViewModel.deleteLocationImg()
                                }) {
                                    Icon(
                                        Icons.Filled.Close,
                                        contentDescription = "",
                                        tint = MaterialTheme.colorScheme.background
                                    )
                                }
                            }

                            if (file != null && b != null) {
                                Spacer(modifier = Modifier.height(30.dp))
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Image(
                                        bitmap = b.asImageBitmap(),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(300.dp)
                                            .fillMaxWidth()
                                            .border(
                                                2.dp,
                                                Color.Gray,
                                            )
                                    )
                                    Spacer(modifier = Modifier.height(30.dp))
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    } else {
        CameraView(
            outputDirectory = cameraViewModel.getOutputDirectory(),
            fileName = "location",
            executor = cameraExecutor,
            onImageCaptured = ::handleImageCapture,
            onError = { Log.e("img", "View error:", it) },
            cancel = { cancelCamera() }
        )
    }
}
