package com.practicalglitch.ao3reader

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.practicalglitch.ao3reader.activities.MainActivityData
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Paths
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class ZipItem(file: File, path: String) {
	val file = file
	val path = path
}

class FileIO : AppCompatActivity() {
	companion object {
		val fDir = MainActivityData.FilesDir
		
		fun File.ls(): Array<File>{
			return ls(this.path)
		}

		fun File.relativePath(): String {
			// No clue why. But passing in an absolute path
			// e.g /data/user/0/com.author.package/files/myfile.txt
			// will break but
			// myfile.txt
			// wont
			// so this just strips the absolute and makes it relative.
			return this.relativeTo(fDir!!).path
		}
		
		fun ls(directory: String): Array<File> {
			try {
				val loc = File(directory)
				Log.d("debug", "file loc: ${loc.path}")
				val files = loc.listFiles()
				return files!!
			} catch (e: IOException) {
				e.printStackTrace()
				return arrayOf()
			}
		}
		
		fun ReadFromFile(fullPath: String): String? {
			try {
				Log.d("debugp", fullPath)
				val file = File(fDir, fullPath)
				val reader = FileReader(file)
				val data = reader.readText()
				reader.close()
				return data
			} catch (e: IOException) {
				e.printStackTrace()
				return null
			}
		}
		
		fun SaveToFile(path: String, fileName: String, contents: String): Boolean {
			return try {
				var fullPath = "$path/$fileName"
				if (path == "")
					fullPath = fileName
				
				val file = File(fDir, fullPath)
				
				Log.d("FileIO", "Saving to ${file.path}, checking dir ${file.parent}")
				
				val parentFolder = File(fDir, path)
				parentFolder.mkdirs()
				if (!Exists(file.path)!!)
					file.createNewFile()
				if (!Exists(file.parent!!)!!)
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
		
		fun ifExists(path: String, action: (String) -> Unit) {
			if (Exists(path)!!)
				action.invoke(path)
		}
		
		fun DeleteFile(path: String): Boolean {
			return try {
				File(fDir, path).delete()
				true
			} catch (e: IOException) {
				e.printStackTrace()
				false
			}
		}
		
		
		fun zip(items: Array<ZipItem>, zipPath: String?) {
			val fos = FileOutputStream(zipPath)
			val zipOut = ZipOutputStream(fos)
			
			for (item in items) {
				Log.d("debug", "item: ${item.path}, ${item.file.path}")
				val fis = FileInputStream(item.file)
				val zipEntry = ZipEntry(Paths.get(item.path, item.file.name).toString())
				zipOut.putNextEntry(zipEntry)
				
				val bytes = ByteArray(1024)
				var length: Int
				while ((fis.read(bytes).also { length = it }) >= 0) {
					zipOut.write(bytes, 0, length)
				}
				fis.close()
			}
			zipOut.close()
			fos.close()
		}
		
		fun unzip(zipPath: String, destPath: File) {
			
			val buffer = ByteArray(1024)
			val zis = ZipInputStream(FileInputStream(zipPath))
			var zipEntry = zis.nextEntry
			while (zipEntry != null) {
				while (zipEntry != null) {
					val newFile: File = unzipFile(destPath, zipEntry)
					if (zipEntry.isDirectory) {
						if (!newFile.isDirectory && !newFile.mkdirs()) {
							throw IOException("Failed to create directory $newFile")
						}
					} else {
						// fix for Windows-created archives
						val parent = newFile.parentFile
						if (!parent.isDirectory && !parent.mkdirs()) {
							throw IOException("Failed to create directory $parent")
						}
						
						// write file content
						val fos = FileOutputStream(newFile)
						var len: Int
						while ((zis.read(buffer).also { len = it }) > 0) {
							fos.write(buffer, 0, len)
						}
						fos.close()
					}
					zipEntry = zis.nextEntry
				}
			}
			
			zis.closeEntry()
			zis.close()
		}
		
		
		private fun unzipFile(destinationDir: File, zipEntry: ZipEntry): File {
			val destFile = File(destinationDir, zipEntry.name)
			
			val destDirPath = destinationDir.canonicalPath
			val destFilePath = destFile.canonicalPath
			
			if (!destFilePath.startsWith(destDirPath + File.separator)) {
				throw IOException("Entry is outside of the target dir: " + zipEntry.name)
			}
			
			return destFile
		}
		
	}
}