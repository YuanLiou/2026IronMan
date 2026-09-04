package com.rayliu.myphotodiary

interface DiaryStore {
    fun load(): List<DiaryEntry>?

    fun save(entries: List<DiaryEntry>)
}
