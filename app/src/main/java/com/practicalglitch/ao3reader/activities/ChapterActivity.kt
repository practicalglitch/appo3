package com.practicalglitch.ao3reader.activities

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.practicalglitch.ao3reader.Library
import com.practicalglitch.ao3reader.LibraryIO
import com.practicalglitch.ao3reader.SavedWork
import com.practicalglitch.ao3reader.Settings
import com.practicalglitch.ao3reader.activities.composable.ReaderSettings
import com.practicalglitch.ao3reader.ui.theme.ArbutusSlabFontFamily
import com.practicalglitch.ao3reader.ui.theme.RederTheme
import com.ireward.htmlcompose.HtmlText
import com.practicalglitch.ao3reader.Internet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apio3.ApiO3
import org.apio3.Types.WorkChapter


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


//thx stackoverflow
@Composable
private fun LazyListState.isAtBottom(): Boolean {
	
	return remember(this) {
		derivedStateOf {
			val visibleItemsInfo = layoutInfo.visibleItemsInfo
			if (layoutInfo.totalItemsCount == 0) {
				false
			} else {
				val lastVisibleItem = visibleItemsInfo.last()
				val viewportHeight = layoutInfo.viewportEndOffset + layoutInfo.viewportStartOffset
				
				(lastVisibleItem.index + 1 == layoutInfo.totalItemsCount &&
						lastVisibleItem.offset + lastVisibleItem.size <= viewportHeight)
			}
		}
	}.value
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

@OptIn(ExperimentalMaterial3Api::class)
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
	
	val listState = rememberLazyListState()
	val scope = rememberCoroutineScope()
	
	val isAtBottom = listState.isAtBottom()
	
	if(loaded.value) {
		// Add work to top of history
		// Remove work if in history, and add it to the top
		Library.history.removeIf { it.WorkID == work.value.Work.Id }
		Library.history.add(0, work.value.Work.Contents[chapter.value.ChapterIndex - 1])
		LibraryIO.SaveHistory(Library.history.toTypedArray())
	}
	
	LaunchedEffect(!loaded.value) {
		Internet().DownloadChapter(chapterId.value, chapter, loaded)
	}
	
	
	
	LaunchedEffect((isAtBottom && loaded.value)) {
		if(isAtBottom && chapter != null) {
			//Log.d("Test", "End of chapter.")
			// get index of the chapter in work
			if(work.value.ReadStatus[chapter.value.ChapterID] != 100f) {
				work.value.ReadStatus[chapter.value.ChapterID] = 100f
				LibraryIO.SaveWorkReadStatus(work.value)
			}
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
			color = Settings.Instance.ReaderBackgroundColor
		) {
			// If the menu isn't open and the user clicks back,
			// First open the menu.
			// If they click back again, it exits normally.
			BackHandler(!menuOpen.value) {
				menuOpen.value = !menuOpen.value
			}
			LazyColumn(
				modifier = Modifier
					.fillMaxSize(),
				state = listState,
				content = {
					item {
						SelectionContainer {
							Column(modifier = Modifier.fillMaxSize()) {
								
								if(loaded.value) {
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
								
								if (prevChap != null) {
									OutlinedButton(
										modifier = Modifier
											.fillMaxWidth()
											.padding(30.dp, 10.dp),
										onClick = {
											// Get previous chapter, set displayed chapter to it.
											chapterId.value = prevChap!!.ChapterID
											loaded.value = false
											scope.launch{ listState.scrollToItem(0) }
										}
									) {
										Text(text = "Previous Chapter: Ch.${prevChap!!.ChapterIndex} - ${prevChap!!.Title}")
									}
								} else {
									Text(
										text = "No previous chapter.",
										color = MaterialTheme.colorScheme.onBackground,
										textAlign = TextAlign.Center,
									)
								}
								
								
								if (!loaded.value) {
									Text(
										text = "Loading...",
										color = MaterialTheme.colorScheme.onBackground,
									)
								} else {
									Text(
										text = "Ch." + chapter.value.ChapterIndex + " - " + chapter.value.Title,
										color = MaterialTheme.colorScheme.onBackground,
										textAlign = TextAlign.Center,
										fontSize = 25.sp,
										lineHeight = 30.sp,
										fontWeight = FontWeight.Bold,
										modifier = Modifier.padding(15.dp),
										fontFamily = ArbutusSlabFontFamily
									)
									if (chapter.value.Summary != null && !chapter.value.Summary.equals("")) {
										Text(
											text = "Chapter Summary",
											color = Settings.Instance.ReaderTextColor,
											textAlign = TextAlign.Center,
											fontWeight = FontWeight.Bold,
											lineHeight = Settings.Instance.ReaderLineHeight.sp,
											fontSize = Settings.Instance.ReaderFontSize.sp,
											modifier = Modifier.padding(10.dp),
											fontFamily = ArbutusSlabFontFamily
										)
										HtmlText(
											text = chapter.value.Summary,
											style = TextStyle(
												color = Settings.Instance.ReaderTextColor,
												lineHeight = Settings.Instance.ReaderLineHeight.sp,
												fontSize = Settings.Instance.ReaderFontSize.sp,
												fontFamily = ArbutusSlabFontFamily),
											modifier = Modifier.padding(10.dp)
										)
									}
									if (chapter.value.StartNotes != null && !chapter.value.StartNotes.equals(
											""
										)
									) {
										Text(
											text = "Chapter Start Notes",
											color = Settings.Instance.ReaderTextColor,
											textAlign = TextAlign.Center,
											lineHeight = Settings.Instance.ReaderLineHeight.sp,
											fontSize = Settings.Instance.ReaderFontSize.sp,
											fontWeight = FontWeight.Bold,
											modifier = Modifier.padding(10.dp),
											fontFamily = ArbutusSlabFontFamily
										)
										HtmlText(
											text = chapter.value.StartNotes,
											style = TextStyle(
												color = Settings.Instance.ReaderTextColor,
												lineHeight = Settings.Instance.ReaderLineHeight.sp,
												fontSize = Settings.Instance.ReaderFontSize.sp,
												fontFamily = ArbutusSlabFontFamily
											),
											modifier = Modifier.padding(10.dp)
										)
									}
									HtmlText(
										text = chapter.value.Body,
										style = TextStyle(
											color = Settings.Instance.ReaderTextColor,
											lineHeight = Settings.Instance.ReaderLineHeight.sp,
											fontSize = Settings.Instance.ReaderFontSize.sp,
											fontFamily = ArbutusSlabFontFamily),
										modifier = Modifier.padding(10.dp)
									)
									if (chapter.value.EndNotes != null && !chapter.value.EndNotes.equals("")) {
										Text(
											text = "Chapter End Notes",
											color = Settings.Instance.ReaderTextColor,
											textAlign = TextAlign.Center,
											lineHeight = Settings.Instance.ReaderLineHeight.sp,
											fontSize = Settings.Instance.ReaderFontSize.sp,
											fontWeight = FontWeight.Bold,
											modifier = Modifier.padding(10.dp),
											fontFamily = ArbutusSlabFontFamily
										)
										HtmlText(
											text = chapter.value.EndNotes,
											style = TextStyle(
												color = Settings.Instance.ReaderTextColor,
												lineHeight = Settings.Instance.ReaderLineHeight.sp,
												fontSize = Settings.Instance.ReaderFontSize.sp,
												fontFamily = ArbutusSlabFontFamily),
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
												//Internet().DownloadChapter(chapterId.value, chapter)
												scope.launch{ listState.scrollToItem(0) }
											}
										) {
											Text(text = "Next Chapter: Ch.${nextChap!!.ChapterIndex} - ${nextChap!!.Title}")
										}
									} else {
										Text(
											text = "No next chapter.",
											color = Settings.Instance.ReaderTextColor,
											textAlign = TextAlign.Center,
										)
									}
								}
							}
						}
					}
				})
			if(menuOpen.value) {
				Column(
					modifier = Modifier.fillMaxSize(),
					verticalArrangement = Arrangement.SpaceBetween
				) {
					TopAppBar(
						colors = TopAppBarDefaults.topAppBarColors(
							containerColor = MaterialTheme.colorScheme.background.copy(0.9f),
							titleContentColor = MaterialTheme.colorScheme.primary,
						),
						title = {
							Text(
								text = chapter.value.Title,
								maxLines = 1,
								overflow = TextOverflow.Ellipsis,
								color = Settings.Instance.ReaderTextColor)
						},
						navigationIcon = {
							IconButton(onClick = { navController!!.popBackStack() }) {
								Icon(
									imageVector = Icons.Filled.ArrowBack,
									contentDescription = "Back"
								)
							}
						}, actions = {
							IconButton(onClick = { menuOpen.value = false }) {
								Icon(
									imageVector = Icons.Filled.Close,
									contentDescription = "Close"
								)
							}
						},
						
						)
					
					BottomAppBar(
						modifier = Modifier.height(60.dp),
						containerColor = MaterialTheme.colorScheme.background.copy(0.9f),
						contentColor = Color.White,
						content = {
							Row(
								modifier = Modifier.weight(1f),
								horizontalArrangement = Arrangement.SpaceEvenly
							)
							{
								IconButton(
									onClick = {
										// Get previous chapter, set displayed chapter to it.
										chapterId.value = prevChap!!.ChapterID
										loaded.value = false
										scope.launch{ listState.scrollToItem(0) }
									}) {
									Icon(
										Icons.Filled.NavigateBefore,
										contentDescription = "Previous Chapter",
									)
								}
								IconButton(onClick = { showSheet.value = true }) {
									Icon(
										Icons.Filled.Settings,
										contentDescription = "Settings",
									)
								}
								IconButton(onClick = {
									// Set this activity to next chapter contents, jump to top of screen
									chapterId.value = nextChap!!.ChapterID
									loaded.value = false
									scope.launch{ listState.scrollToItem(0) }
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
		}
	}
}