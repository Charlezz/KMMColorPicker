package com.bluestars.colorpicker

import com.bluestars.colorpicker.model.BSImage

object GetRandomColorUseCase {

    operator fun invoke(
        image: BSImage
    ): Int {
        val randomX = (0 until image.width).random()
        val randomY = (0 until image.height).random()
        val rgbColor = getColorAtCoordinate(image, randomX, randomY)

        return rgbColor
    }
    private fun getColorAtCoordinate(image: BSImage, x: Int, y: Int): Int {
        if (x < 0 || x >= image.width || y < 0 || y >= image.height) {
            throw IllegalArgumentException("Invalid coordinates: ($x, $y)")
        }

        val index = y * image.width + x
        return image.buffer[index]
    }
}
