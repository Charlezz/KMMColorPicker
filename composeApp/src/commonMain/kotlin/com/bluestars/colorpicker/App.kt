package com.bluestars.colorpicker

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.forEachGesture
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType

@Composable
fun AppScreen(
    dominantColor: Color?,
//    clickedColor : Color?,
    randomSelectColor: Color?,
    clickedColor: Color?,
    clickedPosition: Offset?,
    imageSize: IntSize,
    image: Painter,
    onBackClick: () -> Unit,
    onImagePick: () -> Unit,
//    updateClickedColor: (Offset, IntSize, Painter) -> Unit,

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
                            .aspectRatio(1f)
                            .pointerInput(Unit) {
                                    detectTapGestures { offset ->
                                        val pixelX =
                                            (offset.x / imageSize.width * image.intrinsicSize.width).toInt()
                                        val pixelY =
                                            (offset.y / imageSize.height * image.intrinsicSize.height).toInt()
                                        println("pixelX :$pixelX , pixelY :$pixelY" )
                                        println("offsetX :${offset.x} , offsetY :${offset.y}" )

                                    }
                            }
                            .onGloballyPositioned { layoutCoordinates ->
                            },
                        painter = image,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                    ) {
                        if (dominantColor != null /*&& randomSelectColor != null*/) {
                            Image(
                                modifier = Modifier
                                    .fillMaxSize(),
                                painter = ColorPainter(dominantColor!!),
                                contentDescription = null
                            )

                            Text(
                                modifier = Modifier.background(color = Color.Black).align(
                                    Alignment.Center
                                ),
                                text = "(${(dominantColor.red * 255f).toInt()}, ${(dominantColor.green * 255f).toInt()}, ${(dominantColor.blue * 255f).toInt()})",
                                color = Color.White,
                                fontSize = TextUnit(20f, TextUnitType.Sp)
                            )
                            clickedColor?.let { color ->
                                Text(
                                    modifier = Modifier.background(color = Color.Black).align(
                                        Alignment.CenterEnd
                                    ),
                                    text = "(${(color.red * 255f).toInt()}, ${(color.green * 255f).toInt()}, ${(color.blue * 255f).toInt()})",
                                    color = Color.White,
                                    fontSize = TextUnit(20f, TextUnitType.Sp)
                                )
                            }
                            if(randomSelectColor != null){
                                Text(
                                    modifier = Modifier.background(color = Color.Black).align(
                                        Alignment.CenterEnd),
                                    text = "(${(randomSelectColor.red * 255f).toInt()}, ${(randomSelectColor.green * 255f).toInt()}, ${(randomSelectColor.blue * 255f).toInt()})",
                                    color = Color.White,
                                    fontSize = TextUnit(20f, TextUnitType.Sp )
                                )
                            }
                            Text(
                                modifier = Modifier.background(color = Color.Black).align(
                                    Alignment.TopCenter
                                ),
                                text = dominantColor.toHexCode(),
                                color = Color.White,
                                fontSize = TextUnit(20f, TextUnitType.Sp)
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


private fun Color.toHexCode(): String {
    val r = (red * 255f).toInt().toString(16)
    val g = (green * 255f).toInt().toString(16)
    val b = (blue * 255f).toInt().toString(16)
    return "#${r}${g}${b}"
}