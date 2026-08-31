package com.rayliu.myphotodiary

sealed class DiaryPhoto {
    data class BuiltIn(val resourceId: Int) : DiaryPhoto()
}
