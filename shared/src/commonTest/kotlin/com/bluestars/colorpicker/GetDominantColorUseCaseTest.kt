package com.bluestars.colorpicker

import com.bluestars.colorpicker.model.BitmapImage
import com.bluestars.colorpicker.usecase.Color
import com.bluestars.colorpicker.usecase.GetDominantColorUseCase
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @author soohwan.ok
 */
class GetDominantColorUseCaseTest {
    private val red = 0xFF0000
    private val green = 0x00FF00
    private val blue = 0x0000FF
    @Test
    fun 특정_채널_색상_추출(){
        assertEquals(255,Color.red(red))
        assertEquals(255,Color.green(green))
        assertEquals(255,Color.blue(blue))
    }

    @Test
    fun 대표_색상_찾기() {
        val image = BitmapImage(
            intArrayOf(
                red, red, red, red, red,
                red, red, red, red, red,
                red, red, red, red, red,
                blue, blue, blue, blue, blue,
//                blue, blue, blue, blue, blue,
//                blue, blue, blue, blue, blue,
//                blue, blue, blue, blue, blue,
//                blue, blue, blue, blue, blue,
//                green, green, green, green, green,
//                green, green, green, green, green,
//                green, green, green, green, green,
//                green, green, green, green, green,
                green, green, green, green, green,
            ),
            5,
            5
        )
        val dominantColor = GetDominantColorUseCase(image)


        println("red = $red")
        println("green = $green")
        println("blue = $blue")

        println("dominantColor = $dominantColor(${Color.red(dominantColor)}, ${Color.green(dominantColor)} ${Color.blue(dominantColor)})")



    }

    fun Int.toHex():String {
        val hex = this.toString(16)
        return if(hex.length ==1){
            "0$hex"
        }else{
            hex
        }.uppercase()
    }

}