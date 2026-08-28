package com.rayliu.myphotodiary

import java.time.LocalDateTime
import java.util.UUID

data class DiaryEntry(
    val id: String = UUID.randomUUID().toString(),
    val photoResId: Int,
    val title: String,
    val note: String,
    val mood: String,
    val createdAt: LocalDateTime
)
