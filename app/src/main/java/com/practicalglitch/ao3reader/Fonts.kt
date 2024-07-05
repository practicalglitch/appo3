package com.practicalglitch.ao3reader

import android.graphics.Typeface
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.practicalglitch.ao3reader.FileIO.Companion.ls
import com.practicalglitch.ao3reader.FileIO.Companion.relativePath
import java.io.File

object Fonts {
	
	private var fontFamily: FontFamily? = null
	private var fontFamilyName: String = ""
	
	fun GetFont(): FontFamily {
		if(fontFamily == null || fontFamilyName != Storage.Settings.ReaderFontFamily){
			AssembleFont()
			return fontFamily!!
		} else {
			return fontFamily!!
		}
	}
	
	private fun AssembleFont() {
		val fontStr = Storage.Settings.ReaderFontFamily
		
		if (fontStr.matches("res:.+".toRegex())) {
			
			if (fontStr == "res:arbutus_slab_regular") {
				fontFamily = FontFamily(Font(R.font.arbutus_slab_regular, FontWeight.Normal))
				fontFamilyName = Storage.Settings.ReaderFontFamilyName
			}
			
			
		} else if (fontStr.matches("(sys|usr)file:.+".toRegex())) {
			
			val typeface = Typeface.createFromFile(
				File(
					fontStr
						.removePrefix("usrfile:")
						.removePrefix("sysfile:")
				)
					.absoluteFile
			)
			fontFamily = FontFamily(typeface)
			fontFamilyName = Storage.Settings.ReaderFontFamilyName
		}
	}
	
	private var availableFonts: Array<Pair<String, String>>? = null
	
	// First item: Name, Second item: Location
	fun GetAvailableFonts(): Array<Pair<String, String>> {
		val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()
		
		if(availableFonts != null)
			return availableFonts!!
		
		val pairs = mutableListOf<Pair<String, String>>()
		
		File(FileIO.fDir, "fonts").ls().forEach { ufont ->
			val name = ufont.relativePath().removePrefix("fonts/")
				.removeSuffix(".ttf")
				.removeSuffix(".otf")
				.replace(camelRegex) {" ${it.value}"}
			val loc = "usrfile:" + ufont.absolutePath
			pairs.add(Pair(name, loc))
		}
		
		pairs.add(Pair("Arbutus Slab Regular", "res:arbutus_slab_regular"))
		
		
		File("/system/fonts").listFiles()?.forEach { sfont ->
			val name = sfont.absolutePath.removePrefix("/system/fonts/")
				.removeSuffix(".ttf")
				.removeSuffix(".otf")
				.replace(camelRegex) {" ${it.value}"}
			val loc = "sysfile:" + sfont.absolutePath
			pairs.add(Pair(name, loc))
		}
		
		return pairs.toTypedArray()
	}
	
	fun ResetAvailableFonts(){
		availableFonts = null
	}
}