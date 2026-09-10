package com.rayliu.myphotodiary

interface DisplayEntriesProvider {
    fun getDisplayEntries(
        sourceEntries: List<DiaryEntry>,
        searchQuery: String,
        selectedMoodFilter: Mood?,
        isNewestFirst: Boolean
    ): List<DiaryEntry>
}
