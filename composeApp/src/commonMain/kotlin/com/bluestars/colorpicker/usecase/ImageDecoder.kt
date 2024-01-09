package com.bluestars.colorpicker.usecase

import com.bluestars.colorpicker.model.BSImage

/**
 * @author soohwan.ok
 */
expect class ImageDecoder {
    suspend fun decode(target:Any,size :Int = 120): BSImage
}