package com.bluestars.colorpicker

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType

@Composable
fun AppScreen(
    dominantColor:Color?,
    image :Painter,
    onBackClick :()->Unit,
    onImagePick :()->Unit,
) {
    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },
                    title = { Text(text = "이미지 프로세싱 테스트") }
                )
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .verticalScroll(state = rememberScrollState())
                        .padding(paddingValues)
                ) {
                    //TODO Image Drwaing
                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        painter = image,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                    ) {
                        if (dominantColor != null) {
                            Image(
                                modifier = Modifier.fillMaxSize(),
                                painter = ColorPainter(dominantColor!!),
                                contentDescription = null
                            )
                            Text(
                                modifier = Modifier.background(color = Color.Black).align(
                                    Alignment.Center),
                                text = "(${(dominantColor.red * 255f).toInt()}, ${(dominantColor.green * 255f).toInt()}, ${(dominantColor.blue * 255f).toInt()})",
                                color = Color.White,
                                fontSize = TextUnit(20f, TextUnitType.Sp )
                            )
                        }
                    }
                }
            },
            bottomBar = {
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onImagePick
                ) {
                    Text("이미지 선택")
                }
            }
        )
    }
}