package com.practicalglitch.ao3reader.activities.nav

import SettingsActivity
import TagSearchActivity
import WebViewActivity
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.practicalglitch.ao3reader.SavedWork
import com.practicalglitch.ao3reader.activities.BookInfoActivity
import com.practicalglitch.ao3reader.activities.ChapterActivity
import com.practicalglitch.ao3reader.activities.MainActivity

class NavigationData {
	companion object {
		var BookInfo_workId: String = ""
		
		var WebViewActivity_url: String = ""
		
		var ChapterActivity_savedWork: SavedWork = SavedWork()
		var ChapterActivity_chapterId: String = ""

		var TagSearchActivity_tag: String = ""
		var TagSearchActivity_name: String = ""
	}
}

class Navigator {
	companion object{
		fun ToBookInfoActivity(navController: NavController, workId: String){
			NavigationData.BookInfo_workId = workId
			navController.navigate(Screen.BookInfoActivity.route)
		}
		fun ToChapterActivity(navController: NavController, savedWork: SavedWork, chapterId: String){
			NavigationData.ChapterActivity_savedWork = savedWork
			NavigationData.ChapterActivity_chapterId = chapterId
			navController.navigate(Screen.ChapterActivity.route)
		}
		
		fun ToWebViewActivity(navController: NavController, url: String){
			NavigationData.WebViewActivity_url = url
			navController.navigate(Screen.WebViewActivity.route)
		}
		
		fun ToTagSearchActivity(navController: NavController, tag: String, name: String){
			NavigationData.TagSearchActivity_tag = tag
			NavigationData.TagSearchActivity_name = name
			navController.navigate(Screen.TagSearchActivity.route)
		}
		
		fun ToSettingsActivity(navController: NavController){
			navController.navigate(Screen.SettingsActivity.route)
		}
	}
}

@Composable
fun Navigation() {
	val navController = rememberNavController()
	NavHost(navController = navController, startDestination = Screen.LibraryActivity.route){
		composable(route = Screen.LibraryActivity.route) {
			MainActivity(navController = navController)
		}
		composable(route = Screen.BookInfoActivity.route) {
			BookInfoActivity(navController,
				NavigationData.BookInfo_workId,
			)
		}
		composable(route = Screen.ChapterActivity.route) {
			ChapterActivity(navController,
				NavigationData.ChapterActivity_savedWork,
				NavigationData.ChapterActivity_chapterId)
		}
		composable(route = Screen.WebViewActivity.route) {
			WebViewActivity(NavigationData.WebViewActivity_url)
		}
		composable(route = Screen.TagSearchActivity.route) {
			TagSearchActivity(
				navController,
				NavigationData.TagSearchActivity_tag,
				NavigationData.TagSearchActivity_name)
		}
		composable(route = Screen.SettingsActivity.route) {
			SettingsActivity(navController)
		}
	}
}