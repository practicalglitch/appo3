package com.practicalglitch.ao3reader

import androidx.compose.ui.text.style.TextAlign

// Settings that will be stored in memory
class Settings {
	
	// Reader Settings
	var ReaderFullscreen: Boolean = true
	var ReaderShowBatteryAndTime: Boolean = true
	var ReaderBackgroundColor: Long = 0xFF000000 // Pure black
	var ReaderTextColor: Long = 0xFFFFFFFF // Pure white
	var ReaderLineHeight: Float = 18f
	var ReaderFontSize: Float = 14f
	var ReaderTextAlignment: TextAlign = TextAlign.Left
	
	var ReaderFontFamily: String = "res:arbutus_slab_regular"
	var ReaderFontFamilyName: String = "Arbutus Slab Regular"
	
	
	
	// General
	var GeneralStatsEnabled: Boolean = true
	var GeneralLastKnownVersion: Int = -1
	
	//var ReaderFont: Font
	companion object {
		val loremIpsum: String = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
	}
}

// Settings that only last for the session
class TemporarySettings {
	companion object {
		var DoNotAskRemoveDownload = false
		var DoNotAskRemoveFont = false
	}
}