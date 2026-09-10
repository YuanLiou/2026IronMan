package com.rayliu.myphotodiary

class DiaryEntryMapper {
    fun toDisplayEntry(entry: DiaryEntry): DiaryEntry {
        return entry.copy(
            id = entry.id,
            photo = entry.photo,
            title = entry.title,
            note = entry.note,
            mood = entry.mood,
            createdAt = entry.createdAt
        )
    }
}
