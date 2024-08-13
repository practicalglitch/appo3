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
	val NotRated: Boolean = true,
	
	val Sorting: SortingType = SortingType.AddedOrder,
	val Grouping: GroupingType = GroupingType.Ungrouped
)

enum class SortingType {
	Alphabetical,
	Hits,
	Kudos,
	AddedOrder
}

enum class GroupingType {
	Ungrouped,
	PrimaryFandom,
	Rating
}

val SortingTypeMap: Map<String, SortingType> = mapOf(
	Pair("Alphabetical", SortingType.Alphabetical),
	Pair("Hits", SortingType.Hits),
	Pair("Kudos", SortingType.Kudos),
	Pair("Added Order", SortingType.AddedOrder)
)

val GroupingTypeMap: Map<String, GroupingType> = mapOf(
	Pair("Ungrouped", GroupingType.Ungrouped),
	Pair("Rating", GroupingType.Rating),
	Pair("Primary Fandom", GroupingType.PrimaryFandom)
)

fun <K, V> Map<K, V>.getKey(value: V) =
	entries.firstOrNull { it.value == value }?.key