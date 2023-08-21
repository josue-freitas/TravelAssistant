package pt.ipp.estg.myapplication.ui.screens.home

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import pt.ipp.estg.myapplication.R
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.LabelSmall
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.TitleSmall

@Preview
@Composable
fun PreviewAboutUs() {
    val isToShowAboutUs = remember { mutableStateOf(true) }
    val windowWidthSizeClass = WindowWidthSizeClass.Compact
    AboutUs(isToShowAboutUs, windowWidthSizeClass)
}

@Composable
fun AboutUs(isToShowAboutUs: MutableState<Boolean>, windowWidthSizeClass: WindowWidthSizeClass) {
    val ctx = LocalContext.current
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 10.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.clickable {
                        isToShowAboutUs.value = false
                    }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TitleSmall(
                    text = stringResource(id = R.string.about_us),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            LabelSmall(
                text = stringResource(id = R.string.about_us_text),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        if (windowWidthSizeClass == WindowWidthSizeClass.Expanded)
            Column(modifier = Modifier.width(250.dp)) {
                IntentButtons(235.dp)
                VersionInfo()
            }
        else {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IntentButtons(150.dp)
                }
                VersionInfo()
            }
        }
    }
}

@Preview
@Composable
fun PreviewIntentButtons() {
    IntentButtons(125.dp)
}

@Composable
fun IntentButtons(width: Dp) {
    val ctx = LocalContext.current
    Button(
        modifier = Modifier.width(width),
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary),
        shape = RoundedCornerShape(15.dp),
        onClick = {
            val mIntent =
                Intent(Intent.ACTION_DIAL, Uri.parse("tel:900123732"))
            ctx.startActivity(mIntent)
        }
    ) {
        LabelSmall(
            text = stringResource(id = R.string.call_us),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }

    Spacer(modifier = Modifier.width(15.dp))

    Button(
        modifier = Modifier.width(width),
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary),
        shape = RoundedCornerShape(15.dp),
        onClick = {
            val emailIntent =
                Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:geral@travel.com"))
            ctx.startActivity(emailIntent)
        }
    ) {
        LabelSmall(
            text = stringResource(id = R.string.send_us_email),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Preview
@Composable
fun PreviewVersionInfo() {
    VersionInfo()
}

@Composable
fun VersionInfo() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.width(10.dp))
        LabelSmall(
            text = stringResource(id = R.string.api_version),
            color = MaterialTheme.colorScheme.outline
        )
    }
}