package pt.ipp.estg.myapplication.ui.screens.ui_components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.LabelTiny
import pt.ipp.estg.myapplication.ui.theme.AppTypography

@Composable
fun NormalTextField(
    stringState: MutableState<String>,
    modifier: Modifier = Modifier,
    errorMsg: String? = null,
    validation: ((String) -> Boolean)? = null,
    showDeleteIcon: Boolean = true,
    enabled: Boolean = true,
    colorBackground: Color = MaterialTheme.colorScheme.background,
    textColor: Color = MaterialTheme.colorScheme.primary,
) {
    fun getColor(errorColor: Color, nonErrorColor: Color): Color {
        return if (validation != null) {
            if (!validation(stringState.value) && stringState.value != "") {
                errorColor
            } else {
                nonErrorColor
            }
        } else {
            nonErrorColor
        }
    }

    OutlinedTextField(
        value = stringState.value,
        onValueChange = { stringState.value = it },
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        textStyle = AppTypography.labelLarge,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = colorBackground,
            disabledTextColor = textColor,
            disabledLabelColor = textColor,
            disabledIndicatorColor = textColor,
            textColor = textColor,
            focusedIndicatorColor = getColor(
                errorColor = Color.Red,
                nonErrorColor = textColor
            ),
            unfocusedIndicatorColor = getColor(
                errorColor = Color.Red,
                nonErrorColor = textColor
            ),
            cursorColor = textColor
        ),
        trailingIcon = {
            if (showDeleteIcon && stringState.value != "")
                IconButton(
                    onClick = {
                        stringState.value = ""
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
    if (stringState.value != "" && validation != null && !validation(stringState.value)) {
        Column(modifier = Modifier.padding(top = 5.dp)) {
            LabelTiny(text = errorMsg!!, Color.Red)
        }
    }
}

