package com.rayliu.myphotodiary

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.time.LocalDateTime

class LocalDiaryStorage(context: Context) : DiaryStore {
    private val diaryFile = File(context.filesDir, "diaries.json")

    override fun save(entries: List<DiaryEntry>) {
        val jsonArray = JSONArray()

        for (entry in entries) {
            val jsonObject = JSONObject()
            jsonObject.put("id", entry.id)
            val photoObject = JSONObject()
            when (val photo = entry.photo) {
                is DiaryPhoto.BuiltIn -> {
                    photoObject.put("type", "builtIn")
                    photoObject.put("resourceId", photo.resourceId)
                }
                is DiaryPhoto.ExternalReference -> {
                    photoObject.put("type", "externalReference")
                    photoObject.put("uriString", photo.uriString)
                }
                is DiaryPhoto.OwnedFile -> {
                    photoObject.put("type", "ownedFile")
                    photoObject.put("fileName", photo.fileName)
                }
            }
            jsonObject.put("photo", photoObject)
            jsonObject.put("title", entry.title)
            jsonObject.put("note", entry.note)
            jsonObject.put("mood", entry.mood.displayName)
            jsonObject.put("createdAt", entry.createdAt.toString())
            jsonArray.put(jsonObject)
        }

        diaryFile.writeText(jsonArray.toString())
    }

    override fun load(): List<DiaryEntry>? {
        if (!diaryFile.exists()) {
            return null
        }

        val jsonArray = JSONArray(diaryFile.readText())
        val entries = mutableListOf<DiaryEntry>()

        for (index in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(index)
            val photo: DiaryPhoto
            if (jsonObject.has("photoResId")) {
                photo = DiaryPhoto.BuiltIn(jsonObject.getInt("photoResId"))
            } else {
                val photoObject = jsonObject.getJSONObject("photo")
                val photoType = photoObject.getString("type")
                if (photoType == "builtIn") {
                    photo = DiaryPhoto.BuiltIn(photoObject.getInt("resourceId"))
                } else if (photoType == "externalReference") {
                    photo = DiaryPhoto.ExternalReference(photoObject.getString("uriString"))
                } else if (photoType == "ownedFile") {
                    photo = DiaryPhoto.OwnedFile(photoObject.getString("fileName"))
                } else {
                    throw IllegalArgumentException("Unsupported diary photo type: $photoType")
                }
            }
            val moodDisplayName = jsonObject.getString("mood")
            val mood = when (moodDisplayName) {
                "平靜" -> Mood.CALM
                "專注" -> Mood.FOCUSED
                "震撼" -> Mood.IMPRESSED
                else -> throw IllegalArgumentException("Unsupported diary mood: $moodDisplayName")
            }
            val entry = DiaryEntry(
                id = jsonObject.getString("id"),
                photo = photo,
                title = jsonObject.getString("title"),
                note = jsonObject.getString("note"),
                mood = mood,
                createdAt = LocalDateTime.parse(jsonObject.getString("createdAt"))
            )
            entries.add(entry)
        }

        return entries
    }
}
