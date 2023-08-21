package pt.ipp.estg.myapplication.ui.screens.ui_components

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import pt.ipp.estg.myapplication.R
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.TitleSmall
import pt.ipp.estg.myapplication.ui.theme.AppTypography

@Composable
fun SplashScreen(navController: NavController, isToShowWelcomeScreen : Boolean) {
    val scale = remember {
        androidx.compose.animation.core.Animatable(0f)
    }

    // AnimationEffect
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.7f,
            animationSpec = tween(
                durationMillis = 500,
                easing = {
                    OvershootInterpolator(10f).getInterpolation(it)
                })
        )
        delay(1000)
        navController.navigate(if (isToShowWelcomeScreen) "welcome_screen" else "home")
    }

    // Image
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.splash_logo),
                contentDescription = "Logo",
                modifier = Modifier.scale(scale.value)
            )
            Spacer(modifier = Modifier.height(20.dp))
//            Text(
//                text = stringResource(id = R.string.app_name),
//                style = AppTypography.headlineLarge.copy(color = MaterialTheme.colorScheme.onBackground),
//                fontWeight = FontWeight.ExtraBold
//            )
        }
    }
}