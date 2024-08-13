package com.practicalglitch.ao3reader.activities.composable.subcomposable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun CheckboxSetting(text: String, checked: Boolean, onCheckedChange: ((Boolean) -> Unit)) {
    Row (
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

@Composable
fun RadioSet(options: Array<String>, current: String, onChange: (String) -> Unit){
    val selectedOption = remember { mutableStateOf(current) }
    
    Column(Modifier.selectableGroup()) {
        options.forEach { text ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (text == selectedOption.value),
                        onClick = {
                            selectedOption.value = text
                            onChange.invoke(text) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == selectedOption.value),
                    onClick = null // null recommended for accessibility with screenreaders
                )
                Text(
                    text = text,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}