package pt.ipp.estg.myapplication.ui.screens.charge_station

import androidx.compose.runtime.Composable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.model.LatLng
import pt.ipp.estg.myapplication.R
import pt.ipp.estg.myapplication.enumerations.MapMode
import pt.ipp.estg.myapplication.models.GeneralViewModels
import pt.ipp.estg.myapplication.models.retrofit.charge_station.*
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.*
import pt.ipp.estg.myapplication.ui.theme.AppTypography

@Preview
@Composable
fun DisplayChargeStationPreview() {
    val generalViewModels: GeneralViewModels = viewModel()
    DisplayChargeStation(
        ChargeStationResponseItem(
            OperatorInfo = OperatorInfo(Title = "Title", WebsiteURL = "Website"),
            AddressInfo = AddressInfo(
                "AddressLine",
                Country(ContinentCode = "Continent", ID = 1, ISOCode = "ISO", Title = "Title"),
                0.123,
                41.366873,
                -8.194834,
                "url",
                "State",
                "Town"
            ),
            listOf(
                Connection(
                    Amps = 1,
                    ConnectionType = ConnectionType("FormalName", "Title"),
                    CurrentType = CurrentType(Description = "Description", Title = "Title"),
                    PowerKW = 10.0,
                    Voltage = 10
                )
            ),
            NumberOfPoints = 1
        ),
        generalViewModels = generalViewModels,
        navController = rememberNavController()
    )
}

@Composable
fun DisplayChargeStation(
    chargeStation: ChargeStationResponseItem,
    generalViewModels: GeneralViewModels,
    navController: NavController
) {
    val away = stringResource(id = R.string.away)
    Card(
        shape = RoundedCornerShape(15.dp),
        elevation = 20.dp,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        var expandedState by remember {
            mutableStateOf(false)
        }
        val rotationState by animateFloatAsState(
            targetValue = if (expandedState) 180f else 0f
        )

        Column {
            //general information
            Row(
                Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .border(1.dp, color = MaterialTheme.colorScheme.primary)
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,

                ) {
                Column(Modifier.width(210.dp)) {
                    Text(
                        text = chargeStation.OperatorInfo.Title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = AppTypography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    if (chargeStation.AddressInfo.Town != null) {
                        Text(
                            text = chargeStation.AddressInfo.Town,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = AppTypography.labelLarge
                        )
                    }
                    Text(
                        text = chargeStation.AddressInfo.AddressLine1,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = AppTypography.labelSmall
                    )
                }
                
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    OutlinedButton(
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colorScheme.secondary,
                        ),
                        shape = RoundedCornerShape(15.dp),
                        onClick = {
                            generalViewModels.setMapMode(MapMode.ZOOM_IN)
                            generalViewModels.setTextMarker("EV " + chargeStation.OperatorInfo.Title)
                            generalViewModels.addMarker(
                                LatLng(
                                    chargeStation.AddressInfo.Latitude,
                                    chargeStation.AddressInfo.Longitude
                                )
                            )
                            navController.navigate("map")
                        }) {
                        LabelMedium(
                            text = stringResource(id = R.string.see_on_map),
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }

                    val distance: String = if (chargeStation.AddressInfo.Distance < 1) {
                        chargeStation.AddressInfo.Distance.toString().split(".")[1].substring(
                            0,
                            3
                        ) + "m " + away
                    } else {
                        chargeStation.AddressInfo.Distance.toString()
                            .split(".")[0] + "." + chargeStation.AddressInfo.Distance.toString()
                            .split(".")[1].substring(
                            0,
                            1
                        ) + "km " + away
                    }

                    LabelSmall(
                        text = distance,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }


            //expanded details - connections
            Row(
                Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .border(1.dp, color = MaterialTheme.colorScheme.primary)
                    .padding(start = 20.dp, end = 25.dp, bottom = 5.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                if (expandedState) {
                    Column {
                        LabelMedium(
                            text = stringResource(id = R.string.available_connections),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        chargeStation.Connections.forEach {
                            ConnectionInformation(it)
                            Spacer(modifier = Modifier.height(15.dp))
                        }
                    }
                }
            }

            //expand details button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(bottom = 10.dp)
            ) {
                Row(
                    Modifier
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { expandedState = !expandedState },

                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Drop Down Arrow",
                        modifier = Modifier
                            .alpha(ContentAlpha.medium)
                            .rotate(rotationState)
                            .size(24.dp),
                    )
                    if (!expandedState) {
                        LabelTiny(
                            text = stringResource(id = R.string.see_more_details),
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    } else {
                        LabelTiny(
                            text = stringResource(id = R.string.hide_details),
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ConnectionPreview() {
    ConnectionInformation(
        Connection(
            Amps = 1,
            ConnectionType = ConnectionType("FormalName", "Title"),
            CurrentType = CurrentType(Description = "Description", Title = "Title"),
            PowerKW = 10.0,
            Voltage = 10
        )
    )
}

@Composable
fun ConnectionInformation(connections: Connection) {
    Column {
        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LabelSmall(
                text = stringResource(id = R.string.type_connection),
                color = MaterialTheme.colorScheme.onPrimary,
                weight = FontWeight.Bold
            )
            LabelSmall(
                text = connections.ConnectionType.FormalName,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LabelSmall(
                text = stringResource(id = R.string.voltage),
                color = MaterialTheme.colorScheme.onPrimary,
                weight = FontWeight.Bold
            )
            LabelSmall(
                text = connections.Voltage.toString(),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LabelSmall(
                text = stringResource(id = R.string.amps),
                color = MaterialTheme.colorScheme.onPrimary,
                weight = FontWeight.Bold
            )
            LabelSmall(
                text = connections.Amps.toString(),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LabelSmall(
                text = stringResource(id = R.string.power_in_kw),
                color = MaterialTheme.colorScheme.onPrimary,
                weight = FontWeight.Bold
            )
            LabelSmall(
                text = connections.PowerKW.toString(),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LabelSmall(
                text = stringResource(id = R.string.current_type),
                color = MaterialTheme.colorScheme.onPrimary,
                weight = FontWeight.Bold
            )
            LabelSmall(
                text = connections.CurrentType.Description,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}