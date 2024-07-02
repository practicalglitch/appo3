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
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.practicalglitch.ao3reader.Internet
import com.practicalglitch.ao3reader.Library
import com.practicalglitch.ao3reader.Save
import com.practicalglitch.ao3reader.Storage
import com.practicalglitch.ao3reader.activities.Discovery.Companion.DisplayFandomList
import com.practicalglitch.ao3reader.activities.Discovery.Companion.FandomList
import com.practicalglitch.ao3reader.activities.composable.FandomCard
import com.practicalglitch.ao3reader.activities.composable.NewChapterCard
import com.practicalglitch.ao3reader.activities.nav.Navigation
import com.practicalglitch.ao3reader.activities.nav.Navigator
import com.practicalglitch.ao3reader.activities.nav.Screen
import com.practicalglitch.ao3reader.ui.theme.RederTheme
import org.apio3.Types.Fandom
import rememberForeverLazyListState
import java.io.File
import java.util.Locale


class MainActivityData : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		
		super.onCreate(savedInstanceState)
		FilesDir = applicationContext.filesDir;
		
		// Enable fullscreen
		// TODO: Disable status bar + nav bar
		// TODO: Make setting for enable/disable this work
		enableEdgeToEdge(
			statusBarStyle = SystemBarStyle.dark(
				Color.TRANSPARENT
			),
			navigationBarStyle = SystemBarStyle.dark(
				Color.TRANSPARENT
			)
		)
		
		//WindowInsetsController!!.hide(WindowInsetsCompat.Type.navigationBars())
		
		
		val intent: Intent = intent
		val action: String? = intent.action
		val data: Uri? = intent.data
		
		if (data != null) {
			val urlSplit = data.toString().split("/")
			openInAppWorkID = urlSplit[urlSplit.indexOf("works") + 1]
			//Internet().DownloadWorkMetadata(workID, navToWork)
		}
		
		
		setContent {
			Navigation()
		}
	}
	
	companion object {
		var FilesDir: File? = null
		var openInAppWorkID: String = ""
		//var myLibrary: SnapshotStateList<SavedWork> = SnapshotStateList()
		//var newChapters: SnapshotStateList<WorkChapter> = SnapshotStateList()
		//var activityState = MutableLiveData<Int>(0)
		
		//var UpdateProgress = MutableLiveData<Int>(-1)
		//var navToWork = MutableLiveData<SavedWork?>(null)
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
	val activityState = remember { mutableStateOf(0) }
	val updateProgress = remember { mutableStateOf(-1) }
	val chapterUpdateStatus = remember { mutableStateOf(0) }
	val savedWorkIDs = remember { mutableListOf<String>() }
	
	val bootup = remember { mutableStateOf(false) }
	
	if(!bootup.value){
		
		// Load saved work ids an
		Storage.LoadSavedWorkIDs()
		Storage.SavedWorkIDs.forEach { savedWorkIDs.add(it) }

		Storage.LoadHistory()
		Storage.LoadNewChapters()
		Storage.LoadStatistics()
		Storage.LoadSettings()
		
		
		bootup.value = true
	}
	
	LaunchedEffect(MainActivityData.openInAppWorkID != "") {
		if (MainActivityData.openInAppWorkID != "") {
			Log.d("debug", "Workid:${MainActivityData.openInAppWorkID}")
			Navigator.ToBookInfoActivity(navController!!,
				MainActivityData.openInAppWorkID)
			MainActivityData.openInAppWorkID = ""
		}
	}
	
	
	RederTheme {
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
						IconButton(onClick = {
							navController!!.navigate(Screen.SettingsActivity.route)
						}) {
							Icon(Icons.Filled.Settings, "Menu")
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
							if (activityState.value != 0)
								activityState.value = 0
						}
					)
					NavigationBarItem(
						selected = true,
						icon = { Icon(Icons.Default.Update, "Updates") },
						label = { Text("Updates") },
						onClick = { activityState.value = 1 }
					)
					NavigationBarItem(
						selected = true,
						icon = { Icon(Icons.Default.History, "History") },
						label = { Text("History") },
						onClick = { activityState.value = 3 }
					)
					NavigationBarItem(
						selected = true,
						icon = { Icon(Icons.Default.Search, "Discover") },
						label = { Text("Discover") },
						onClick = {
							if (activityState.value != 2)
								activityState.value = 2
							
						}
					)
				}
			}
		) {
			Box (Modifier.padding(it)) {
				// If Library
				if (activityState.value == 0) {
					LazyColumn(
						state = rememberForeverLazyListState(key = "Library"),
						modifier = Modifier
							.padding(vertical = 6.dp)
					) {
						items(
							items = savedWorkIDs
						) { workId -> LibraryWorkCard(navController, workId, false) }
					}
				}
				// If Recents
				if (activityState.value == 1) {
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
									Internet().UpdateSavedWorks(
										chapterUpdateStatus
									)
								}
							) {
								Text(
									text =
									if (updateProgress.value == -1)
										"Update Library"
									else if (updateProgress.value == savedWorkIDs.size)
										"Update finished"
									else
										"Updating ${updateProgress}/${savedWorkIDs.size}"
								)
							}
						}
						items(
							items = Storage.NewChapters.reversed()
						) { chap -> NewChapterCard(navController, chap) }
					}
				}
				if (activityState.value == 2) {
					Discovery(navController)
				}
				if(activityState.value == 3){
					LazyColumn(
						modifier = Modifier
							.padding(vertical = 6.dp)
					) {
						items(
							items = Storage.History
						) { chap -> NewChapterCard(navController, chap) }
					}
				}
			}
		}
	}
}

class Discovery{
	companion object{
		var FandomList: MutableList<Fandom> = mutableListOf()
		var DisplayFandomList: SnapshotStateList<Fandom> = SnapshotStateList();
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Discovery(
	navController: NavController?
) {
	var text by remember { mutableStateOf("") }
	var query by remember { mutableStateOf("") }
	var lastquery by remember { mutableStateOf("") }
	var active by remember { mutableStateOf(false) }
	var loading = remember {mutableStateOf(false)}
	var searchHistory = remember {
		mutableStateListOf( "_PLACEHOLDER_LIST")
	}
	val readyToSearch = remember { mutableStateOf(false) }
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
				readyToSearch.value = false
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
				
				loading.value = true
				
				// REAL stuff
				Internet().DownloadAllFandoms(FandomList, readyToSearch, true)
			},
			active = active,
			onActiveChange = { active = it },
			placeholder = { Text(text = "Search for fandom...") },
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
		
		if(loading.value) {
			Text(text = "Loading, please note this may take a while...")
		}
		
		if(readyToSearch.value){
			loading.value = false
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
				Discovery(null)
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