package com.bluestars.colorpicker

import com.bluestars.colorpicker.model.BSImage
import com.bluestars.colorpicker.Color.blue
import com.bluestars.colorpicker.Color.green
import com.bluestars.colorpicker.Color.red
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


object GetDominantColorUseCase {

    private const val QUANTIZE_WORD_WIDTH = 5
    private const val QUANTIZE_WORD_MASK = ((1 shl QUANTIZE_WORD_WIDTH) - 1) shl 3

    /**
     * @param image 비트맵 이미지
     * @return 대표 색상
     */
    operator fun invoke(
        image: BSImage
    ): Int {

        val colorMap = mutableMapOf<Int, Int>()
        val width = image.width // 10
        val height = image.height //10
        for (h in 0 until height) {
            for (w in 0 until width) {
                val rgb = image.buffer[(width * h) + w]
                val quantizedArgb = quantizeFromRgb888(rgb)
                if (!isGray(quantizedArgb)) {
                    var counter = colorMap[quantizedArgb]
                    if (counter == null) {
                        counter = 0
                    }
                    colorMap[quantizedArgb] = ++counter
                } else {
                    println("무채색 Pass")
                }
            }
        }
        return getMostCommonColor(colorMap)
    }

    private fun getMostCommonColor(map: Map<Int, Int>): Int {
        return map.entries
            .sortedByDescending { entry -> entry.value }
            .first()
            .key

    }


    private fun isGray(pixel: Int): Boolean {
        val tolerance = 10
        val red = red(pixel)
        val green = green(pixel)
        val blue = blue(pixel)

        val minChannel = min(red, min(blue, green))
        val maxChannel = max(red, max(blue, green))

        val isGrey = abs(minChannel - maxChannel) < tolerance

        return isGrey

    }

    private fun quantizeFromRgb888(color: Int): Int {
        val red = (color shr 16) and QUANTIZE_WORD_MASK
        val green = (color shr 8) and QUANTIZE_WORD_MASK
        val blue = color and QUANTIZE_WORD_MASK
        return Color.rgb(red, green, blue)
    }

}