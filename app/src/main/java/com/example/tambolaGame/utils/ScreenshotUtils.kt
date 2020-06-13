package com.example.tambolaGame.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import java.io.File
import java.io.FileOutputStream


class ScreenshotUtils {

    fun getScreenShot(view: View): Bitmap? {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    fun getMainDirectoryName(context: Context): File? {
        val mainDir = File(context.getExternalFilesDir(null), "Screenshots")

        if (!mainDir.exists()) {
            mainDir.mkdir()
        }
        return mainDir
    }

    fun store(bm: Bitmap, fileName: String?, context: Context): File? {
        val dir = File(getMainDirectoryName(context)!!.absolutePath)

        if (!dir.exists())
            dir.mkdirs()

        val file = File(getMainDirectoryName(context)!!.absolutePath, fileName!!)

        try {
            val fOut = FileOutputStream(file)
            bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut)
            fOut.flush()
            fOut.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file
    }
}