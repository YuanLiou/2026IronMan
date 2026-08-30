package com.rayliu.myphotodiary

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.time.LocalDateTime

class LocalDiaryStorage(context: Context) {
    private val diaryFile = File(context.filesDir, "diaries.json")

    fun save(entries: List<DiaryEntry>) {
        val jsonArray = JSONArray()

        for (entry in entries) {
            val jsonObject = JSONObject()
            jsonObject.put("id", entry.id)
            jsonObject.put("photoResId", entry.photoResId)
            jsonObject.put("title", entry.title)
            jsonObject.put("note", entry.note)
            jsonObject.put("mood", entry.mood)
            jsonObject.put("createdAt", entry.createdAt.toString())
            jsonArray.put(jsonObject)
        }

        diaryFile.writeText(jsonArray.toString())
    }

    fun load(): List<DiaryEntry>? {
        if (!diaryFile.exists()) {
            return null
        }

        val jsonArray = JSONArray(diaryFile.readText())
        val entries = mutableListOf<DiaryEntry>()

        for (index in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(index)
            val entry = DiaryEntry(
                id = jsonObject.getString("id"),
                photoResId = jsonObject.getInt("photoResId"),
                title = jsonObject.getString("title"),
                note = jsonObject.getString("note"),
                mood = jsonObject.getString("mood"),
                createdAt = LocalDateTime.parse(jsonObject.getString("createdAt"))
            )
            entries.add(entry)
        }

        return entries
    }
}
