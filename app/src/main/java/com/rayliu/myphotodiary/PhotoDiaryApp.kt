package com.rayliu.myphotodiary

import android.net.Uri
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import java.io.FileNotFoundException
import java.time.format.DateTimeFormatter

@Composable
fun PhotoDiaryApp(viewModel: PhotoDiaryViewModel) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { selectedUri ->
            if (selectedUri != null) {
                viewModel.selectExternalPhoto(selectedUri.toString())
            }
        }
    )

    if (viewModel.isAddingDiary) {
        DiaryForm(
            title = viewModel.title,
            note = viewModel.note,
            photo = viewModel.draftPhoto,
            mood = viewModel.draftMood,
            validationError = viewModel.validationError,
            onTitleChange = viewModel::updateTitle,
            onNoteChange = viewModel::updateNote,
            onMoodChange = { selectedMood -> viewModel.updateDraftMood(selectedMood) },
            onSave = viewModel::saveDiary,
            onCancel = viewModel::cancelAddingDiary,
            onChoosePhoto = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        )
    } else {
        DiaryList(
            entries = viewModel.getDisplayEntries(),
            searchQuery = viewModel.searchQuery,
            onSearchQueryChange = { query -> viewModel.updateSearchQuery(query) },
            selectedMoodFilter = viewModel.selectedMoodFilter,
            onMoodFilterChange = { mood -> viewModel.updateMoodFilter(mood) },
            onAddClick = viewModel::startAddingDiary,
            onDeleteDiary = { id -> viewModel.deleteDiary(id) },
            onMoodChange = { diaryId, selectedMood ->
                viewModel.entries = viewModel.entries.map { diaryEntry ->
                    if (diaryEntry.id == diaryId) {
                        diaryEntry.copy(mood = selectedMood)
                    } else {
                        diaryEntry
                    }
                }
            },
            isNewestFirst = viewModel.isNewestFirst,
            onToggleSortOrder = { viewModel.toggleSortOrder() }
        )
    }
}

@Composable
private fun DiaryList(
    entries: List<DiaryEntry>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedMoodFilter: Mood?,
    onMoodFilterChange: (Mood?) -> Unit,
    onAddClick: () -> Unit,
    onDeleteDiary: (String) -> Unit,
    onMoodChange: (String, Mood) -> Unit,
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
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { query -> onSearchQueryChange(query) },
            label = { Text(text = "搜尋標題") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        MoodFilterDropdown(
            selectedMoodFilter = selectedMoodFilter,
            onMoodFilterChange = { mood -> onMoodFilterChange(mood) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(entries) { diaryEntry ->
                DiaryCard(
                    entry = diaryEntry,
                    onDeleteClick = { onDeleteDiary(diaryEntry.id) },
                    onMoodChange = { selectedMood ->
                        onMoodChange(diaryEntry.id, selectedMood)
                    }
                )
            }
        }
    }
}

@Composable
private fun MoodFilterDropdown(
    selectedMoodFilter: Mood?,
    onMoodFilterChange: (Mood?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedMoodLabel = if (selectedMoodFilter == null) {
        "全部"
    } else {
        selectedMoodFilter.displayName
    }

    Box {
        Button(
            onClick = { expanded = true }
        ) {
            Text(text = "篩選心情：$selectedMoodLabel")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(text = "全部") },
                onClick = {
                    expanded = false
                    onMoodFilterChange(null)
                }
            )
            for (moodOption in Mood.entries) {
                DropdownMenuItem(
                    text = { Text(text = moodOption.displayName) },
                    onClick = {
                        expanded = false
                        onMoodFilterChange(moodOption)
                    }
                )
            }
        }
    }
}

@Composable
private fun DiaryCard(
    entry: DiaryEntry,
    onDeleteClick: () -> Unit,
    onMoodChange: (Mood) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column {
            DiaryPhotoImage(
                photo = entry.photo,
                contentDescription = entry.title,
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
                MoodDropdown(
                    selectedMood = entry.mood,
                    onMoodSelected = onMoodChange
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "建立時間：${entry.createdAt.format(displayDateTimeFormatter)}")
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
    photo: DiaryPhoto,
    mood: Mood,
    validationError: String?,
    onTitleChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onMoodChange: (Mood) -> Unit,
    onSave: (String, String) -> Unit,
    onCancel: () -> Unit,
    onChoosePhoto: () -> Unit
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
        DiaryPhotoImage(
            photo = photo,
            contentDescription = "目前選取的照片",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        )
        Button(onClick = { onChoosePhoto() }) {
            Text(text = "選擇照片")
        }
        MoodDropdown(
            selectedMood = mood,
            onMoodSelected = onMoodChange
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

@Composable
private fun MoodDropdown(
    selectedMood: Mood,
    onMoodSelected: (Mood) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(
            onClick = { expanded = true }
        ) {
            Text(text = "心情：${selectedMood.displayName}")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            for (moodOption in Mood.entries) {
                DropdownMenuItem(
                    text = { Text(text = moodOption.displayName) },
                    onClick = {
                        expanded = false
                        onMoodSelected(moodOption)
                    }
                )
            }
        }
    }
}

@Composable
private fun DiaryPhotoImage(
    photo: DiaryPhoto,
    contentDescription: String,
    modifier: Modifier
) {
    when (photo) {
        is DiaryPhoto.BuiltIn -> {
            Image(
                painter = painterResource(id = photo.resourceId),
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = modifier
            )
        }
        is DiaryPhoto.ExternalReference -> {
            ExternalPhotoImage(
                uriString = photo.uriString,
                contentDescription = contentDescription,
                modifier = modifier
            )
        }
        is DiaryPhoto.OwnedFile -> {
            OwnedPhotoImage(
                fileName = photo.fileName,
                contentDescription = contentDescription,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun OwnedPhotoImage(
    fileName: String,
    contentDescription: String,
    modifier: Modifier
) {
    val context = LocalContext.current
    val photoStore = LocalPhotoStore(context)
    val photoReadResult = photoStore.open(DiaryPhoto.OwnedFile(fileName))
    when (photoReadResult) {
        is PhotoReadResult.Available -> {
            AndroidView(
                factory = { imageContext ->
                    ImageView(imageContext)
                },
                update = { imageView ->
                    imageView.contentDescription = contentDescription
                    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                    imageView.setImageBitmap(photoReadResult.bitmap)
                },
                modifier = modifier.clipToBounds()
            )
        }
        is PhotoReadResult.MissingFile -> {
            Box(
                modifier = modifier.clipToBounds(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "照片檔案已遺失")
            }
        }
        is PhotoReadResult.Unavailable -> {
            Box(
                modifier = modifier.clipToBounds(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "照片暫時無法讀取")
            }
        }
    }
}

@Composable
private fun ExternalPhotoImage(
    uriString: String,
    contentDescription: String,
    modifier: Modifier
) {
    AndroidView(
        factory = { context ->
            ImageView(context)
        },
        update = { imageView ->
            imageView.contentDescription = contentDescription
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            // URI 只是一條外部路徑，先嘗試把它讀成 Bitmap。
            val bitmap = try {
                imageView.context.contentResolver
                    .openInputStream(Uri.parse(uriString))
                    ?.use(BitmapFactory::decodeStream)
            } catch (_: SecurityException) {
                null
            } catch (_: FileNotFoundException) {
                null
            }
            // URI 失效時回傳 null，讓畫面留白而不讓 App crash；不持久化權限、不複製檔案，也不補預設圖。
            imageView.setImageBitmap(bitmap)
        },
        modifier = modifier.clipToBounds()
    )
}

private val displayDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
