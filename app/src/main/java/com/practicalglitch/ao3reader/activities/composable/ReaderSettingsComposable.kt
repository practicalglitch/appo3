package com.practicalglitch.ao3reader.activities.composable

import PopupDialog
import android.annotation.SuppressLint
import android.content.res.Configuration
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ireward.htmlcompose.HtmlText
import com.practicalglitch.ao3reader.FileIO
import com.practicalglitch.ao3reader.FileIO.Companion.relativePath
import com.practicalglitch.ao3reader.Fonts
import com.practicalglitch.ao3reader.Settings
import com.practicalglitch.ao3reader.Storage
import com.practicalglitch.ao3reader.TemporarySettings
import com.practicalglitch.ao3reader.activities.composable.subcomposable.CheckboxSetting
import com.practicalglitch.ao3reader.ui.theme.RederTheme
import java.io.File


@Composable
fun SettingSwitch(text: String, checked: Boolean, onCheckedChange: ((Boolean) -> Unit)?) {
	Row (
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier
			.fillMaxWidth()
			.padding(25.dp, 10.dp)
	
	){
		Text(text = text)
		Switch(checked = checked, onCheckedChange = onCheckedChange)
	}
}

@Composable
fun SettingNumber(text: String, observer: Float?, changeAmt: Float, onChange: (Float) -> Unit ){
	Row (
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier
			.fillMaxWidth()
			.padding(25.dp, 10.dp)
	){
		Text(text = text)
		
		
		Row (
			verticalAlignment = Alignment.CenterVertically
		) {
			IconButton(onClick = { onChange.invoke(observer!! - changeAmt) }) {
				Icon(
					Icons.Filled.Remove,
					contentDescription = "Decrease",
				)
			}
			OutlinedTextField(
				value = observer.toString(),
				modifier = Modifier.width(80.dp),
				keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
				onValueChange = { str ->
					try {
						val num = str.toFloat()
						onChange.invoke(num)
					} catch (_: Error){
					
					}
					
				})
			IconButton(onClick = { onChange.invoke(observer!! + changeAmt) }) {
				Icon(
					Icons.Filled.Add,
					contentDescription = "Increase",
				)
			}
		}
	}
}


@SuppressLint("Range")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderSettings() {
	val fullscreen = remember { mutableStateOf(Storage.Settings.ReaderFullscreen) }
	val showBatAndTime = remember { mutableStateOf(Storage.Settings.ReaderShowBatteryAndTime) }
	
	val dispBackgroundColor = remember { mutableStateOf(java.lang.Long.toHexString(Storage.Settings.ReaderBackgroundColor)) }
	val backgroundColor = remember { mutableLongStateOf(Storage.Settings.ReaderBackgroundColor) }
	val dispTextColor = remember { mutableStateOf(java.lang.Long.toHexString(Storage.Settings.ReaderTextColor)) }
	val textColor = remember { mutableLongStateOf(Storage.Settings.ReaderTextColor) }
	val lineHeight = remember { mutableFloatStateOf(Storage.Settings.ReaderLineHeight) }
	val fontSize = remember { mutableFloatStateOf(Storage.Settings.ReaderFontSize) }
	
	val fontChoiceDropdownExpanded = remember { mutableStateOf(false) }
	val readerFontFamily = remember { mutableStateOf(Storage.Settings.ReaderFontFamily) }
	val readerFontFamilyName = remember { mutableStateOf(Storage.Settings.ReaderFontFamilyName) }
	
	val contentResolver = LocalContext.current.contentResolver
	val fontPicker = rememberLauncherForActivityResult(
		ActivityResultContracts.GetContent()) { uri ->
		if (uri != null) {
			
			val cursor = contentResolver.query(uri, null, null, null, null)
			
			val nameIndex = cursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
			cursor.moveToFirst()
			val name = cursor.getString(nameIndex)
			
			val fontsloc = File(FileIO.fDir, "fonts")
			fontsloc.mkdirs()
			val stream = contentResolver.openInputStream(uri)!!
			val data = stream.readBytes()
			val file = File(fontsloc, name)
			file.writeBytes(data)
			Fonts.ResetAvailableFonts()
		} else {
			Log.d("Debug", "No file selected.")
		}
	}
	
	val fontRemoveDialogue = remember { mutableStateOf(false) }
	val fontToRemove = remember { mutableStateOf(Pair("", "")) }
	val doNotAskRemoveFont = remember { mutableStateOf(TemporarySettings.DoNotAskRemoveFont) }
	when { fontRemoveDialogue.value ->
		PopupDialog(
			onDismissRequest = { fontRemoveDialogue.value = false },
			onConfirmation = {
				// If font is selected when deleted,
				// auto switch to arbutus slab
				if(fontToRemove.value.second == Storage.Settings.ReaderFontFamily){
					readerFontFamilyName.value = "Arbutus Slab Regular"
					Storage.Settings.ReaderFontFamilyName = "Arbutus Slab Regular"
					readerFontFamily.value = "res:arbutus_slab_regular"
					Storage.Settings.ReaderFontFamily = "res:arbutus_slab_regular"
				}
				FileIO.DeleteFile(File(fontToRemove.value.second.removePrefix("usrfile:")).relativePath())
				Fonts.ResetAvailableFonts()
			},
			title = "Remove font ${fontToRemove.value.first}?",
			content = {
				Column {
					Text(text = "This action cannot be undone.")
					CheckboxSetting(text = "Do Not Ask Again", checked = doNotAskRemoveFont.value) {
						doNotAskRemoveFont.value = !doNotAskRemoveFont.value
						TemporarySettings.DoNotAskRemoveFont = doNotAskRemoveFont.value
					}
				}
			}
		)
	}
	
	Column(modifier = Modifier.fillMaxWidth()) {
		SettingSwitch(text = "Fullscreen", checked = fullscreen.value, onCheckedChange = {
			Storage.Settings.ReaderFullscreen = !fullscreen.value
			fullscreen.value = !fullscreen.value
			Storage.SaveSettings()
		})
		SettingSwitch(text = "Show Battery and Time", checked = showBatAndTime.value, onCheckedChange = {
			Storage.Settings.ReaderShowBatteryAndTime = !showBatAndTime.value
			showBatAndTime.value = !showBatAndTime.value
			Storage.SaveSettings()
		})
		HorizontalDivider()
		
		// Text preview
		Surface(
			modifier = Modifier
				.fillMaxWidth()
				.height(150.dp)
				.padding(10.dp)
				.clip(RoundedCornerShape(5.dp)),
			color = Color(backgroundColor.longValue)
		) {
			HtmlText(
				text = Settings.loremIpsum,
				style = TextStyle(
					color = Color(textColor.longValue),
					lineHeight = lineHeight.floatValue.sp,
					fontSize = fontSize.floatValue.sp,
					fontFamily = Fonts.GetFont()
				),
				modifier = Modifier.padding(10.dp)
			)
		}
		
		// Background color picker
		Row (
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier
				.fillMaxWidth()
				.padding(25.dp, 10.dp)
		){
			Text(text = "Background Color")
			
			OutlinedTextField(
				value = dispBackgroundColor.value,
				modifier = Modifier.width(150.dp),
				onValueChange = { str ->
					dispBackgroundColor.value = str
					try {
						val newColor = str.toLong(radix = 16)
						Storage.Settings.ReaderBackgroundColor = newColor
						backgroundColor.longValue = newColor
						Storage.SaveSettings()
					} catch (_: NumberFormatException){ }
				})
		}
		// Text color picker
		Row (
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier
				.fillMaxWidth()
				.padding(25.dp, 10.dp)
		){
			Text(text = "Font Color")
			
			OutlinedTextField(
				value = dispTextColor.value,
				modifier = Modifier.width(150.dp),
				onValueChange = { str ->
					dispTextColor.value = str
					try {
						val newColor = str.toLong(radix = 16)
						Storage.Settings.ReaderTextColor = newColor
						textColor.longValue = newColor
						Storage.SaveSettings()
					} catch (_: NumberFormatException){ }
				})
		}
		
		SettingNumber(text = "Font Size", observer = fontSize.floatValue, changeAmt = 1f, onChange = {
			num ->
			fontSize.floatValue = num
			Storage.Settings.ReaderFontSize = num
			Storage.SaveSettings()
		})
		
		SettingNumber(text = "Line Height", observer = lineHeight.floatValue, changeAmt = 0.5f, onChange = {
				num ->
			lineHeight.floatValue = num
			Storage.Settings.ReaderLineHeight = num
			Storage.SaveSettings()
		})
		
		Row (
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier
				.fillMaxWidth()
				.padding(25.dp, 10.dp)
		
		){
			Text(text = "Font")
			
			Row (verticalAlignment = Alignment.CenterVertically){
				ExposedDropdownMenuBox(
					expanded = fontChoiceDropdownExpanded.value,
					onExpandedChange = { fontChoiceDropdownExpanded.value = it },
					modifier = Modifier.width(200.dp)
				) {
					TextField(
						value = readerFontFamilyName.value,
						onValueChange = {},
						readOnly = true,
						trailingIcon = {
							ExposedDropdownMenuDefaults.TrailingIcon(expanded = fontChoiceDropdownExpanded.value)
						},
						colors = ExposedDropdownMenuDefaults.textFieldColors(),
						modifier = Modifier.menuAnchor(),
						singleLine = true
					)
					
					ExposedDropdownMenu(
						expanded = fontChoiceDropdownExpanded.value,
						onDismissRequest = { fontChoiceDropdownExpanded.value = false }) {
						for (font in Fonts.GetAvailableFonts())
							DropdownMenuItem(text = {
								Row(
									modifier = Modifier
										.height(25.dp)
										.fillMaxWidth(),
									verticalAlignment = Alignment.CenterVertically,
									horizontalArrangement = Arrangement.SpaceBetween) {
									Text(font.first)
									if(font.second.startsWith("usrfile:"))
										IconButton(
											modifier = Modifier.size(25.dp),
											onClick = {
												if(doNotAskRemoveFont.value){
													// If font is selected when deleted,
													// auto switch to arbutus slab
													if(fontToRemove.value.second == Storage.Settings.ReaderFontFamily){
														readerFontFamilyName.value = "Arbutus Slab Regular"
														Storage.Settings.ReaderFontFamilyName = "Arbutus Slab Regular"
														readerFontFamily.value = "res:arbutus_slab_regular"
														Storage.Settings.ReaderFontFamily = "res:arbutus_slab_regular"
													}
													FileIO.DeleteFile(File(fontToRemove.value.second.removePrefix("usrfile:")).relativePath())
													Fonts.ResetAvailableFonts()
												} else {
													fontToRemove.value = font
													fontRemoveDialogue.value = true
												}
											}) {
											Icon(
												Icons.Filled.Remove,
												contentDescription = "Remove Font",
											)
										}
								}
							}, onClick = {
								readerFontFamilyName.value = font.first
								Storage.Settings.ReaderFontFamilyName = font.first
								readerFontFamily.value = font.second
								Storage.Settings.ReaderFontFamily = font.second
								Storage.SaveSettings()
							})
					}
				}
				IconButton(onClick = { fontPicker.launch("font/*") }) {
					Icon(
						Icons.Filled.AddCircleOutline,
						contentDescription = "Increase",
					)
				}
			}
		}
	}
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun ReaderSettingsPreview() {
	RederTheme { Surface(modifier = Modifier.fillMaxSize()) { ReaderSettings() } }
}
