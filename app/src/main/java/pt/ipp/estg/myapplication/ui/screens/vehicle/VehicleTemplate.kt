package pt.ipp.estg.myapplication.ui.screens.vehicle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.TitleLarge
import pt.ipp.estg.myapplication.R
import pt.ipp.estg.myapplication.enumerations.VehiclesTypes

/*
  Disclaimer: This preview is empty since it is a template for all screens in this module
 */
@Preview
@Composable
fun PreviewCarTemplate() {
    CarTemplate{}
}

@Composable
fun CarTemplate(content: @Composable () -> Unit) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    Box(
        Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .heightIn(min = screenHeight.dp)
            .padding(15.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
        ) {
            TitleLarge(text = stringResource(id = R.string.my_vehicles))

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                content()
            }
        }
    }
}