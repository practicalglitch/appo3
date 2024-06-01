
import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.practicalglitch.ao3reader.Internet
import com.practicalglitch.ao3reader.LibraryIO
import com.practicalglitch.ao3reader.SavedWork
import com.practicalglitch.ao3reader.activities.nav.NavigationData
import com.practicalglitch.ao3reader.activities.nav.Screen
import com.practicalglitch.ao3reader.ui.theme.RederTheme
import kotlinx.coroutines.flow.collectLatest


class TagSearchActivity {
	companion object {
		var DisplayedWorks: SnapshotStateList<SavedWork> = SnapshotStateList()
		var TagText: String = ""
		var TagUrl = MutableLiveData<String>("")
		var UpdateData = MutableLiveData(false)
	}
}

//args: Tag, TagText
@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagSearchActivity (navController: NavController?) {
	val url by TagSearchActivity.TagUrl.observeAsState()
	val updateData by TagSearchActivity.UpdateData.observeAsState()
	
	LaunchedEffect(updateData!!) {
		if(updateData!!) {
		TagSearchActivity.UpdateData.postValue(false)
		Log.d("Search", "Downloading works of https://archiveofourown.org/tags/${url!!}/works")
		Internet().DownloadWorks(url!!, 1, TagSearchActivity.DisplayedWorks, true)
		}
	}
	
	RederTheme {
		Surface (modifier = Modifier.fillMaxSize()){
			Column {
				CenterAlignedTopAppBar(
					title = {
						Text("Library", maxLines = 1, overflow = TextOverflow.Ellipsis)
					},
					navigationIcon = {
						IconButton(onClick = { /*TODO*/ }) {
							Icon(Icons.Default.FilterList, "Filter")
						}
					},
					//Double check this. What would this button do?
					actions = {
						IconButton(onClick = {
							NavigationData.WebViewActivity_url = "https://archiveofourown.org/tags/${url!!}/works"
							navController!!.navigate(Screen.WebViewActivity.route)
						}) {
							Icon(Icons.Filled.Public, "Open In Webview")
						}
					}
				)
				
				
				LazyColumn(
					modifier = Modifier
						.padding(vertical = 6.dp),
					state = rememberForeverLazyListState(key = "TagSearch")
				) {
					items(
						items = TagSearchActivity.DisplayedWorks
					) { work -> LibraryWorkCard(navController, work) }
				}
			}
		}
	}
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun TagSearchActivityPreview() {
	TagSearchActivity.TagText = "Hollow Knight (Video Game)"
	RederTheme {
		Surface {
			TagSearchActivity(null)
		}
	}
}