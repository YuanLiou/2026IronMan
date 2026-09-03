package com.rayliu.myphotodiary

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.FileNotFoundException

sealed class PhotoReadResult {
    data class Available(val bitmap: Bitmap) : PhotoReadResult()
    class MissingFile : PhotoReadResult()
    class Unavailable : PhotoReadResult()
}

class LocalPhotoStore(context: Context) {
    private val photoDirectory = context.filesDir
    private val contentResolver = context.contentResolver

    fun import(
        externalReference: DiaryPhoto.ExternalReference,
        diaryId: String
    ): DiaryPhoto.OwnedFile {
        val fileName = "$diaryId.photo"
        val localFile = photoDirectory.resolve(fileName)
        val inputStream = contentResolver.openInputStream(Uri.parse(externalReference.uriString))
            ?: throw FileNotFoundException(externalReference.uriString)
        val outputStream = localFile.outputStream()
        try {
            inputStream.copyTo(outputStream)
        } finally {
            inputStream.close()
            outputStream.close()
        }
        return DiaryPhoto.OwnedFile(fileName)
    }

    fun open(ownedFile: DiaryPhoto.OwnedFile): PhotoReadResult {
        val inputStream = try {
            photoDirectory.resolve(ownedFile.fileName).inputStream()
        } catch (_: FileNotFoundException) {
            return PhotoReadResult.MissingFile()
        }
        val bitmap = try {
            BitmapFactory.decodeStream(inputStream)
        } finally {
            inputStream.close()
        }
        return if (bitmap == null) {
            PhotoReadResult.Unavailable()
        } else {
            PhotoReadResult.Available(bitmap)
        }
    }
}
