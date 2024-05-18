package com.practicalglitch.ao3reader

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.google.gson.reflect.TypeToken

class Settings {
	
	var ReaderFullscreen: Boolean = true
	var ReaderShowBatteryAndTime: Boolean = true
	var ReaderBackgroundColor: Color = Color.Black
	var ReaderTextColor: Color = Color.White
	var ReaderLineHeight: Float = 18f
	var ReaderFontSize: Float = 14f
	var ReaderTextAlignment: TextAlign = TextAlign.Left
	//var ReaderFont: Font
	companion object {
		var Instance: Settings = Settings()
		
		val thefuckingloremipsum: String = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
		
		fun SaveSettings(): Boolean {
			val json = LibraryIO.gson.toJson(Instance)
			Log.d("Settings", "Saving settings")
			return FileIO.SaveToFile("", "settings.json", json)
		}
		
		fun LoadSettings() {
			Log.d("Settings", "Loading Settings")
			val json = FileIO.ReadFromFile("settings.json")
			Instance = LibraryIO.gson.fromJson(json, object : TypeToken<Settings>() {}.type)
		}
	}
}