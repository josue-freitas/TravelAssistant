package pt.ipp.estg.myapplication.ui.screens.ui_components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import pt.ipp.estg.myapplication.R
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.*
import pt.ipp.estg.myapplication.ui.theme.AppTypography

enum class DataTypes {
    CLOUD, LOCAL
}

@Preview(showBackground = true)
@Composable
fun LoginPopUpPreview() {
    val dialogIsOpen = remember { mutableStateOf(true) }
    val vehicleChoice = remember { mutableStateOf(DataTypes.CLOUD) }
    val couponsChoice = remember { mutableStateOf(DataTypes.CLOUD) }
    val locationsChoice = remember { mutableStateOf(DataTypes.CLOUD) }
    LoginPopUp(
        dialogIsOpen = dialogIsOpen,
        vehicleChoice = vehicleChoice,
        couponsChoice = couponsChoice,
        locationsChoice = locationsChoice,
    ) {}
}

@Composable
fun LoginPopUp(
    vehicleChoice: MutableState<DataTypes>,
    couponsChoice: MutableState<DataTypes>,
    locationsChoice: MutableState<DataTypes>,
    dialogIsOpen: MutableState<Boolean>,

    onCancel: () -> Unit = {},
    onSubmit: () -> Unit,
) {
    Dialog(
        onDismissRequest = {
            onCancel()
            dialogIsOpen.value = false
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .padding(10.dp, 5.dp, 5.dp, 10.dp)
                .fillMaxWidth(),
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.cloud),
                    contentDescription = "",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .padding(vertical = 35.dp)
                        .height(60.dp)
                        .fillMaxWidth(),
                )

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.login_cloud_option),
                        style = AppTypography.headlineSmall.copy(color = MaterialTheme.colorScheme.onBackground),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = stringResource(id = R.string.get_options_details),
                        style = AppTypography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Normal
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 10.dp, bottom = 35.dp)
                    )


                    RowContentLoginPopUp(
                        title = stringResource(id = R.string.my_vehicles),
                        vehicleChoice
                    )
                    Spacer(modifier = Modifier.height(25.dp))

                    RowContentLoginPopUp(
                        title = stringResource(id = R.string.my_coupons),
                        couponsChoice
                    )
                    Spacer(modifier = Modifier.height(25.dp))

                    RowContentLoginPopUp(
                        title = stringResource(id = R.string.my_locations),
                        locationsChoice
                    )
                    Spacer(modifier = Modifier.height(25.dp))

                }
                //.......................................................................
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 15.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {

                    TextButton(onClick = {
                        dialogIsOpen.value = false
                        onCancel()
                    }) {
                        Text(
                            stringResource(id = R.string.cancel),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                        )
                    }
                    TextButton(onClick = {
                        onSubmit()
                        dialogIsOpen.value = false
                    }) {
                        Text(
                            stringResource(id = R.string.submit),
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RowContentLoginPopUp(title: String, dataType: MutableState<DataTypes>) {
    var tabRowStatus by remember { mutableStateOf(0) }
    val listTab = listOf("Cloud", "Local")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LabelSmall(
            text = title,
            color = MaterialTheme.colorScheme.onBackground
        )
        TabRow(
            selectedTabIndex =
            when (dataType.value) {
                DataTypes.CLOUD -> 0
                DataTypes.LOCAL -> 1
            },
            backgroundColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.width(130.dp)
        ) {
            listTab.forEachIndexed { index, title ->
                Tab(
                    selected = tabRowStatus == index,
                    onClick = {
                        tabRowStatus = index
                        when (index) {
                            0 -> dataType.value = DataTypes.CLOUD
                            1 -> dataType.value = DataTypes.LOCAL
                        }
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
    }
}