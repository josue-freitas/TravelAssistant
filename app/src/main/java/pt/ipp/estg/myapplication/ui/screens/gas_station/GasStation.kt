package pt.ipp.estg.myapplication.ui.screens.gas_station

import android.content.Context.MODE_PRIVATE
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.model.LatLng
import com.shashank.sony.fancytoastlib.FancyToast
import pt.ipp.estg.myapplication.R
import pt.ipp.estg.myapplication.connection.ConnectivityObserver
import pt.ipp.estg.myapplication.enumerations.CouponType
import pt.ipp.estg.myapplication.enumerations.GasType
import pt.ipp.estg.myapplication.enumerations.MapMode
import pt.ipp.estg.myapplication.enumerations.OrderBy
import pt.ipp.estg.myapplication.location.LocationDetails
import pt.ipp.estg.myapplication.models.GeneralViewModels
import pt.ipp.estg.myapplication.models.database.coupon.CouponViewModels
import pt.ipp.estg.myapplication.models.database.gas_station.GasStation
import pt.ipp.estg.myapplication.models.database.gas_station.GasStationViewModels
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.*
import pt.ipp.estg.myapplication.ui.theme.AppTypography

@Preview
@Composable
fun PreviewGasStation() {
    val generalViewModels: GeneralViewModels = viewModel()
    GasStation(
        currentLocation = LocationDetails(10.0, 10.0, 10f),
        generalViewModels = generalViewModels,
        navController = rememberNavController()
    )
}

@Composable
fun GasStation(
    currentLocation: LocationDetails?,
    generalViewModels: GeneralViewModels,
    navController: NavController
) {
    val context = LocalContext.current

    val listGasTypes = listOf(
        stringResource(R.string.diesel),
        stringResource(R.string.gasoline_95),
        stringResource(R.string.gasoline_98),
        stringResource(R.string.lpg),
    )

    val gasStationsPref = context.getSharedPreferences("myPref", MODE_PRIVATE)
    val lastDistrict = gasStationsPref.getString("lastDistrict", null)
    val lastDate = gasStationsPref.getString("lastUpdateDate", null)

    val rangeDistance = rememberSaveable { mutableStateOf(5) }
    val orderBy = rememberSaveable { mutableStateOf(OrderBy.DISTANCE) }
    val gasType = rememberSaveable { mutableStateOf(GasType.SIMPLEDIESEL) }

    var tabRowStatus by rememberSaveable { mutableStateOf(0) }

    val couponsViewModel: CouponViewModels = viewModel()
    val coupons = couponsViewModel.allCoupons.observeAsState()
    couponsViewModel.getValidCoupons()

    val gasStationsViewModel: GasStationViewModels = viewModel()
    val gasStations = gasStationsViewModel.gasStations.observeAsState()
    gasStationsViewModel.onInit(coupons)

    val stringNoInternetConnection = stringResource(id = R.string.no_internet_connection)
    val selectGasType = stringResource(id = R.string.select_gas_type)

    LaunchedEffect(Unit) {
        if (lastDistrict !== null) {
            gasStationsViewModel.getGasStationsOffline(
                GasType.SIMPLEDIESEL,
                rangeDistance.value,
                orderBy.value.atributeName
            )
        } else {
            if (generalViewModels.status.value == ConnectivityObserver.Status.Available) {
                gasStationsViewModel.getAllGasStations(
                    currentLocation,
                )
                FancyToast.makeText(
                    context,
                    selectGasType,
                    FancyToast.LENGTH_LONG,
                    FancyToast.INFO,
                    false
                ).show()
            } else {
                FancyToast.makeText(
                    context,
                    stringNoInternetConnection, FancyToast.LENGTH_LONG, FancyToast.ERROR, false
                ).show()
            }
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
            //modifier = Modifier.verticalScroll(rememberScrollState())
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            TitleLarge(text = stringResource(id = R.string.gas_station_menu))

            Spacer(modifier = Modifier.height(15.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                elevation = 0.dp,
                backgroundColor = MaterialTheme.colorScheme.background
            ) {
                Row(
                    Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,

                    ) {
                    if (lastDistrict != null) {
                        Column {
                            LabelSmall(
                                text = (stringResource(R.string.last_update) + ": " + lastDate),
                                color = MaterialTheme.colorScheme.primary
                            )
                            LabelSmall(
                                text = (stringResource(R.string.last_district) + ": " + lastDistrict),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedButton(
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colorScheme.secondary,
                            ),
                            shape = RoundedCornerShape(15.dp),
                            onClick = {
                                if (generalViewModels.status.value == ConnectivityObserver.Status.Available) {
                                    gasStationsViewModel.getAllGasStations(
                                        currentLocation
                                    )
                                    FancyToast.makeText(
                                        context,
                                        selectGasType,
                                        FancyToast.LENGTH_LONG,
                                        FancyToast.INFO,
                                        false
                                    ).show()
                                } else {
                                    FancyToast.makeText(
                                        context,
                                        stringNoInternetConnection,
                                        FancyToast.LENGTH_LONG,
                                        FancyToast.ERROR,
                                        false
                                    ).show()
                                }
                            }) {
                            LabelMedium(
                                text = stringResource(id = R.string.update),
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                SliderRange(
                    rangeDistance,
                    gasStationsViewModel,
                    gasType,
                    orderBy
                )

                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LabelSmall(
                        text = stringResource(id = R.string.order_by),
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = {
                        orderBy.value = OrderBy.DISTANCE
                        gasStationsViewModel.getGasStationsOffline(
                            gasType.value,
                            rangeDistance.value,
                            OrderBy.DISTANCE.atributeName
                        )
                    }) {
                        Icon(
                            imageVector = Icons.Default.Map, contentDescription = "",
                            tint = if (orderBy.value == OrderBy.DISTANCE) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = {
                        orderBy.value = OrderBy.PRICE
                        gasStationsViewModel.getGasStationsOffline(
                            gasType.value,
                            rangeDistance.value,
                            OrderBy.PRICE.atributeName
                        )
                    }) {
                        Icon(
                            imageVector = Icons.Default.AttachMoney, contentDescription = "",
                            tint = if (orderBy.value == OrderBy.PRICE) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.secondary)

                TabRow(
                    selectedTabIndex =
                    when (gasType.value) {
                        GasType.SIMPLEDIESEL -> 0
                        GasType.SIMPLEGASOLINE95 -> 1
                        GasType.SIMPLEGASOLINE98 -> 2
                        GasType.LPG -> 3
                    },
                    backgroundColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.secondary
                ) {
                    listGasTypes.forEachIndexed { index, title ->
                        Tab(
                            selected = tabRowStatus == index,
                            onClick = {
                                tabRowStatus = index
                                when (index) {
                                    0 -> {
                                        gasType.value = GasType.SIMPLEDIESEL
                                    }
                                    1 -> {
                                        gasType.value = GasType.SIMPLEGASOLINE95
                                    }
                                    2 -> {
                                        gasType.value = GasType.SIMPLEGASOLINE98
                                    }
                                    3 -> {
                                        gasType.value = GasType.LPG
                                    }
                                }
                                gasStationsViewModel.getGasStationsOffline(
                                    gasType.value,
                                    rangeDistance.value,
                                    orderBy.value.atributeName
                                )
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

                Spacer(modifier = Modifier.height(15.dp))

                Column() {
                    gasStations.value?.forEach {
                        DisplayGasStation(
                            gasStation = it,
                            generalViewModels = generalViewModels,
                            navController = navController
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewDisplayGasStation() {
    val generalViewModels: GeneralViewModels = viewModel()
    DisplayGasStation(
        GasStation(
            "Gasóleo simples",
            "Braga",
            100,
            41.366873,
            "Felgueiras",
            -8.194834,
            "GALP",
            "Rua do Curral",
            "Felgueiras",
            1.912f,
            1.810f,
            CouponType.PERLITER,
            0.1
        ), generalViewModels = generalViewModels, navController = rememberNavController()
    )
}

@Composable
fun DisplayGasStation(
    gasStation: GasStation,
    generalViewModels: GeneralViewModels,
    navController: NavController
) {
    Card(
        shape = RoundedCornerShape(15.dp),
        elevation = 20.dp,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column {
            Row(
                Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .border(1.dp, color = MaterialTheme.colorScheme.primary)
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 5.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,

                ) {
                Column {
                    LabelMedium(
                        text = gasStation.Marca,
                        color = MaterialTheme.colorScheme.onPrimary,
                        weight = FontWeight.Bold
                    )
                    LabelSmall(
                        text = gasStation.Localidade,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    LabelTiny(
                        text = gasStation.Distance.toString() + " km away",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End,
                ) {
                    LabelLarge(
                        text = gasStation.PrecoComDesconto.toString() + " €",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    if (gasStation.TipoDesconto == CouponType.PERLITER) {
                        Text(
                            text = gasStation.Preco.toString() + " €",
                            style = AppTypography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onPrimary,
                                textDecoration = TextDecoration.LineThrough
                            ),
                        )
                        Row {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "",
                                modifier = Modifier.size(14.dp)
                            )
                            LabelTiny(
                                text = stringResource(id = R.string.with_coupon),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    } else {
                        if (gasStation.TipoDesconto == CouponType.PERTOTALVALUE) {
                            Row {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "",
                                    modifier = Modifier.size(14.dp)
                                )
                                LabelTiny(
                                    text = stringResource(id = R.string.discount_per_total_value),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            LabelTiny(
                                text = stringResource(id = R.string.check_my_coupons),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(bottom = 5.dp)
            ) {

                OutlinedButton(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colorScheme.secondary,
                    ),
                    shape = RoundedCornerShape(15.dp),
                    onClick = {
                        generalViewModels.setMapMode(MapMode.ZOOM_IN)
                        generalViewModels.setTextMarker(gasStation.Marca + " - " + gasStation.Localidade)
                        generalViewModels.addMarker(
                            LatLng(
                                gasStation.Latitude,
                                gasStation.Longitude
                            )
                        )
                        navController.navigate("map")
                    }) {
                    LabelTiny(
                        text = stringResource(id = R.string.see_on_map),
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun SliderRange(
    rangeDistance: MutableState<Int>,
    gasStationsViewModel: GasStationViewModels,
    gasType: MutableState<GasType>,
    orderBy: MutableState<OrderBy>
) {

    var sliderPosition by remember { mutableStateOf(5f) }
    LabelSmall(
        text = stringResource(id = R.string.range_distance) + ": $sliderPosition km",
        color = MaterialTheme.colorScheme.primary
    )
    Slider(
        value = sliderPosition,
        onValueChange = { sliderPosition = it },
        valueRange = 5f..20f,
        onValueChangeFinished = {
            rangeDistance.value = sliderPosition.toInt()
            gasStationsViewModel.getGasStationsOffline(
                gasType.value,
                rangeDistance.value,
                orderBy.value.atributeName
            )
        },
        steps = 2,
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.secondary,
            activeTrackColor = MaterialTheme.colorScheme.secondary
        )
    )
}