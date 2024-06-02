package com.practicalglitch.ao3reader.activities.composable.subcomposable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SwipeContainer(
	item: T,
	onSwipe: (T) -> Unit,
	backgroundColor: Color,
	backgroundContent: @Composable () -> Unit,
	content: @Composable (T) -> Unit
) {
	var isSwiped by remember { mutableStateOf(false) }

	val swipeToDismissState = rememberSwipeToDismissBoxState(
		confirmValueChange = {
			isSwiped = true
			true
		}
	)
	
	if(swipeToDismissState.currentValue != SwipeToDismissBoxValue.Settled) {
		if(isSwiped) {
			onSwipe(item)
			isSwiped = false
		}
		
		LaunchedEffect(Unit) {
			swipeToDismissState.reset()
		}
	}
	
	SwipeToDismissBox(
		state = swipeToDismissState,
		backgroundContent = {
			Box(modifier = Modifier
				.fillMaxSize()
				.background(backgroundColor)){
				backgroundContent()
			}
		},
		enableDismissFromStartToEnd = false) {
		content(item)
	}
}