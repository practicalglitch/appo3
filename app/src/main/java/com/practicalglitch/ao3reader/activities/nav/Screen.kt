package com.practicalglitch.ao3reader.activities.nav

sealed class Screen(val route: String){
	object LibraryActivity : Screen("activity_library")
	
	object BookInfoActivity : Screen("activity_bookinfo")
	
	object WebViewActivity : Screen("activity_webview")
	object ChapterActivity : Screen("activity_chapter")
	
	object TagSearchActivity : Screen("activity_tag_search")
	object SettingsActivity : Screen("activity_settings")
}