package pt.ipp.estg.myapplication.ui.screens.ui_components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import pt.ipp.estg.myapplication.ui.theme.AppTypography

@Composable
fun FloatTextField(
    numberState: MutableState<Float>,
    modifier: Modifier = Modifier,
    showDeleteIcon: Boolean = true,
) {
    OutlinedTextField(
        value = numberState.value.toString(),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        onValueChange = {
            numberState.value =
                if (it.toFloatOrNull() != null) {
                    it.toFloat()
                } else {
                    numberState.value
                }
        },
        shape = RoundedCornerShape(12.dp),
        textStyle = AppTypography.labelLarge,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colorScheme.background,
            textColor = MaterialTheme.colorScheme.primary,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary,
        ),
        trailingIcon = {
            if (showDeleteIcon && numberState.value != 0f )
            IconButton(
                onClick = {
                    numberState.value = 0f
                },
            ) {
                Icon(
                    Icons.Default.Clear,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        },
        singleLine = true,
        modifier = modifier
    )
}
