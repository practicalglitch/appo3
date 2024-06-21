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
	
	companion object {
		// Caches
		var allFandoms = mutableListOf<Fandom>()
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
	fun DownloadWorks(
		tag: String,
		page: Int,
		state: SnapshotStateList<SavedWork>,
		resetState: Boolean
	) {
		Log.d("Internet", "Downloading works of tag ${tag}:page ${page}")
		lifecycleScope.launch {
			withContext(Dispatchers.IO) {
				if (resetState)
					state.removeAll { true }
				val w = ApiO3.GetListOfRecentWorks(tag, page)
				for (work in w) {
					val sWork = SavedWork()
					sWork.Work = work
					sWork.CachedInfoOnly = true
					Storage.CachedWorks.add(sWork)
					state.add(sWork)
				}
			}
		}
	}
	
	/**
	 * Gets metadata of a work + chapter metadata. Does not return chapter contents.
	 *
	 * @param workID The ID of the work to retrieve metadata from.
	 * @param obj The MutableState to return the SavedWork to.
	 * @param cacheHit If true, accepts a cached version if exists.
	 * @param acceptCacheInfoOnly If true, accepts a cached, CacheInfoOnly version. If false, will skip over cached work to download online.
	 */
	fun DownloadWorkMetadata(
		workID: String,
		obj: MutableState<SavedWork>,
		flip: MutableState<Boolean>,
		cacheHit: Boolean,
		acceptCacheInfoOnly: Boolean,
		saveAfter: Boolean
	) {
		Log.d("ApiO3", "Getting work ${workID}...")
		
		lifecycleScope.launch {
			withContext(Dispatchers.IO) {
				// Mutex is used here to prevent everyone missing cache and getting
				// the same information at the same time...
				getWorkMetadataMutex.lock(this)
				if (cacheHit) {
					Storage.CachedWorks.firstOrNull { it.Work.Id == workID }?.let { hit ->
						if (!hit.CachedInfoOnly || acceptCacheInfoOnly) {
							obj.value = hit
							getWorkMetadataMutex.unlock()
							return@withContext
						}
					}
				}
				val w = ApiO3.GetWorkMetadata(workID)
				val sWork = SavedWork()
				sWork.Work = w
				
				val c = ApiO3.GetChapterMetadatas(workID)
				sWork.Work.Contents = c
				
				if(!rewriteReadHistory)
					sWork.ReadStatus = obj.value.ReadStatus
				
				obj.value = sWork
				// Update cache
				Storage.CachedWorks.removeIf { it.Work.Id == sWork.Work.Id }
				Storage.CachedWorks.add(sWork)
				getWorkMetadataMutex.unlock()
				
				// Save to saved works if true
				if(saveAfter)
					Storage.SaveSavedWork(sWork, rewriteReadHistory)
				
				flip.value = true
			}
		}
	}
	
	fun DownloadChapter(
		chapterID: String,
		chapter: MutableState<WorkChapter>,
		returnBool: MutableState<Boolean>? = null
	) {
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
	fun DownloadAllFandoms(
		state: MutableList<Fandom>,
		flip: MutableState<Boolean>,
		getCached: Boolean = true
	) {
		if (getCached && allFandoms.size != 0) {
			if (state != allFandoms) {
				state.removeAll { true }
				state.addAll(allFandoms)
			}
			flip.value = true
		} else {
			lifecycleScope.launch {
				withContext(Dispatchers.IO) {
					val fandoms = ApiO3.GetAllFandoms()
					allFandoms.addAll(fandoms)
					Log.d("s", "from online: ${fandoms.size}, ${allFandoms.size}")
					state.addAll(fandoms)
					flip.value = true
				}
			}
		}
	}
	
	fun UpdateSavedWorks(out: MutableList<WorkChapter>, progress: MutableState<Int>) {
		
		val works = Storage.SavedWorkIDs
		
		val SavedWorks = mutableListOf<SavedWork>()
		
		lifecycleScope.launch {
			withContext(Dispatchers.IO) {
				
				progress.value = 0
				
				// Get all chapter info already saved
				for (work in works)
					SavedWorks.add(Storage.LoadSavedWork(work, true))
				
				for (sWork in SavedWorks) {
					
					Log.d("Update", "Updating work ${sWork.Work.Id}, ${sWork.Work.Title}...")
					
					
					val onlineChapters = ApiO3.GetChapterMetadatas(sWork.Work.Id)
					
					val savedChapters = sWork.Work.Contents
					val savedChapterIDs: MutableList<String> = mutableListOf()
					for (savedChapter in savedChapters)
						savedChapterIDs.add(savedChapter.ChapterID)
					
					for (onlineChapter in onlineChapters) {
						if (!savedChapterIDs.contains(onlineChapter.ChapterID)) {
							// There was a change!
							
							// If it does not exist in the NewChapter array yet
							// Add it to it
							if ((out.filter { ch -> ch.ChapterID == onlineChapter.ChapterID }).isEmpty()) {
								out.add(onlineChapter)
								Log.d(
									"Update",
									"Found update for ${sWork.Work.Id}, ${sWork.Work.Title}: ${onlineChapter.ChapterID}, ${onlineChapter.Title}"
								)
							} else {
								Log.d(
									"Update",
									"Skipped update for ${sWork.Work.Id}, ${sWork.Work.Title}: ${onlineChapter.ChapterID}, ${onlineChapter.Title}: Already exists"
								)
							}
							
							// Update work and save
							sWork.Work.Contents = onlineChapters
							Storage.SaveSavedWork(sWork, false)
							
							LibraryIO.SaveNewChapters(out.toTypedArray())
						}
					}
					progress.value += 1
				}
			}
		}
	}
}