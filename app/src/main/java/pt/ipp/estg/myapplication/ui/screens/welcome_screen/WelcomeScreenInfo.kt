package pt.ipp.estg.myapplication.ui.screens.welcome_screen

import androidx.annotation.DrawableRes
import pt.ipp.estg.myapplication.R

sealed class WelcomeScreenInfo(
    @DrawableRes
    val image: Int,
    val title: Int,
    val description: Int
) {
    object First : WelcomeScreenInfo(
        image = R.drawable.fuel_prices,
        title = R.string.gas_assistance,
        description = R.string.gas_assistance_msg
    )

    object Second : WelcomeScreenInfo(
        image = R.drawable.navigator,
        title = R.string.navigation,
        description = R.string.navigation_msg
    )

    object Third : WelcomeScreenInfo(
        image = R.drawable.maintenance,
        title = R.string.maintenance,
        description = R.string.maintenance_msg
    )
}