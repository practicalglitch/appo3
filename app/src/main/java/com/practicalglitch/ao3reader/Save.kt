package com.practicalglitch.ao3reader

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Save {
	companion object {
		fun SaveSearchHistory(list: Array<String>): Boolean {
			Log.d("Save", "Saving search history")
			val json = Gson().toJson(list)
			return FileIO.SaveToFile(
				"",
				"search_history.json",
				json
			)
		}
		
		fun LoadSearchHistory(): Array<String> {
			Log.d("Save", "Loading search history")
			if(!FileIO.Exists("search_history.json")!!)
				return arrayOf<String>()
			val json = FileIO.ReadFromFile("search_history.json")
			return LibraryIO.gson.fromJson(json, object : TypeToken<Array<String>>() {}.type)
		}
	}
}