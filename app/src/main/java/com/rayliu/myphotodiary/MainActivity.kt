package com.rayliu.myphotodiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.rayliu.myphotodiary.ui.theme.MyPhotoDiaryTheme

class MainActivity : ComponentActivity() {
    private val photoDiaryViewModel: PhotoDiaryViewModel by viewModels {
        val diaryStore = LocalDiaryStorage(applicationContext)
        val displayEntriesProvider = DisplayEntriesUseCase()
        PhotoDiaryViewModelFactory(diaryStore, displayEntriesProvider)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyPhotoDiaryTheme {
                PhotoDiaryApp(viewModel = photoDiaryViewModel)
            }
        }
    }
}
