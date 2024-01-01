package com.bluestars.colorpicker.component

import android.graphics.Bitmap
import android.media.ThumbnailUtils
import androidx.compose.ui.unit.IntSize

object BitmapCalculator {

    /**
     * Scale the source with maintaining the source's aspect ratio
     * so that both dimensions (width and height) of the source will be equal to or less than the
     * corresponding dimension of the target size.
     */
    internal fun scaleBitmap(bitmap: Bitmap, targetSize: IntSize): Bitmap {
        return Bitmap.createScaledBitmap(
            bitmap,
            targetSize.width,
            targetSize.height,
            false,
        )
    }
    /**
     * Crop ths source the corresponding dimension of the target size.
     * so that if the dimensions (width and height) source is bigger than the target size,
     * it will be cut off from the center.
     */
    internal fun cropBitmap(bitmap: Bitmap, targetSize: IntSize): Bitmap {
        return ThumbnailUtils.extractThumbnail(bitmap, targetSize.width, targetSize.height)
    }
}
