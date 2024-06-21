package com.practicalglitch.ao3reader

import android.util.Log
import com.google.gson.reflect.TypeToken
import org.apio3.Types.Work
import org.apio3.Types.WorkChapter


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
	}
}