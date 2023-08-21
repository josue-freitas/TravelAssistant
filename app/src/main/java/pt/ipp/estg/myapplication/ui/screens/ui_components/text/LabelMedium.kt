package pt.ipp.estg.myapplication.ui.screens.ui_components.text

import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import pt.ipp.estg.myapplication.ui.theme.AppTypography

@Composable
fun LabelMedium(text: String , color : Color = MaterialTheme.colorScheme.primary, weight: FontWeight = FontWeight.W400) {
    //plate
    Text(
        text = text,
        color = color,
        style = AppTypography.titleMedium,
        fontWeight = weight
    )
}