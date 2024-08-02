
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.practicalglitch.ao3reader.ui.theme.RederTheme

@Preview(
	showBackground = true,
	uiMode = Configuration.UI_MODE_NIGHT_YES,
	name = "Dark Mode"
)
@Composable
fun WebViewActivityPreview(){
	WebViewActivity(url = "https://archiveofourown.org/works/52388716", null, true)
}

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewActivity(
	url: String,
	replacedTitle: String? = null,
	preview: Boolean = false
) {
	var backEnabled by remember { mutableStateOf(false) }
	var webView: WebView? = null
	var dispUrl by remember { mutableStateOf(url) }
	var title by  remember { mutableStateOf("") }
	var expanded by remember { mutableStateOf(false) }
	val context = LocalContext.current
	
	BackHandler(enabled = backEnabled) {
		webView?.goBack()
	}
	
	RederTheme {
		Surface(
			modifier = Modifier.fillMaxSize(),
			color = MaterialTheme.colorScheme.onSecondary
		) {
			Scaffold(
				topBar = {
					TopAppBar(title = {
						Box(modifier = Modifier
							.background(MaterialTheme.colorScheme.background)
							.height(65.dp)
							.fillMaxWidth()){
							Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically){
								Column(
									modifier = Modifier
										.weight(3f)
										.fillMaxHeight()
										.padding(10.dp),
									verticalArrangement = Arrangement.SpaceBetween
								) {
									Text(
										text = replacedTitle?: title,
										softWrap = false,
										maxLines = 1,
										style = MaterialTheme.typography.labelLarge,
										color = MaterialTheme.colorScheme.onBackground,
										overflow = TextOverflow.Ellipsis
									)
									Text(
										text = dispUrl,
										softWrap = false,
										maxLines = 1,
										fontSize = 10.sp,
										color = MaterialTheme.colorScheme.onBackground,
										overflow = TextOverflow.Ellipsis
									)
								}
								IconButton(onClick = {
									expanded = !expanded
									
								}) {
									Icon(
										Icons.Filled.MoreVert,
										contentDescription = "Options",
										tint = MaterialTheme.colorScheme.onBackground
									)
									DropdownMenu(
										expanded = expanded,
										onDismissRequest = { expanded = false }) {
										DropdownMenuItem(text = { Text(text = "Refresh") }, onClick = { webView?.reload() })
										DropdownMenuItem(text = { Text(text = "Open in Browser") }, onClick = {
											val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
											context.startActivity(browserIntent) })
									}
								}
							}
							
						}
					})
					
				}
			) {
				Box (modifier = Modifier.padding(it)) {
					if (!preview)
						AndroidView(factory = {
							return@AndroidView WebView(context).apply {
								settings.javaScriptEnabled = true
								webViewClient = WebViewClient()
								settings.loadWithOverviewMode = true
								settings.useWideViewPort = true
								settings.setSupportZoom(false)
								
								webViewClient = object : WebViewClient() {
									override fun onPageStarted(
										view: WebView?,
										url: String?,
										favicon: Bitmap?
									) {
										if (view != null) {
											backEnabled = view.canGoBack()
											dispUrl = view.url!! // Oh boy!!
										}
									}
									
									override fun onPageFinished(view: WebView?, url: String?) {
										if (view != null) {
											title = view.title!!
										}
									}
								}
								
								layoutParams = ViewGroup.LayoutParams(
									ViewGroup.LayoutParams.MATCH_PARENT,
									ViewGroup.LayoutParams.MATCH_PARENT
								)
								
								loadUrl(url)
								webView = this
							}
						}, update = {
							webView = it
						})
				}
			}
		}
	}
}
