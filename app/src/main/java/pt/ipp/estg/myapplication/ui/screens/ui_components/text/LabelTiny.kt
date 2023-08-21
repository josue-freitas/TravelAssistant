package pt.ipp.estg.myapplication.ui.screens.ui_components.text

import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import pt.ipp.estg.myapplication.ui.theme.AppTypography

@Composable
fun LabelTiny(text: String, color : Color = MaterialTheme.colorScheme.primary ) {
    //plate
    Text(
        text = text,
        style = AppTypography.labelSmall.copy(color = color)
    )
}