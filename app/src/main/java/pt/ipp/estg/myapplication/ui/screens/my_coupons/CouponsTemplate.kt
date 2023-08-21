package pt.ipp.estg.myapplication.ui.screens.my_coupons

import androidx.compose.foundation.background
import  pt.ipp.estg.myapplication.R
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.TitleLarge

@Preview
@Composable
fun PreviewCouponsTemplate() {
    CouponsTemplate {}
}

@Composable
fun CouponsTemplate(content: @Composable () -> Unit) {
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
            TitleLarge(text = stringResource(id = R.string.my_coupons))

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                content()
            }
        }
    }
}