package com.blanktheevil.inkmangareader.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun TextInputField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    shape: Shape = RectangleShape,
    placeholder: String = "",
    trailingIcon: (@Composable () -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) = TextField(
    modifier = modifier,
    value = value,
    onValueChange = onValueChange,
    trailingIcon = trailingIcon,
    placeholder = placeholder.takeIf { it.isNotEmpty() }?.let { { Text(placeholder) } },
    singleLine = true,
    shape = shape,
    visualTransformation = visualTransformation,
    colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color.Transparent,
        unfocusedBorderColor = Color.Transparent,
        disabledBorderColor = Color.Transparent,
        focusedContainerColor = MaterialTheme.colorScheme.surfaceDim,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceDim,
        disabledContainerColor = MaterialTheme.colorScheme.surfaceDim,
    )
)
