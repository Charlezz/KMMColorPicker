package com.bluestars.colorpicker.usecase

/**
 * @author soohwan.ok
 */
object Color {
    fun red(color: Int): Int {
        return (color shr 16) and 0xFF
    }

    fun green(color: Int): Int {
        return (color shr 8) and 0xFF
    }

    fun blue(color: Int): Int {
        return color and 0xFF
    }

    fun rgb(red:Int, green:Int, blue:Int):Int{
        return (0xFF000000u or (red.toUInt() shl 16) or (green.toUInt() shl 8) or blue.toUInt()).toInt()
    }

}