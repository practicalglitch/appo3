package com.practicalglitch.ao3reader

import java.util.Locale

fun SavedWork.isInFilter(filter: Filters): Boolean{
	if(Work.Rating == org.apio3.Types.Work.ValidRatings[0] && !filter.Gen)
		return false
	if(Work.Rating == org.apio3.Types.Work.ValidRatings[1] && !filter.Teen)
		return false
	if(Work.Rating == org.apio3.Types.Work.ValidRatings[2] && !filter.Mature)
		return false
	if(Work.Rating == org.apio3.Types.Work.ValidRatings[3] && !filter.Explicit)
		return false
	if(Work.Rating == org.apio3.Types.Work.ValidRatings[4] && !filter.NotRated)
		return false
	return true
}

fun String.filterFrom(filter: String): Boolean {
	return this.lowercase(Locale.getDefault()).filterNot { it.isWhitespace() }.contains(filter.lowercase().filterNot { it.isWhitespace() })
}


data class Filters(
	val Gen: Boolean = true,
	val Teen: Boolean = true,
	val Mature: Boolean = true,
	val Explicit: Boolean = true,
	val NotRated: Boolean = true
)