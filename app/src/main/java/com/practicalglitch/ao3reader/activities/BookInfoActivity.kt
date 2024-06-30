package com.practicalglitch.ao3reader.activities

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ireward.htmlcompose.HtmlText
import com.practicalglitch.ao3reader.Get
import com.practicalglitch.ao3reader.Internet
import com.practicalglitch.ao3reader.SavedWork
import com.practicalglitch.ao3reader.Storage
import com.practicalglitch.ao3reader.activities.composable.subcomposable.SwipeContainer
import com.practicalglitch.ao3reader.activities.nav.NavigationData
import com.practicalglitch.ao3reader.activities.nav.Navigator
import com.practicalglitch.ao3reader.activities.nav.Screen
import com.practicalglitch.ao3reader.ui.theme.RederTheme
import org.apio3.Types.WorkChapter

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
	showBackground = true,
	uiMode = Configuration.UI_MODE_NIGHT_YES,
	name = "Dark Mode"
)
@Composable
fun WorkInfoPreview(){
	//var work = SavedWork.DummySavedWork()
	//BookInfoActivity.chapterInfoList.add(work.Work.Contents[0])
	//BookInfoActivity.chapterInfoList.add(work.Work.Contents[1])
	//BookInfoActivity.chapterInfoList.add(work.Work.Contents[2])
	//RederTheme {
	//	BookInfoActivity(null, work, true)
	//}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookInfoActivity(
	navController: NavController? = null,
	workID: String,
	preview: Boolean = false
) {
	val inLib = remember { mutableStateOf(Storage.SavedWorkIDs.contains(workID)) }
	
	val work = remember { mutableStateOf(SavedWork()) }
	val workLoaded = remember { mutableStateOf(false) }
	
	val stopDoubleCalling = remember { mutableStateOf(false) }
	
	
	LaunchedEffect(!workLoaded.value) {
		if(!stopDoubleCalling.value) { // whye
			stopDoubleCalling.value = true
			Get.SavedWork(workID, work, workLoaded, false)
		}
	}
	
	/*
	// Handle saving data
	if(BookInfoActivity.chapterInfoList.size != 0 && chapLoadByMem != true && preview == false) {
		work.Work.Contents = BookInfoActivity.chapterInfoList.toTypedArray()
		Log.d("Debugt", "${work.Work.Contents.size}, ${BookInfoActivity.chapterInfoList.size}")
		LibraryIO.SaveWorkMetadata(work)
	}*/
	
	
	
	
	RederTheme {
		
		if(work.value.Work == null) {
			Log.d("test", "Work null!")
		} else {
			if(work.value.Work.Contents == null)
				Log.d("test", "Contents null!")
		}
		
		if(workLoaded.value && work.value.Work != null) {
			
			Surface(
				modifier =
				Modifier.fillMaxSize(),
				color = MaterialTheme.colorScheme.background
			) {
				Column {
					CenterAlignedTopAppBar(
						title = {
							Text("Library", maxLines = 1, overflow = TextOverflow.Ellipsis)
						},
						navigationIcon = {
							IconButton(onClick = {
								/*TODO*/
							}) {
								Icon(Icons.Default.Menu, "Menu")
							}
						},
						//Double check this. What would this button do?
						actions = {
							IconButton(onClick = { /*TODO*/ }) {
								Icon(Icons.Filled.Favorite, "Menu")
							}
						}
					)
					LazyColumn(
						modifier = Modifier
							.padding(vertical = 6.dp)
							.weight(1f)
					) {
						item {
							// Contain the big stuff
							SelectionContainer {
								Column(
									modifier = Modifier.padding(20.dp)
								) {
									Text(
										text = work.value.Work.Title,
										style = MaterialTheme.typography.titleLarge,
										textAlign = TextAlign.Center,
										modifier = Modifier.padding(0.dp, 5.dp)
									)
									Text(
										text = work.value.Work.Author,
										style = MaterialTheme.typography.labelMedium,
										textAlign = TextAlign.Left,
										modifier = Modifier.padding(0.dp, 2.dp)
									)
									Text(
										text = work.value.FandomList(-1),
										style = MaterialTheme.typography.labelMedium,
										textAlign = TextAlign.Left,
										modifier = Modifier.padding(0.dp, 2.dp)
									)
									Text(
										text = work.value.Work.Rating,
										style = MaterialTheme.typography.labelMedium,
										textAlign = TextAlign.Left,
										modifier = Modifier.padding(0.dp, 2.dp)
									)
									Text(
										text = if (work.value.Work.Finished) "Finished" else "Ongoing",
										style = MaterialTheme.typography.labelMedium,
										textAlign = TextAlign.Left,
										modifier = Modifier.padding(0.dp, 2.dp)
									)
									Text(
										text = "${"%,d".format(work.value.Work.Words)} Words - Avg. ${
											"%,d".format(
												work.value.Work.Words / work.value.Work.ChaptersAvailable
											)
										} Words Per Chapter",
										style = MaterialTheme.typography.labelMedium,
										textAlign = TextAlign.Left,
										modifier = Modifier.padding(0.dp, 2.dp)
									)
								}
							}
							
							var isOverflowing by remember { mutableStateOf(false) }
							var expandSummary by remember { mutableStateOf(false) }
							SelectionContainer {
								Column {
									HtmlText(
										text = work.value.Work.Summary,
										style = TextStyle(color = MaterialTheme.colorScheme.onBackground),
										maxLines = if (expandSummary) 9999 else 4,
										modifier = Modifier.padding(10.dp),
										onTextLayout = { textLayoutResult ->
											if (!isOverflowing && textLayoutResult.didOverflowHeight)
												isOverflowing = true
										},
										overflow = TextOverflow.Ellipsis
									)
									Divider()
									
									val charList =
										work.value.CharacterList(if (expandSummary) 99999 else 4)
									val relList =
										work.value.RelationshipList(if (expandSummary) 99999 else 4)
									val freeList =
										work.value.FreeformList(if (expandSummary) 99999 else 4)
									
									if (charList != "") {
										Text(text = charList,
											style = MaterialTheme.typography.labelMedium,
											modifier = Modifier.padding(10.dp),
											onTextLayout = {
												if (!isOverflowing && work.value.Work.Freeforms.size > 4)
													isOverflowing = true
											})
									}
									
									if (charList != "" && relList != "")
										Divider()
									
									if (relList != "") {
										Text(text = relList,
											style = MaterialTheme.typography.labelMedium,
											modifier = Modifier.padding(10.dp),
											onTextLayout = {
												if (!isOverflowing && work.value.Work.Freeforms.size > 4)
													isOverflowing = true
											})
									}
									
									// There's probably an easier way to do this.
									// Suppose Exists (E) and Not exists (N), in order
									// char, rel, free
									// First condition checks *EE, second checks ENE
									if ((relList != "" && freeList != "") || (relList == "" && charList != "" && freeList != "")) {
										Divider()
									}
									
									if (freeList != "") {
										Text(text = freeList,
											style = MaterialTheme.typography.labelMedium,
											modifier = Modifier.padding(10.dp),
											onTextLayout = {
												if (!isOverflowing && work.value.Work.Freeforms.size > 4)
													isOverflowing = true
											})
									}
								}
							}
							if (isOverflowing || expandSummary) {
								Row(
									modifier =
									Modifier
										.fillMaxWidth()
										.height(40.dp)
										.clickable {
											expandSummary = !expandSummary
										},
									horizontalArrangement = Arrangement.Center,
									verticalAlignment = Alignment.CenterVertically,
								) {
									Icon(
										if (!expandSummary)
											Icons.Filled.ExpandMore
										else
											Icons.Filled.ExpandLess,
										"Expand Summary",
										modifier = Modifier.size(30.dp),
									)
								}
							}
							
							NavigationBar(
								containerColor = Color.Transparent
							) {
								NavigationBarItem(
									selected = true,
									colors = NavigationBarItemDefaults.colors(
										selectedIconColor = MaterialTheme.colorScheme.primary,
										indicatorColor = MaterialTheme.colorScheme.background
									),
									icon = {
										Icon(
											if (inLib.value)
												Icons.Default.CollectionsBookmark
											else
												Icons.Outlined.CollectionsBookmark, "Library"
										)
									},
									label = {
										Text(
											if (inLib.value)
												"In Library"
											else
												"Add to Library"
										)
									},
									onClick = {
										if (inLib.value) {
											Storage.SavedWorkIDs.removeIf { id -> id == workID }
											Storage.SaveSavedWorkIDs()
											Storage.RemoveSavedWork(workID)
										} else {
											Storage.SavedWorkIDs.add(workID)
											Storage.SaveSavedWorkIDs()
											Storage.SaveSavedWork(work.value, false)
										}
										inLib.value = !inLib.value
									}
								)
								NavigationBarItem(
									selected = true,
									colors = NavigationBarItemDefaults.colors(
										selectedIconColor = MaterialTheme.colorScheme.primary,
										indicatorColor = MaterialTheme.colorScheme.background
									),
									icon = { Icon(Icons.Outlined.Refresh, "Refresh") },
									label = { Text("Refresh") },
									onClick = {
										// TODO: Add new chapters to Recents
										Internet().DownloadWorkMetadata(workID, work, workLoaded, false, false, true, false)
										//BookInfoActivity().GetChapters(context, work, true)
									}
								)
								NavigationBarItem(
									selected = true,
									colors = NavigationBarItemDefaults.colors(
										selectedIconColor = MaterialTheme.colorScheme.primary,
										indicatorColor = MaterialTheme.colorScheme.background
									),
									icon = { Icon(Icons.Default.Public, "WebView") },
									label = { Text("WebView") },
									onClick = {
										NavigationData.WebViewActivity_url =
											"https://archiveofourown.org/works/" + work.value.Work.Id
										navController!!.navigate(Screen.WebViewActivity.route)
									}
								)
								NavigationBarItem(
									selected = true,
									colors = NavigationBarItemDefaults.colors(
										selectedIconColor = MaterialTheme.colorScheme.primary,
										indicatorColor = MaterialTheme.colorScheme.background
									),
									icon = { Icon(Icons.Outlined.Download, "Download") },
									label = { Text("Download") },
									onClick = { /*TODO*/ }
								)
							}
							Button(
								modifier = Modifier
									.fillMaxWidth()
									.padding(30.dp, 10.dp),
								onClick = { /*TODO*/ }
							) {
								Text(text = "Continue Reading Chapter ")
							}
							Text(
								text =
								if(work.value.Work.Contents != null)
									work.value.Work.Contents.size.toString() + " Chapters"
								else
									"0 Chapters",
								modifier = Modifier
									.fillMaxWidth()
									.padding(12.dp, 5.dp),
								style = MaterialTheme.typography.labelLarge,
								textAlign = TextAlign.Left
							)
						}
						
						items(
							items =
							if(work.value.Work.Contents != null)
								work.value.Work.Contents
							else
								arrayOf<WorkChapter>()
						) { chapter ->
							var readStatus =
								work.value.ReadStatus.getOrDefault(chapter.ChapterID, 0f)
							var isRead = readStatus == 100f
							
							SwipeContainer(
								item = chapter,
								onSwipe = {
									if (isRead)
										work.value.ReadStatus[chapter.ChapterID] = 0f
									else
										work.value.ReadStatus[chapter.ChapterID] = 100f
									
									Storage.SaveReadStatus(work.value)
									readStatus =
										work.value.ReadStatus.getOrDefault(chapter.ChapterID, 0f)
									isRead = readStatus == 100f
								},
								backgroundColor = MaterialTheme.colorScheme.primary,
								backgroundContent = {
									Row(
										modifier = Modifier
											.padding(10.dp, 0.dp)
											.fillMaxSize(),
										horizontalArrangement = Arrangement.End,
										verticalAlignment = Alignment.CenterVertically
									) {
										Icon(
											imageVector = Icons.Default.Visibility,
											contentDescription = "Change Read Status",
											tint = MaterialTheme.colorScheme.onPrimary
										)
										Text(
											text = if (isRead) "Set Unread" else "Set Read",
											modifier = Modifier.padding(10.dp, 0.dp),
											textAlign = TextAlign.Center,
											color = MaterialTheme.colorScheme.onPrimary
										)
									}
								}) {
								Column(
									modifier = Modifier
										.background(MaterialTheme.colorScheme.background)
										.padding(10.dp, 4.dp)
										.clickable {
											Navigator.ToChapterActivity(
												navController!!,
												work.value,
												chapter.ChapterID
											)
										}
								) {
									Text(
										text = "Ch." + chapter.ChapterIndex.toString() + " - " + chapter.Title.toString(),
										modifier = Modifier
											.fillMaxWidth()
											.padding(2.dp),
										style = MaterialTheme.typography.labelMedium,
										textAlign = TextAlign.Left,
										// This is a stupid asf hack
										// Gonna fail at some point
										color = if (isRead) Color.Gray else Color.White
									)
									Text(
										text = "${chapter.UploadDate}",
										modifier = Modifier
											.fillMaxWidth()
											.padding(2.dp),
										style = MaterialTheme.typography.labelSmall,
										textAlign = TextAlign.Left,
										color = if (isRead) Color.Gray else Color.White
									)
									
								}
								
							}
						}
					}
				}
			}
		}
	}
}

@Preview
@Composable
fun test() {
	Row(modifier = Modifier
		.padding(10.dp, 0.dp)
		.fillMaxWidth(),
		horizontalArrangement = Arrangement.End,
		verticalAlignment = Alignment.CenterVertically) {
		Icon(imageVector = Icons.Default.Visibility, contentDescription = "Unread")
		Text(text = "Set Unread", modifier = Modifier.padding(10.dp, 0.dp))
	}
}
