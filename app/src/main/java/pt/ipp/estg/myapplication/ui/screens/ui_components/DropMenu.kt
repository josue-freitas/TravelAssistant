package pt.ipp.estg.myapplication.ui.screens.ui_components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import pt.ipp.estg.myapplication.ui.theme.AppTypography

@Composable
fun DropMenu(
    suggestions: List<String>,
    suggestionChosen: MutableState<String>,
    label: String? = null,
    enabled: Boolean? = false,
    onValueChange: () -> Unit = {},
    onClick: () -> Unit = {},
    showError: Boolean = false,
    testTag : String = "",
) {
    var expanded by remember { mutableStateOf(false) }

    var textfieldSize by remember { mutableStateOf(Size.Zero) }

    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column() {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { textfieldSize.width.toDp() })
        ) {
            suggestions.forEach { label ->
                DropdownMenuItem(onClick = {
                    suggestionChosen.value = label
                    expanded = false
                    onValueChange()
                    onClick()
                }) {
                    Text(text = label)
                }
            }
        }
        OutlinedTextField(
            value = suggestionChosen.value,
            onValueChange = { Log.d("dropmenu", "on value change") },
            enabled = false,
            shape = RoundedCornerShape(12.dp),
            textStyle = AppTypography.labelLarge,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colorScheme.background,
                disabledTextColor = MaterialTheme.colorScheme.primary,
                disabledLabelColor = MaterialTheme.colorScheme.primary,
                disabledIndicatorColor = MaterialTheme.colorScheme.primary,
                textColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = if (showError) Color.Red else MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = if (showError) Color.Red else MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag)
                .clickable(onClick = { expanded = !expanded })
                .onGloballyPositioned { coordinates ->
                    textfieldSize = coordinates.size.toSize()
                },
            label = { label?.let { Text(label) } },
            trailingIcon = {
                Icon(icon, "contentDescription",
                    Modifier.clickable { expanded = !expanded })
            },
        )
    }
}