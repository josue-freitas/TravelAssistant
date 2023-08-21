package pt.ipp.estg.myapplication.ui.screens.map

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.OutlinedButton
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import com.shashank.sony.fancytoastlib.FancyToast
import pt.ipp.estg.myapplication.R
import pt.ipp.estg.myapplication.enumerations.MaintenanceStatus
import pt.ipp.estg.myapplication.enumerations.MapMode
import pt.ipp.estg.myapplication.enumerations.VehiclesTypes
import pt.ipp.estg.myapplication.location.LocationDetails
import pt.ipp.estg.myapplication.models.GeneralViewModels
import pt.ipp.estg.myapplication.models.database.vehicle.Vehicle
import pt.ipp.estg.myapplication.ui.screens.vehicle.VehicleViewModels
import pt.ipp.estg.myapplication.services.CountKmService
import pt.ipp.estg.myapplication.ui.screens.locations.PreferencesViewModel
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.LabelLarge
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.LabelMedium
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.LabelSmall
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.LabelTiny
import kotlin.math.roundToInt


@Preview
@Composable
fun PreviewLocations() {
    val generalViewModels: GeneralViewModels = viewModel()
    val preferencesViewModel: PreferencesViewModel = viewModel()
    val isDark = remember { mutableStateOf(false) }
    MapScreen(
        LocationDetails(41.366873, -8.194834, 0.0f), generalViewModels, preferencesViewModel, isDark
    )
}

@Composable
fun MapScreen(
    currentLocation: LocationDetails?,
    generalViewModels: GeneralViewModels,
    preferencesViewModel: PreferencesViewModel,
    isDark: State<Boolean?>
) {
    val context = LocalContext.current
    val uiSettings = remember {
        MapUiSettings(myLocationButtonEnabled = true)
    }
    var properties by remember {
        mutableStateOf(
            MapProperties(
                isMyLocationEnabled = true,
                mapStyleOptions = MapStyleOptions(MapStyleLight.json)
            )
        )
    }
    properties = if (isDark.value == true) {
        (MapProperties(
            isMyLocationEnabled = true,
            mapStyleOptions = MapStyleOptions(MapStyleDark.json)
        ))
    } else {
        (MapProperties(
            isMyLocationEnabled = true,
            mapStyleOptions = MapStyleOptions(MapStyleLight.json)
        ))
    }

    val locationSaved = stringResource(id = R.string.location_saved)
    val destinationRoute = generalViewModels.destinationRoute.observeAsState()

    when (generalViewModels.mapMode.observeAsState().value) {
        //User is gonna add a location
        MapMode.ADD_LOCATION -> {
            GoogleMap(
                modifier = Modifier.fillMaxHeight(),
                uiSettings = uiSettings,
                properties = properties,
                cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(
                        LatLng(
                            currentLocation?.latitude!!.toDouble(),
                            currentLocation?.longitude!!.toDouble()
                        ), 10f
                    )
                },
                onMapLongClick = {
                    generalViewModels.setMapMode(MapMode.NORMAL)
                    FancyToast.makeText(
                        context,
                        locationSaved,
                        FancyToast.LENGTH_LONG,
                        FancyToast.SUCCESS,
                        false
                    ).show()
                    generalViewModels.addMarker(it)
                    preferencesViewModel.updatePreference(it)
                }
            )
        }
        //User clicked to see on map a specific location
        MapMode.ZOOM_IN -> {
            GoogleMap(
                modifier = Modifier.fillMaxHeight(),
                uiSettings = uiSettings,
                properties = properties,
                cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(
                        LatLng(
                            generalViewModels.marker.value!!.latitude,
                            generalViewModels.marker.value!!.longitude
                        ), 20f
                    )
                }
            ) {
                if (generalViewModels.marker.observeAsState().value != null) {
                    Marker(
                        generalViewModels, currentLocation,
                        stringResource(id = R.string.long_click_to_navigate)
                    )
                }
            }
        }
        MapMode.NORMAL -> {
            GoogleMap(
                modifier = Modifier.fillMaxHeight(),
                uiSettings = uiSettings,
                properties = properties,
                cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(
                        LatLng(
                            currentLocation?.latitude!!.toDouble(),
                            currentLocation?.longitude!!.toDouble()
                        ), 18f
                    )
                }
            ) {
                if (generalViewModels.marker.observeAsState().value != null) {
                    Marker(
                        generalViewModels, currentLocation,
                        stringResource(id = R.string.long_click_to_navigate)
                    )
                }
            }
        }
        //User asked for a route
        MapMode.ROUTES -> {
            GoogleMap(
                modifier = Modifier.fillMaxHeight(),
                uiSettings = uiSettings,
                properties = properties,
                cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(
                        LatLng(
                            currentLocation!!.latitude,
                            currentLocation.longitude,
                        ), 20f
                    )
                }
            ) {
                if (generalViewModels.marker.observeAsState().value != null) {
                    Marker(
                        generalViewModels, currentLocation,
                        stringResource(id = R.string.location_you_are_navigating_to)
                    )
                }
                generalViewModels.routesDetails.value?.let { Polyline(points = it) }
            }
        }
    }
    Speedometer(currentLocation, generalViewModels, preferencesViewModel, destinationRoute)
}

@Composable
fun Marker(
    generalViewModels: GeneralViewModels,
    currentLocation: LocationDetails?,
    string: String
) {
    Marker(
        position = LatLng(
            generalViewModels.marker.value!!.latitude,
            generalViewModels.marker.value!!.longitude
        ),
        title = generalViewModels.textMarket.value,
        snippet = string,
        onInfoWindowLongClick = {
            if (generalViewModels.mapMode.value == MapMode.NORMAL || generalViewModels.mapMode.value == MapMode.ZOOM_IN) {
                generalViewModels.getRoute(
                    currentLocation,
                    generalViewModels.marker.value!!
                )
                generalViewModels.setMapMode(MapMode.ROUTES)
            }
        },
        onClick = {
            it.showInfoWindow()
            true
        },
        icon = BitmapDescriptorFactory.defaultMarker(
            BitmapDescriptorFactory.HUE_GREEN
        )
    )
}

@Preview
@Composable
fun PreviewSpeedometer() {
    val generalViewModels: GeneralViewModels = viewModel()
    val preferencesViewModel: PreferencesViewModel = viewModel()
    val location = remember { mutableStateOf(LatLng(41.366873, -8.194834)) }
    Speedometer(
        LocationDetails(41.366873, -8.194834, 40.0f),
        generalViewModels,
        preferencesViewModel,
        location
    )
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Speedometer(
    currentLocation: LocationDetails?,
    generalViewModels: GeneralViewModels,
    preferencesViewModel: PreferencesViewModel,
    destinationRoute: State<LatLng?>
) {
    val vehicleViewModels: VehicleViewModels = viewModel()
    val vehicles = vehicleViewModels.allVehicles.observeAsState()

    val isTracking = CountKmService.isTracking.observeAsState()
    val context = LocalContext.current

    val stopString = stringResource(id = R.string.stopped_counting)
    val recordedString = stringResource(id = R.string.recorded)

    val openDialog = remember { mutableStateOf(false) }
    Column(modifier = Modifier.padding(5.dp)) {
        Box(
            modifier = Modifier
                .size(size = 70.dp)
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.background)
                .padding(10.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                currentLocation?.speed?.let {
                    if ((it * 3.6) > 1) {
                        LabelLarge(
                            (((it * 3.6) * 100.0).roundToInt() / 100.0).toString(),
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        LabelLarge(
                            "0.0",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                LabelTiny("km/h", color = MaterialTheme.colorScheme.secondary)
            }
        }
        if (generalViewModels.mapMode.observeAsState().value == MapMode.ROUTES && destinationRoute.value != null) {
            OutlinedButton(
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                onClick = {
                    val gmmIntentUri =
                        Uri.parse("google.navigation:q=${destinationRoute.value!!.latitude},${destinationRoute.value!!.longitude}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    context.startActivity(mapIntent)

                    generalViewModels.setMapMode(MapMode.NORMAL)
                }) {
                Text(
                    text = stringResource(id = R.string.open_maps)
                )
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        OutlinedButton(
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ),
            onClick = {
                //If is not tracking show pop up to let user select the vehicle he is driving in
                if (isTracking.value == null || isTracking.value == false) {
                    openDialog.value = true
                } else {
                    //If is was tracking show the distance made in that travel
                    generalViewModels.registerDistance(
                        vehicleViewModels,
                        preferencesViewModel.plateTracking!!.value.toString(),
                        CountKmService.distanceCounted.value!!.toDouble()
                    )
                    FancyToast.makeText(
                        context,
                        stopString + "\n" + recordedString + " " + CountKmService.distanceCounted.value!!.roundToInt().toString() + " km",
                        FancyToast.LENGTH_LONG,
                        FancyToast.INFO,
                        false
                    ).show()
                    Intent(context, CountKmService::class.java).also {
                        it.action = "Stop"
                        context.startService(it)
                    }
                }
            }) {
            if (isTracking.value == null || isTracking.value == false) {
                Text(text = stringResource(id = R.string.count_km_made))
            } else {
                Text(text = stringResource(id = R.string.stop_count_km_made))
            }
        }

        if (generalViewModels.mapMode.observeAsState().value == MapMode.ROUTES) {
            OutlinedButton(
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                onClick = {
                    generalViewModels.setMapMode(MapMode.NORMAL)
                }) {
                Text(
                    text = stringResource(id = R.string.arrived_to_the_destiny)
                )
            }
        }
        if (openDialog.value) DialogVehicles(openDialog, vehicles, preferencesViewModel)
    }
}

@Composable
fun DialogVehicles(
    openDialog: MutableState<Boolean>,
    vehicles: State<List<Vehicle>?>,
    preferencesViewModel: PreferencesViewModel
) {
    val listingList: List<Vehicle>? = vehicles.value

    Dialog(
        onDismissRequest = {
            openDialog.value = false
        },
        content = {
            Surface(shape = RoundedCornerShape(8.dp)) {
                Column(
                    modifier = Modifier.padding(10.dp),
                ) {
                    LabelMedium(text = stringResource(id = R.string.select_vehicle))
                    if (listingList != null) {
                        LazyColumn {
                            items(listingList) { vehicle ->
                                CardVehicle(vehicle, openDialog, preferencesViewModel)
                            }
                        }
                    } else {
                        LabelMedium(stringResource(id = R.string.no_vehicles_founded))
                    }
                    OutlinedButton(
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        onClick = {
                            openDialog.value = false
                        }) {
                        LabelMedium(
                            stringResource(id = R.string.cancel),
                            MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        },
    )
}

@Preview
@Composable
fun PreviewCardVehicle() {
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
    val preferencesViewModel: PreferencesViewModel = viewModel()
    CardVehicle(
        vehicle,
        dialogIsOpen,
        preferencesViewModel
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CardVehicle(
    vehicle: Vehicle,
    openDialog: MutableState<Boolean>,
    preferencesViewModel: PreferencesViewModel
) {
    val context = LocalContext.current
    val startString = stringResource(id = R.string.starting_counting)

    val plateSelected = remember { mutableStateOf("") }

    Card(
        shape = RoundedCornerShape(15.dp),
        elevation = 20.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        onClick = {
            openDialog.value = false
            preferencesViewModel.insertPlateRecord(vehicle.plate)
            Intent(context, CountKmService::class.java).also {
                it.action = "Start"
                context.startService(it)
            }
            FancyToast.makeText(
                context,
                startString,
                FancyToast.LENGTH_LONG,
                FancyToast.INFO,
                false
            ).show()
        }
    ) {
        InfoCard(vehicle)
    }
}

@Preview
@Composable
fun PreviewInfoCard() {
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
    InfoCard(
        vehicle
    )
}

@Composable
fun InfoCard(
    vehicle: Vehicle,
) {
    val img: Int =
        if (vehicle.vehicleType == VehiclesTypes.AUTOMOBILE) {
            R.drawable.car
        } else {
            R.drawable.motorbike
        }
    Row(
        Modifier
            .background(MaterialTheme.colorScheme.primary)
            .border(
                1.dp,
                color = MaterialTheme.colorScheme.primary
            )
            .padding(10.dp),
        horizontalArrangement = Arrangement.Start,

        ) {
        Column(Modifier.padding(10.dp)) {
            Image(
                painter = painterResource(id = img),
                contentDescription = "image",
                modifier = Modifier.size(40.dp)
            )
        }
        Column(Modifier.padding(10.dp)) {
            LabelSmall(
                text = vehicle.brand + " " + vehicle.model,
                color = MaterialTheme.colorScheme.onPrimary
            )
            LabelTiny(
                text = vehicle.plate,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
