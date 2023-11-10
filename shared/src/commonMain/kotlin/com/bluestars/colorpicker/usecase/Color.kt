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
    fun argb(alpha: Int, red: Int, green: Int, blue: Int): Int {
        return (alpha and 0xFF shl 24) or (red and 0xFF shl 16) or (green and 0xFF shl 8) or (blue and 0xFF)
    }
    fun rgba(red: Int, green: Int, blue: Int, alpha: Int): Int {
        return argb(alpha, red, green, blue)
    }

    fun hexCode(red: Float, green: Float, blue: Float, alpha: Float): String {
        val a = alpha.toHex()
        val r = red.toHex()
        val g = green.toHex()
        val b = blue.toHex()
        return "$a$r$g$b"
    }

    private fun Float.toHex(): String {
        val intValue = (this * 255).toInt()
        val hexChars = "0123456789ABCDEF"

        return buildString {
            append(hexChars[(intValue shr 4) and 0x0F])
            append(hexChars[intValue and 0x0F])
        }
    }
}