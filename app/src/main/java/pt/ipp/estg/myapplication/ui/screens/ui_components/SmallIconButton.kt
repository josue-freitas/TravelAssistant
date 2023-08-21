package pt.ipp.estg.myapplication.ui.screens.ui_components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SmallIconButton(icon: ImageVector, onCLick: () -> Unit = {}) {
    Button(
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colorScheme.onPrimary,
            contentColor = MaterialTheme.colorScheme.primary,
        ),
        modifier = Modifier.height(45.dp),
        onClick = { onCLick() }) {
        Icon(
            imageVector = icon,
            contentDescription = "image",
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}