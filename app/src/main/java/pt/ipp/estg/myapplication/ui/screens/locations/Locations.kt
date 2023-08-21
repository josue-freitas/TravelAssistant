package pt.ipp.estg.myapplication.ui.screens.locations

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.model.LatLng
import com.shashank.sony.fancytoastlib.FancyToast
import pt.ipp.estg.myapplication.R
import pt.ipp.estg.myapplication.enumerations.MapMode
import pt.ipp.estg.myapplication.enumerations.TypeLocation
import pt.ipp.estg.myapplication.models.GeneralViewModels
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.*
import pt.ipp.estg.myapplication.ui.theme.AppTypography

@Preview
@Composable
fun PreviewLocations() {
    val generalViewModels: GeneralViewModels = viewModel()
    val preferencesViewModel: PreferencesViewModel = viewModel()
    Locations(
        generalViewModels = generalViewModels,
        navController = rememberNavController(),
        preferencesViewModel = preferencesViewModel
    )
}

@Composable
fun Locations(
    generalViewModels: GeneralViewModels,
    navController: NavController,
    preferencesViewModel: PreferencesViewModel
) {
    val preferencesHome = preferencesViewModel.preferencesHome.observeAsState()
    val preferencesWork = preferencesViewModel.preferencesWork.observeAsState()

    val homeLocationString = stringResource(id = R.string.home_location)
    val workLocationString = stringResource(id = R.string.work_location)
    val longClickToSaveString = stringResource(id = R.string.long_click_on_location_to_save)

    val context = LocalContext.current

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
                .verticalScroll(rememberScrollState())
        ) {
            TitleLarge(text = stringResource(id = R.string.home_location))

            Spacer(modifier = Modifier.height(10.dp))

            Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.secondary)

            Spacer(modifier = Modifier.height(10.dp))

            if (preferencesHome.value?.district == " ") {
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
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            LabelLarge(
                                text = stringResource(id = R.string.home_location_not_defined),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            OutlinedButton(
                                modifier = Modifier.padding(top = 15.dp),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = MaterialTheme.colorScheme.secondary,
                                ),
                                shape = RoundedCornerShape(15.dp),
                                onClick = {
                                    preferencesViewModel.setTypeLocation(TypeLocation.HOME)
                                    generalViewModels.setTextMarker(homeLocationString)
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
                                painter = painterResource(id = R.drawable.house),
                                contentDescription = "House icon",
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(5.dp)
                            )
                            Column(Modifier.width(200.dp)) {
                                Text(
                                    text = preferencesHome.value?.street.toString(),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold,
                                    style = AppTypography.titleMedium
                                )
                                Text(
                                    text = preferencesHome.value?.district.toString() + ", " + preferencesHome.value?.country.toString(),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    style = AppTypography.titleSmall
                                )
                                LabelTiny(
                                    text = stringResource(id = R.string.last_update) + ": " + preferencesHome.value?.lastUpdate.toString(),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                OutlinedButton(
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = MaterialTheme.colorScheme.secondary,
                                    ),
                                    shape = RoundedCornerShape(15.dp),
                                    onClick = {
                                        preferencesViewModel.setTypeLocation(TypeLocation.HOME)
                                        generalViewModels.setTextMarker(homeLocationString)
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
                                        TypeLocation.HOME
                                    )
                                }) {
                                    Icon(
                                        Icons.Filled.Close,
                                        contentDescription = "",
                                        tint = MaterialTheme.colorScheme.background
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(30.dp))

            TitleLarge(text = stringResource(id = R.string.work_location))

            Spacer(modifier = Modifier.height(10.dp))

            Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.secondary)

            Spacer(modifier = Modifier.height(10.dp))

            if (preferencesWork.value?.district == " ") {
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
                        horizontalArrangement = Arrangement.SpaceEvenly,

                        ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            LabelLarge(
                                text = stringResource(id = R.string.work_location_not_defined),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            OutlinedButton(
                                modifier = Modifier.padding(top = 15.dp),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = MaterialTheme.colorScheme.secondary,
                                ),
                                shape = RoundedCornerShape(15.dp),
                                onClick = {
                                    preferencesViewModel.setTypeLocation(TypeLocation.WORK)
                                    generalViewModels.setTextMarker(workLocationString)
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
                    elevation = 20.dp,
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
                                .border(1.dp, color = MaterialTheme.colorScheme.primary)
                                .padding(15.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,

                            ) {
                            Image(
                                painter = painterResource(id = R.drawable.work),
                                contentDescription = "Work icon",
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(5.dp)
                            )
                            Column(Modifier.width(200.dp)) {
                                Text(
                                    text = preferencesWork.value?.street.toString(),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold,
                                    style = AppTypography.titleMedium
                                )
                                Text(
                                    text = preferencesWork.value?.district.toString() + ", " + preferencesWork.value?.country.toString(),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    style = AppTypography.titleSmall
                                )
                                LabelTiny(
                                    text = stringResource(id = R.string.last_update) + ": " + preferencesWork.value?.lastUpdate.toString(),
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
                                        preferencesViewModel.setTypeLocation(TypeLocation.WORK)
                                        generalViewModels.setTextMarker(workLocationString)
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
                                        TypeLocation.WORK
                                    )
                                }) {
                                    Icon(
                                        Icons.Filled.Close,
                                        contentDescription = "",
                                        tint = MaterialTheme.colorScheme.background
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}