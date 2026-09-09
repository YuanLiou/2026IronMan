package com.rayliu.myphotodiary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import java.time.LocalDateTime

class PhotoDiaryViewModel(
    application: Application,
    private val diaryStore: DiaryStore
) : AndroidViewModel(application) {
    private val photoStore = LocalPhotoStore(application.applicationContext)
    private val diaryValidator = DiaryValidator()

    var entries by mutableStateOf(loadInitialEntries())

    var isAddingDiary by mutableStateOf(false)
        private set

    var title by mutableStateOf("")
        private set

    var note by mutableStateOf("")
        private set

    var draftPhoto by mutableStateOf<DiaryPhoto>(
        DiaryPhoto.BuiltIn(R.drawable.diary_default)
    )
        private set

    var draftMood by mutableStateOf(Mood.CALM)
        private set

    var validationError by mutableStateOf<String?>(null)
        private set

    var isNewestFirst by mutableStateOf(false)
        private set

    var searchQuery by mutableStateOf("")
        private set

    var selectedMoodFilter by mutableStateOf<Mood?>(null)
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

    fun updateDraftMood(mood: Mood) {
        draftMood = mood
    }

    fun selectExternalPhoto(uriString: String) {
        if (uriString.isNotBlank()) {
            draftPhoto = DiaryPhoto.ExternalReference(uriString)
        }
    }

    fun saveDiary(title: String, note: String) {
        val normalizedTitle = title.trim()
        val normalizedNote = note.trim()
        val validationMessage = diaryValidator.validate(normalizedTitle, normalizedNote)

        if (validationMessage != null) {
            validationError = validationMessage
        } else {
            val newEntry = DiaryEntry(
                photo = draftPhoto,
                title = normalizedTitle,
                note = normalizedNote,
                mood = draftMood,
                createdAt = LocalDateTime.now()
            )
            val entryToSave: DiaryEntry
            if (newEntry.photo is DiaryPhoto.ExternalReference) {
                val externalReference = newEntry.photo as DiaryPhoto.ExternalReference
                val ownedFile = try {
                    photoStore.import(externalReference, newEntry.id)
                } catch (_: SecurityException) {
                    return
                } catch (_: java.io.FileNotFoundException) {
                    return
                }
                entryToSave = newEntry.copy(photo = ownedFile)
            } else {
                entryToSave = newEntry
            }
            entries = listOf(entryToSave) + entries
            diaryStore.save(entries)
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
            diaryStore.save(entries)
        }
    }

    fun toggleSortOrder() {
        isNewestFirst = !isNewestFirst
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    fun updateMoodFilter(mood: Mood?) {
        selectedMoodFilter = mood
    }

    fun getDisplayEntries(): List<DiaryEntry> {
        val normalizedQuery = searchQuery.trim()
        val matchingEntries = if (normalizedQuery.isEmpty()) {
            entries
        } else {
            entries.filter { diaryEntry ->
                diaryEntry.title.contains(normalizedQuery, ignoreCase = true)
            }
        }
        val moodFilter = selectedMoodFilter
        val moodMatchingEntries = if (moodFilter == null) {
            matchingEntries
        } else {
            matchingEntries.filter { diaryEntry ->
                diaryEntry.mood == moodFilter
            }
        }

        if (isNewestFirst) {
            return moodMatchingEntries.sortedByDescending { diaryEntry -> diaryEntry.createdAt }
        }
        return moodMatchingEntries
    }

    private fun clearForm() {
        title = ""
        note = ""
        validationError = null
        draftPhoto = DiaryPhoto.BuiltIn(R.drawable.diary_default)
        draftMood = Mood.CALM
    }

    private fun loadInitialEntries(): List<DiaryEntry> {
        val loadedEntries = diaryStore.load()
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
        mood = Mood.IMPRESSED,
        createdAt = LocalDateTime.parse("2026-03-10T17:52:37")
    ),
    DiaryEntry(
        photo = DiaryPhoto.BuiltIn(R.drawable.diary_example002),
        title = "夜裡的共享空間",
        note = "雨夜裡留下來整理想法，空間安靜得剛剛好。",
        mood = Mood.FOCUSED,
        createdAt = LocalDateTime.parse("2026-04-04T21:04:51")
    ),
    DiaryEntry(
        photo = DiaryPhoto.BuiltIn(R.drawable.diary_example003),
        title = "山海之間的晴天",
        note = "站在高處看著海岸線，雲和海把心情慢慢拉開。",
        mood = Mood.CALM,
        createdAt = LocalDateTime.parse("2026-01-04T11:23:44")
    )
)
