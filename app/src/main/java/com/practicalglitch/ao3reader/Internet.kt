package com.practicalglitch.ao3reader

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import org.apio3.ApiO3
import org.apio3.Types.Fandom
import org.apio3.Types.WorkChapter

class Internet : ComponentActivity() {
	
	companion object{
		// Caches
		var allFandoms = mutableListOf<Fandom>()
		val savedWorkCache = mutableListOf<SavedWork>()
		val getWorkMetadataMutex = Mutex()
	}
	
	/**
	 * Downloads recent works from AO3.
	 *
	 * @param tag Tag to download from.
	 * @param page Indexed page. Each page contains 25 entries at most.
	 * @param state List to add works to.
	 * @param resetState If true, clears list before adding works. If false, appends to list.
	 */
	fun DownloadWorks(tag: String, page: Int, state: SnapshotStateList<SavedWork>, resetState: Boolean) {
		Log.d("Internet", "Downloading works of tag ${tag}:page ${page}")
		lifecycleScope.launch {
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
	}
	
	/**
	 * Gets metadata of a work. Returns chapter metadata, but not contents.
	 *
	 * @param workID The ID of the work to retrieve metadata from.
	 * @param obj The MutableState to return the SavedWork to.
	 * @param cacheHit If true, accepts a cached version if exists.
	 */
	fun DownloadWorkMetadata(workID: String, obj: MutableState<SavedWork?>, cacheHit: Boolean) {
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
	
	fun DownloadChapter(chapterID: String, chapter: MutableState<WorkChapter>, returnBool: MutableState<Boolean>? = null) {
		lifecycleScope.launch {
			withContext(Dispatchers.IO) {
				var dlchapter = ApiO3.DownloadSingleChapter(chapterID)
				dlchapter.Body = dlchapter.Body.replace("\n", "\n<br>")
				dlchapter.ChapterID = chapterID
				chapter.value = dlchapter
				returnBool?.value = true
			}
		}
	}
	
	
	
	/**
	 * Downloads all fandoms from AO3.
	 *
	 * @param state List to return fandoms to.
	 * @param flip Set to true when the state is completely populated.
	 * @param getCached If true, accepts a cached copy, if exists.
	 */
	fun DownloadAllFandoms(state: MutableList<Fandom>, flip: MutableState<Boolean>, getCached: Boolean = true) {
		if(getCached && allFandoms.size != 0) {
			if(state != allFandoms) {
				state.removeAll {true}
				state.addAll(allFandoms)
			}
			flip.value = true
		} else {
			lifecycleScope.launch {
				withContext(Dispatchers.IO) {
					val fandoms = ApiO3.GetAllFandoms()
					allFandoms.addAll(fandoms)
					Log.d("s","from online: ${fandoms.size}, ${allFandoms.size}")
					state.addAll(fandoms)
					flip.value = true
				}
			}
		}
	}
	
	fun UpdateAllWorks(library: Library, out: SnapshotStateList<WorkChapter>, progress: MutableState<Int>){
		
		val savedChapterMap: MutableMap<String, Array<WorkChapter>> = mutableMapOf()
		
		// pre-get all the chap metadata
		for(work in library.works)
			savedChapterMap.put(work.Work.Id, LibraryIO.LoadWorkMetadata(work.Work.Id))
		
		lifecycleScope.launch { withContext(Dispatchers.IO) {
			
			progress.value = 0
			
			for (work in library.works) {
				
				Log.d("Update", "Updating work ${work.Work.Id}, ${work.Work.Title}...")
				
				
				val onlineChapters = ApiO3.GetChapterMetadatas(work.Work.Id)
				
				
				val savedChapters = savedChapterMap[work.Work.Id]!!
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
						work.Work.Contents = LibraryIO.LoadWorkMetadata(work.Work.Id)
						if ((work.Work.Contents.filter { ch -> ch.ChapterID == onlineChapter.ChapterID }).isEmpty()) {
							work.Work.Contents += onlineChapter
							LibraryIO.SaveWorkMetadata(work)
						}
						
						
					}
				}
				if(wasUpdate){
					work.Work.Contents = onlineChapters
					
					LibraryIO.SaveNewChapters(out.toTypedArray())
				}
				progress.value += 1
			}
		} }
	}
}