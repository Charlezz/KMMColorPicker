package com.bluestars.colorpicker.component

import androidx.compose.ui.graphics.Color

data class ColorEnvelope(
    val color : Color,
    val hexCode : String,
    val fromUser : Boolean
)
