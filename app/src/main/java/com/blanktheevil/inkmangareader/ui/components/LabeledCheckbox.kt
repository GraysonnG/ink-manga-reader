package com.blanktheevil.inkmangareader.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.blanktheevil.inkmangareader.ui.theme.LocalPrimarySwatch

@Composable
fun LabeledCheckbox(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    checkboxColors: CheckboxColors = CheckboxDefaults.colors(
        checkedColor = LocalPrimarySwatch.current.rawColor,
        checkmarkColor = LocalPrimarySwatch.current.rawOnColor,
    ),
    short: Boolean = false,
) {
    Row(
        modifier = Modifier
            .clickable(
                role = Role.Switch,
            ) {
                onCheckedChange(!checked)
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Checkbox(
            modifier = if (short) Modifier.height(32.dp) else Modifier,
            checked = checked,
            colors = checkboxColors,
            onCheckedChange = null,
        )
        Text(text = text)
    }
}