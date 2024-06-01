package com.practicalglitch.ao3reader.activities.composable

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.practicalglitch.ao3reader.Internet
import com.practicalglitch.ao3reader.LibraryIO
import com.practicalglitch.ao3reader.SavedWork
import com.practicalglitch.ao3reader.activities.nav.Navigator
import com.practicalglitch.ao3reader.activities.nav.Screen
import com.practicalglitch.ao3reader.ui.theme.RederTheme
import org.apio3.Types.WorkChapter


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun NewChapterCardPreview() {
	val w = SavedWork.DummySavedWork()
	val ch = w.Work.Contents[2]
	RederTheme {
		Surface {
			NewChapterCard(null, ch)
		}
	}
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewChapterCard(
	navController: NavController? = null,
	newChapter: WorkChapter) {
	val context = LocalContext.current
	val downloaded = remember { mutableStateOf(false) }
	val work = remember { mutableStateOf<SavedWork?>(null) }
	
	var isRead = false
	if(work.value?.ReadStatus?.get(newChapter.ChapterID) != null)
		if(work.value!!.ReadStatus[newChapter.ChapterID]!! >= 100f)
			isRead = true
	
	LaunchedEffect(!downloaded.value) {
		Internet().DownloadWorkMetadata(newChapter.WorkID, work, true)
		downloaded.value = true
	}
	
	OutlinedCard(
		onClick = {
			if(work.value != null) {
				Navigator.ToChapterActivity(navController!!, work.value!!, newChapter.ChapterID)
			}
		},
		shape = MaterialTheme.shapes.small,
		modifier = Modifier
			.width(LocalConfiguration.current.screenWidthDp.dp)
			.height(90.dp)
			.padding(3.dp)
	) {
			Column (modifier = Modifier
				.weight(1f)
				.padding(10.dp, 10.dp),
				verticalArrangement = Arrangement.SpaceBetween) {
				Row (modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween) {
					Text(text = if(work.value != null) work.value!!.Work.Title else "",
						color = if(isRead) Color.Gray else Color.White)
					Text(
						text = if(newChapter.UploadDate != null) newChapter.UploadDate.toString() else "",
						textAlign = TextAlign.End,
						color = if(isRead) Color.Gray else Color.White
					)
				}
				
				Text(
					text = if(work.value != null) work.value!!.FandomList(1) else "",
					color = if(isRead) Color.Gray else Color.White
				)
				Text(
					text = "Ch.${newChapter.ChapterIndex} - ${newChapter.Title}",
					color = if(isRead) Color.Gray else Color.White
				)
			}
			
	}
}