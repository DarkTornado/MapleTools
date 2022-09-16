package com.darktornado.library

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ImageSaver {

    @Throws(IOException::class)
    fun saveImage(ctx: Context, bitmap: Bitmap, path: String, fileName: String): Uri? {
        if (Build.VERSION.SDK_INT >= 29) {
            return saveImageQ(ctx, bitmap, path, fileName)
        } else {
            val file = saveImageAsFile(bitmap, path, fileName)
            MediaScannerConnection.scanFile(ctx, arrayOf(file.absolutePath), null) { path1: String?, uri: Uri? -> }
            return Uri.fromFile(file)
        }
    }

    private fun saveImageQ(ctx: Context, bitmap: Bitmap, path: String, fileName: String): Uri? {
        if (Build.VERSION.SDK_INT < 29) return null //그냥 노란 줄 없애는 용도
        val resolver = ctx.contentResolver
        val imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.RELATIVE_PATH, path)
        values.put(MediaStore.Images.Media.IS_PENDING, 1)
        val uri = resolver.insert(imageCollection, values)
        val pfd = resolver.openFileDescriptor(uri!!, "w", null)
        val fos = FileOutputStream(pfd!!.fileDescriptor)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.close()
        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(uri, values, null, null)
        return uri
    }

    private fun saveImageLegacy(ctx: Context, bitmap: Bitmap, fileName: String): Uri { //어차피 안쓰는데 그냥 남겨둠
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val _path = MediaStore.Images.Media.insertImage(ctx.contentResolver, bitmap, fileName, null)
        return Uri.parse(_path)
    }

    private fun saveImageAsFile(bitmap: Bitmap, path: String, fileName: String?): File {
        var path = path
        var fileName = fileName
        fileName += ".png"
        path = Environment.getExternalStorageDirectory().absolutePath + "/" + path
        File(path).mkdirs()
        val file = File(path, fileName)
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.close()
        return file
    }
}