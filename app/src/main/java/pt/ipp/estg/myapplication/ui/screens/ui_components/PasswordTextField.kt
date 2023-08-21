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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.LabelTiny
import pt.ipp.estg.myapplication.ui.theme.AppTypography

@Composable
fun PasswordTextField(
    stringState: MutableState<String>,
    modifier: Modifier = Modifier,
    errorMsg: String? = null,
    validation: ((String) -> Boolean)? = null,
    showDeleteIcon: Boolean = true,
    enabled: Boolean = true,
    colorBackground: Color = MaterialTheme.colorScheme.background,
    textColor: Color = MaterialTheme.colorScheme.primary
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

    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = stringState.value,
        onValueChange = { stringState.value = it },
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        textStyle = AppTypography.labelLarge,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = colorBackground,
            disabledTextColor = MaterialTheme.colorScheme.primary,
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
            cursorColor = textColor,
        ),
        trailingIcon = {
            val image = if (passwordVisible)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff

            // Localized description for accessibility services
            val description = if (passwordVisible) "Hide password" else "Show password"

            // Toggle button to hide or display password
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, description, tint = textColor)
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
