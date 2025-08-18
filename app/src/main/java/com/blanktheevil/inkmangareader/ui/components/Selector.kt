package com.blanktheevil.inkmangareader.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.blanktheevil.inkmangareader.R
import com.blanktheevil.inkmangareader.helpers.mutableStateOfFalse
import com.blanktheevil.inkmangareader.ui.InkIcon
import com.blanktheevil.inkmangareader.ui.cap
import java.util.Locale

@Composable
fun <T> Selector(
    modifier: Modifier = Modifier,
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    locale: Locale = LocalContext.current.resources.configuration.locales[0],
    itemToString: @Composable (T) -> String = { it.toString().cap(locale) }
) {
    var expanded by remember { mutableStateOfFalse() }
    var selected by remember { mutableStateOf(selectedItem) }

    Column {
        OutlinedButton(
            shape = RoundedCornerShape(4.dp),
            modifier = modifier,
            onClick = { expanded = true },
        ) {
            if (items.isNotEmpty()) {
                Text(
                    modifier = Modifier.padding(end = 8.dp),
                    text = itemToString(selected)
                )
            }

            InkIcon(
                resId = R.drawable.round_keyboard_arrow_down_24
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach {
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            InkIcon(
                                resId = if (it == selected) {
                                    R.drawable.round_check_circle_24
                                } else {
                                    R.drawable.outline_circle_24
                                },
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp),
                            )
                            Text(text = itemToString(it))
                        }
                    },
                    onClick = {
                        expanded = false
                        selected = it
                        onItemSelected(it)
                    }
                )
            }
        }
    }
}


