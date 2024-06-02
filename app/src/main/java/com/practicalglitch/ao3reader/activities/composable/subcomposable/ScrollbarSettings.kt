package com.practicalglitch.ao3reader.activities.composable.subcomposable

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import my.nanihadesuka.compose.ScrollbarLayoutSide
import my.nanihadesuka.compose.ScrollbarSelectionActionable
import my.nanihadesuka.compose.ScrollbarSelectionMode
import my.nanihadesuka.compose.ScrollbarSettings


val DefaultScrollSettings = ScrollbarSettings(
	enabled = true,
	side = ScrollbarLayoutSide.End,
	alwaysShowScrollbar = false,
	thumbThickness = 2.dp,
	scrollbarPadding = 3.dp,
	thumbMinLength = 0.1f,
	thumbUnselectedColor = Color.Gray,
	thumbSelectedColor = Color.White,
	thumbShape = CircleShape,
	selectionMode = ScrollbarSelectionMode.Thumb,
	selectionActionable = ScrollbarSelectionActionable.Always,
	hideDelayMillis = 100,
	hideDisplacement = 14.dp,
	hideEasingAnimation = FastOutSlowInEasing,
	durationAnimationMillis = 500,
)