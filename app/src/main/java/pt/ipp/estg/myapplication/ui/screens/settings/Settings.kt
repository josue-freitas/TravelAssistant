package pt.ipp.estg.myapplication.ui.screens.settings

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.google.firebase.messaging.ktx.messaging
import com.shashank.sony.fancytoastlib.FancyToast
import pt.ipp.estg.myapplication.R
import pt.ipp.estg.myapplication.connection.ConnectivityObserver
import pt.ipp.estg.myapplication.ui.screens.locations.PreferencesViewModel
import pt.ipp.estg.myapplication.enumerations.SystemTheme
import pt.ipp.estg.myapplication.models.GeneralViewModels
import pt.ipp.estg.myapplication.ui.screens.ui_components.NormalCheckBox
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.LabelSmall
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.TitleLarge

@Preview
@Composable
fun PreviewSettings() {
    val preferencesViewModel: PreferencesViewModel = viewModel()
    val generalViewModels: GeneralViewModels = viewModel()
    Settings(preferencesViewModel, generalViewModels)
}

@Composable
fun Settings(
    preferencesViewModel: PreferencesViewModel,
    generalViewModels: GeneralViewModels
) {
    val actualTheme = preferencesViewModel.theme
    val language = preferencesViewModel.language
    val isToShowWelcomeScreen = preferencesViewModel.isToShowWelcomeScreen.observeAsState()
    val subFuelNotification = preferencesViewModel.subFuelNotification.observeAsState()
    val isLoading = preferencesViewModel.subFuelIsLoading.observeAsState()

    val ctx = LocalContext.current
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
            TitleLarge(text = stringResource(id = R.string.my_preferences))

            Spacer(modifier = Modifier.height(40.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                val languages = listOf("PT", "EN")
                OptionCard(
                    list = languages,
                    initialOption = if (language.value == "pt") 0 else 1,
                    title = stringResource(id = R.string.language),
                    icon = painterResource(id = R.drawable.languages),
                    onItemSelection = {
                        when (it) {
                            0 -> preferencesViewModel.updateLanguage(ctx, "pt")
                            1 -> preferencesViewModel.updateLanguage(ctx, "en")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                val systemTheme = listOf(
                    stringResource(id = R.string.light_mode),
                    stringResource(id = R.string.dark_mode),
                    stringResource(id = R.string.system_mode)
                )
                OptionCard(
                    list = systemTheme,
                    initialOption = if (actualTheme.value == SystemTheme.LIGHT) 0 else if (actualTheme.value == SystemTheme.DARK) 1 else 2,
                    title = stringResource(id = R.string.theme),
                    icon = painterResource(id = R.drawable.theme),
                    onItemSelection = {
                        when (it) {
                            0 -> preferencesViewModel.updateTheme(SystemTheme.LIGHT)
                            1 -> preferencesViewModel.updateTheme(SystemTheme.DARK)
                            2 -> preferencesViewModel.updateTheme(SystemTheme.SYSTEM)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (subFuelNotification.value != null) {
                    Log.d("settings", "sub fuel ${subFuelNotification.value}")
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary),
                        shape = RoundedCornerShape(15.dp),
                        onClick = {
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    modifier = Modifier.size(30.dp),
                                    painter = painterResource(id = R.drawable.subscription),
                                    contentDescription = ""
                                )
                                Spacer(modifier = Modifier.width(15.dp))
                                LabelSmall(
                                    text = stringResource(id = R.string.notification_sub),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            Log.d("isLoading", isLoading.value.toString())
                            if (isLoading.value == true) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(30.dp)
                                )
                            } else {
                                Switch(
                                    checked = subFuelNotification.value!!,
                                    onCheckedChange = {
                                        subFuelPricesLogic(
                                            ctx,
                                            generalViewModels.status.value
                                                ?: ConnectivityObserver.Status.Unavailable,
                                            preferencesViewModel,
                                            !subFuelNotification.value!!
                                        )
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = MaterialTheme.colorScheme.background,
                                        checkedTrackColor = MaterialTheme.colorScheme.onPrimary,

                                        uncheckedThumbColor = MaterialTheme.colorScheme.background,
                                        uncheckedTrackColor = MaterialTheme.colorScheme.onBackground,

                                        disabledCheckedThumbColor = MaterialTheme.colorScheme.background,
                                        disabledCheckedTrackColor = MaterialTheme.colorScheme.background,

                                        disabledUncheckedThumbColor = MaterialTheme.colorScheme.background,
                                        disabledUncheckedTrackColor = MaterialTheme.colorScheme.background
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isToShowWelcomeScreen.value == false) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary),
                    shape = RoundedCornerShape(15.dp),
                    onClick = {
                        preferencesViewModel.updateWelcomeScreen(ctx, true)
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                modifier = Modifier.size(30.dp),
                                painter = painterResource(id = R.drawable.eye),
                                contentDescription = ""
                            )
                            Spacer(modifier = Modifier.width(15.dp))
                            LabelSmall(
                                text = stringResource(id = R.string.show_welcome),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewOptionCard() {
    val preferencesViewModel: PreferencesViewModel = viewModel()
    val systemTheme = listOf(
        stringResource(id = R.string.light_mode),
        stringResource(id = R.string.dark_mode),
        stringResource(id = R.string.system_mode)
    )
    val actualTheme = preferencesViewModel.theme

    OptionCard(list = systemTheme,
        initialOption = if (actualTheme.value == SystemTheme.LIGHT) 0 else if (actualTheme.value == SystemTheme.DARK) 1 else 2,
        title = stringResource(id = R.string.theme),
        icon = painterResource(id = R.drawable.theme),
        onItemSelection = {
            when (it) {
                0 -> preferencesViewModel.updateTheme(SystemTheme.LIGHT)
                1 -> preferencesViewModel.updateTheme(SystemTheme.DARK)
                2 -> preferencesViewModel.updateTheme(SystemTheme.SYSTEM)
            }
        })
}

fun subFuelPricesLogic(
    ctx: Context,
    connStatus: ConnectivityObserver.Status,
    preferencesViewModel: PreferencesViewModel,
    isToSub: Boolean
) {
    if (connStatus != ConnectivityObserver.Status.Available) {
        FancyToast.makeText(
            ctx,
            ctx.resources.getString(R.string.no_internet_connection),
            FancyToast.LENGTH_LONG,
            FancyToast.ERROR,
            false
        ).show()
    } else {
        if (isToSub) {
            preferencesViewModel.subscribeToTopic(ctx, "fuel_notifications")
        } else {
            preferencesViewModel.unsubscribeFromTopic(ctx, "fuel_notifications")
            Log.d("FireBaseMessaging", "deleted token")
        }
    }
}

@Composable
fun OptionCard(
    title: String,
    initialOption: Int,
    icon: Painter,
    list: List<String>,
    onItemSelection: (selectedItemIndex: Int) -> Unit
) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary),
        shape = RoundedCornerShape(15.dp),
        onClick = {
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier.size(30.dp),
                    painter = icon,
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.width(15.dp))
                LabelSmall(
                    text = title,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            SegmentedControl(
                items = list,
                defaultSelectedItemIndex = initialOption,
                onItemSelection = onItemSelection
            )
        }
    }
}

@Preview
@Composable
fun PreviewSegmentedControl() {
    val preferencesViewModel: PreferencesViewModel = viewModel()
    val list = listOf(
        stringResource(id = R.string.light_mode),
        stringResource(id = R.string.dark_mode),
        stringResource(id = R.string.system_mode)
    )
    val actualTheme = preferencesViewModel.theme
    SegmentedControl(items = list,
        defaultSelectedItemIndex = if (actualTheme.value == SystemTheme.LIGHT) 0 else if (actualTheme.value == SystemTheme.DARK) 1 else 2,
        onItemSelection = {
            when (it) {
                0 -> preferencesViewModel.updateTheme(SystemTheme.LIGHT)
                1 -> preferencesViewModel.updateTheme(SystemTheme.DARK)
                2 -> preferencesViewModel.updateTheme(SystemTheme.SYSTEM)
            }
        })
}

@Composable
fun SegmentedControl(
    items: List<String>,
    defaultSelectedItemIndex: Int = 0,
    useFixedWidth: Boolean = false,
    itemWidth: Dp = 100.dp,
    cornerRadius: Int = 10,
    color: Color = MaterialTheme.colorScheme.background,
    onItemSelection: (selectedItemIndex: Int) -> Unit
) {
    val selectedIndex = remember { mutableStateOf(defaultSelectedItemIndex) }

    Row(
        modifier = Modifier
    ) {
        items.forEachIndexed { index, item ->
            OutlinedButton(
                modifier = when (index) {
                    0 -> {
                        if (useFixedWidth) {
                            Modifier
                                .width(itemWidth)
                                .offset(0.dp, 0.dp)
                                .zIndex(if (selectedIndex.value == index) 1f else 0f)
                        } else {
                            Modifier
                                .wrapContentSize()
                                .offset(0.dp, 0.dp)
                                .zIndex(if (selectedIndex.value == index) 1f else 0f)
                        }
                    }
                    else -> {
                        if (useFixedWidth)
                            Modifier
                                .width(itemWidth)
                                .offset((-1 * index).dp, 0.dp)
                                .zIndex(if (selectedIndex.value == index) 1f else 0f)
                        else Modifier
                            .wrapContentSize()
                            .offset((-1 * index).dp, 0.dp)
                            .zIndex(if (selectedIndex.value == index) 1f else 0f)
                    }
                },
                onClick = {
                    selectedIndex.value = index
                    onItemSelection(selectedIndex.value)
                },
                shape = when (index) {
                    /**
                     * left outer button
                     */
                    0 -> RoundedCornerShape(
                        topStartPercent = cornerRadius,
                        topEndPercent = 0,
                        bottomStartPercent = cornerRadius,
                        bottomEndPercent = 0
                    )
                    /**
                     * right outer button
                     */
                    items.size - 1 -> RoundedCornerShape(
                        topStartPercent = 0,
                        topEndPercent = cornerRadius,
                        bottomStartPercent = 0,
                        bottomEndPercent = cornerRadius
                    )
                    /**
                     * middle button
                     */
                    else -> RoundedCornerShape(
                        topStartPercent = 0,
                        topEndPercent = 0,
                        bottomStartPercent = 0,
                        bottomEndPercent = 0
                    )
                },
                border = BorderStroke(
                    1.dp, if (selectedIndex.value == index) {
                        color
                    } else {
                        color
                    }
                ),
                colors = if (selectedIndex.value == index) {
                    /**
                     * selected colors
                     */
                    ButtonDefaults.outlinedButtonColors(
                        backgroundColor = color
                    )
                } else {
                    /**
                     * not selected colors
                     */
                    ButtonDefaults.outlinedButtonColors(backgroundColor = Color.Transparent)
                },
            ) {
                LabelSmall(
                    text = item,
                    color = if (selectedIndex.value == index) {
                        MaterialTheme.colorScheme.onBackground
                    } else {
                        MaterialTheme.colorScheme.onPrimary
                    },
                )
            }
        }
    }
}