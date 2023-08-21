package pt.ipp.estg.myapplication.ui.screens.charge_station

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.shashank.sony.fancytoastlib.FancyToast
import pt.ipp.estg.myapplication.R
import pt.ipp.estg.myapplication.connection.ConnectivityObserver
import pt.ipp.estg.myapplication.location.LocationDetails
import pt.ipp.estg.myapplication.models.GeneralViewModels
import pt.ipp.estg.myapplication.models.database.charge_station.ChargeStationViewModels
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.*

/*
  Disclaimer: this preview doesn't work pretty well, because the internet connection is not available on the run time of the preview.
  Run the app to check all the functionalities and the normal behavior on this screen.
 */
@Preview
@Composable
fun ChargeStationPreview() {
    val generalViewModels: GeneralViewModels = viewModel()
    ChargeStation(
        LocationDetails(41.366873, -8.194834, 0.0f),
        generalViewModels = generalViewModels,
        navController = rememberNavController()
    )
}

@Composable
fun ChargeStation(
    currentLocation: LocationDetails?,
    generalViewModels: GeneralViewModels,
    navController: NavController
) {
    val context = LocalContext.current
    val stringNoInternetConnection = stringResource(id = R.string.no_internet_connection)

    val chargeStationViewModels: ChargeStationViewModels = viewModel()
    val chargesStations = chargeStationViewModels.chargesStations.observeAsState()
    val radioDistance = listOf(5, 10, 15, 20)
    val distance = remember { mutableStateOf(5) }

    var tabRowStatus by remember { mutableStateOf(5) }

    LaunchedEffect(Unit) {
        if (generalViewModels.status.value == ConnectivityObserver.Status.Available) {
            chargeStationViewModels.getChargeStations(currentLocation, 5)
        } else {
            FancyToast.makeText(
                context,
                stringNoInternetConnection, FancyToast.LENGTH_LONG, FancyToast.ERROR, false
            ).show()
        }
    }

    Box(
        Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(15.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            TitleLarge(text = stringResource(id = R.string.charge_station_menu))

            Spacer(modifier = Modifier.height(10.dp))

            LabelMedium(text = stringResource(id = R.string.range_distance))

            Spacer(modifier = Modifier.height(5.dp))

            TabRow(
                selectedTabIndex =
                when (distance.value) {
                    5 -> 0
                    10 -> 1
                    15 -> 2
                    20 -> 3
                    else -> {
                        0
                    }
                },
                backgroundColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.secondary
            ) {
                radioDistance.forEachIndexed { index, title ->
                    Tab(
                        selected = tabRowStatus == index,
                        onClick = {
                            tabRowStatus = index
                            when (index) {
                                0 -> {
                                    distance.value = 5
                                }
                                1 -> {
                                    distance.value = 10
                                }
                                2 -> {
                                    distance.value = 15
                                }
                                3 -> {
                                    distance.value = 20
                                }
                            }
                            chargeStationViewModels.getChargeStations(
                                currentLocation,
                                distance.value
                            )
                        }) {
                        Column(
                            verticalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.padding(vertical = 10.dp)
                        ) {
                            LabelSmall(
                                text = "$title km", color =
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

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    chargesStations.value?.forEach {
                        DisplayChargeStation(it, generalViewModels, navController)
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }
            }
        }
    }
}