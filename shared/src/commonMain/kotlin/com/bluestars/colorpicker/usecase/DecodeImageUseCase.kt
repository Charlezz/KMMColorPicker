package com.bluestars.colorpicker.usecase

import com.bluestars.colorpicker.model.BitmapImage

/**
 * @author soohwan.ok
 */
expect interface DecodeImageUseCase {
    open fun decode(target:Any):BitmapImage
}