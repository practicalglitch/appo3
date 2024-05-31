import android.content.res.Configuration
import android.graphics.drawable.Icon
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.practicalglitch.ao3reader.activities.composable.ReaderSettings
import com.practicalglitch.ao3reader.activities.nav.Screen
import com.practicalglitch.ao3reader.ui.theme.RederTheme


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
				if(currentScreen.value == 1)
					ReaderSettings()
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
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
fun AboutPage(){
	val uriHandler = LocalUriHandler.current
	RederTheme {
		Surface (modifier = Modifier.fillMaxSize()) {
			Column {
				Text(
					modifier = Modifier.padding(15.dp),
					text = "AppO3 is Free and Open Source Software distributed under the GPL-3.0.\nAppO3 is not in any way associated with the owner(s) or developer(s) of Archive of Our Own.", textAlign = TextAlign.Center)
				ClickableText(text = "Version", subtext = "v0.0.1") {
				
				}
				ClickableText(text = "Source Code", subtext = "Hosted on GitHub") {
					uriHandler.openUri("https://github.com/practicalglitch/appo3")
				}
				ClickableText(text = "Privacy Policy") {
					//TODO: add it
				}
			}
		}
	}
}

@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
fun StoragePage(){
	RederTheme {
		Surface (modifier = Modifier.fillMaxSize()) {
		
		}
	}
}

@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
fun StatsPage(){
	RederTheme {
		Surface (modifier = Modifier.fillMaxSize()) {
		
		}
	}
}



@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun SettingsActivityPreview(){
	SettingsActivity(navController = null)
}