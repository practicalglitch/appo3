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
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.ireward.htmlcompose.HtmlText
import com.practicalglitch.ao3reader.Settings
import com.practicalglitch.ao3reader.ui.theme.ArbutusSlabFontFamily
import com.practicalglitch.ao3reader.ui.theme.RederTheme


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
		val DisplayedFullscreen: MutableLiveData<Boolean> = MutableLiveData(Settings.Instance.ReaderFullscreen)
		val DisplayedShowBatteryAndTime: MutableLiveData<Boolean> = MutableLiveData(Settings.Instance.ReaderShowBatteryAndTime)
		val DisplayedBackgroundColor: MutableLiveData<String> = MutableLiveData(Integer.toHexString(Settings.Instance.ReaderBackgroundColor.toArgb()))
		val DisplayedTextColor: MutableLiveData<String> = MutableLiveData(Integer.toHexString(Settings.Instance.ReaderTextColor.toArgb()))
		val DisplayedLineHeight: MutableLiveData<Float> = MutableLiveData(Settings.Instance.ReaderLineHeight)
		val DisplayedFontSize: MutableLiveData<Float> = MutableLiveData(Settings.Instance.ReaderFontSize)
	}
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderSettings() {
	val fullscreen by SettingsComposable.DisplayedFullscreen.observeAsState()
	val showBatAndTime by SettingsComposable.DisplayedShowBatteryAndTime.observeAsState()
	
	val backgroundColor by SettingsComposable.DisplayedBackgroundColor.observeAsState()
	val textColor by SettingsComposable.DisplayedTextColor.observeAsState()
	val lineHeight by SettingsComposable.DisplayedLineHeight.observeAsState()
	val fontSize by SettingsComposable.DisplayedFontSize.observeAsState()
	
	// true -> in preview
	if(!LocalInspectionMode.current)
		Settings.SaveSettings()
	
	Column(modifier = Modifier.fillMaxWidth()) {
		SettingSwitch(text = "Fullscreen", checked = fullscreen!!, onCheckedChange = {
			Settings.Instance.ReaderFullscreen = !fullscreen!!
			SettingsComposable.DisplayedFullscreen.postValue(!fullscreen!!)
		})
		SettingSwitch(text = "Show Battery and Time", checked = showBatAndTime!!, onCheckedChange = {
			Settings.Instance.ReaderShowBatteryAndTime = !showBatAndTime!!
			SettingsComposable.DisplayedShowBatteryAndTime.postValue(!showBatAndTime!!)
		})
		Divider()
		
		Surface(
			modifier = Modifier
				.fillMaxWidth()
				.height(150.dp)
				.padding(10.dp)
				.clip(RoundedCornerShape(5.dp)),
			color = Settings.Instance.ReaderBackgroundColor
		) {
			HtmlText(
				text = Settings.thefuckingloremipsum,
				style = TextStyle(
					color = Settings.Instance.ReaderTextColor,
					lineHeight = lineHeight!!.sp,
					fontSize = fontSize!!.sp,
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
			
			/*Surface (modifier = Modifier.padding(10.dp)){
				Box(modifier = Modifier
					.size(40.dp)
					.clip(CircleShape)
					.background(backgroundColor!!))
			}*/
			
			OutlinedTextField(
				value = backgroundColor!!,
				modifier = Modifier.width(150.dp),
				onValueChange = { str ->
					try {
						val newColor = Color(str.toLong(radix = 16))
						Settings.Instance.ReaderBackgroundColor = newColor
					} catch (_: Error){ }
					SettingsComposable.DisplayedBackgroundColor.postValue(str)
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
			
			/*Surface (modifier = Modifier.padding(10.dp)){
				Box(modifier = Modifier
					.size(40.dp)
					.clip(CircleShape)
					.background(backgroundColor!!))
			}*/
			
			OutlinedTextField(
				value = textColor!!.toString(),
				modifier = Modifier.width(150.dp),
				onValueChange = { str ->
					try {
						val newColor = Color(str.toLong(radix = 16))
						Settings.Instance.ReaderTextColor = newColor
					} catch (_: Error){ }
					SettingsComposable.DisplayedTextColor.postValue(str)
				})
		}
		
		SettingNumber(text = "Font Size", observer = fontSize, changeAmt = 1f, onChange = {
			num ->
			Settings.Instance.ReaderFontSize = num
			SettingsComposable.DisplayedFontSize.postValue(num)
		})
		
		SettingNumber(text = "Line Height", observer = lineHeight, changeAmt = 0.5f, onChange = {
				num ->
			Settings.Instance.ReaderLineHeight = num
			SettingsComposable.DisplayedLineHeight.postValue(num)
		})
		
		
	}
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
fun ReaderSettingsPreview() {
	RederTheme { Surface(modifier = Modifier.fillMaxSize()) { ReaderSettings() } }
}
