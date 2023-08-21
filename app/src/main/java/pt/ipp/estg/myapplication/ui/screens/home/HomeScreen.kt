package pt.ipp.estg.myapplication.ui.screens.home

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.android.gms.maps.model.LatLng
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import pt.ipp.estg.myapplication.location.LocationDetails
import pt.ipp.estg.myapplication.R
import pt.ipp.estg.myapplication.camera.CameraViewModel
import pt.ipp.estg.myapplication.enumerations.MapMode
import pt.ipp.estg.myapplication.models.GeneralViewModels
import pt.ipp.estg.myapplication.ui.screens.locations.PreferencesData
import pt.ipp.estg.myapplication.models.database.coupon.CouponViewModels
import pt.ipp.estg.myapplication.ui.screens.locations.PreferencesViewModel
import pt.ipp.estg.myapplication.models.database.search_place.SearchPlaceViewModels
import pt.ipp.estg.myapplication.ui.screens.vehicle.VehicleViewModels
import pt.ipp.estg.myapplication.models.retrofit.search_place.Place
import pt.ipp.estg.myapplication.models.firebase.AuthViewModel
import pt.ipp.estg.myapplication.models.retrofit.search_place.Geometry
import pt.ipp.estg.myapplication.models.retrofit.search_place.Location
import pt.ipp.estg.myapplication.ui.screens.charge_station.ChargeStation
import pt.ipp.estg.myapplication.ui.screens.gas_station.GasStation
import pt.ipp.estg.myapplication.ui.screens.locations.Locations
import pt.ipp.estg.myapplication.ui.screens.map.MapScreen
import pt.ipp.estg.myapplication.ui.screens.my_coupons.CouponDetails
import pt.ipp.estg.myapplication.ui.screens.my_coupons.CreateCoupon
import pt.ipp.estg.myapplication.ui.screens.my_coupons.MyCoupons
import pt.ipp.estg.myapplication.ui.screens.locations.SaveParking
import pt.ipp.estg.myapplication.ui.screens.settings.Settings
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.LabelLarge
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.LabelSmall
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.LabelTiny
import pt.ipp.estg.myapplication.ui.screens.user_info.RegistrationUser
import pt.ipp.estg.myapplication.ui.screens.user_info.Login
import pt.ipp.estg.myapplication.ui.screens.user_info.UserInfo
import pt.ipp.estg.myapplication.ui.screens.vehicle.*
import pt.ipp.estg.myapplication.ui.theme.AppTypography
import pt.ipp.estg.myapplication.ui.theme.MyApplicationTheme

//@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview
@Composable
fun PreviewHomeScreen() {
    /*
    val generalViewModels: GeneralViewModels = viewModel()
    val preferencesViewModel: PreferencesViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val vehicleViewModel: VehicleViewModels = viewModel()
    val couponViewModel: CouponViewModels = viewModel()
    val isDark = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val windowSize = calculateWindowSizeClass()
    HomeScreen(
        preferencesViewModel,
        generalViewModels,
        LocationDetails(41.366873, -8.194834, 0.0f),
        isDark,
        authViewModel,
        vehicleViewModel,
        couponViewModel
    )
    */
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    preferencesViewModel: PreferencesViewModel,
    generalViewModels: GeneralViewModels,
    currentLocation: LocationDetails?,
    isDark: State<Boolean?>,
    authViewModel: AuthViewModel,
    vehicleViewModel: VehicleViewModels,
    couponViewModel: CouponViewModels,
    windowSize: WindowWidthSizeClass
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val navController: NavHostController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val currentRoute =
        remember { mutableStateOf(navController.currentBackStackEntry?.destination?.route) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val config = LocalConfiguration.current
    val heightDp = config.screenHeightDp

    val searchBarInput = rememberSaveable { mutableStateOf("") }

    val preferencesViewModel: PreferencesViewModel = viewModel()

    val actualTheme = preferencesViewModel.theme.observeAsState()

    val preferencesHome = preferencesViewModel.preferencesHome.observeAsState()
    val preferencesWork = preferencesViewModel.preferencesWork.observeAsState()
    val preferencesPark = preferencesViewModel.preferencesPark.observeAsState()

    val searchViewModel: SearchPlaceViewModels = viewModel()
    val searchedPlaces = searchViewModel.places.observeAsState()
    val cameraViewModel: CameraViewModel = viewModel()

    val isToShowAboutUs = remember { mutableStateOf(false) }

    actualTheme.value?.let {
        MyApplicationTheme(actualTheme = it) {
            ModalBottomSheetLayout(
                sheetContent = {
                    Box(
                        modifier = Modifier
                            .height((heightDp / 1.2).dp)
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp)
                        ) {
                            //Search bar
                            SearchBarBottomSheet(
                                searchBarInput,
                                stringResource(id = R.string.search_here),
                                colorSecondary = MaterialTheme.colorScheme.primary,
                                searchViewModel,
                                currentLocation
                            )
                            Spacer(modifier = Modifier.height(15.dp))
                            //My locations
                            LabelLarge(text = stringResource(id = R.string.my_locations))
                            Spacer(modifier = Modifier.height(5.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                MyLocationsCard(
                                    painterResource(id = R.drawable.home),
                                    stringResource(id = R.string.home),
                                    preferencesHome,
                                    generalViewModels,
                                    navController
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                MyLocationsCard(
                                    painterResource(id = R.drawable.job),
                                    stringResource(id = R.string.work),
                                    preferencesWork,
                                    generalViewModels,
                                    navController
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                MyLocationsCard(
                                    painterResource(id = R.drawable.park),
                                    stringResource(id = R.string.park),
                                    preferencesPark,
                                    generalViewModels,
                                    navController
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Divider(
                                modifier = Modifier
                                    .height(1.dp)
                                    .padding(vertical = 10.dp),
                                color = MaterialTheme.colorScheme.onBackground,
                                thickness = 1.dp
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            LabelLarge(text = stringResource(id = R.string.results))

                            Spacer(modifier = Modifier.height(5.dp))

                            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                                searchedPlaces.value?.forEach { place ->
                                    DisplaySearchPlaces(
                                        place, generalViewModels, navController
                                    )
                                    Spacer(modifier = Modifier.height(15.dp))
                                }
                            }
                        }
                    }
                },
                sheetState = sheetState,
                sheetBackgroundColor = MaterialTheme.colorScheme.background
            ) {
                Scaffold(
                    scaffoldState = scaffoldState,
                    drawerGesturesEnabled = (navBackStackEntry?.destination?.route.toString() != "map" || scaffoldState.drawerState.isOpen) && windowSize != WindowWidthSizeClass.Expanded,
                    drawerContent = {
                        if (windowSize != WindowWidthSizeClass.Expanded) {
                            NavBar(navController, authViewModel, scope, scaffoldState)
                        }
                    },
                    bottomBar = {
                        BottomBar(
                            onClick = {
                                coroutineScope.launch {
                                    //sheetState.show() // changed here make the modal full screen at the beginning
                                    sheetState.animateTo(ModalBottomSheetValue.Expanded)
                                }
                            }, navController,
                            navBackStackEntry,
                            scope, scaffoldState, windowSize
                        )
                    },
                    floatingActionButton = {
                        if (navBackStackEntry?.destination?.route.toString() == "vehicles_list") {
                            ExtendedFloatingActionButton(
                                onClick = { navController.navigate("vehicle_create") },
                                backgroundColor = MaterialTheme.colorScheme.secondary,
                                text = {
                                    LabelSmall(
                                        text = stringResource(id = R.string.add_vehicle),
                                        color = MaterialTheme.colorScheme.onSecondary
                                    )
                                },
                                icon = { Icon(Icons.Filled.Add, "") }
                            )
                        }
                        if (navBackStackEntry?.destination?.route.toString() == "my_coupons_screen") {
                            ExtendedFloatingActionButton(
                                onClick = { navController.navigate("create_coupons") },
                                backgroundColor = MaterialTheme.colorScheme.secondary,
                                text = {
                                    LabelSmall(
                                        text = stringResource(id = R.string.add_coupon),
                                        color = MaterialTheme.colorScheme.onSecondary
                                    )
                                },
                                icon = { Icon(Icons.Filled.Add, "") }
                            )
                        }
                    },
                    content = { padding ->
                        Row(
                            modifier = Modifier.padding(padding)
                        ) {
                            if (!isToShowAboutUs.value && windowSize == WindowWidthSizeClass.Expanded) {
                                NavBarComponentTablet(
                                    navController = navController,
                                    scope = scope,
                                    scaffoldState = scaffoldState,
                                    isToShowAboutUs = isToShowAboutUs,
                                    authViewModel = authViewModel
                                )
                            } else if (isToShowAboutUs.value && windowSize == WindowWidthSizeClass.Expanded) {
                                Column(modifier = Modifier.width(250.dp)) {
                                    AboutUs(
                                        isToShowAboutUs = isToShowAboutUs,
                                        windowWidthSizeClass = windowSize
                                    )
                                }
                            }

                            NavHost(
                                navController = navController,
                                startDestination = "map"
                            ) {
                                composable("map") {
                                    MapScreen(
                                        currentLocation,
                                        generalViewModels,
                                        preferencesViewModel,
                                        isDark
                                    )
                                }
                                composable("vehicles_list") {
                                    VehiclesList(navController)
                                }
                                composable("vehicle_create") {
                                    CreateVehicle(navController)
                                }
                                composable("vehicle_edit/{plate}") { navBackStackEntry ->
                                    /* Extracting the id from the route */
                                    val plate =
                                        navBackStackEntry.arguments?.getString("plate")
                                    /* We check if is null */
                                    plate?.let {
                                        EditVehicle(plate = plate, navController)
                                    }
                                }
                                composable("vehicle_details/{plate}") { navBackStackEntry ->
                                    /* Extracting the id from the route */
                                    val plate =
                                        navBackStackEntry.arguments?.getString("plate")
                                    /* We check if is null */
                                    plate?.let {
                                        VehicleDetails(
                                            plate = plate,
                                            navController = navController
                                        )
                                    }
                                }
                                composable("gas_screen") {
                                    GasStation(currentLocation, generalViewModels, navController)
                                }
                                composable("charge_stations_screen") {
                                    ChargeStation(currentLocation, generalViewModels, navController)
                                }
                                composable("location_home_screen") {
                                    Locations(
                                        generalViewModels,
                                        navController,
                                        preferencesViewModel
                                    )
                                }
                                composable("save_location_screen") {
                                    SaveParking(
                                        generalViewModels,
                                        cameraViewModel,
                                        navController,
                                        preferencesViewModel,
                                    )
                                }
                                composable("my_coupons_screen") {
                                    MyCoupons(navController)
                                }
                                composable("create_coupons") {
                                    CreateCoupon(navController)
                                }
                                composable("coupon_details/{id}") { navBackStackEntry ->
                                    /* Extracting the id from the route */
                                    val id =
                                        navBackStackEntry.arguments?.getString("id")
                                    /* We check if is null */
                                    id?.let {
                                        CouponDetails(
                                            id = id.toInt(),
                                            navController = navController
                                        )
                                    }
                                }
                                composable("settings") {
                                    Settings(
                                        preferencesViewModel = preferencesViewModel,
                                        generalViewModels = generalViewModels
                                    )
                                }
                                composable("user_info") {
                                    UserInfo(
                                        authViewModel = authViewModel,
                                        navController = navController,
                                        vehicleViewModels = vehicleViewModel,
                                        couponViewModel = couponViewModel,
                                        preferencesViewModel = preferencesViewModel,
                                        generalViewModels = generalViewModels
                                    )
                                }
                                composable("user_registration") {
                                    RegistrationUser(
                                        navController = navController,
                                        authViewModel = authViewModel,
                                        generalViewModels = generalViewModels
                                    )
                                }
                                composable("user_login") {
                                    Login(
                                        navController = navController,
                                        authViewModel = authViewModel,
                                        generalViewModels = generalViewModels,
                                        vehicleViewModels = vehicleViewModel,
                                        couponViewModel = couponViewModel,
                                        preferencesViewModel = preferencesViewModel
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun PreviewBottomBar() {
    val coroutineScope = rememberCoroutineScope()
    val sheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val navController: NavHostController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val windowSize = WindowWidthSizeClass.Expanded
    BottomBar(
        onClick = {
            coroutineScope.launch {
                sheetState.animateTo(ModalBottomSheetValue.Expanded)
            }
        },
        navController = rememberNavController(),
        navBackStackEntry = navBackStackEntry,
        scope = scope,
        scaffoldState, windowSize
    )
}

@Composable
fun BottomBar(
    onClick: () -> Unit,
    navController: NavController,
    navBackStackEntry: NavBackStackEntry?,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    windowSize: WindowWidthSizeClass
) {
    val selectedIndex = remember { mutableStateOf(0) }
    BottomNavigation(
        elevation = 10.dp,
        backgroundColor = MaterialTheme.colorScheme.background
    ) {
        if (windowSize != WindowWidthSizeClass.Expanded) {
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = if (navBackStackEntry?.destination?.route.toString() == "map") Icons.Default.Menu else Icons.Default.Map,
                        "",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                label = {
                    Text(
                        text =
                        if (navBackStackEntry?.destination?.route.toString() == "map")
                            stringResource(R.string.menu)
                        else
                            stringResource(R.string.map),

                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                selected = (selectedIndex.value == 0),
                onClick = {
                    if (navBackStackEntry?.destination?.route.toString() == "map")
                        scope.launch { scaffoldState.drawerState.open() }
                    else
                        navController.navigate("map")
                    selectedIndex.value = 1
                }
            )
        }

        BottomNavigationItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    "",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.search),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            selected = (selectedIndex.value == 0),
            onClick = onClick,

            )

        BottomNavigationItem(icon = {
            Icon(
                imageVector = Icons.Default.CarRepair,
                "",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        },
            label = {
                Text(
                    text = stringResource(R.string.vehicle),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            selected = (selectedIndex.value == 1),
            onClick = {
                navController.navigate("vehicles_list")
                selectedIndex.value = 1
            })

        BottomNavigationItem(icon = {
            Icon(
                imageVector = Icons.Default.LocalGasStation,
                "",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        },
            label = {
                Text(
                    text = stringResource(R.string.gas_station_menu),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            selected = (selectedIndex.value == 2),
            onClick = {
                navController.navigate("gas_screen")
                selectedIndex.value = 2
            })

        BottomNavigationItem(icon = {
            Icon(
                imageVector = Icons.Default.EvStation,
                "",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        },
            label = {
                Text(
                    text = stringResource(R.string.charge_station_menu),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            selected = (selectedIndex.value == 3),
            onClick = {
                navController.navigate("charge_stations_screen")
                selectedIndex.value = 3
            })
    }
}

@Preview
@Composable
fun PreviewMyLocationsCard() {
    val generalViewModels: GeneralViewModels = viewModel()
    val navController: NavHostController = rememberNavController()
    val preferencesViewModel: PreferencesViewModel = viewModel()
    val preferencesPark = preferencesViewModel.preferencesPark.observeAsState()

    MyLocationsCard(
        painterResource(id = R.drawable.park),
        stringResource(id = R.string.park),
        preferencesPark,
        generalViewModels,
        navController
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MyLocationsCard(
    icon: Painter,
    title: String,
    data: State<PreferencesData?>,
    generalViewModels: GeneralViewModels,
    navController: NavController
) {
    val config = LocalConfiguration.current
    val context = LocalContext.current
    val widthDp = config.screenWidthDp
    val notDefinedString = stringResource(id = R.string.location_not_defined)

    Card(
        shape = RoundedCornerShape(15.dp),
        backgroundColor = MaterialTheme.colorScheme.primary,
        onClick = {
            generalViewModels.setMapMode(MapMode.NORMAL)
            //If location was not defined
            if (data.value!!.lat == 0.0.toString() && data.value!!.long == 0.0.toString()) {
                FancyToast.makeText(
                    context,
                    notDefinedString,
                    FancyToast.LENGTH_LONG,
                    FancyToast.WARNING,
                    false
                ).show()
            } else {
                generalViewModels.addMarker(
                    LatLng(
                        data.value!!.lat!!.toDouble(),
                        data.value!!.long!!.toDouble()
                    )
                )
                generalViewModels.setMapMode(MapMode.ZOOM_IN)
                navController.navigate("map")
            }
        }
    ) {
        Log.e("OBJ", data.value.toString())
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .width((widthDp / 3.2).dp)
                .padding(vertical = 5.dp, horizontal = 10.dp)
        ) {
            Image(
                modifier = Modifier.size(30.dp),
                painter = icon,
                contentDescription = ""
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                LabelSmall(
                    text = title,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    data.value?.district.toString(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = AppTypography.labelMedium.copy(MaterialTheme.colorScheme.onPrimary),
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewSearchBarBottomSheet() {
    val searchViewModel: SearchPlaceViewModels = viewModel()
    val input = remember { mutableStateOf("") }
    SearchBarBottomSheet(
        input,
        stringResource(id = R.string.search_here),
        colorSecondary = MaterialTheme.colorScheme.primary,
        searchViewModel,
        LocationDetails(41.366873, -8.194834, 0.0f),
    )
}

@Composable
fun SearchBarBottomSheet(
    input: MutableState<String>,
    placeholder: String,
    colorSecondary: Color = MaterialTheme.colorScheme.primary,
    searchViewModel: SearchPlaceViewModels,
    currentLocation: LocationDetails?
) {
    BasicTextField(
        value = "",
        onValueChange = {},
        modifier = Modifier
            .background(
                Color.White,
                CircleShape
            )
            .border(1.dp, colorSecondary, RoundedCornerShape(25.dp))
            .fillMaxWidth(),
        maxLines = 1,
        textStyle = AppTypography.labelLarge.copy(colorSecondary),
        decorationBox = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "image",
                    tint = colorSecondary
                )
                TextField(
                    value = input.value,
                    onValueChange = {
                        input.value = it
                        searchViewModel.getSearchPlaces(
                            input.value,
                            currentLocation
                        )
                    },
                    label = { LabelTiny(text = placeholder, colorSecondary) },
                    textStyle = AppTypography.labelLarge.copy(colorSecondary),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White,
                        textColor = colorSecondary,
                        focusedIndicatorColor = Color.White,
                        cursorColor = colorSecondary,
                        disabledIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.White
                    ),
                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier

                        .fillMaxWidth()
                ) {
                    IconButton(
                        onClick = {
                            input.value = ""
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "image",
                            tint = colorSecondary
                        )
                    }
                }
            }
        }
    )
}

@Preview
@Composable
fun PreviewDisplaySearchPlaces() {
    val generalViewModels: GeneralViewModels = viewModel()
    val navController: NavHostController = rememberNavController()
    DisplaySearchPlaces(
        Place(
            "FormattedAddress",
            Geometry(Location(41.366873, -8.194834)),
            "Name",
            10.0
        ),
        generalViewModels,
        navController
    )
}

@Composable
fun DisplaySearchPlaces(
    place: Place,
    generalViewModels: GeneralViewModels,
    navController: NavController
) {
    Card(
        shape = RoundedCornerShape(15.dp),
        elevation = 20.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                generalViewModels.setMapMode(MapMode.ZOOM_IN)
                generalViewModels.setTextMarker(place.formatted_address)
                generalViewModels.addMarker(
                    LatLng(
                        place.geometry.location.lat,
                        place.geometry.location.lng
                    )
                )
                generalViewModels.addDestinationRoute(
                    LatLng(
                        place.geometry.location.lat,
                        place.geometry.location.lng
                    )
                )
                navController.navigate("map")
            }
    ) {
        Column {
            Row(
                Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .border(1.dp, color = MaterialTheme.colorScheme.primary)
                    .padding(15.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,

                ) {
                Row(Modifier.padding(top = 5.dp)) {
                    Image(
                        modifier = Modifier.size(30.dp),
                        painter = painterResource(id = R.drawable.pin_places),
                        contentDescription = ""
                    )
                }
                Column(Modifier.width(270.dp)) {
                    Text(
                        text = place.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        style = AppTypography.titleMedium
                    )
                    Text(
                        text = place.formatted_address,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = AppTypography.titleSmall.copy(color = MaterialTheme.colorScheme.onPrimary),
                    )
                }
                Column(
                    Modifier.width(50.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LabelLarge(
                        text = place.distance.toString(),
                        color = MaterialTheme.colorScheme.onPrimary,
                        weight = FontWeight.Bold
                    )
                    LabelTiny(
                        text = "km",
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        }
    }
}