package com.rayliu.myphotodiary

class DisplayEntriesUseCase(
    private val diaryEntryMapper: DiaryEntryMapper = DiaryEntryMapper()
) : DisplayEntriesProvider {
    override fun getDisplayEntries(
        sourceEntries: List<DiaryEntry>,
        searchQuery: String,
        selectedMoodFilter: Mood?,
        isNewestFirst: Boolean
    ): List<DiaryEntry> {
        val query = AndDiaryEntryQuery(
            queries = listOf(
                TitleContainsQuery(searchQuery),
                MoodFilterQuery(selectedMoodFilter)
            )
        )
        val matchingEntries = sourceEntries.filter { entry -> query.matches(entry) }
        val orderedEntries = if (isNewestFirst) {
            matchingEntries.sortedByDescending { entry -> entry.createdAt }
        } else {
            matchingEntries
        }
        return orderedEntries.map { entry -> diaryEntryMapper.toDisplayEntry(entry) }
    }
}
