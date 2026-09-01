package com.rayliu.myphotodiary

import android.content.Context
import android.net.Uri
import java.io.FileNotFoundException
import java.io.InputStream

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

    fun open(ownedFile: DiaryPhoto.OwnedFile): InputStream {
        return photoDirectory.resolve(ownedFile.fileName).inputStream()
    }
}
