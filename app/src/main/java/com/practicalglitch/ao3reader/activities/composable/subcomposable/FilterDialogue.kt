package com.practicalglitch.ao3reader.activities.composable.subcomposable

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.practicalglitch.ao3reader.Filters
import com.practicalglitch.ao3reader.GroupingTypeMap
import com.practicalglitch.ao3reader.SortingTypeMap
import com.practicalglitch.ao3reader.getKey

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun FilterDialoguePreview() {
	FilterDialogue(filters = mutableStateOf(Filters()), true)
}

@Composable
fun FastFilterChip(name: String, value: Boolean, onClick: () -> Unit) {
	FilterChip(
		modifier = Modifier.padding(3.dp, 0.dp),
		onClick = { onClick.invoke() },
		label = {
			Text(name)
		},
		selected = value,
		leadingIcon = if (value) {
			{
				Icon(
					imageVector = Icons.Filled.Done,
					contentDescription = "Done icon",
					modifier = Modifier.size(FilterChipDefaults.IconSize)
				)
			}
		} else {
			null
		}
	)
}



@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterDialogue(filters: MutableState<Filters>, AddSortingAndGrouping: Boolean){
	Column(modifier = Modifier.padding(5.dp)) {
		Text(text = "Content Rating", style = MaterialTheme.typography.titleMedium)
		FlowRow() {
			FastFilterChip(name = "General", value = filters.value.Gen) {
				filters.value = filters.value.copy(Gen = !filters.value.Gen)
			}
			FastFilterChip(name = "Teen", value = filters.value.Teen) {
				filters.value = filters.value.copy(Teen = !filters.value.Teen)
			}
			FastFilterChip(name = "Mature", value = filters.value.Mature) {
				filters.value = filters.value.copy(Mature = !filters.value.Mature)
			}
			FastFilterChip(name = "Explicit", value = filters.value.Explicit) {
				filters.value = filters.value.copy(Explicit = !filters.value.Explicit)
			}
			FastFilterChip(name = "No Rating", value = filters.value.NotRated) {
				filters.value = filters.value.copy(NotRated = !filters.value.NotRated)
			}
		}
		
		if(AddSortingAndGrouping){
			Text(text = "Group By", style = MaterialTheme.typography.titleMedium)
			RadioSet(options = GroupingTypeMap.keys.toTypedArray(), current = GroupingTypeMap.getKey(filters.value.Grouping)!!) {
				filters.value = filters.value.copy(Grouping = GroupingTypeMap[it]!!)
			}
			Text(text = "Sort By", style = MaterialTheme.typography.titleMedium)
			RadioSet(options = SortingTypeMap.keys.toTypedArray(), current = SortingTypeMap.getKey(filters.value.Sorting)!!) {
				filters.value = filters.value.copy(Sorting = SortingTypeMap[it]!!)
			}
		}
	}
}