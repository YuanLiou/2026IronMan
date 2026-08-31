package com.rayliu.myphotodiary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import java.time.LocalDateTime

class PhotoDiaryViewModel(application: Application) : AndroidViewModel(application) {
    private val storage = LocalDiaryStorage(application.applicationContext)

    var entries by mutableStateOf(loadInitialEntries())

    var isAddingDiary by mutableStateOf(false)
        private set

    var title by mutableStateOf("")
        private set

    var note by mutableStateOf("")
        private set

    var validationError by mutableStateOf<String?>(null)
        private set

    var isNewestFirst by mutableStateOf(false)
        private set

    fun startAddingDiary() {
        clearForm()
        isAddingDiary = true
    }

    fun updateTitle(title: String) {
        this.title = title
    }

    fun updateNote(note: String) {
        this.note = note
    }

    fun saveDiary(title: String, note: String) {
        if (title.isBlank() && note.isBlank()) {
            validationError = "請至少填寫標題或內容"
        } else {
            val newEntry = DiaryEntry(
                photo = DiaryPhoto.BuiltIn(R.drawable.diary_default),
                title = title,
                note = note,
                mood = "平靜",
                createdAt = LocalDateTime.now()
            )
            entries = listOf(newEntry) + entries
            storage.save(entries)
            clearForm()
            isAddingDiary = false
        }
    }

    fun cancelAddingDiary() {
        clearForm()
        isAddingDiary = false
    }

    fun deleteDiary(id: String) {
        val updatedEntries = entries.toMutableList()
        val wasDeleted = updatedEntries.removeIf { diaryEntry -> diaryEntry.id == id }
        entries = updatedEntries
        if (wasDeleted) {
            storage.save(entries)
        }
    }

    fun toggleSortOrder() {
        isNewestFirst = !isNewestFirst
    }

    fun getDisplayEntries(): List<DiaryEntry> {
        if (isNewestFirst) {
            return entries.sortedByDescending { diaryEntry -> diaryEntry.createdAt }
        }
        return entries
    }

    private fun clearForm() {
        title = ""
        note = ""
        validationError = null
    }

    private fun loadInitialEntries(): List<DiaryEntry> {
        val loadedEntries = storage.load()
        if (loadedEntries == null) {
            return sampleEntries()
        }
        return loadedEntries
    }
}

private fun sampleEntries(): List<DiaryEntry> = listOf(
    DiaryEntry(
        photo = DiaryPhoto.BuiltIn(R.drawable.diary_example001),
        title = "城市縮影裡的警醒",
        note = "看見熟悉的城市被做成防災模型，才發現準備不能只停在想像。",
        mood = "震撼",
        createdAt = LocalDateTime.parse("2026-03-10T17:52:37")
    ),
    DiaryEntry(
        photo = DiaryPhoto.BuiltIn(R.drawable.diary_example002),
        title = "夜裡的共享空間",
        note = "雨夜裡留下來整理想法，空間安靜得剛剛好。",
        mood = "專注",
        createdAt = LocalDateTime.parse("2026-04-04T21:04:51")
    ),
    DiaryEntry(
        photo = DiaryPhoto.BuiltIn(R.drawable.diary_example003),
        title = "山海之間的晴天",
        note = "站在高處看著海岸線，雲和海把心情慢慢拉開。",
        mood = "平靜",
        createdAt = LocalDateTime.parse("2026-01-04T11:23:44")
    )
)
