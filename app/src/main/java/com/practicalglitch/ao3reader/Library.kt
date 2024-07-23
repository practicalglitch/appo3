package com.practicalglitch.ao3reader

import androidx.activity.ComponentActivity
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson

// TODO: PHASE OUT THIS CLASS

class Library {
	
	companion object {
		fun ContainsWork(list: SnapshotStateList<SavedWork>, id: String): Boolean{
			for(work in list)
				if(work.Work.Id == id)
					return true
			return false
		}
		
		// History tab info
		//var history: MutableList<WorkChapter> = mutableListOf()
	}
	
	val works: MutableList<SavedWork> = mutableListOf()
	
	// Populates library with some dummy works.
	fun quickPopulate() {
		works.add(SavedWork.DummySavedWork())
		works.add(SavedWork.DummySavedWork())
		works.add(SavedWork.DummySavedWork())
		works.add(SavedWork.DummySavedWork())
	}
	
	fun From(snapshotStateList: SnapshotStateList<SavedWork>): Library {
		for(work in snapshotStateList)
			works.add(work)
		return this
	}
	
	fun getWorkFromID(id: String): SavedWork? {
		try{
			return works.first { work -> work.Work.Id == id }
		} catch (_: NoSuchElementException) {
			return null
		}
	}
	
}


private class SpecificClassExclusionStrategy(private val excludedThisClass: Class<*>) :
	ExclusionStrategy {
	override fun shouldSkipClass(clazz: Class<*>): Boolean {
		return excludedThisClass == clazz
	}
	
	override fun shouldSkipField(f: FieldAttributes): Boolean {
		return excludedThisClass == f.declaredClass
	}
}

private class SpecificFieldExclusionStrategy(private val fieldName: String) :
	ExclusionStrategy {
	override fun shouldSkipClass(clazz: Class<*>): Boolean {
		return false
	}
	
	override fun shouldSkipField(f: FieldAttributes): Boolean {
		return f.name == fieldName
	}
}

class LibraryIO : ComponentActivity() {
	
	companion object {
		val gson = Gson()
		

		

	}
}