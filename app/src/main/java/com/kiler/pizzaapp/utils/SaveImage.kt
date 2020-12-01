package com.kiler.pizzaapp.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.os.SystemClock
import android.provider.MediaStore
import android.widget.Toast
import com.kiler.pizzaapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.File.separator
import java.io.FileOutputStream
import java.io.OutputStream




fun Bitmap.saveImage(context: Context): Uri? {
    if (android.os.Build.VERSION.SDK_INT >= 29) {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/pizzas")
        values.put(MediaStore.Images.Media.IS_PENDING, true)
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "img_${SystemClock.uptimeMillis()}")

        val uri: Uri? =
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            saveImageToStream(context,this, context.contentResolver.openOutputStream(uri))
            values.put(MediaStore.Images.Media.IS_PENDING, false)
            context.contentResolver.update(uri, values, null, null)

            return uri
        }
    } else {
        val directory =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + separator + "pizzas")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val fileName =  "img_${SystemClock.uptimeMillis()}"+ ".jpeg"
        val file = File(directory, fileName)
        saveImageToStream(context, this, FileOutputStream(file))
        if (file.absolutePath != null) {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.DATA, file.absolutePath)
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

            return Uri.fromFile(file)
        }
    }
    return null
}


fun saveImageToStream(context: Context, bitmap: Bitmap, outputStream: OutputStream?) {
    if (outputStream != null) {
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(context, context.getString(R.string.image_downloaded), Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

