package pt.ipp.estg.myapplication.ui.screens.ui_components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import pt.ipp.estg.myapplication.helper.Validations
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.LabelTiny
import pt.ipp.estg.myapplication.ui.theme.AppTypography

@Composable
fun IntTextField(
    numberState: MutableState<Int>,
    ignoreValue: Int,
    modifier: Modifier = Modifier,
    showDeleteIcon: Boolean = true,
    errorMsg: String? = null,
    validation: ((Int) -> Boolean)? = null,
    colorBackground: Color = MaterialTheme.colorScheme.background,
    textColor: Color = MaterialTheme.colorScheme.primary
) {

    fun getColor(errorColor: Color, nonErrorColor: Color): Color {
        return if (validation != null) {
            if (!validation(numberState.value) && numberState.value != ignoreValue) {
                errorColor
            } else {
                nonErrorColor
            }
        } else {
            nonErrorColor
        }
    }

    OutlinedTextField(
        value = if (numberState.value == ignoreValue) "" else numberState.value.toString() ,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        onValueChange = {
            numberState.value =
                if (it.toIntOrNull() != null) {
                    it.toInt()
                } else {
                    numberState.value
                }
        },
        shape = RoundedCornerShape(12.dp),
        textStyle = AppTypography.labelLarge,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = colorBackground,
            textColor = textColor,
            focusedIndicatorColor = getColor(
                errorColor = Color.Red,
                nonErrorColor = textColor
            ),
            unfocusedIndicatorColor = getColor(
                errorColor = Color.Red,
                nonErrorColor = textColor
            ),
            cursorColor = textColor,
        ),
        trailingIcon = {
            if (showDeleteIcon && numberState.value != 0 )
            IconButton(
                onClick = {
                    numberState.value = 0
                },
            ) {
                Icon(
                    Icons.Default.Clear,
                    contentDescription = "",
                    tint = textColor
                )
            }
        },
        singleLine = true,
        modifier = modifier
    )
    if (numberState.value != ignoreValue && validation != null && !validation(numberState.value)) {
        Column(modifier = Modifier.padding(top = 5.dp)) {
            LabelTiny(text = errorMsg!!, Color.Red)
        }
    }
}
