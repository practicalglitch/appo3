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
		
		var allFandoms = mutableListOf<Fandom>()
		
		val savedWorkCache = mutableListOf<SavedWork>()
		val getWorkMetadataMutex = Mutex()
		
	}
	
	fun DownloadWorks(tag: String, page: Int, state: SnapshotStateList<SavedWork>, resetState: Boolean): LibraryIO {
		lifecycleScope.launch { DownloadWorksAsync(tag, page, state, resetState) }
		return this
	}
	
	suspend fun DownloadWorksAsync(tag: String, page: Int, state: SnapshotStateList<SavedWork>, resetState: Boolean) {
		withContext(Dispatchers.IO) {
			if(resetState)
				state.removeAll { true }
			val w = ApiO3.GetListOfRecentWorks(tag, page)
			for (work in w) {
				val sWork = SavedWork()
				sWork.Work = work
				state.add(sWork)
			}
		}
	}
	
	
	// TODO: PLEASE FUCKING FIX THIS I BEG YOU
	fun GetWork(workID: String, obj: MutableLiveData<SavedWork?>) {
		Log.d("ApiO3", "Getting work ${workID}...")
		lifecycleScope.launch { GetWorkAsync(workID, obj) }
	}
	
	fun GetWorkMetadata(workID: String, obj: MutableState<SavedWork?>, cacheHit: Boolean) {
		Log.d("ApiO3", "Getting work ${workID}...")
		
		lifecycleScope.launch {
			withContext(Dispatchers.IO) {
				// Mutex is used here to prevent everyone missing cache and getting
				// the same information at the same time...
				getWorkMetadataMutex.lock(this)
				if(cacheHit) {
					val hit = savedWorkCache.firstOrNull { it.Work.Id == workID }
					if(hit != null) {
						obj.value = hit
						Log.d("ApiO3", "${workID} cache hit.")
						getWorkMetadataMutex.unlock()
						return@withContext
					} else
						Log.d("ApiO3", "${workID} failed cache hit.")
				}
				
				val w = ApiO3.GetWorkMetadata(workID)
				val sWork = SavedWork()
				sWork.Work = w
				obj.value = sWork
				if(!savedWorkCache.contains(sWork))
					savedWorkCache.add(sWork)
				getWorkMetadataMutex.unlock()
			}
		}
	}
	
	
	suspend fun GetWorkAsync(workID: String, obj: MutableLiveData<SavedWork?>) {
		withContext(Dispatchers.IO) {
			val w = ApiO3.GetWorkMetadata(workID)
			val sWork = SavedWork()
			sWork.Work = w
			obj.postValue(sWork)
		}
	}
	
	suspend fun GetWorkAsyncd(workID: String, obj: MutableState<SavedWork?>) {
		withContext(Dispatchers.IO) {
			val w = ApiO3.GetWorkMetadata(workID)
			val sWork = SavedWork()
			sWork.Work = w
			obj.value = sWork
		}
	}
	
	fun DownloadAllFandoms(state: MutableList<Fandom>, flip: MutableLiveData<Boolean>, getCached: Boolean = true): LibraryIO {
		if(getCached && allFandoms.size != 0) {
			if(state != allFandoms) {
				state.removeAll {true}
				state.addAll(allFandoms)
			}
			flip.postValue(true)
		} else {
			lifecycleScope.launch { DownloadAllFandomsAsync(state, flip) }
		}
		return this
	}
	
	suspend fun DownloadAllFandomsAsync(state: MutableList<Fandom>, flip: MutableLiveData<Boolean>) {
		withContext(Dispatchers.IO) {
			val fandoms = ApiO3.GetAllFandoms()
			allFandoms.addAll(fandoms)
			Log.d("s","from online: ${fandoms.size}, ${allFandoms.size}")
			state.addAll(fandoms)
			flip.postValue(true)
		}
	}
	
	
	fun UpdateAllWorks(library: Library, out: SnapshotStateList<WorkChapter>, progress: MutableLiveData<Int>){
		
		val savedChapterMap: MutableMap<String, Array<WorkChapter>> = mutableMapOf()
		
		// pre-get all the chap metadata
		for(work in library.works)
			savedChapterMap.put(work.Work.Id, LibraryIO.LoadWorkMetadata(work.Work.Id))
		
		lifecycleScope.launch { UpdateAllWorksAsync(library, savedChapterMap, out, progress) }
	}
	
	suspend fun UpdateAllWorksAsync(library: Library, map: MutableMap<String, Array<WorkChapter>>, out: SnapshotStateList<WorkChapter>, progress: MutableLiveData<Int>) {
		withContext(Dispatchers.IO) {
			
			progress.postValue(0)
			
			for (work in library.works) {
				
				Log.d("Update", "Updating work ${work.Work.Id}, ${work.Work.Title}...")
				
				
				val onlineChapters = ApiO3.GetChapterMetadatas(work.Work.Id)
				
				
				val savedChapters = map[work.Work.Id]!!
				val savedChapterIDs: MutableList<String> = mutableListOf()
				for (savedChapter in savedChapters)
					savedChapterIDs.add(savedChapter.ChapterID)
				
				var wasUpdate = false
				
				for (onlineChapter in onlineChapters) {
					if (!savedChapterIDs.contains(onlineChapter.ChapterID)) {
						wasUpdate = true
						
						// If it does not exist in the NewChapter array yet
						if ((out.filter { ch -> ch.ChapterID == onlineChapter.ChapterID }).isEmpty()) {
							out.add(onlineChapter)
							Log.d(
								"Update",
								"Found update for ${work.Work.Id}, ${work.Work.Title}: ${onlineChapter.ChapterID}, ${onlineChapter.Title}"
							)
						} else {
							Log.d(
								"Update",
								"Skipped update for ${work.Work.Id}, ${work.Work.Title}: ${onlineChapter.ChapterID}, ${onlineChapter.Title}: Already exists"
							)
						}
						
						// If it does not exist in the Work, yet
						work.Work.Contents = LoadWorkMetadata(work.Work.Id)
						if ((work.Work.Contents.filter { ch -> ch.ChapterID == onlineChapter.ChapterID }).isEmpty()) {
							work.Work.Contents += onlineChapter
							SaveWorkMetadata(work)
						}
						
						
					}
				}
				if(wasUpdate){
					work.Work.Contents = onlineChapters
					
					SaveNewChapters(out.toTypedArray())
				}
				progress.postValue(progress.value!! + 1)
			}
		}
	}
}