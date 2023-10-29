package com.bluestars.colorpicker

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform