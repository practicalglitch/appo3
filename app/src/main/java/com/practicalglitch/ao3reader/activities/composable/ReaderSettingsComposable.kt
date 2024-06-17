package com.practicalglitch.ao3reader.activities.composable

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.ireward.htmlcompose.HtmlText
import com.practicalglitch.ao3reader.Settings
import com.practicalglitch.ao3reader.Storage
import com.practicalglitch.ao3reader.ui.theme.ArbutusSlabFontFamily
import com.practicalglitch.ao3reader.ui.theme.RederTheme
import java.lang.NumberFormatException


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



class SettingsComposable {
	companion object {
		//val DisplayedFullscreen: MutableLiveData<Boolean> = MutableLiveData(Settings.Instance.ReaderFullscreen)
		//val DisplayedShowBatteryAndTime: MutableLiveData<Boolean> = MutableLiveData(Settings.Instance.ReaderShowBatteryAndTime)
		//val DisplayedBackgroundColor: MutableLiveData<String> = MutableLiveData(Integer.toHexString(Settings.Instance.ReaderBackgroundColor.toArgb()))
		//val DisplayedTextColor: MutableLiveData<String> = MutableLiveData(Integer.toHexString(Settings.Instance.ReaderTextColor.toArgb()))
		//val DisplayedLineHeight: MutableLiveData<Float> = MutableLiveData(Settings.Instance.ReaderLineHeight)
		//val DisplayedFontSize: MutableLiveData<Float> = MutableLiveData(Settings.Instance.ReaderFontSize)
	}
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderSettings() {
	val fullscreen = remember { mutableStateOf(Storage.Settings.ReaderFullscreen) }
	val showBatAndTime = remember { mutableStateOf(Storage.Settings.ReaderShowBatteryAndTime) }
	
	// "%x".format(number) -> number to hex string
	
	val dispBackgroundColor = remember { mutableStateOf(java.lang.Long.toHexString(Storage.Settings.ReaderBackgroundColor)) }
	val backgroundColor = remember { mutableLongStateOf(Storage.Settings.ReaderBackgroundColor) }
	val dispTextColor = remember { mutableStateOf(java.lang.Long.toHexString(Storage.Settings.ReaderTextColor)) }
	val textColor = remember { mutableLongStateOf(Storage.Settings.ReaderTextColor) }
	val lineHeight = remember { mutableFloatStateOf(Storage.Settings.ReaderLineHeight) }
	val fontSize = remember { mutableFloatStateOf(Storage.Settings.ReaderFontSize) }
	
	
	
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
					fontFamily = ArbutusSlabFontFamily
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
		
		
	}
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun ReaderSettingsPreview() {
	RederTheme { Surface(modifier = Modifier.fillMaxSize()) { ReaderSettings() } }
}
