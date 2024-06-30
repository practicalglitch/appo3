package com.practicalglitch.ao3reader

import android.util.Log
import com.google.gson.reflect.TypeToken
import com.practicalglitch.ao3reader.FileIO.Companion.ls
import com.practicalglitch.ao3reader.LibraryIO.Companion.gson
import com.practicalglitch.ao3reader.activities.MainActivityData
import org.apio3.Types.Work
import org.apio3.Types.WorkChapter
import java.io.File
import java.nio.file.Paths


class WorkTopMeta(work: Work) {
	var title: String = work.Title
	var auth: String = work.Author
	var fandom: Array<String> = work.Fandoms
	
	fun Fill(work: Work) {
		work.Title = title
		work.Author = auth
		work.Fandoms = fandom
	}
}

class WorkBotMeta(work: Work) {
	var authloc: String = work.AuthorLoc
	var rating: String = work.Rating
	var cat: Array<String> = work.Category
	var warn: String = work.Warning
	var finish = work.Finished
	var relation: Array<String> = work.Relationships
	var character: Array<String> = work.Characters
	var freeform: Array<String> = work.Freeforms
	var lang: String = work.Language
	var fandomloc: Array<String> = work.FandomsLoc
	var sum: String = work.Summary
	var pub: String = work.DatePublished
	var updte: String? = work.DateUpdated
	var chavail = work.ChaptersAvailable
	var chtot = work.ChaptersTotal
	var word = work.Words
	var comment = work.Comments
	var kudo = work.Kudos
	var bmark = work.Bookmarks
	var hit = work.Hits
	
	fun Fill(work: Work) {
		work.AuthorLoc = authloc
		work.Rating = rating
		work.Category = cat
		work.Warning = warn
		work.Finished = finish
		work.Relationships = relation
		work.Characters = character
		work.Freeforms = freeform
		work.Language = lang
		work.FandomsLoc = fandomloc
		work.Summary = sum
		work.DatePublished = pub
		work.DateUpdated = updte
		work.ChaptersAvailable = chavail
		work.ChaptersTotal = chtot
		work.Words = word
		work.Comments = comment
		work.Kudos = kudo
		work.Bookmarks = bmark
		work.Hits = hit
	}
}

class ChapterMeta(chapter: WorkChapter) {
	var cid: String = chapter.ChapterID
	var title: String = chapter.Title
	var dl = chapter.Downloaded
	var indx = chapter.ChapterIndex
	var dt = chapter.UploadDate
	
	fun Fill(chapter: WorkChapter) {
		chapter.ChapterID = cid
		chapter.Title = title
		chapter.Downloaded = dl
		chapter.ChapterIndex = indx
		chapter.UploadDate = dt
	}
}

class Storage {
	companion object {
		
		var CachedWorkIDs: MutableList<String> = mutableListOf()
		fun SaveCachedWorks(cached: Array<String>, saved: Array<String>): Boolean {
			Log.d("Data", "Saving cached works")
			val json = LibraryIO.gson.toJson(cached + saved)
			return FileIO.SaveToFile("", "cached_works.json", json)
		}
		
		fun LoadCachedWorks(): Array<String> {
			Log.d("Data", "Loading cached works")
			val json = FileIO.ReadFromFile("cached_works.json")
			return LibraryIO.gson.fromJson(json, object : TypeToken<Array<String>>() {}.type)
		}
		
		var SavedWorkIDs: MutableList<String> = mutableListOf()
		
		// Should be called anytime SavedWorkIDs is changed
		fun SaveSavedWorkIDs(): Boolean {
			Log.d("Data", "Saving saved works")
			val json = LibraryIO.gson.toJson(SavedWorkIDs)
			return FileIO.SaveToFile("", "saved_works.json", json)
		}
		
		// Should only be called at bootup
		fun LoadSavedWorkIDs() {
			Log.d("Data", "Loading saved works")
			FileIO.ifExists("saved_works.json") {
				val json = FileIO.ReadFromFile(it)
				val out: Array<String> =
					LibraryIO.gson.fromJson(json, object : TypeToken<Array<String>>() {}.type)
				SavedWorkIDs.removeIf { true }
				SavedWorkIDs.addAll(out)
			}
		}
		
		// Caches all loaded / downloaded saved works
		var CachedWorks: MutableList<SavedWork> = mutableListOf()
		
		// Should be called whenever the info of a saved work is modified
		fun SaveSavedWork(savedWork: SavedWork, overwriteReadStatus: Boolean) {
			Log.d("Data", "Saving a work as saved: ${savedWork.Work.Id}")
			
			// Update CachedWorks
			//CachedWorks.removeIf { it.Work.Id == savedWork.Work.Id }
			//CachedWorks.add(savedWork)
			
			FileIO.SaveToFile(
				"work_${savedWork.Work.Id}",
				"top_meta.json",
				LibraryIO.gson.toJson(WorkTopMeta(savedWork.Work))
			)
			FileIO.SaveToFile(
				"work_${savedWork.Work.Id}",
				"bot_meta.json",
				LibraryIO.gson.toJson(WorkBotMeta(savedWork.Work))
			)
			if(overwriteReadStatus) {
				FileIO.SaveToFile(
					"work_${savedWork.Work.Id}",
					"ch_read.json",
					LibraryIO.gson.toJson(savedWork.ReadStatus)
				)
			}
			
			val chapmeta: MutableList<ChapterMeta> = mutableListOf()
			savedWork.Work.Contents.forEach { chapter -> chapmeta.add(ChapterMeta(chapter)) }
			FileIO.SaveToFile(
				"work_${savedWork.Work.Id}",
				"ch_meta.json",
				LibraryIO.gson.toJson(chapmeta.toTypedArray())
			)
		}
		
		fun LoadSavedWork(id: String, getCached: Boolean): SavedWork {
			Log.d("Data", "Loading a work as saved: ${id}")
			
			// If it is cached, return it. Otherwise, continue
			if(getCached)
				CachedWorks.firstOrNull { it.Work.Id == id }?.let { return it }
			
			
			val work = SavedWork()
			work.Work = Work()
			work.Work.Id = id
			
			
			FileIO.ifExists("work_${id}/top_meta.json") { path ->
				val topjson = FileIO.ReadFromFile(path)
				val topMeta: WorkTopMeta =
					LibraryIO.gson.fromJson(topjson, object : TypeToken<WorkTopMeta>() {}.type)
				topMeta.Fill(work.Work)
			}
			
			FileIO.ifExists("work_${id}/bot_meta.json") { path ->
				val botjson = FileIO.ReadFromFile(path)
				val botMeta: WorkBotMeta =
					LibraryIO.gson.fromJson(botjson, object : TypeToken<WorkBotMeta>() {}.type)
				botMeta.Fill(work.Work)
			}
			
			FileIO.ifExists("work_${id}/ch_read.json") { path ->
				val chreadjson = FileIO.ReadFromFile(path)
				val chreadData: HashMap<String, Float> = LibraryIO.gson.fromJson(
					chreadjson,
					object : TypeToken<HashMap<String, Float>>() {}.type
				)
				work.ReadStatus = chreadData
			}
			
			FileIO.ifExists("work_${id}/ch_meta.json") { path ->
				val chmetajson = FileIO.ReadFromFile(path)
				val chmetaData: Array<ChapterMeta> = LibraryIO.gson.fromJson(
					chmetajson,
					object : TypeToken<Array<ChapterMeta>>() {}.type
				)
				
				val chapterOut = mutableListOf<WorkChapter>()
				
				chmetaData.forEach {
					val wc = WorkChapter()
					wc.WorkID = work.Work.Id
					it.Fill(wc)
					chapterOut.add(wc)
				}
				work.Work.Contents = chapterOut.toTypedArray()
			}
			CachedWorks.add(work)
			return work
		}
		
		fun RemoveSavedWork(wid: String){
			FileIO.ifExists("work_${wid}/bot_meta.json") { FileIO.DeleteFile(it) }
			FileIO.ifExists("work_${wid}/ch_meta.json") { FileIO.DeleteFile(it) }
			// TODO: FIND ALL SAVED CHAPTERS AND DELETE
		}
		
		fun RemoveWork(wid: String){
			FileIO.ifExists("work_${wid}") { FileIO.DeleteFile(it) }
		}
		
		fun SaveReadStatus(work: SavedWork){
			FileIO.SaveToFile(
				"work_${work.Work.Id}",
				"ch_read.json",
				LibraryIO.gson.toJson(work.ReadStatus)
			)
		}
		
		fun SaveReadStatus(id: String, readStatus: HashMap<String, Float>){
			FileIO.SaveToFile(
				"work_${id}",
				"ch_read.json",
				LibraryIO.gson.toJson(readStatus)
			)
		}
		
		fun LoadReadStatus(id: String): HashMap<String, Float>{
			val chreadjson = FileIO.ReadFromFile("work_${id}/ch_read.json")
			val chreadData: HashMap<String, Float> = LibraryIO.gson.fromJson(
				chreadjson,
				object : TypeToken<HashMap<String, Float>>() {}.type
			)
			return chreadData
		}
		
		fun SaveCachedWork(savedWork: SavedWork) {
			Log.d("Data", "Saving a work as cache: ${savedWork.Work.Id}")
			
			// Update CachedWorks
			//CachedWorks.removeIf { it.Work.Id == savedWork.Work.Id }
			//CachedWorks.add(savedWork)
			
			FileIO.SaveToFile(
				"work_${savedWork.Work.Id}",
				"top_meta.json",
				LibraryIO.gson.toJson(WorkTopMeta(savedWork.Work))
			)
			FileIO.SaveToFile(
				"work_${savedWork.Work.Id}",
				"ch_read.json",
				LibraryIO.gson.toJson(savedWork.ReadStatus)
			)
		}
		
		fun LoadCachedWork(id: String, getCached: Boolean): SavedWork {
			Log.d("Data", "Loading a work as cache: ${id}")
			
			// If it is cached, return it. Otherwise, continue
			if(getCached)
				CachedWorks.firstOrNull { it.Work.Id == id }?.let { return it }
			
			
			val work = SavedWork()
			work.Work.Id = id
			
			val topjson = FileIO.ReadFromFile("work_${id}/top_meta.json")
			val topMeta: WorkTopMeta =
				LibraryIO.gson.fromJson(topjson, object : TypeToken<WorkTopMeta>() {}.type)
			topMeta.Fill(work.Work)
			
			val chreadjson = FileIO.ReadFromFile("work_${id}/ch_read.json")
			val chreadData: HashMap<String, Float> = LibraryIO.gson.fromJson(
				chreadjson,
				object : TypeToken<HashMap<String, Float>>() {}.type
			)
			work.ReadStatus = chreadData
			
			work.CachedInfoOnly = true
			
			CachedWorks.add(work)
			return work
		}


		val History = mutableListOf<WorkChapter>()

		fun SaveHistory(): Boolean {
			val json = gson.toJson(History.toTypedArray())
			Log.d("Data", "Saving history")
			return FileIO.SaveToFile("", "history.json", json)
		}

		fun LoadHistory() {
			Log.d("Data", "Loading History")
			FileIO.ifExists("history.json") { file ->
				val json = FileIO.ReadFromFile(file)
				val out = gson.fromJson<Array<WorkChapter>>(
					json,
					object : TypeToken<Array<WorkChapter>>() {}.type
				)
				History.removeIf { true }
				History.addAll(out)
			}
		}

		val NewChapters = mutableListOf<WorkChapter>()
		fun SaveNewChapters(): Boolean {
			val json = gson.toJson(NewChapters.toTypedArray())
			Log.d("Data", "Saving new chapters")
			return FileIO.SaveToFile("", "new_chapters.json", json)
		}

		fun LoadNewChapters(){
			Log.d("Data", "Loading New Chapters")
			FileIO.ifExists("new_chapters.json") { file ->
				val json = FileIO.ReadFromFile(file)
				val out = gson.fromJson<Array<WorkChapter>>(
					json,
					object : TypeToken<Array<WorkChapter>>() {}.type
				)
				NewChapters.removeIf { true }
				NewChapters.addAll(out)
			}
		}

		var Stats = Statistics()
		fun SaveStatistics(){
			FileIO.SaveToFile(
				"",
				"stats.json",
				LibraryIO.gson.toJson(Stats)
			)
		}
		
		fun LoadStatistics(){
			FileIO.ifExists("stats.json") { path ->
				val statsRaw = FileIO.ReadFromFile(path)
				Stats = LibraryIO.gson.fromJson(
					statsRaw,
					object : TypeToken<Statistics>() {}.type
				)
			}
		}
		
		var Settings: Settings = Settings()
		
		fun SaveSettings(): Boolean {
			val json = LibraryIO.gson.toJson(Settings)
			Log.d("Settings", "Saving settings")
			return FileIO.SaveToFile("", "settings.json", json)
		}
		
		fun LoadSettings() {
			Log.d("Settings", "Loading Settings")
			FileIO.ifExists("settings.json") { path ->
				val json = FileIO.ReadFromFile(path)
				Settings = LibraryIO.gson.fromJson(json, object : TypeToken<Settings>() {}.type)
			}
		}
		
		
		fun ExportBackup(info: BackupInfo): File {
			val fDir = MainActivityData.FilesDir!!
			
			val files = mutableListOf<ZipItem>()

			if (info.readHistory)
			// Get all read chapters and add it to files
				FileIO.ls(fDir.path).forEach { file ->
					if (file.isDirectory && file.name.contains("work_")) {
						ls(file.path).firstOrNull { subfile -> subfile.name == "ch_read.json" }
							?.let { readstat ->
								files.add(ZipItem(readstat, file.name))
							}
					}
				}

			if(info.history)
				FileIO.ifExists("history.json") { item ->
					files.add(ZipItem(File(fDir.path, item), "")) }

			if(info.savedWorks)
				FileIO.ifExists("saved_works.json") { item ->
					files.add(ZipItem(File(fDir.path, item), "")) }

			if(info.newChapters)
				FileIO.ifExists("new_chapters.json") { item ->
					files.add(ZipItem(File(fDir.path, item), "")) }

			if(info.settings)
				FileIO.ifExists("settings.json") { item ->
					files.add(ZipItem(File(fDir.path, item), "")) }

			if(info.stats)
				FileIO.ifExists("stats.json") { item ->
					files.add(ZipItem(File(fDir.path, item), "")) }
			
			val outLoc = Paths.get(fDir.path, "backup.zip").toString()
			
			FileIO.zip(files.toTypedArray(), outLoc)
			
			return File(outLoc)
		}


		data class BackupInfo(
			var readHistory: Boolean,
			var searchHistory: Boolean,
			var history: Boolean,
			var savedWorks: Boolean,
			var newChapters: Boolean,
			var settings: Boolean,
			var stats: Boolean
		)

		
		fun ImportBackup(backup: File, info: BackupInfo){
			val fDir = MainActivityData.FilesDir!!
			val restoreLoc = File(fDir, "restore")
			FileIO.unzip(backup.path, restoreLoc)
			backup.delete()
			

			if(info.readHistory)
			// Merge all read data
				restoreLoc.ls().forEach { file ->
					if(file.isDirectory && file.name.contains("work_")){
						ls(file.path).firstOrNull { subfile -> subfile.name == "ch_read.json" }
							?.let { readstat ->
								if(FileIO.Exists(file.name)!!){
									// Merge with existing read status
									val wid = file.name.removePrefix("work_")
									val currentReadStatus = Storage.LoadReadStatus(wid)

									val importedReadStatus: HashMap<String, Float>
									= LibraryIO.gson.fromJson(file.readText(),
										object : TypeToken<HashMap<String, Float>>() {}.type)

									for(entry in importedReadStatus){
										// If already read, replace if imported read status > current read stat
										// Else, if not read, simply add it
										if(currentReadStatus.containsKey(entry.key)){
											if(entry.value > currentReadStatus[entry.key]!!)
												currentReadStatus[entry.key] = entry.value
										} else {
											currentReadStatus[entry.key] = entry.value
										}
									}
									Storage.SaveReadStatus(wid, currentReadStatus)
								} else {
									// If no existing read status, save to it
									FileIO.SaveToFile(
										file.name,
										"ch_read.json",
										FileIO.ReadFromFile(readstat.path)!!)
								}
							}
					}
				}

			if(info.searchHistory)
				FileIO.ifExists("restore/search_history.json") { file ->
					Storage.History.addAll(gson.fromJson(FileIO.ReadFromFile(file),
						object : TypeToken<Array<WorkChapter>>() {}.type))
					Storage.SaveHistory()
				}

			if(info.history)
				FileIO.ifExists("restore/history.json") { file ->
					Storage.History.addAll(gson.fromJson(FileIO.ReadFromFile(file),
						object : TypeToken<Array<WorkChapter>>() {}.type))
					Storage.SaveHistory()
				}

			if(info.savedWorks)
				FileIO.ifExists("restore/saved_works.json") { file ->
					val importedItems: Array<String>
							= LibraryIO.gson.fromJson(FileIO.ReadFromFile(file),
						object : TypeToken<HashMap<String, Float>>() {}.type)
					importedItems.forEach {  wid ->
						if(!Storage.SavedWorkIDs.contains(wid))
							Storage.SavedWorkIDs.add(wid)
					}
					Storage.SaveSavedWorkIDs()
				}

			if(info.newChapters)
				// Should not be performed if newchapters already contain chapters
				FileIO.ifExists("restore/new_chapters.json") { file ->
					Storage.NewChapters.addAll(gson.fromJson(FileIO.ReadFromFile(file),
						object : TypeToken<Array<WorkChapter>>() {}.type))
					Storage.SaveNewChapters()
				}

			if(info.settings)
				FileIO.ifExists("restore/settings.json") { file ->
					Storage.Settings = LibraryIO.gson.fromJson(FileIO.ReadFromFile(file),
						object : TypeToken<HashMap<String, Float>>() {}.type)
					Storage.SaveSettings()
				}

			if(info.stats)
				FileIO.ifExists("restore/stats.json") { file ->
					Storage.Stats = LibraryIO.gson.fromJson(FileIO.ReadFromFile(file),
						object : TypeToken<HashMap<String, Float>>() {}.type)
					Storage.SaveStatistics()
				}
		}
	}
}