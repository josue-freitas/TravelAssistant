package pt.ipp.estg.myapplication.ui.screens.welcome_screen

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.*
import pt.ipp.estg.myapplication.R
import pt.ipp.estg.myapplication.ui.screens.locations.PreferencesViewModel
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.*

@Preview
@Composable
fun PreviewWelcomeScreen() {
    var preferencesViewModel: PreferencesViewModel = viewModel();
    WelcomeScreen(rememberNavController(), preferencesViewModel)
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalPagerApi::class)
@Composable
fun WelcomeScreen(
    navController: NavController,
    preferencesViewModel: PreferencesViewModel
) {
    val context = LocalContext.current
    val pages = listOf(
        WelcomeScreenInfo.First,
        WelcomeScreenInfo.Second,
        WelcomeScreenInfo.Third
    )
    val pagerState = rememberPagerState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        //will make the slide to the sides change pages
        HorizontalPager(
            modifier = Modifier.weight(10f),
            count = 3,
            state = pagerState,
            verticalAlignment = Alignment.Top
        ) { position ->
            PagerScreen(pageToShow = pages[position])
        }
        //the dots in the end of screen
        HorizontalPagerIndicator(
            inactiveColor = MaterialTheme.colorScheme.primary,
            activeColor = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .weight(1f),
            pagerState = pagerState
        )
        FinishButton(
            modifier = Modifier.weight(1f),
            pagerState = pagerState
        ) {
            navController.navigate("home")
            preferencesViewModel.updateWelcomeScreen(context, false)
//            val preferences = context.getSharedPreferences("myPref", Context.MODE_PRIVATE)
//            val editor = preferences.edit()
//            editor.apply {
//                editor.putBoolean("isToShowWelcomeScreen", false)
//                apply()
//            }
        }
    }
}

@Composable
fun PagerScreen(pageToShow: WelcomeScreenInfo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .fillMaxHeight(0.5f),
            painter = painterResource(id = pageToShow.image),
            contentDescription = "Pager Image"
        )
        TitleLarge(
            text = stringResource(id = pageToShow.title),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(50.dp))
        LabelLarge(
            text = stringResource(id = pageToShow.description),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
fun FinishButton(
    modifier: Modifier,
    pagerState: PagerState,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .padding(horizontal = 40.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            modifier = Modifier.fillMaxWidth(),
            visible = pagerState.currentPage == 2
        ) {
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    backgroundColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(text = stringResource(id = R.string.finish))
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun FirstOnBoardingScreenPreview() {
    Column(modifier = Modifier.fillMaxSize()) {
        PagerScreen(pageToShow = WelcomeScreenInfo.First)
    }
}

@Composable
@Preview(showBackground = true)
fun SecondOnBoardingScreenPreview() {
    Column(modifier = Modifier.fillMaxSize()) {
        PagerScreen(pageToShow = WelcomeScreenInfo.Second)
    }
}

@Composable
@Preview(showBackground = true)
fun ThirdOnBoardingScreenPreview() {
    Column(modifier = Modifier.fillMaxSize()) {
        PagerScreen(pageToShow = WelcomeScreenInfo.Third)
    }
}