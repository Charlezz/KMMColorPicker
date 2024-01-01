package com.bluestars.colorpicker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ChoiceScreen(
    onImageRoute : () -> Unit,
    onColorRoute : () -> Unit
) {

    Column(modifier = Modifier.fillMaxSize(),
       horizontalAlignment = Alignment.CenterHorizontally ) {
        Spacer(modifier = Modifier.weight(0.5f))
        TextButton(
            onClick = onImageRoute
        ) {
            Text("Image Picker 선택")
        }
        Spacer(modifier = Modifier.weight(0.5f))
        TextButton(
            onClick = onColorRoute
        ) {
            Text("Color Picker 선택")
        }
        Spacer(modifier = Modifier.weight(0.5f))
    }
}
