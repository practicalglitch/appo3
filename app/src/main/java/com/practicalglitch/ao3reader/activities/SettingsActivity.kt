
import android.content.res.Configuration
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.practicalglitch.ao3reader.BuildConfig
import com.practicalglitch.ao3reader.FileIO.Companion.fDir
import com.practicalglitch.ao3reader.Statistics
import com.practicalglitch.ao3reader.Storage
import com.practicalglitch.ao3reader.activities.composable.ReaderSettings
import com.practicalglitch.ao3reader.activities.composable.SettingSwitch
import com.practicalglitch.ao3reader.activities.composable.subcomposable.CheckboxSetting
import com.practicalglitch.ao3reader.ui.theme.RederTheme
import java.io.File


class SettingsActivity {
	companion object {
		var CurrentScreen = MutableLiveData(0)
	}
}

@Composable
fun SettingsMenuObject(text: String, icon: ImageVector, contDesc: String, ref: Int, change: MutableLiveData<Int>){
	Row(modifier = Modifier
		.fillMaxWidth()
		.height(40.dp)
		.clickable { change.postValue(ref) },
		verticalAlignment = Alignment.CenterVertically
	) {
		Icon(
			imageVector = icon,
			contentDescription = contDesc,
			modifier = Modifier
				.padding(horizontal = 20.dp, vertical = 0.dp)
				.size(20.dp))
		Text(
			text = text,
			style = MaterialTheme.typography.titleSmall
		)
	}
}

@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
fun PreviewStatObject(){
	var color = CardDefaults.cardColors().copy(
		containerColor = MaterialTheme.colorScheme.tertiary,
		contentColor = MaterialTheme.colorScheme.onTertiary)
	StatObject(title = "Seconds Read", stat = "2,390", color)
}

@Composable
fun StatObject(title: String, stat: String, color: CardColors){
	Card (
		modifier = Modifier.size(175.dp, 80.dp),
		colors = color
	){
		Column(
			modifier = Modifier.fillMaxSize(),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.SpaceEvenly
		) {
			Text(
				text = title,
				style = MaterialTheme.typography.titleLarge)
			Text(
				text = stat,
				style = MaterialTheme.typography.headlineSmall)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsActivity(navController: NavController?) {
	var currentScreen = SettingsActivity.CurrentScreen.observeAsState()
	
	BackHandler{
		if(currentScreen.value != 0)
			SettingsActivity.CurrentScreen.postValue(0)
		else
			navController!!.popBackStack()
		
	}
	
	RederTheme {
		Scaffold(
			topBar = {TopAppBar(title = {
				Row (verticalAlignment = Alignment.CenterVertically){
					IconButton(onClick = {
						if(currentScreen.value != 0)
							SettingsActivity.CurrentScreen.postValue(0)
						else
							navController!!.popBackStack()
					}) {
						Icon(Icons.AutoMirrored.Filled.ArrowBack, "Menu")
					}
					Text(text = "Settings")
				}
			})}
		) {
			Surface(modifier = Modifier.padding(it)) {
				if(currentScreen.value == 0){
					LazyColumn( )
					{
						item {
							SettingsMenuObject(
								text = "General Settings",
								icon = Icons.Default.Settings,
								contDesc = "Reader Settings",
								ref = 5,
								change = SettingsActivity.CurrentScreen
							)
						}
						item {
							SettingsMenuObject(
								text = "Reader Settings",
								icon = Icons.Default.Book,
								contDesc = "Reader Settings",
								ref = 1,
								change = SettingsActivity.CurrentScreen
							)
						}
						item {
							SettingsMenuObject(
								text = "Storage",
								icon = Icons.Default.Storage,
								contDesc = "Storage",
								ref = 2,
								change = SettingsActivity.CurrentScreen
							)
						}
						item {
							SettingsMenuObject(
								text = "Stats",
								icon = Icons.Default.QueryStats,
								contDesc = "Stats",
								ref = 3,
								change = SettingsActivity.CurrentScreen
							)
						}
						item {
							SettingsMenuObject(
								text = "About",
								icon = Icons.Default.Info,
								contDesc = "About",
								ref = 4,
								change = SettingsActivity.CurrentScreen
							)
						}
					}
				}
				if(currentScreen.value == 5)
					GeneralSettingsPage()
				if(currentScreen.value == 1)
					ReaderSettings()
				if(currentScreen.value == 2)
					StoragePage()
				if(currentScreen.value == 3)
					StatsPage()
				if(currentScreen.value == 4)
					AboutPage()
			}
		}
	}
}


@Composable
fun ClickableText(text: String, subtext: String = "", onClick: () -> Unit){
	Column(
		verticalArrangement = Arrangement.Center,
		modifier = Modifier
			.height(70.dp)
			.fillMaxWidth()
			.clickable { onClick.invoke() }) {
		Text(
			text = text,
			style = MaterialTheme.typography.titleLarge,
			modifier = Modifier.padding(20.dp, 0.dp))
		if(subtext != "")
			Text(
				text = subtext,
				style = MaterialTheme.typography.titleSmall,
				modifier = Modifier.padding(20.dp, 0.dp))
	}
}

@Composable
fun PopupDialog(
	onDismissRequest: () -> Unit,
	onConfirmation: () -> Unit,
	title: String,
	content: @Composable () -> Unit,
	confirmText: String = "Confirm",
	dismissText: String = "Cancel"
	){
	AlertDialog(
		title = { Text(text = title) },
		text = {
			content.invoke()
		},
		onDismissRequest = { onDismissRequest.invoke() },
		confirmButton = {
			TextButton(onClick = { onConfirmation.invoke() }) {
				Text(text = confirmText)
			}
		},
		dismissButton = {
			TextButton(onClick = { onDismissRequest.invoke() }) {
				Text(text = dismissText)
			}
		}
	)
}

@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
fun GeneralSettingsPage(){
	val statsEnabled = remember { mutableStateOf(Storage.Settings.GeneralStatsEnabled) }
	val openStatsEnabledAlert = remember { mutableStateOf(false) }
	
	RederTheme {
		Surface (modifier = Modifier.fillMaxSize()) {
			
			when { openStatsEnabledAlert.value ->
				PopupDialog(
					onDismissRequest = { openStatsEnabledAlert.value = false },
					onConfirmation = {
						statsEnabled.value = false
						Storage.Settings.GeneralStatsEnabled = false
						// Overwrite stats
						Storage.Stats = Statistics()
						Storage.SaveStatistics()
						openStatsEnabledAlert.value = false
					},
					title = "Disable Statistics?",
					content = { Text(text =
					"Disabling statistics will also erase all currently saved statistics." +
							" While you can re-enable statistics in the future," +
							" your previous statistics will not be recoverable." +
							"\nThe following statistics will be disabled and deleted:" +
							"\n- Time spent reading" +
							"\n- Chapters read"
					)
					}
				)
			}
			
			
			Column {
				SettingSwitch(text = "Enable Statistics", checked = statsEnabled.value) {
					if (statsEnabled.value) {
						openStatsEnabledAlert.value = true
					} else {
						statsEnabled.value = true
						Storage.Settings.GeneralStatsEnabled = true
					}
				}
			}
		}
	}
}

@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
fun AboutPage(){
	val uriHandler = LocalUriHandler.current
	RederTheme {
		Surface (modifier = Modifier.fillMaxSize()) {
			Column {
				Text(
					modifier = Modifier.padding(15.dp),
					text = "AppO3 is Free and Open Source Software distributed under the GPL-3.0." +
							"\nAppO3 is not in any way associated with the owner(s) or " +
							"developer(s) of Archive of Our Own.", textAlign = TextAlign.Center)
				ClickableText(text = "Check For New Updates", subtext = "Current Version: v${BuildConfig.VERSION_NAME}") {
					uriHandler.openUri("https://github.com/practicalglitch/appo3/releases")
				}
				ClickableText(text = "Source Code", subtext = "Hosted on GitHub") {
					uriHandler.openUri("https://github.com/practicalglitch/appo3")
				}
				ClickableText(text = "Privacy Policy", subtext = "TL;DR We don't collect anything.") {
					uriHandler.openUri("https://github.com/practicalglitch/appo3/blob/main/PRIVACY.md")
				}
			}
		}
	}
}

@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
fun StoragePage(){
	val openExportDialogue = remember { mutableStateOf(false) }
	val openImportDialogue = remember { mutableStateOf(false) }

	val backupSettings = remember { mutableStateOf(Storage.Companion.BackupInfo(true, true, true, true, true, true, true)) }

	val contentResolver = LocalContext.current.contentResolver
	
	var backup: File? = null

	// Selects file to export to.
	val backupCreator = rememberLauncherForActivityResult(
		ActivityResultContracts.CreateDocument("application/zip")) { selectedUri ->
		if (selectedUri != null) {
			contentResolver.openOutputStream(selectedUri)?.use {
				it.write(backup!!.readBytes())
				backup!!.delete()
			}
		} else {
			Log.d("Debug", "No file selected to backup to.")
		}
	}

	// Selects backup for import.
	val backupPicker = rememberLauncherForActivityResult(
		ActivityResultContracts.GetContent()) { selectedUri ->
		if (selectedUri != null) {
			Log.d("Debug", "URI: ${selectedUri.path}")
			val stream = contentResolver.openInputStream(selectedUri)!!
			val data = stream.readBytes()
			val file = File(fDir, "backup.zip")
			file.writeBytes(data)
			Storage.ImportBackup(file, backupSettings.value)
		} else {
			Log.d("Debug", "No file selected to restore from.")
		}
	}
	
	
	
	RederTheme {
		Surface (modifier = Modifier.fillMaxSize()) {


			// Import backup dialogue
			// TODO: Check what CAN be imported first!
			when { openImportDialogue.value ->
				PopupDialog(
					onDismissRequest = { openImportDialogue.value = false },
					onConfirmation = {
						backupPicker.launch("application/zip")
					},
					title = "Import Backup?",
					content = {
						Column {
							Text(text = "Please select which items to restore.")
							CheckboxSetting(text = "Settings", checked = backupSettings.value.settings) {backupSettings.value = backupSettings.value.copy(settings = !backupSettings.value.settings)}
							CheckboxSetting(text = "Read History", checked = backupSettings.value.readHistory) {backupSettings.value = backupSettings.value.copy(readHistory = !backupSettings.value.readHistory)}
							CheckboxSetting(text = "Saved Works", checked = backupSettings.value.savedWorks) {backupSettings.value = backupSettings.value.copy(savedWorks = !backupSettings.value.savedWorks)}
							CheckboxSetting(text = "Stats", checked = backupSettings.value.stats) {backupSettings.value = backupSettings.value.copy(stats = !backupSettings.value.stats)}
							CheckboxSetting(text = "History", checked = backupSettings.value.history) {backupSettings.value = backupSettings.value.copy(history = !backupSettings.value.history)}
							CheckboxSetting(text = "New Chapters", checked = backupSettings.value.newChapters) {backupSettings.value = backupSettings.value.copy(newChapters = !backupSettings.value.newChapters)}
							CheckboxSetting(text = "Search History", checked = backupSettings.value.searchHistory) {backupSettings.value = backupSettings.value.copy(searchHistory = !backupSettings.value.searchHistory)}
							if(backupSettings.value.readHistory)
								Text(text = "Please note that read history is not time aware. All read history will be imported as if it was just read.")
						}
					}
				)
			}

			// Export backup dialogue
			when { openExportDialogue.value ->
				PopupDialog(
					onDismissRequest = { openExportDialogue.value = false },
					onConfirmation = {
						backup = Storage.ExportBackup(backupSettings.value)
						backupCreator.launch("appo3_backup.zip")
					},
					title = "Export Backup?",
					content = {
						Column {
							Text(text = "Please select which items to export.")
							CheckboxSetting(text = "Settings", checked = backupSettings.value.settings) {backupSettings.value = backupSettings.value.copy(settings = !backupSettings.value.settings)}
							CheckboxSetting(text = "Read History", checked = backupSettings.value.readHistory) {backupSettings.value = backupSettings.value.copy(readHistory = !backupSettings.value.readHistory)}
							CheckboxSetting(text = "Saved Works", checked = backupSettings.value.savedWorks) {backupSettings.value = backupSettings.value.copy(savedWorks = !backupSettings.value.savedWorks)}
							CheckboxSetting(text = "Stats", checked = backupSettings.value.stats) {backupSettings.value = backupSettings.value.copy(stats = !backupSettings.value.stats)}
							CheckboxSetting(text = "History", checked = backupSettings.value.history) {backupSettings.value = backupSettings.value.copy(history = !backupSettings.value.history)}
							CheckboxSetting(text = "New Chapters", checked = backupSettings.value.newChapters) {backupSettings.value = backupSettings.value.copy(newChapters = !backupSettings.value.newChapters)}
							CheckboxSetting(text = "Search History", checked = backupSettings.value.searchHistory) {backupSettings.value = backupSettings.value.copy(searchHistory = !backupSettings.value.searchHistory)}
						}
					}
				)
			}

			Column {
				ClickableText(text = "Export Data", subtext = "Use to transfer data or create backup") {
					openExportDialogue.value = true
				}
				ClickableText(text = "Import Data", subtext = "Restore or transfer data") {
					openImportDialogue.value = true
				}
			}
		}
	}
}


@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
fun StatsPage(){
	var color = CardDefaults.cardColors().copy(
		containerColor = MaterialTheme.colorScheme.tertiary,
		contentColor = MaterialTheme.colorScheme.onTertiary)
	RederTheme {
		Surface (modifier = Modifier.fillMaxSize()) {
			Column {
				Column (
					modifier = Modifier.height(200.dp),
					verticalArrangement = Arrangement.SpaceEvenly,
					horizontalAlignment = Alignment.CenterHorizontally) {
					Row (
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.SpaceEvenly
					){
						val totalSecs = Storage.Stats.SecondsRead
						val hours = totalSecs / 3600
						val minutes = (totalSecs % 3600) / 60
						val seconds = totalSecs % 60
						
						val timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
						
						StatObject(title = "Time Reading", stat = timeString, color = color)
						StatObject(title = "Chapters Read", stat = Storage.Stats.ChaptersRead.toString(), color = color)
					}
					// more space for future
				}
			}
			
		}
	}
}



@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun SettingsActivityPreview(){
	SettingsActivity(navController = null)
}