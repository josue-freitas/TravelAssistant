package pt.ipp.estg.myapplication.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.*
import androidx.compose.ui.text.font.Font
import pt.ipp.estg.myapplication.R

//Replace with your font locations
val fontChosen = FontFamily(
    Font(R.font.manrope_extrabold, FontWeight.W800),
    Font(R.font.manrope_bold, FontWeight.W700),
    Font(R.font.manrope_semibold, FontWeight.W600),
    Font(R.font.manrope_medium, FontWeight.W500),
    Font(R.font.manrope_regular, FontWeight.W400),
    Font(R.font.manrope_light, FontWeight.W300),
    Font(R.font.manrope_extralight, FontWeight.W200),
)


val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = fontChosen,
        fontWeight = FontWeight.W400,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
    ),
    displayMedium = TextStyle(
        fontFamily = fontChosen,
        fontWeight = FontWeight.W400,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = fontChosen,
        fontWeight = FontWeight.W400,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp,
    ),

    //starts here
    headlineLarge = TextStyle(
        fontFamily = fontChosen,
        fontWeight = FontWeight.W400,
        fontSize = 30.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = fontChosen,
        fontWeight = FontWeight.W400,
        fontSize = 26.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = fontChosen,
        fontWeight = FontWeight.W400,
        fontSize = 22.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = fontChosen,
        fontWeight = FontWeight.W400,
        fontSize = 18.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = fontChosen,
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ), titleSmall = TextStyle(
        fontFamily = fontChosen,
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = fontChosen,
        fontWeight = FontWeight.W400,
        fontSize = 12.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    )
)