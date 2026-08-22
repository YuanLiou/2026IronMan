package com.rayliu.myphotodiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.rayliu.myphotodiary.ui.theme.MyPhotoDiaryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyPhotoDiaryTheme {
                PhotoDiaryApp()
            }
        }
    }
}
