package com.practicalglitch.ao3reader

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.lifecycleScope
import com.practicalglitch.ao3reader.activities.BookInfoActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

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
				Storage.LoadSavedWork(id, true)
				Log.d("Get", "Getting work ${id} via saved work storage.")
				flip.value = true
				return
			}
			
			if(preferCacheOverInternet){
				Storage.CachedWorkIDs.firstOrNull { it == id }?.let {
					Storage.LoadCachedWork(id, true)
					Log.d("Get", "Getting work ${id} via cached work storage.")
					flip.value = true
					return
				}
			}
			
			Log.d("Get", "Getting work ${id} via internet.")
			Internet().DownloadWorkMetadata(id, out, flip, false, false, false)
		}
	}
}