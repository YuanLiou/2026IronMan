package com.rayliu.myphotodiary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras

class PhotoDiaryViewModelFactory(
    private val diaryStore: DiaryStore,
    private val displayEntriesProvider: DisplayEntriesProvider
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        if (modelClass.isAssignableFrom(PhotoDiaryViewModel::class.java)) {
            val application = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                ?: throw IllegalArgumentException("Application is required")
            @Suppress("UNCHECKED_CAST")
            return PhotoDiaryViewModel(
                application = application,
                diaryStore = diaryStore,
                displayEntriesProvider = displayEntriesProvider
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
