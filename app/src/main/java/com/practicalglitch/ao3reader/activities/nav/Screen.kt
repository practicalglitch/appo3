package com.practicalglitch.ao3reader.activities.nav

sealed class Screen(val route: String){
	object LibraryScreen : Screen("library_screen")
	
	object BookInfoScreen : Screen("book_info_screen")
	
	object WebViewActivity : Screen("activity_webview")
	object ChapterActivity : Screen("activity_chapter")
	
	object TagSearchActivity : Screen("tag_activity_search")
}