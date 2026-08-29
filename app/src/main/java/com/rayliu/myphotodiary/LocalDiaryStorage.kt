package com.rayliu.myphotodiary

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

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
}
