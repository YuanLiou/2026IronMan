package com.rayliu.myphotodiary

import android.app.Application
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PhotoDiaryViewModelTest {
    private lateinit var diaryFile: File

    @Before
    fun clearDiaryFileBeforeTest() {
        diaryFile = diaryFile()
        diaryFile.delete()
    }

    @After
    fun clearDiaryFileAfterTest() {
        diaryFile.delete()
    }

    @Test
    fun bothBlank_doesNotAddDiary_andShowsValidationError() {
        // Given：建立日記狀態物件，並記錄目前已有幾篇日記
        val viewModel = PhotoDiaryViewModel(application())
        val initialEntryCount = viewModel.entries.size

        // When：從 public saveDiary 傳入兩個都只有空白的欄位
        viewModel.saveDiary("   ", "\n")

        // Then：確認沒有新增日記，並顯示既有的驗證錯誤
        assertEquals(initialEntryCount, viewModel.entries.size)
        assertEquals("請至少填寫標題或內容", viewModel.validationError)
    }

    @Test
    fun titleOnly_trimsValues_andAddsDiary() {
        // Given：建立日記狀態物件，並記錄目前已有幾篇日記
        val viewModel = PhotoDiaryViewModel(application())
        val initialEntryCount = viewModel.entries.size

        // When：從 public saveDiary 傳入前後有空白的標題，內容只有空白
        viewModel.saveDiary("  旅行  ", "   ")

        // Then：確認新增日記，且保存 trim 後的標題與空內容
        assertEquals(initialEntryCount + 1, viewModel.entries.size)
        assertEquals("旅行", viewModel.entries.first().title)
        assertEquals("", viewModel.entries.first().note)
        assertNull(viewModel.validationError)
    }

    @Test
    fun noteOnly_trimsValues_andAddsDiary() {
        // Given：建立日記狀態物件，並記錄目前已有幾篇日記
        val viewModel = PhotoDiaryViewModel(application())
        val initialEntryCount = viewModel.entries.size

        // When：從 public saveDiary 傳入只有空白的標題與前後有空白的內容
        viewModel.saveDiary(" \t", "  今天下雨  ")

        // Then：確認新增日記，且保存 trim 後的空標題與內容
        assertEquals(initialEntryCount + 1, viewModel.entries.size)
        assertEquals("", viewModel.entries.first().title)
        assertEquals("今天下雨", viewModel.entries.first().note)
        assertNull(viewModel.validationError)
    }

    private fun application(): Application {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        return context.applicationContext as Application
    }

    private fun diaryFile(): File {
        return File(application().filesDir, "diaries.json")
    }
}
