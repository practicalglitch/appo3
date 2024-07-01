package com.practicalglitch.ao3reader.activities.composable.subcomposable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CheckboxSetting(text: String, checked: Boolean, onCheckedChange: ((Boolean) -> Unit)) {
    Row (
        //horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange.invoke(!checked) }

    ){
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(text = text)
    }
}

@Composable
fun CheckboxSetting(text: String, bool: MutableState<Boolean>) {
    Row (
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(25.dp, 10.dp)

    ){
        Checkbox(checked = bool.value, onCheckedChange = {bool.value = !bool.value})
        Text(text = text)
    }
}