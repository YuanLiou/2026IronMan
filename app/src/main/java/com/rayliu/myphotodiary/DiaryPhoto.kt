package com.rayliu.myphotodiary

sealed class DiaryPhoto {
    data class BuiltIn(val resourceId: Int) : DiaryPhoto()
    data class ExternalReference(val uriString: String) : DiaryPhoto()
}
