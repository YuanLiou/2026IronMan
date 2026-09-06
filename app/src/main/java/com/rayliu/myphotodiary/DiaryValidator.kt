package com.rayliu.myphotodiary

class DiaryValidator {
    fun validate(normalizedTitle: String, normalizedNote: String): String? {
        if (normalizedTitle.isBlank() && normalizedNote.isBlank()) {
            return "請至少填寫標題或內容"
        }
        return null
    }
}
