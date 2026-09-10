package com.rayliu.myphotodiary

interface DiaryEntryQuery {
    fun matches(entry: DiaryEntry): Boolean
}

class TitleContainsQuery(searchQuery: String) : DiaryEntryQuery {
    private val normalizedQuery = searchQuery.trim()

    override fun matches(entry: DiaryEntry): Boolean {
        return normalizedQuery.isEmpty() || entry.title.contains(normalizedQuery, ignoreCase = true)
    }
}

class MoodFilterQuery(private val selectedMood: Mood?) : DiaryEntryQuery {
    override fun matches(entry: DiaryEntry): Boolean {
        return selectedMood == null || entry.mood == selectedMood
    }
}

class AndDiaryEntryQuery(
    private val queries: List<DiaryEntryQuery>
) : DiaryEntryQuery {
    override fun matches(entry: DiaryEntry): Boolean {
        return queries.all { query -> query.matches(entry) }
    }
}
