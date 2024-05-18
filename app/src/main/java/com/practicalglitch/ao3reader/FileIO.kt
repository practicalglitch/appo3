package com.practicalglitch.ao3reader

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.practicalglitch.ao3reader.activities.MainActivityData
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

class FileIO : AppCompatActivity() {
	companion object {
		val fDir = MainActivityData.FilesDir
		
		fun ReadFromFile(path : String, fileName: String): String? {
			return ReadFromFile("$path/$fileName")
		}
		
		fun ReadFromFile(fullPath: String): String?{
			return try {
				val file = File(fDir, fullPath)
				val reader = FileReader(file)
				val data = reader.readText()
				reader.close()
				return data
			} catch (e: IOException) {
				e.printStackTrace()
				null
			}
		}
		
		fun SaveToFile(path: String, fileName: String, contents : String): Boolean {
			return try {
				var fullPath = "$path/$fileName"
				if(path == "")
					fullPath = fileName
				
				val file = File(fDir, fullPath)
				
				Log.d("FileIO", "Saving to ${file.path}, checking dir ${file.parent}")
				
				val parentFolder = File(fDir, path)
				parentFolder.mkdirs()
				if(!Exists(file.path)!!)
					file.createNewFile()
				if(!Exists(file.parent!!)!!)
					file.mkdirs()
				val writer = FileWriter(file)
				writer.write(contents)
				writer.close()
				true
			} catch (e: IOException) {
				e.printStackTrace()
				false
			}
		}
		
		fun Exists(path: String): Boolean? {
			return try {
				File(fDir, path).exists()
			} catch (e: IOException) {
				e.printStackTrace()
				null
			}
		}
		
		fun DeleteFile(path: String): Boolean{
			return try {
				File(fDir, path).delete()
				true
			} catch (e: IOException) {
				e.printStackTrace()
				false
			}
		}
	}
}