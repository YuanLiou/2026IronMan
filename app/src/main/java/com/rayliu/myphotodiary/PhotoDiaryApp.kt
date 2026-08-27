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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.format.DateTimeFormatter

@Composable
fun PhotoDiaryApp(viewModel: PhotoDiaryViewModel = viewModel()) {
    if (viewModel.isAddingDiary) {
        DiaryForm(
            title = viewModel.title,
            note = viewModel.note,
            validationError = viewModel.validationError,
            onTitleChange = viewModel::updateTitle,
            onNoteChange = viewModel::updateNote,
            onSave = viewModel::saveDiary,
            onCancel = viewModel::cancelAddingDiary
        )
    } else {
        DiaryList(
            entries = viewModel.getDisplayEntries(),
            onAddClick = viewModel::startAddingDiary,
            onDeleteDiary = { displayIndex -> viewModel.deleteDiaryAt(displayIndex) },
            isNewestFirst = viewModel.isNewestFirst,
            onToggleSortOrder = { viewModel.toggleSortOrder() }
        )
    }
}

@Composable
private fun DiaryList(
    entries: List<DiaryEntry>,
    onAddClick: () -> Unit,
    onDeleteDiary: (Int) -> Unit,
    isNewestFirst: Boolean,
    onToggleSortOrder: () -> Unit
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
        Button(
            onClick = { onToggleSortOrder() },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isNewestFirst) {
                Text(text = "目前：最新在前（切換為原始順序）")
            } else {
                Text(text = "目前：原始順序（切換為最新在前）")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(entries) { displayIndex, diaryEntry ->
                DiaryCard(
                    entry = diaryEntry,
                    onDeleteClick = { onDeleteDiary(displayIndex) }
                )
            }
        }
    }
}

@Composable
private fun DiaryCard(
    entry: DiaryEntry,
    onDeleteClick: () -> Unit
) {
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
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { onDeleteClick() }) {
                    Text(text = "刪除")
                }
            }
        }
    }
}

@Composable
private fun DiaryForm(
    title: String,
    note: String,
    validationError: String?,
    onTitleChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: (String, String) -> Unit,
    onCancel: () -> Unit
) {
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
            onValueChange = onTitleChange,
            label = { Text(text = "標題") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = note,
            onValueChange = onNoteChange,
            label = { Text(text = "內容") },
            minLines = 4,
            modifier = Modifier.fillMaxWidth()
        )
        validationError?.let { errorMessage ->
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }
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
