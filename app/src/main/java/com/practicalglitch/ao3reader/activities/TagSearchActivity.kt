
import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.practicalglitch.ao3reader.Filters
import com.practicalglitch.ao3reader.Internet
import com.practicalglitch.ao3reader.SavedWork
import com.practicalglitch.ao3reader.activities.nav.NavigationData
import com.practicalglitch.ao3reader.activities.nav.Screen
import com.practicalglitch.ao3reader.isInFilter
import com.practicalglitch.ao3reader.ui.theme.RederTheme


class TagSearchActivity {
	companion object {
		var DisplayedWorks: SnapshotStateList<SavedWork> = SnapshotStateList()
	}
}

fun LazyListState.isScrolledToTheEnd() = layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1


//args: Tag, TagText
@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagSearchActivity (
	navController: NavController?,
	tag: String,
	name: String
) {
	val updateData = remember { mutableStateOf(true) }
	val currentPage = remember { mutableIntStateOf(1) }
	val listState = rememberForeverLazyListState(key = "TagSearch")

	// do on start
	LaunchedEffect(updateData) {
		if (updateData.value) {
			updateData.value = false
			Log.d("Search", "Downloading works of https://archiveofourown.org/tags/${tag}/works")
			Internet().DownloadWorks(
				tag,
				currentPage.intValue,
				TagSearchActivity.DisplayedWorks,
				currentPage.intValue == 1
			)
		}
	}
	
	LaunchedEffect(listState.isScrolledToTheEnd()) {
		if (listState.isScrolledToTheEnd() && TagSearchActivity.DisplayedWorks.size != 0){
			currentPage.intValue++
			Log.d("Search", "Downloading works of https://archiveofourown.org/tags/${tag}/works, page ${currentPage.intValue}")
			Internet().DownloadWorks(
				tag,
				currentPage.intValue,
				TagSearchActivity.DisplayedWorks,
				currentPage.intValue == 1
			)
		}
	}
	
	RederTheme {
		Surface (modifier = Modifier.fillMaxSize()){
			
			val showFilters = remember { mutableStateOf(false)}
			val filters = remember { mutableStateOf(Filters())}
			if(showFilters.value) {
				ModalBottomSheet(
					onDismissRequest = { showFilters.value = false },
					sheetState = rememberModalBottomSheetState(),
					dragHandle = { BottomSheetDefaults.DragHandle() },
				) {
					FilterDialogue(filters = filters)
				}
			}
			
			Column {
				CenterAlignedTopAppBar(
					title = {
						Text(name, maxLines = 1, overflow = TextOverflow.Ellipsis)
					},
					navigationIcon = {
						IconButton(onClick = { showFilters.value = true }) {
							Icon(Icons.Default.FilterList, "Filter")
						}
					},
					actions = {
						IconButton(onClick = {
							NavigationData.WebViewActivity_url = "https://archiveofourown.org/tags/${tag!!}/works"
							navController!!.navigate(Screen.WebViewActivity.route)
						}) {
							Icon(Icons.Filled.Public, "Open In Webview")
						}
					}
				)
				
				
				LazyColumn(
					modifier = Modifier
						.padding(vertical = 6.dp),
					state = listState
				) {
					items(
						items = TagSearchActivity.DisplayedWorks
					) { work ->
						if(work.isInFilter(filters.value))
							LibraryWorkCard(navController, work.Work.Id, true)
					}
					
					item { SearchThrobber() }
				}
			}
		}
	}
}



@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun FilterDialoguePreview() {
	FilterDialogue(filters = mutableStateOf(Filters()))
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
fun FilterDialogue(filters: MutableState<Filters>){
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
	}
}


@Composable
fun SearchThrobber() {
	// TODO: make a throbber
	// what a dumb name btw
	Text(text = "Loading works, please wait...")
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun TagSearchActivityPreview() {
	RederTheme {
		Surface {
			TagSearchActivity(null, "Hollow Knight (Video Game)", "Hollow Knight (Video Game)")
		}
	}
}