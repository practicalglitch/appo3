package com.practicalglitch.ao3reader.activities

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ireward.htmlcompose.HtmlText
import com.practicalglitch.ao3reader.Internet
import com.practicalglitch.ao3reader.SavedWork
import com.practicalglitch.ao3reader.Storage
import com.practicalglitch.ao3reader.activities.composable.ReaderSettings
import com.practicalglitch.ao3reader.activities.composable.subcomposable.DefaultScrollSettings
import com.practicalglitch.ao3reader.activities.nav.Navigator
import com.practicalglitch.ao3reader.ui.theme.ArbutusSlabFontFamily
import com.practicalglitch.ao3reader.ui.theme.RederTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import my.nanihadesuka.compose.ColumnScrollbar
import org.apio3.Types.WorkChapter
import kotlin.time.Duration.Companion.seconds


suspend fun PointerInputScope.detectTapGestureIfMatch(
	onTap: (Offset) -> Boolean,
) {
	awaitEachGesture {
		awaitFirstDown()
		
		val up = waitForUpOrCancellation()
		if (up != null && onTap(up.position)) {
			up.consume()
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(onDismiss: () -> Unit) {
	val modalBottomSheetState = rememberModalBottomSheetState()
	
	ModalBottomSheet(
		onDismissRequest = { onDismiss() },
		sheetState = modalBottomSheetState,
		dragHandle = { BottomSheetDefaults.DragHandle() },
	) {
		ReaderSettings()
	}
}


@Preview(
	showBackground = true,
	uiMode = Configuration.UI_MODE_NIGHT_YES,
	name = "Dark Mode"
)
@Composable
fun ChapterActivityPreview(){
	RederTheme {
		ChapterActivity(navController = null, SavedWork(), "")
	}
}

@Composable
fun ChapterActivity(navController: NavController?, savedWork: SavedWork, inChapterId: String) {
	val chapterId = remember { mutableStateOf(inChapterId) }
	val chapter = remember { mutableStateOf(WorkChapter()) }
	val work = remember { mutableStateOf(savedWork) }
	val loaded = remember { mutableStateOf(false) }
	val menuOpen = remember { mutableStateOf(false) }
	
	var prevChap: WorkChapter? = null
	var nextChap: WorkChapter? = null
	
	val showSheet = remember { mutableStateOf(false) }
	
	val columnScrollState = rememberScrollState()
	val scope = rememberCoroutineScope()
	
	val isAtBottom = columnScrollState.value == columnScrollState.maxValue
	
	if(loaded.value) {
		// Add work to top of history
		// Remove work if in history, and add it to the top
		Storage.History.removeIf { it.WorkID == work.value.Work.Id }
		Storage.History.add(0, work.value.Work.Contents[chapter.value.ChapterIndex - 1])
		Storage.SaveHistory()
	}
	
	// Downloads chapter
	LaunchedEffect(!loaded.value) {
		Internet().DownloadChapter(chapterId.value, chapter, loaded)
	}
	
	// Records seconds read timer
	LaunchedEffect(Unit) {
		while(Storage.Settings.GeneralStatsEnabled){
			delay(1.seconds)
			Storage.Stats.SecondsRead++
		}
	}
	
	// Handle if reach end of chapter
	LaunchedEffect(isAtBottom) {
		if(isAtBottom && loaded.value) {
			//Log.d("Test", "End of chapter.")
			// get index of the chapter in work
			if (work.value.ReadStatus[chapter.value.ChapterID] != 100f) {
				work.value.ReadStatus[chapter.value.ChapterID] = 100f
				Storage.SaveReadStatus(work.value)
				if (Storage.Settings.GeneralStatsEnabled)
					Storage.Stats.ChaptersRead++
				
			}
		}
	}
	
	// If the menu isn't open and the user clicks back,
	// First open the menu.
	// If they click back again, it exits normally.
	BackHandler {
		if(!menuOpen.value)
			menuOpen.value = !menuOpen.value
		else {
			Storage.SaveStatistics()
			navController!!.popBackStack()
		}
	}
	
	RederTheme {
		
		if (showSheet.value) {
			BottomSheet() {
				showSheet.value = false
			}
		}
		
		Surface(
			modifier = Modifier
				.fillMaxSize()
				.pointerInput(Unit) {
					detectTapGestureIfMatch { position ->
						menuOpen.value = !menuOpen.value
						true
					}
				},
			color = Color(Storage.Settings.ReaderBackgroundColor)
		) {
			// If the menu isn't open and the user clicks back,
			// First open the menu.
			// If they click back again, it exits normally.
			BackHandler(!menuOpen.value) {
				menuOpen.value = !menuOpen.value
			}
			
			
			ColumnScrollbar(state = columnScrollState, settings = DefaultScrollSettings) {
				Column(
					modifier = Modifier
						.fillMaxSize()
						.verticalScroll(columnScrollState)
				) {
						SelectionContainer {
							Column(modifier = Modifier.fillMaxSize()) {
								
								if (loaded.value) {
									// Get and set the previous and next chapter
									for (i in 0 until work.value.Work.Contents.size) {
										if (work.value.Work.Contents[i].ChapterID == chapter.value.ChapterID) {
											if (i != 0)
												prevChap = work.value.Work.Contents[i - 1]
											if (i != work.value.Work.Contents.size - 1)
												nextChap = work.value.Work.Contents[i + 1]
										}
									}
								}

								Spacer(modifier = Modifier.height(100.dp))

								if (prevChap != null) {
									OutlinedButton(
										modifier = Modifier
											.fillMaxWidth()
											.padding(30.dp, 10.dp),
										onClick = {
											// Get previous chapter, set displayed chapter to it.
											chapterId.value = prevChap!!.ChapterID
											loaded.value = false
											scope.launch { columnScrollState.scrollTo(0) }
										}
									) {
										Text(text = "Previous Chapter: Ch.${prevChap!!.ChapterIndex} - ${prevChap!!.Title}")
									}
								}
								
								
								if (!loaded.value) {
									Text(
										text = "Loading...",
										color = Color(Storage.Settings.ReaderTextColor),
									)
								} else {
									Text(
										text = "Ch." + chapter.value.ChapterIndex + " - " + chapter.value.Title,
										color = Color(Storage.Settings.ReaderTextColor),
										textAlign = TextAlign.Center,
										fontSize = 25.sp,
										lineHeight = 30.sp,
										fontWeight = FontWeight.Bold,
										modifier = Modifier.padding(15.dp),
										fontFamily = ArbutusSlabFontFamily
									)
									if (chapter.value.Summary != null && !chapter.value.Summary.equals(
											""
										)
									) {
										Text(
											text = "Chapter Summary",
											color = Color(Storage.Settings.ReaderTextColor),
											textAlign = TextAlign.Center,
											fontWeight = FontWeight.Bold,
											lineHeight = Storage.Settings.ReaderLineHeight.sp,
											fontSize = Storage.Settings.ReaderFontSize.sp,
											modifier = Modifier.padding(10.dp),
											fontFamily = ArbutusSlabFontFamily
										)
										HtmlText(
											text = chapter.value.Summary,
											style = TextStyle(
												color = Color(Storage.Settings.ReaderTextColor),
												lineHeight = Storage.Settings.ReaderLineHeight.sp,
												fontSize = Storage.Settings.ReaderFontSize.sp,
												fontFamily = ArbutusSlabFontFamily
											),
											modifier = Modifier.padding(10.dp)
										)
									}
									if (chapter.value.StartNotes != null && !chapter.value.StartNotes.equals(
											""
										)
									) {
										Text(
											text = "Chapter Start Notes",
											color = Color(Storage.Settings.ReaderTextColor),
											textAlign = TextAlign.Center,
											lineHeight = Storage.Settings.ReaderLineHeight.sp,
											fontSize = Storage.Settings.ReaderFontSize.sp,
											fontWeight = FontWeight.Bold,
											modifier = Modifier.padding(10.dp),
											fontFamily = ArbutusSlabFontFamily
										)
										HtmlText(
											text = chapter.value.StartNotes,
											style = TextStyle(
												color = Color(Storage.Settings.ReaderTextColor),
												lineHeight = Storage.Settings.ReaderLineHeight.sp,
												fontSize = Storage.Settings.ReaderFontSize.sp,
												fontFamily = ArbutusSlabFontFamily
											),
											modifier = Modifier.padding(10.dp)
										)
									}
									HtmlText(
										text = chapter.value.Body,
										style = TextStyle(
											color = Color(Storage.Settings.ReaderTextColor),
											lineHeight = Storage.Settings.ReaderLineHeight.sp,
											fontSize = Storage.Settings.ReaderFontSize.sp,
											fontFamily = ArbutusSlabFontFamily
										),
										modifier = Modifier.padding(10.dp)
									)
									if (chapter.value.EndNotes != null && !chapter.value.EndNotes.equals(
											""
										)
									) {
										Text(
											text = "Chapter End Notes",
											color = Color(Storage.Settings.ReaderTextColor),
											textAlign = TextAlign.Center,
											lineHeight = Storage.Settings.ReaderLineHeight.sp,
											fontSize = Storage.Settings.ReaderFontSize.sp,
											fontWeight = FontWeight.Bold,
											modifier = Modifier.padding(10.dp),
											fontFamily = ArbutusSlabFontFamily
										)
										HtmlText(
											text = chapter.value.EndNotes,
											style = TextStyle(
												color = Color(Storage.Settings.ReaderTextColor),
												lineHeight = Storage.Settings.ReaderLineHeight.sp,
												fontSize = Storage.Settings.ReaderFontSize.sp,
												fontFamily = ArbutusSlabFontFamily
											),
											modifier = Modifier.padding(10.dp)
										)
									}
									if (nextChap != null) {
										OutlinedButton(
											modifier = Modifier
												.fillMaxWidth()
												.padding(30.dp, 10.dp),
											onClick = {
												// Set this activity to next chapter contents, jump to top of screen
												chapterId.value = nextChap!!.ChapterID
												loaded.value = false
												scope.launch { columnScrollState.scrollTo(0) }
											}
										) {
											Text(text = "Next Chapter: Ch.${nextChap!!.ChapterIndex} - ${nextChap!!.Title}")
										}
									} else {
										Text(
											text = "No next chapter.",
											color = Color(Storage.Settings.ReaderTextColor),
											textAlign = TextAlign.Center,
										)
									}
									Spacer(modifier = Modifier.height(100.dp))
								}
							}
						}
				}
			}
			if(menuOpen.value) {
				ChapterActivityMenu(
					navController = navController,
					chapter = chapter,
					showSheet = showSheet,
					scrollState = columnScrollState,
					showForward = if (nextChap == null) true else false,
					showBackward = if (prevChap == null) true else false,
					workId = work.value.Work.Id
				) { forward ->
					if(forward) {
						// Set this activity to next chapter contents, jump to top of screen
						chapterId.value = nextChap!!.ChapterID
						loaded.value = false
						scope.launch{ columnScrollState.scrollTo(0) }
					} else {
						// Get previous chapter, set displayed chapter to it.
						chapterId.value = prevChap!!.ChapterID
						loaded.value = false
						scope.launch{ columnScrollState.scrollTo(0) }
					}
				}
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterActivityMenu(
	navController: NavController?,
	chapter: MutableState<WorkChapter>,
	showSheet: MutableState<Boolean>,
	scrollState: ScrollState,
	showForward: Boolean,
	showBackward: Boolean,
	workId: String,
	onChangeChapter: (Boolean) -> Unit
) {
	val wordCount = remember { mutableStateOf(0) }
	
	if(wordCount.value == 0)
		wordCount.value = wordCount(chapter.value.Body)
	
	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(Color.Transparent),
		verticalArrangement = Arrangement.SpaceBetween
	) {
		// Top bar
		Column {
			TopAppBar(
				colors = TopAppBarDefaults.topAppBarColors(

					// gotta love hard coded colours!!
					containerColor = Color(0x12, 0x13, 0x16, 230),
					titleContentColor = MaterialTheme.colorScheme.primary,
				),
				title = {
					Column {
						Text(
							text = chapter.value.Title,
							maxLines = 1,
							overflow = TextOverflow.Ellipsis,
							color = Color(Storage.Settings.ReaderTextColor)
						)
						Text(
							text = "${"%,d".format(wordCount.value)} words",
							maxLines = 1,
							overflow = TextOverflow.Ellipsis,
							color = Color(Storage.Settings.ReaderTextColor),
							style = MaterialTheme.typography.titleSmall
						)
					}
					
				},
				navigationIcon = {
					IconButton(onClick = {
						Storage.SaveStatistics()
						navController!!.popBackStack()
					}) {
						Icon(
							imageVector = Icons.Filled.ArrowBack,
							contentDescription = "Back",
							tint = Color.White
						)
					}
				}, actions = {
					IconButton(onClick = {
						Navigator.ToBookInfoActivity(navController!!, workId)
					}) {
						Icon(
							imageVector = Icons.Filled.Book,
							contentDescription = "See Work",
							tint = Color.White
						)
					}
				})
			if(scrollState.maxValue != 0)
				LinearProgressIndicator(
					modifier = Modifier.fillMaxWidth(),
					color = MaterialTheme.colorScheme.tertiary,
					trackColor = Color(0x44, 0x47, 0x4f, 230),
					progress = { scrollState.value.toFloat() / scrollState.maxValue.toFloat() }
				)
		}
		
		// Bottom bar
		BottomAppBar(
			modifier = Modifier.height(60.dp),
			containerColor = Color(0x12, 0x13, 0x16, 230),
			contentColor = Color.White,
			content = {
				Row(
					modifier = Modifier.weight(1f),
					horizontalArrangement = Arrangement.SpaceEvenly
				)
				{
					IconButton(
						modifier = if(showBackward) Modifier.alpha(0f) else Modifier,
						onClick = {
							onChangeChapter(false)
						}) {
						Icon(
							Icons.Filled.NavigateBefore,
							contentDescription = "Previous Chapter",
						)
					}

					IconButton(
						onClick = { showSheet.value = true }) {
						Icon(
							Icons.Filled.Settings,
							contentDescription = "Settings",
						)
					}
					IconButton(
						modifier = if(showForward) Modifier.alpha(0f) else Modifier,
						onClick = {
						onChangeChapter(true)
					}) {
						Icon(
							Icons.Filled.NavigateNext,
							contentDescription = "Next Chapter",
						)
					}
				}
			}
		)
	}
}

@Preview(
	showBackground = true,
	uiMode = Configuration.UI_MODE_NIGHT_YES,
	name = "Dark Mode"
)
@Preview(
	showBackground = true,
	uiMode = Configuration.UI_MODE_NIGHT_NO,
	name = "Light Mode"
)
@Composable
fun ChapterActivityMenuPreview(){
	val chapter = remember { mutableStateOf(WorkChapter()) }
	
	chapter.value.Title = "Test"
	chapter.value.Body = "hello, world"
	
	val menuOpen = remember { mutableStateOf(true) }
	val showSheet = remember { mutableStateOf(false) }
	val scrollState = rememberScrollState()
	RederTheme {
		Surface(modifier = Modifier
			.fillMaxSize()
			.background(Color.Black)) {
			ChapterActivityMenu(
				navController = null,
				chapter = chapter,
				showSheet = showSheet,
				scrollState = scrollState,
				showBackward = true,
				showForward = true,
				workId = ""
			) {
			
			}
		}
	}
}

fun wordCount(str: String): Int {
	val trimmedStr = str.trim()
	return if (trimmedStr.isEmpty()) {
		0
	} else {
		trimmedStr.split("\\s+".toRegex()).size
	}
}
