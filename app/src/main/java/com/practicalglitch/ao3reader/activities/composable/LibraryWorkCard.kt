import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.practicalglitch.ao3reader.Get
import com.practicalglitch.ao3reader.SavedWork
import com.practicalglitch.ao3reader.Storage
import com.practicalglitch.ao3reader.activities.nav.Navigator
import com.practicalglitch.ao3reader.ui.theme.RederTheme

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun LibraryCardPreview1() {
	val w = SavedWork.DummySavedWork()
	RederTheme {
		Surface {
			LibraryWorkCard(null, "", false)
		}
	}
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun LibraryCardPreview2() {
	val w = SavedWork.DummySavedWork()
	RederTheme {
		Surface {
			LibraryWorkCard(null, "", true)
		}
	}
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryWorkCard(
	navController: NavController? = null,
	id: String,
	onlineView: Boolean
) {
	val work = remember { mutableStateOf(SavedWork()) }
	val workLoaded = remember { mutableStateOf(false) }
	
	
	val isPreview = remember { mutableStateOf(false) }
	LaunchedEffect(!workLoaded.value) {
		if (id == "") {
			work.value = SavedWork.DummySavedWork() // preview
			workLoaded.value = true
			isPreview.value = true
			}
		else
			Get.SavedWork(id, work, workLoaded, true)
	}
	
	Box {
		
		OutlinedCard(
			onClick = {
				Navigator.ToBookInfoActivity(navController!!, work.value.Work.Id)
			},
			shape = MaterialTheme.shapes.small,
			modifier = Modifier
				.width(LocalConfiguration.current.screenWidthDp.dp)
				.height(100.dp)
				.padding(3.dp)
		) {
			Column(
				modifier = Modifier
					.weight(1f)
					.padding(10.dp, 10.dp),
				verticalArrangement = Arrangement.SpaceBetween
			) {
				Row(
					modifier = Modifier.fillMaxWidth(),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween
				) {
					Text(
						modifier = Modifier.weight(1f),
						text =
						if (workLoaded.value && work.value.Work != null)
							work.value.Work.Title
						else
							"",
						maxLines = 1,
						overflow = TextOverflow.Ellipsis
					)
					if (!onlineView) {
						Badge(containerColor = MaterialTheme.colorScheme.secondary) {
							Text(
								text =
								if (workLoaded.value && work.value.Work != null)
									work.value.UnreadChapters().toString()
								else
									""
							)
						}
					}
				}
				
				Text(
					text =
					if (workLoaded.value && work.value.Work != null)
						work.value.FandomList(1)
					else ""
				)
				var dispTot = "?"
				if (workLoaded.value && work.value.Work != null && work.value.Work.ChaptersTotal != -1)
					dispTot = work.value.Work.ChaptersTotal.toString()
				
				Text(
					text =
					if (workLoaded.value && work.value.Work != null)
						work.value.Work.ChaptersAvailable.toString() + "/" + dispTot
					else
						""
				)
			}
		}
		
		// tl;dr if online view & saved or is in preview
		if (onlineView && ((workLoaded.value && work.value.Work != null && Storage.SavedWorkIDs.contains(
				work.value.Work.Id
			)) || isPreview.value)
		) {
			Badge(containerColor = MaterialTheme.colorScheme.secondary) {
				Text(
					text = "In Library"
				)
			}
		}
	}
}