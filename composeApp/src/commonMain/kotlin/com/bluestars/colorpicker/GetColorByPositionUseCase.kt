package com.bluestars.colorpicker

import com.bluestars.colorpicker.model.BSImage

object GetColorByPositionUseCase {

    operator fun invoke(
        image: BSImage,
        x : Int,
        y : Int
    ): Int {

        val rgbColor = getColorAtCoordinate(image, x, y)

        return rgbColor
    }
    private fun getColorAtCoordinate(image: BSImage, x: Int, y: Int): Int {
        if (x < 0 || x > image.width || y < 0 || y > image.height) {
            throw IllegalArgumentException("Invalid coordinates: ($x, $y), image :  (${image.width} , ${image.height}")
        }

        val index = y * image.width + x
        return image.buffer[index]
    }
}
