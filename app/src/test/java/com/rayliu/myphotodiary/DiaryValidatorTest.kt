package com.rayliu.myphotodiary

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DiaryValidatorTest {
    @Test
    fun bothBlank_returnsExistingValidationMessage() {
        // Given：建立一個 DiaryValidator，並準備已 normalized 的兩個空字串
        val validator = DiaryValidator()
        val normalizedTitle = ""
        val normalizedNote = ""

        // When：驗證標題與內容
        val result = validator.validate(normalizedTitle, normalizedNote)

        // Then：回傳既有的驗證錯誤訊息
        assertEquals("請至少填寫標題或內容", result)
    }

    @Test
    fun titleOnly_returnsNull() {
        // Given：建立一個 DiaryValidator，並準備只有標題的已 normalized 欄位
        val validator = DiaryValidator()
        val normalizedTitle = "旅行"
        val normalizedNote = ""

        // When：驗證標題與內容
        val result = validator.validate(normalizedTitle, normalizedNote)

        // Then：回傳 null，表示這組輸入有效
        assertNull(result)
    }

    @Test
    fun noteOnly_returnsNull() {
        // Given：建立一個 DiaryValidator，並準備只有內容的已 normalized 欄位
        val validator = DiaryValidator()
        val normalizedTitle = ""
        val normalizedNote = "今天下雨"

        // When：驗證標題與內容
        val result = validator.validate(normalizedTitle, normalizedNote)

        // Then：回傳 null，表示這組輸入有效
        assertNull(result)
    }
}
