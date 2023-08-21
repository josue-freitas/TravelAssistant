package pt.ipp.estg.myapplication.ui.screens.ui_components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pt.ipp.estg.myapplication.ui.screens.ui_components.text.LabelTiny
import pt.ipp.estg.myapplication.ui.theme.AppTypography


@Composable
fun SearchBar(
    input: MutableState<String>,
    placeholder: String,
    colorSecondary: Color = MaterialTheme.colorScheme.secondary
) {
    BasicTextField(
        value = "",
        onValueChange = {},
        modifier = Modifier
            .background(
                Color.White,
                CircleShape
            )
            .border(1.dp, colorSecondary, RoundedCornerShape(25.dp))
            .fillMaxWidth(),
        maxLines = 1,
        textStyle = AppTypography.labelLarge.copy(colorSecondary),
        decorationBox = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "image",
                    tint = colorSecondary
                )
                TextField(
                    value = input.value,
                    onValueChange = { input.value = it },
                    label = { LabelTiny(text = placeholder, colorSecondary) },
                    textStyle = AppTypography.labelLarge.copy(colorSecondary),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White,
                        textColor = colorSecondary,
                        focusedIndicatorColor = Color.White,
                        cursorColor = colorSecondary,
                        disabledIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.White
                    ),
                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier

                        .fillMaxWidth()
                ) {
                    IconButton(
                        onClick = {
                            input.value = ""
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "image",
                            tint = colorSecondary
                        )
                    }
                }
            }
        }
    )
}