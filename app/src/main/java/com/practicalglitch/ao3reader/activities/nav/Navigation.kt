package com.practicalglitch.ao3reader.activities.nav

import TagSearchActivity
import WebViewActivity
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.practicalglitch.ao3reader.SavedWork
import com.practicalglitch.ao3reader.activities.BookInfo
import com.practicalglitch.ao3reader.activities.ChapterActivity
import com.practicalglitch.ao3reader.activities.MainActivity

class NavigationData(){
	companion object {
		var BookInfo_work: SavedWork = SavedWork();
		var WebViewActivity_url: String = ""
	}
}

@Composable
fun Navigation() {
	
	val navController = rememberNavController()
	NavHost(navController = navController, startDestination = Screen.LibraryScreen.route){
		composable(route = Screen.LibraryScreen.route) {
			MainActivity(navController = navController)
		}
		composable(route = Screen.BookInfoScreen.route) {
			BookInfo(navController, NavigationData.BookInfo_work)
		}
		composable(route = Screen.ChapterActivity.route) {
			ChapterActivity(navController)
		}
		composable(route = Screen.WebViewActivity.route) {
			WebViewActivity(NavigationData.WebViewActivity_url)
		}
		composable(route = Screen.TagSearchActivity.route) {
			TagSearchActivity(navController)
		}
	}
}