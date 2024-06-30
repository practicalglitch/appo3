package com.practicalglitch.ao3reader.activities.composable

import TagSearchActivity
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.practicalglitch.ao3reader.activities.nav.Navigator
import com.practicalglitch.ao3reader.activities.nav.Screen
import com.practicalglitch.ao3reader.ui.theme.RederTheme
import org.apio3.Types.Fandom
import org.apio3.Types.WorkChapter


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun FandomCardPreview() {
	val f = Fandom();
	f.Name = "Awesome Man Universe"
	f.Category = "Category of Awesome"
	f.WorksCount = 1;
	RederTheme {
		Surface {
			FandomCard(null, f)
		}
	}
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FandomCard(
	navController: NavController? = null,
	fandom: Fandom) {
	val context = LocalContext.current
	
	OutlinedCard(
		onClick = {
			// TODO: Change this to navigator!
			TagSearchActivity.TagText = fandom.Name
			TagSearchActivity.TagUrl.postValue(fandom.Url)
			TagSearchActivity.UpdateData.postValue(true)
			Navigator.ToTagSearchActivity(navController!!)
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
				Text(text = fandom.Name,
					color = Color.White)
				Text(
					text = fandom.WorksCount.toString() + " Works",
					textAlign = TextAlign.End,
					color = Color.White
				)
			}
			
			Text(
				text = fandom.Category,
				color = Color.White
			)
		}
		
	}
}