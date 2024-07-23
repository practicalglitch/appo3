package com.practicalglitch.ao3reader.activities.composable.subcomposable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// This should probably be a text file?

@Composable
fun ShowChangelog(lastVer: Int, curVer: Int){
	Column {
		if (lastVer == -1) {
			
			Text(
				modifier = Modifier.padding(10.dp),
				text = "App Of Our Own, or AppO3, is an app for reading from Archive of Our Own.")
			Text(
				modifier = Modifier.padding(10.dp),
				text = "However, please note that AppO3 is in pre-alpha. Expect bugs, instability, missing features, the whole nine yards.")
			Text(
				modifier = Modifier.padding(10.dp),
				text = "You are still welcome to try the app, and to PLEASE report any bugs you find on the app Github!\n(You can find the link in Settings > About)")
			Text(
				modifier = Modifier.padding(10.dp),
				text = "Thank you for trying out AppO3.")
		}
		// tack on more for any app updates
	}
}