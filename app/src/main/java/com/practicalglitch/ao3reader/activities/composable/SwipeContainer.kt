package com.practicalglitch.ao3reader.activities.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.rememberDismissState
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
){
	var isRemoved by remember{
		mutableStateOf(false)
	}
	val state = rememberDismissState(
		confirmValueChange = {value -> if(value == DismissValue.DismissedToStart) {
			isRemoved = true
			true
		} else
		false}
	)
	
	if(isRemoved && state.currentValue != DismissValue.Default) {
		onSwipe(item)
	}
	if(state.currentValue != DismissValue.Default)
		LaunchedEffect(Unit) {
			state.reset()
		}
	
	SwipeToDismiss(state = state,
		background = { SwipeItem(swipeState = state, color = backgroundColor) {
			backgroundContent()
		} },
		dismissContent = { content(item) },
		directions = setOf(DismissDirection.EndToStart)
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeItem(
	swipeState: DismissState,
	color: Color,
	content: @Composable () -> Unit
){
	val displayedColor = if(swipeState.dismissDirection == DismissDirection.EndToStart)
		color
	else
		Color.Transparent
	
	Box (
		modifier = Modifier
			.fillMaxSize()
			.background(displayedColor)
	){
		content()
	}
}