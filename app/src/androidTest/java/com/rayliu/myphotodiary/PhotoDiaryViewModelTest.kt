package com.rayliu.myphotodiary

import android.app.Application
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class PhotoDiaryViewModelTest {
    @Test
    fun bothBlank_doesNotAddDiary_andShowsValidationError() {
        // Given：建立日記狀態物件，並記錄目前已有幾篇日記
        val diaryStore = FakeDiaryStore()
        val viewModel = PhotoDiaryViewModel(application(), diaryStore)
        val initialEntryCount = viewModel.entries.size

        // When：從 public saveDiary 傳入兩個都只有空白的欄位
        viewModel.saveDiary("   ", "\n")

        // Then：確認沒有新增日記，並顯示既有的驗證錯誤
        assertEquals(initialEntryCount, viewModel.entries.size)
        assertEquals("請至少填寫標題或內容", viewModel.validationError)
        assertEquals(0, diaryStore.saveCallCount)
    }

    @Test
    fun titleOnly_trimsValues_andAddsDiary() {
        // Given：建立日記狀態物件，並記錄目前已有幾篇日記
        val diaryStore = FakeDiaryStore()
        val viewModel = PhotoDiaryViewModel(application(), diaryStore)
        val initialEntryCount = viewModel.entries.size

        // When：從 public saveDiary 傳入前後有空白的標題，內容只有空白
        viewModel.saveDiary("  旅行  ", "   ")

        // Then：確認新增日記，且保存 trim 後的標題與空內容
        assertEquals(initialEntryCount + 1, viewModel.entries.size)
        assertEquals("旅行", viewModel.entries.first().title)
        assertEquals("", viewModel.entries.first().note)
        assertNull(viewModel.validationError)
        assertEquals(1, diaryStore.saveCallCount)
        assertEquals(viewModel.entries, diaryStore.savedEntries)
    }

    @Test
    fun noteOnly_trimsValues_andAddsDiary() {
        // Given：建立日記狀態物件，並記錄目前已有幾篇日記
        val diaryStore = FakeDiaryStore()
        val viewModel = PhotoDiaryViewModel(application(), diaryStore)
        val initialEntryCount = viewModel.entries.size

        // When：從 public saveDiary 傳入只有空白的標題與前後有空白的內容
        viewModel.saveDiary(" \t", "  今天下雨  ")

        // Then：確認新增日記，且保存 trim 後的空標題與內容
        assertEquals(initialEntryCount + 1, viewModel.entries.size)
        assertEquals("", viewModel.entries.first().title)
        assertEquals("今天下雨", viewModel.entries.first().note)
        assertNull(viewModel.validationError)
        assertEquals(1, diaryStore.saveCallCount)
        assertEquals(viewModel.entries, diaryStore.savedEntries)
    }

    @Test
    fun selectedMood_addsDiary_andSendsMoodToStore() {
        // Given：建立日記狀態物件
        val diaryStore = FakeDiaryStore()
        val viewModel = PhotoDiaryViewModel(application(), diaryStore)

        // When：先選擇專注，再從 public saveDiary 新增日記
        viewModel.updateDraftMood(Mood.FOCUSED)
        viewModel.saveDiary("旅行", "今天很專注")

        // Then：確認新增日記與 fake storage 收到的日記都是專注
        assertEquals(Mood.FOCUSED, viewModel.entries.first().mood)
        assertEquals(Mood.FOCUSED, diaryStore.savedEntries?.first()?.mood)
    }

    @Test
    fun searchQuery_returnsTitleMatchingProjection_withoutChangingSourceEntries() {
        // Given：建立兩篇具有固定 ID 與不同標題的來源日記
        val sourceEntries = listOf(
            testEntry(id = "entry-1", title = "晨光散步"),
            testEntry(id = "entry-2", title = "夜色閱讀")
        )
        val diaryStore = FakeDiaryStore(initialEntries = sourceEntries)
        val viewModel = PhotoDiaryViewModel(application(), diaryStore)

        // When：更新搜尋文字，再取得目前要顯示的日記
        viewModel.updateSearchQuery("  晨光  ")
        val displayEntries = viewModel.getDisplayEntries()

        // Then：只顯示符合標題的 ID，來源清單仍維持原本內容
        assertEquals(listOf("entry-1"), displayEntries.map { diaryEntry -> diaryEntry.id })
        assertEquals(sourceEntries, viewModel.entries)
    }

    @Test
    fun emptyStore_keepsEntriesEmpty() {
        // Given：建立一個 load 回傳既有空清單的 fake storage
        val diaryStore = FakeDiaryStore(initialEntries = emptyList())

        // When：把 fake storage 傳入 ViewModel constructor
        val viewModel = PhotoDiaryViewModel(application(), diaryStore)

        // Then：確認 ViewModel 保留空清單，不改用 sample entries
        assertEquals(0, viewModel.entries.size)
    }

    private fun testEntry(id: String, title: String): DiaryEntry {
        return DiaryEntry(
            id = id,
            photo = DiaryPhoto.BuiltIn(R.drawable.diary_default),
            title = title,
            note = "測試內容",
            mood = Mood.CALM,
            createdAt = LocalDateTime.parse("2026-01-01T12:00:00")
        )
    }

    private fun application(): Application {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        return context.applicationContext as Application
    }
}

private class FakeDiaryStore(
    private val initialEntries: List<DiaryEntry>? = null
) : DiaryStore {
    var savedEntries: List<DiaryEntry>? = null
        private set

    var saveCallCount: Int = 0
        private set

    override fun load(): List<DiaryEntry>? {
        return initialEntries
    }

    override fun save(entries: List<DiaryEntry>) {
        savedEntries = entries
        saveCallCount += 1
    }
}
