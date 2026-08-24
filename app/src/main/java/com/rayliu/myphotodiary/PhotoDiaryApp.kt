package com.rayliu.myphotodiary

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun PhotoDiaryApp() {
    var entries by remember { mutableStateOf(sampleEntries()) }
    var isAddingDiary by remember { mutableStateOf(false) }

    if (isAddingDiary) {
        DiaryForm(
            onSave = { title, note ->
                val newEntry = DiaryEntry(
                    photoResId = R.drawable.diary_default,
                    title = title,
                    note = note,
                    mood = "平靜",
                    createdAt = LocalDateTime.now()
                )
                entries = listOf(newEntry) + entries
                isAddingDiary = false
            },
            onCancel = { isAddingDiary = false }
        )
    } else {
        DiaryList(
            entries = entries,
            onAddClick = { isAddingDiary = true }
        )
    }
}

@Composable
private fun DiaryList(
    entries: List<DiaryEntry>,
    onAddClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        Text(
            text = "照片日記",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = { onAddClick() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "新增日記")
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(entries) { entry ->
                DiaryCard(entry = entry)
            }
        }
    }
}

@Composable
private fun DiaryCard(entry: DiaryEntry) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column {
            Image(
                painter = painterResource(id = entry.photoResId),
                contentDescription = entry.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = entry.note)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(text = "心情：${entry.mood}")
                    Text(text = "建立時間：${entry.createdAt.format(displayDateTimeFormatter)}")
                }
            }
        }
    }
}

@Composable
private fun DiaryForm(
    onSave: (String, String) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "新增日記",
            style = MaterialTheme.typography.headlineMedium
        )
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(text = "標題") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text(text = "內容") },
            minLines = 4,
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { onSave(title, note) }) {
                Text(text = "儲存")
            }
            Button(onClick = onCancel) {
                Text(text = "取消")
            }
        }
    }
}

private val displayDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

private fun sampleEntries(): List<DiaryEntry> = listOf(
    DiaryEntry(
        photoResId = R.drawable.diary_example001,
        title = "城市縮影裡的警醒",
        note = "看見熟悉的城市被做成防災模型，才發現準備不能只停在想像。",
        mood = "震撼",
        createdAt = LocalDateTime.parse("2026-03-10T17:52:37")
    ),
    DiaryEntry(
        photoResId = R.drawable.diary_example002,
        title = "夜裡的共享空間",
        note = "雨夜裡留下來整理想法，空間安靜得剛剛好。",
        mood = "專注",
        createdAt = LocalDateTime.parse("2026-04-04T21:04:51")
    ),
    DiaryEntry(
        photoResId = R.drawable.diary_example003,
        title = "山海之間的晴天",
        note = "站在高處看著海岸線，雲和海把心情慢慢拉開。",
        mood = "平靜",
        createdAt = LocalDateTime.parse("2026-01-04T11:23:44")
    )
)
