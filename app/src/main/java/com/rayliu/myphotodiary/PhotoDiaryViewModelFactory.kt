package com.rayliu.myphotodiary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras

class PhotoDiaryViewModelFactory(
    private val diaryStore: DiaryStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        if (modelClass.isAssignableFrom(PhotoDiaryViewModel::class.java)) {
            val application = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                ?: throw IllegalArgumentException("Application is required")
            @Suppress("UNCHECKED_CAST")
            return PhotoDiaryViewModel(application, diaryStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
