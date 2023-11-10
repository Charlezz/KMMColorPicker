package com.bluestars.colorpicker.usecase

import com.bluestars.colorpicker.model.BitmapImage

/**
 * @author soohwan.ok
 */
expect class ImageDecoder {
    suspend fun decode(target:Any):BitmapImage
}