package com.bluestars.colorpicker.usecase

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import coil.ImageLoader
import coil.executeBlocking
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.target.Target
import com.bluestars.colorpicker.model.BitmapImage

/**
 * @author soohwan.ok
 */
actual class ImageDecoder constructor(
    private val context: Context
) {
    actual suspend fun decode(target: Any): BitmapImage {
        when (target) {
            is Uri -> {
                val imageLoader = ImageLoader(context)
                val imageRequest = ImageRequest.Builder(context)
                    .data(target)
                    .size(120)
                    .allowHardware(false)
                    .target(object : Target {
                    }).build()
                val imageResult = imageLoader.execute(imageRequest)
                val bitmap = ((imageResult as? SuccessResult)?.drawable as? BitmapDrawable)?.bitmap
                    ?: throw IllegalStateException("디코딩 실패")
                val buffer: IntArray = IntArray(bitmap.width * bitmap.height)
                bitmap.getPixels(buffer, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
                return BitmapImage(buffer = buffer, width = bitmap.width, height = bitmap.height)
            }
            else -> {
                throw IllegalArgumentException("지원하지 않는 타입")
            }
        }
    }
}