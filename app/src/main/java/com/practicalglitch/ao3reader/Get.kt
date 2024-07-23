package com.practicalglitch.ao3reader

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.gson.reflect.TypeToken
import org.apio3.Types.Fandom
import org.apio3.Types.WorkChapter

class Get {
	companion object {
		fun SavedWork(id: String, out: MutableState<SavedWork>, flip: MutableState<Boolean>, preferCacheOverInternet: Boolean) {
			// Return cached work if exists
			Storage.CachedWorks.firstOrNull { it.Work.Id == id }?.let {
				// If CIO + PCOI or !CIO, return
				// Otherwise, if CIO + !PCOI, skip
				if(!it.CachedInfoOnly || preferCacheOverInternet) {
					out.value = it
					Log.d("Get", "Getting work ${id} via cached. Is CachedInfoOnly: ${it.CachedInfoOnly}")
					flip.value = true
					return
				}
			}
			
			// Else, check storage
			Storage.SavedWorkIDs.firstOrNull { it == id }?.let {
				if(FileIO.Exists("work_${id}/top_meta.json")!!
					&& FileIO.Exists("work_${id}/bot_meta.json")!!){
					Storage.LoadSavedWork(id, true)
					Log.d("Get", "Getting work ${id} via saved work storage.")
					flip.value = true
					return
				}
			}
			
			Log.d("Get", "Getting work ${id} via internet.")
			Internet().DownloadWorkMetadata(id, out, flip, false, false, true, false)
		}
		
		fun Chapter(wid: String, cid: String, out: MutableState<WorkChapter>, loaded: MutableState<Boolean>? = null){
			
			val path = "work_${wid}/ch_${cid}.json"
			if(FileIO.Exists(path)!!){
				val json = FileIO.ReadFromFile(path)
				out.value = LibraryIO.gson.fromJson(json, object : TypeToken<WorkChapter>() {}.type)
				loaded?.value = true
			} else {
				Internet().DownloadChapter(cid, out, loaded)
			}
		}
		
		fun FandomsList(out: SnapshotStateList<Fandom>) {
			// Call for a refresh every 72hrs
			if(Storage.FandomsListTimestamp < (System.currentTimeMillis() / 1000) + 259200){
				out.addAll(Storage.FandomsList)
			} else {
				Internet().DownloadAllFandoms(out)
			}
			
		}
	}
}