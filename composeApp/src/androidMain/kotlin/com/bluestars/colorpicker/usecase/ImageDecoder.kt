package com.bluestars.colorpicker.usecase

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.request.SuccessResult
import coil.target.Target
import com.bluestars.colorpicker.model.BSImage

actual class ImageDecoder constructor(
    private val context: Context
) {
    actual suspend fun decode(target: Any,size: Int): BSImage {
        val imageLoader = ImageLoader(context)
        val imageRequest = ImageRequest.Builder(context)
            .data(target)
            .size(size)
            .allowHardware(false)
            .target(object : Target {
            }).build()
        val imageResult:ImageResult = imageLoader.execute(imageRequest)
        val bitmap = ((imageResult as? SuccessResult)?.drawable as? BitmapDrawable)?.bitmap
            ?: throw IllegalStateException("디코딩 실패")
        val buffer: IntArray = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(buffer, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        return BSImage(buffer = buffer, width = bitmap.width, height = bitmap.height)
    }
}