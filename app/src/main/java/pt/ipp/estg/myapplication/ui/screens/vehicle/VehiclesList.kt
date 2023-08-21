package pt.ipp.estg.myapplication.ui.screens.vehicle

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import pt.ipp.estg.myapplication.R
import pt.ipp.estg.myapplication.enumerations.MaintenanceStatus
import pt.ipp.estg.myapplication.enumerations.VehiclesTypes
import pt.ipp.estg.myapplication.models.database.vehicle.*
import pt.ipp.estg.myapplication.ui.screens.ui_components.SearchBar
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.LabelSmall
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.TitleSmall

enum class ViewType { GRID, LIST }
/*
  Disclaimer: This preview may be empty, if there is no vehicles registered
 */
@Preview
@Composable
fun PreviewVehiclesList() {
    val navController: NavHostController = rememberNavController()
    VehiclesList(navController = navController)
}

@Composable
fun VehiclesList(
    navController: NavController
) {
    //get actual size of screen
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    val searchBarInput = rememberSaveable { mutableStateOf("") }

    val vehicleViewModels: VehicleViewModels = viewModel()
    val vehicles = vehicleViewModels.allVehicles.observeAsState()
    vehicleViewModels.updateMaintenanceInfo()

    val viewType = rememberSaveable { mutableStateOf(ViewType.GRID) }

    CarTemplate {
        SearchBar(searchBarInput, stringResource(id = R.string.model))
        Spacer(modifier = Modifier.height(20.dp))

        if (vehicles.value != null && vehicles.value!!.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { viewType.value = ViewType.GRID }) {
                        Icon(
                            imageVector = Icons.Default.GridView, contentDescription = "",
                            tint = if (viewType.value == ViewType.GRID) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { viewType.value = ViewType.LIST }) {
                        Icon(
                            imageVector = Icons.Default.ViewList, contentDescription = "",
                            tint = if (viewType.value == ViewType.LIST) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.secondary)
        ListOfVehicles(navController, searchBarInput, viewType)
    }
}

/*
  Disclaimer: This preview may be empty, if there is no vehicles registered
 */
@Preview
@Composable
fun PreviewListOfVehicles() {
    val navController: NavHostController = rememberNavController()
    val actualSearch = remember { mutableStateOf("") }
    val viewType = remember { mutableStateOf(ViewType.GRID) }
    ListOfVehicles(navController = navController, actualSearch, viewType)
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun ListOfVehicles(
    navController: NavController,
    actualSearch: MutableState<String>,
    viewType: MutableState<ViewType>
) {
    val vehicleViewModels: VehicleViewModels = viewModel()
    val vehicles = vehicleViewModels.allVehicles.observeAsState()

    Column {
        Spacer(modifier = Modifier.height(15.dp))

        val listingList: List<Vehicle>? = vehicles.value?.filter { v ->
            v.model.uppercase().contains(actualSearch.value.uppercase())
        }

        if (listingList != null && listingList.isNotEmpty()) {

            AnimatedContent(
                targetState = viewType.value
            ) { targetState ->
                when (targetState) {
                    ViewType.LIST -> LazyColumn() {
                        items(listingList) { vehicle ->
                            CarListElement(vehicle, navController)
                            Spacer(modifier = Modifier.height(15.dp))
                        }
                    }

                    ViewType.GRID -> LazyVerticalGrid(columns = GridCells.Fixed(2))
                    {
                        items(listingList) { vehicle ->
                            CarGridListElement(vehicle, navController)
                        }
                    }
                }

            }
        } else {
            TitleSmall(
                text = stringResource(id = R.string.without_vehicles),
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Preview
@Composable
fun PreviewCarListElement() {
    val navController: NavHostController = rememberNavController()
    val vehicle = Vehicle(
        "00-AA-00",
        "Brand",
        "Model",
        VehiclesTypes.AUTOMOBILE,
        "Specitification",
        "Associtation",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        MaintenanceStatus.DANGER
    )
    CarListElement(navController = navController, vehicle = vehicle)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CarListElement(vehicle: Vehicle, navController: NavController) {
    Card(
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .fillMaxWidth(),
        onClick = {
            navController.navigate("vehicle_details/${vehicle.plate}")
        }
    ) {
        Row(
            Modifier
                .background(MaterialTheme.colorScheme.primary)
                .border(1.dp, color = MaterialTheme.colorScheme.primary)
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,

            ) {
            Column() {
                LabelSmall(
                    text = vehicle.brand,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                LabelSmall(
                    text = vehicle.plate,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                LabelSmall(
                    text = vehicle.model,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LabelSmall(
                    text = stringResource(id = R.string.maintenance),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(15.dp))

                //check the level of maintenance
                when (vehicle.maintenanceStatus) {
                    MaintenanceStatus.OK ->
                        Image(
                            painter = painterResource(id = R.drawable.checked),
                            contentDescription = "image",
                            modifier = Modifier.size(30.dp)
                        )

                    MaintenanceStatus.WITHOUT_DATA ->
                        Image(
                            painter = painterResource(id = R.drawable.question),
                            contentDescription = "image",
                            modifier = Modifier.size(30.dp)
                        )


                    MaintenanceStatus.WARNING ->
                        Image(
                            painter = painterResource(id = R.drawable.warning),
                            contentDescription = "image",
                            modifier = Modifier.size(30.dp)
                        )

                    MaintenanceStatus.DANGER ->
                        Image(
                            painter = painterResource(id = R.drawable.danger),
                            contentDescription = "image",
                            modifier = Modifier.size(35.dp)
                        )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewCarGridListElement() {
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
    CarGridListElement(vehicle = vehicle, navController = navController)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CarGridListElement(vehicle: Vehicle, navController: NavController) {
    Card(
        shape = RoundedCornerShape(15.dp),
        contentColor = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .width(40.dp)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        onClick = {
            navController.navigate("vehicle_details/${vehicle.plate}")
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.primary)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                LabelSmall(
                    text = vehicle.brand,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                LabelSmall(
                    text = vehicle.plate,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                LabelSmall(
                    text = vehicle.model,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Divider(modifier = Modifier.height(1.dp))
            Spacer(modifier = Modifier.height(10.dp))
            LabelSmall(
                text = stringResource(id = R.string.maintenance),
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(5.dp))
            //check the level of maintenance
            Log.d("vehicle_", vehicle.toString())
            when (vehicle.maintenanceStatus) {
                MaintenanceStatus.OK ->
                    Image(
                        painter = painterResource(id = R.drawable.checked),
                        contentDescription = "image",
                        modifier = Modifier.size(30.dp)
                    )

                MaintenanceStatus.WITHOUT_DATA ->
                    Image(
                        painter = painterResource(id = R.drawable.question),
                        contentDescription = "image",
                        modifier = Modifier.size(30.dp)
                    )

                MaintenanceStatus.WARNING ->
                    Image(
                        painter = painterResource(id = R.drawable.warning),
                        contentDescription = "image",
                        modifier = Modifier.size(30.dp)
                    )

                MaintenanceStatus.DANGER ->
                    Image(
                        painter = painterResource(id = R.drawable.danger),
                        contentDescription = "image",
                        modifier = Modifier.size(35.dp)
                    )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
