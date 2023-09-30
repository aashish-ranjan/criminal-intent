package com.example.criminalintent.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlin.math.roundToInt

object PictureUtils {

    fun getScaledBitmap(path: String, destinationWidth: Int, destinationHeight: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)

        val srcWidth = options.outWidth.toFloat()
        val srcHeight = options.outHeight.toFloat()
        val sampleSize = if (srcWidth <= destinationWidth && srcHeight <= destinationHeight) {
            1
        } else {
            val widthScale = srcWidth/destinationWidth
            val heightScale = srcHeight/destinationHeight
            minOf(widthScale, heightScale).roundToInt()
        }
        return BitmapFactory.decodeFile(path, BitmapFactory.Options().apply {
            inSampleSize = sampleSize
        })
    }
}