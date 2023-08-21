package pt.ipp.estg.myapplication.ui.screens.ui_components

import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun NormalCheckBox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier
) {
    Checkbox(
        checked = checked,
        modifier = modifier,
        onCheckedChange = onCheckedChange,
        colors = CheckboxDefaults.colors(
            checkedColor = MaterialTheme.colorScheme.secondary,
            checkmarkColor = MaterialTheme.colorScheme.onSecondary,
            uncheckedColor = MaterialTheme.colorScheme.secondary
        )
    )
}