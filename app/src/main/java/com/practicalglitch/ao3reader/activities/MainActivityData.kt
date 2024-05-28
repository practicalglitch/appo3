package com.practicalglitch.ao3reader.activities

import LibraryWorkCard
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.practicalglitch.ao3reader.FileIO
import com.practicalglitch.ao3reader.Library
import com.practicalglitch.ao3reader.LibraryIO
import com.practicalglitch.ao3reader.Save
import com.practicalglitch.ao3reader.SavedWork
import com.practicalglitch.ao3reader.Settings
import com.practicalglitch.ao3reader.activities.Discovery.Companion.DisplayFandomList
import com.practicalglitch.ao3reader.activities.Discovery.Companion.FandomList
import com.practicalglitch.ao3reader.activities.Discovery.Companion.readyToSearch
import com.practicalglitch.ao3reader.activities.composable.FandomCard
import com.practicalglitch.ao3reader.activities.composable.NewChapterCard
import com.practicalglitch.ao3reader.activities.nav.Navigation
import com.practicalglitch.ao3reader.activities.nav.NavigationData
import com.practicalglitch.ao3reader.activities.nav.Screen
import com.practicalglitch.ao3reader.ui.theme.RederTheme
import org.apio3.Types.Fandom
import org.apio3.Types.WorkChapter
import rememberForeverLazyListState
import java.io.File
import java.util.Locale


class MainActivityData : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		
		super.onCreate(savedInstanceState)
		FilesDir = applicationContext.filesDir;
		// Apparently storing context in the companion is a memory leak
		// 1984
		
		/*// add fullscreen functionality
		WindowInsetsController =
			WindowCompat.getInsetsController(window, window.decorView)
		// Configure the behavior of the hidden system bars.
		WindowInsetsController!!.systemBarsBehavior =
			WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE*/
		
		enableEdgeToEdge(
			statusBarStyle = SystemBarStyle.dark(
				Color.TRANSPARENT
			),
			navigationBarStyle = SystemBarStyle.dark(
				Color.TRANSPARENT
			)
		)
		
		//WindowInsetsController!!.hide(WindowInsetsCompat.Type.navigationBars())
		
		Settings.SaveSettings()
		
		if (FileIO.Exists(LibraryIO.HistoryFileName)!!)
			Library.history = LibraryIO.LoadHistory().toMutableList()
		
		if (FileIO.Exists(LibraryIO.SavedWorksFileName)!!) {
			val myLib = LibraryIO.LoadSavedWorks()
			for (work in myLib.works)
				myLibrary.add(work)
		}
		
		if (FileIO.Exists(LibraryIO.NewChaptersFileName)!!) {
			val nChaps = LibraryIO.LoadNewChapters()
			newChapters.addAll(nChaps)
		}
		
		val intent: Intent = intent
		val action: String? = intent.action
		val data: Uri? = intent.data
		
		if (data != null) {
			val urlSplit = data.toString().split("/")
			val workID = urlSplit[urlSplit.indexOf("works") + 1]
			LibraryIO().GetWork(workID, navToWork)
		}
		
		
		setContent {
			Navigation()
		}
	}
	
	companion object {
		var FilesDir: File? = null
		var discover: SnapshotStateList<SavedWork> = SnapshotStateList()
		var myLibrary: SnapshotStateList<SavedWork> = SnapshotStateList()
		var newChapters: SnapshotStateList<WorkChapter> = SnapshotStateList()
		var activityState = MutableLiveData<Int>(0)
		var WindowInsetsController: WindowInsetsControllerCompat? = null
		
		var UpdateProgress = MutableLiveData<Int>(-1)
		var navToWork = MutableLiveData<SavedWork?>(null)
	}
	
}

//@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
//@Preview(showBackground = true, name = "Light Mode")
//names: List<String> = List(1000) { "$it" }
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainActivity(navController: NavController?) {
	// state 0 -> library
	// state 1 -> recent
	// state 2 -> discover
	val state by MainActivityData.activityState.observeAsState()
	
	// rState 0 -> Updates
	// rState 1 -> History
	var recentState by remember { mutableStateOf(0) }
	val navTo by MainActivityData.navToWork.observeAsState()
	val updateProgress by MainActivityData.UpdateProgress.observeAsState()
	val context = LocalContext.current
	
	RederTheme {
		
		if (navTo != null && MainActivityData.navToWork.value != null) {
			NavigationData.BookInfo_work = navTo!!
			Log.d("test", "test")
			MainActivityData.navToWork.postValue(null)
			BookInfoActivity().GetChapters(LocalContext.current, navTo!!)
			navController!!.navigate(Screen.BookInfoScreen.route)
		}
		
		Scaffold (
			topBar = {
				CenterAlignedTopAppBar(
					title = {
						Text("Library", maxLines = 1, overflow = TextOverflow.Ellipsis)
					},
					navigationIcon = {
						IconButton(onClick = { /*TODO*/ }) {
							Icon(Icons.Default.Menu, "Menu")
						}
					},
					//Double check this. What would this button do?
					actions = {
						IconButton(onClick = { /*TODO*/ }) {
							Icon(Icons.Filled.Favorite, "Menu")
						}
					}
				)
			},
			bottomBar = {
				NavigationBar {
					NavigationBarItem(
						selected = true,
						icon = { Icon(Icons.AutoMirrored.Filled.LibraryBooks, "Library") },
						label = { Text("Library") },
						onClick = {
							if (state != 0) {
								MainActivityData.activityState.value = 0
							}
						}
					)
					NavigationBarItem(
						selected = true,
						icon = { Icon(Icons.Default.Update, "Updates") },
						label = { Text("Updates") },
						onClick = { MainActivityData.activityState.value = 1 }
					)
					NavigationBarItem(
						selected = true,
						icon = { Icon(Icons.Default.History, "History") },
						label = { Text("History") },
						onClick = { MainActivityData.activityState.value = 3 }
					)
					NavigationBarItem(
						selected = true,
						icon = { Icon(Icons.Default.Search, "Discover") },
						label = { Text("Discover") },
						onClick = {
							if (state != 2) {
								MainActivityData.activityState.value = 2
							}
						}
					)
				}
			}
		) {
			Box (Modifier.padding(it)) {
				// If Library
				if (state == 0) {
					LazyColumn(
						state = rememberForeverLazyListState(key = "Library"),
						modifier = Modifier
							.padding(vertical = 6.dp)
					) {
						items(
							items =
								MainActivityData.myLibrary
						) { work -> LibraryWorkCard(navController, work) }
					}
				}
				// If Recents
				if (state == 1) {
					LazyColumn(
						modifier = Modifier
							.padding(vertical = 6.dp)
					) {
						item {
							Button(
								modifier = Modifier
									.fillMaxWidth()
									.padding(30.dp, 10.dp),
								onClick = {
									LibraryIO().UpdateAllWorks(
										Library().From(MainActivityData.myLibrary),
										MainActivityData.newChapters,
										MainActivityData.UpdateProgress
									)
								}
							) {
								Text(
									text =
									if (updateProgress!! == -1)
										"Update Library"
									else if (updateProgress!! == MainActivityData.myLibrary.size)
										"Update finished"
									else
										"Updating ${updateProgress}/${MainActivityData.myLibrary.size}"
								)
							}
						}
						items(
							
							items = MainActivityData.newChapters.reversed()
						) { chap -> NewChapterCard(navController, chap) }
					}
				}
				if (state == 2) {
					Discovery(navController = navController)
				}
				if(state == 3){
					LazyColumn(
						modifier = Modifier
							.padding(vertical = 6.dp)
					) {
						items(
							items = Library.history
						) { chap -> NewChapterCard(navController, chap) }
					}
				}
			}
		}
	}
}

class Discovery{
	companion object{
		var readyToSearch: MutableLiveData<Boolean> = MutableLiveData(false)
		var FandomList: MutableList<Fandom> = mutableListOf()
		var DisplayFandomList: SnapshotStateList<Fandom> = SnapshotStateList();
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Discovery(navController: NavController?) {
	var text by remember { mutableStateOf("") }
	var query by remember { mutableStateOf("") }
	var lastquery by remember { mutableStateOf("") }
	var active by remember { mutableStateOf(false) }
	//var display by remember { mutableStateOf(false) }
	var searchHistory = remember {
		mutableStateListOf( "_PLACEHOLDER_LIST")
	}
	val rts by readyToSearch.observeAsState()
	if(searchHistory.contains("_PLACEHOLDER_LIST") && searchHistory.size == 1) {
		searchHistory.remove("_PLACEHOLDER_LIST");
		val loadedSeachHistory = Save.LoadSearchHistory();
		
		loadedSeachHistory.forEach {
			searchHistory.add(it)
		}
	}
	
	Column (
		modifier = Modifier.fillMaxWidth(),
		horizontalAlignment = Alignment.CenterHorizontally
	){
		SearchBar(
			query = text,
			onQueryChange = { text = it
				readyToSearch.postValue(false)
							//display = false
				},
			onSearch = {
				query = it;
				// Search bar stuff
				Log.d("Search", "Starting search Early")
				//display = false
				active = false
				if (searchHistory.contains(it))
					searchHistory.remove(it)
				searchHistory.add(0, text)
				if (searchHistory.size > 8)
					searchHistory.removeRange(5, searchHistory.size)
				Save.SaveSearchHistory(searchHistory.toTypedArray())
				
				// REAL stuff
				LibraryIO().DownloadAllFandoms(FandomList, readyToSearch, true)
			},
			active = active,
			onActiveChange = { active = it },
			placeholder = { Text(text = "Search for work or category...") },
			trailingIcon = {
				if (active)
					Icon(
						imageVector = Icons.Default.Close,
						contentDescription = "Close Search",
						modifier = Modifier.clickable {
							if (text.isEmpty())
								active = false
							else
								text = ""
						})
			}
		) {
			searchHistory.forEach {
				Row(modifier = Modifier
					.padding(14.dp)
					.clickable {
						text = it
					}) {
					Icon(
						modifier = Modifier.padding(end = 10.dp),
						imageVector = Icons.Default.History,
						contentDescription = "History"
					)
					Text(it)
					Spacer(modifier = Modifier.weight(1f))
					Icon(
						modifier = Modifier
							.padding(end = 10.dp)
							.clickable {
								searchHistory.remove(it)
							},
						imageVector = Icons.Default.Close,
						contentDescription = "Clear"
					)
				}
			}
		}
		
		if(rts!!){
			if(query != lastquery) {
				DisplayFandomList.removeRange(0, DisplayFandomList.size)
				Log.d("Search", "Starting search...")
				// TODO: Fuzzy Search
				// IT WORKS OKAY
				val queryLower = query.lowercase().filterNot { it.isWhitespace() }
				val list =
					FandomList.filter { fandom -> fandom.Name
						.lowercase(Locale.getDefault())
						.filterNot { it.isWhitespace() }
						.contains(queryLower) }
						.toMutableList()
				Log.d("Search", "Found ${list.size} matches from ${FandomList.size} fandoms.")
				list.sortBy { fandom -> fandom.WorksCount }
				list.reverse()
				while (list.size > 25)
					list.removeAt(24)
				Log.d("Search", "Displaying ${list.size} fandoms...")
				DisplayFandomList.addAll(list)
				lastquery = query
			} else {
				Log.d("Search", "Query same as last. ${query}, ${lastquery}. Skipped")
			}
			
			LazyColumn(
				modifier = Modifier
					.padding(vertical = 6.dp)
			) {
				items(
					items = DisplayFandomList
				) { fandom -> FandomCard(navController, fandom) }
			}
		}
	}
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun DiscoveryPreview(){
	RederTheme {
		Surface {
			Box(modifier = Modifier.fillMaxSize()){
				Discovery(navController = null)
			}
		}
	}
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
//@Preview(showBackground = true, name = "Light Mode")
@Composable
fun MainActivityLibraryPreview() {
	var library = Library()
	library.quickPopulate()
	RederTheme {
		Surface {
			MainActivity(null)
		}
	}
}