
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.practicalglitch.ao3reader.SavedWork
import com.practicalglitch.ao3reader.activities.BookInfoActivity
import com.practicalglitch.ao3reader.activities.nav.NavigationData
import com.practicalglitch.ao3reader.activities.nav.Screen
import com.practicalglitch.ao3reader.ui.theme.RederTheme

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun LibraryCardPreview() {
	val w = SavedWork.DummySavedWork()
	RederTheme {
		Surface {
			LibraryWorkCard(null, w)
		}
	}
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryWorkCard(
	navController: NavController? = null,
	work: SavedWork
) {
	val context = LocalContext.current
	
	OutlinedCard(
		onClick = {
			NavigationData.BookInfo_work = work
			BookInfoActivity().GetChapters(context, work)
			navController!!.navigate(Screen.BookInfoActivity.route)
		},
		shape = MaterialTheme.shapes.small,
		modifier = Modifier
			.width(LocalConfiguration.current.screenWidthDp.dp)
			.height(90.dp)
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
					text = work.Work.Title,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis
				)
				Badge(containerColor = MaterialTheme.colorScheme.secondary) {
					Text(text = work.UnreadChapters().toString())
				}
			}
			
			Text(
				text = work.FandomList(1)
			)
			var dispTot = "?"
			if (work.Work.ChaptersTotal != -1)
				dispTot = work.Work.ChaptersTotal.toString()
			
			Text(
				text = work.Work.ChaptersAvailable.toString() + "/" + dispTot
			)
		}
		
	}
}