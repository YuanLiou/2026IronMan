package com.rayliu.myphotodiary

import java.time.LocalDateTime

data class DiaryEntry(
    val photoResId: Int,
    val title: String,
    val note: String,
    val mood: String,
    val createdAt: LocalDateTime
)
