package com.practicalglitch.ao3reader

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import org.apio3.ApiO3
import org.apio3.Types.Fandom
import org.apio3.Types.WorkChapter


class Library {
	
	companion object {
		fun ContainsWork(list: SnapshotStateList<SavedWork>, id: String): Boolean{
			for(work in list)
				if(work.Work.Id == id)
					return true
			return false
		}
		
		// History tab info
		var history: MutableList<WorkChapter> = mutableListOf()
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
		
		val SavedWorksFileName = "saved_works.json"
		val NewChaptersFileName = "new_chapters.json"
		val HistoryFileName = "history.json"
		
		fun WorkChapterDataPath(id: String) : String {
			return "work_${id}"
		}
		
		fun WorkChapterMetadataFileName() : String {
			return "metadata.json"
		}
		
		fun WorkChapterReadStatusFileName() : String {
			return "read_status.json"
		}
		
		fun WorkChapterPath(wId: String) : String {
			return "work_${wId}"
		}
		
		fun WorkChapterFileName(cId: String) : String {
			return "chapter_${cId}.json"
		}
		
		fun SaveSavedWorks(library: Library): Boolean{
			
			val removeWorkChapters: ExclusionStrategy = SpecificClassExclusionStrategy(WorkChapter::class.java)
			val removeReadStatus: ExclusionStrategy = SpecificFieldExclusionStrategy("ReadStatus")
			val gson = GsonBuilder()
				.addSerializationExclusionStrategy(removeWorkChapters)
				.addSerializationExclusionStrategy(removeReadStatus)
				.create()
			val json = gson.toJson(library)
			
			Log.d("LibIO", "Saving my library")
			
			return FileIO.SaveToFile("", SavedWorksFileName, json)
		}
		
		fun SaveNewChapters(chapters: Array<WorkChapter>): Boolean {
			val json = gson.toJson(chapters)
			Log.d("LibIO", "Saving new chapters")
			return FileIO.SaveToFile("", NewChaptersFileName, json)
		}
		
		fun LoadNewChapters(): Array<WorkChapter> {
			Log.d("LibIO", "Loading New Chapters")
			val json = FileIO.ReadFromFile(NewChaptersFileName)
			return gson.fromJson(json, object : TypeToken<Array<WorkChapter>>() {}.type)
		}
		
		fun SaveHistory(chapters: Array<WorkChapter>): Boolean {
			val json = gson.toJson(chapters)
			Log.d("LibIO", "Saving history")
			return FileIO.SaveToFile("", HistoryFileName, json)
		}
		
		fun LoadHistory(): Array<WorkChapter> {
			Log.d("LibIO", "Loading History")
			val json = FileIO.ReadFromFile(HistoryFileName)
			return gson.fromJson(json, object : TypeToken<Array<WorkChapter>>() {}.type)
		}
		
		fun SaveWorkMetadata(savedWork: SavedWork): Boolean{
			val chapterMetadatas: MutableList<WorkChapter> = mutableListOf()
			for(content in savedWork.Work.Contents) {
				val chMD = WorkChapter()
				chMD.Downloaded = content.Downloaded
				chMD.ChapterID = content.ChapterID
				chMD.WorkID = content.WorkID
				chMD.ChapterIndex = content.ChapterIndex
				chMD.Title = content.Title
				chMD.UploadDate = content.UploadDate
				chapterMetadatas.add(chMD)
			}
			
			Log.d("LibIO", "Saving work chapter metadata")
			val json = Gson().toJson(chapterMetadatas)
			
			return FileIO.SaveToFile(
				WorkChapterDataPath(savedWork.Work.Id),
				WorkChapterMetadataFileName(),
				json)
		}
		
		fun SaveWorkReadStatus(savedWork: SavedWork): Boolean {
			Log.d("LibIO", "Saving Read Data")
			val jsonRS = Gson().toJson(savedWork.ReadStatus)
			return FileIO.SaveToFile(
				WorkChapterDataPath(savedWork.Work.Id),
				WorkChapterReadStatusFileName(),
				jsonRS)
		}
		
		fun LoadWorkReadStatus(workID: String): HashMap<String, Float> {
			Log.d("LibIO", "Loading Read Data")
			val json = FileIO.ReadFromFile(
				WorkChapterDataPath(workID),
				WorkChapterReadStatusFileName())
			return gson.fromJson(json, object : TypeToken<HashMap<String, Float>>() {}.type)
		}
		
		
		fun SaveWorkChapter(workChapter: WorkChapter): Boolean {
			Log.d("LibIO", "Saving work chapter")
			val json = Gson().toJson(workChapter)
			return FileIO.SaveToFile(
				WorkChapterPath(workChapter.WorkID),
				WorkChapterFileName(workChapter.ChapterID),
				json)
		}
		
		fun LoadSavedWorks(): Library {
			Log.d("LibIO", "Loading my library")
			val json = FileIO.ReadFromFile(SavedWorksFileName)
			return gson.fromJson(json, object : TypeToken<Library>() {}.type)
		}
		
		fun LoadWorkMetadata(workID: String): Array<WorkChapter> {
			Log.d("LibIO", "Loading work chapter metadata")
			val json = FileIO.ReadFromFile(
				WorkChapterDataPath(workID),
				WorkChapterMetadataFileName()
			)
			return gson.fromJson(json, object : TypeToken<Array<WorkChapter>>() {}.type)
		}
		
		fun LoadWorkChapter(workID: String, chapterID: String): WorkChapter {
			Log.d("LibIO", "Loading work chapter")
			val json = FileIO.ReadFromFile(
				WorkChapterPath(workID),
				WorkChapterFileName(chapterID)
			)
			return gson.fromJson(json, object : TypeToken<WorkChapter>() {}.type)
		}
	}
}