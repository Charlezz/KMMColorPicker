package com.bluestars.colorpicker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bluestars.colorpicker.component.AlphaTileDrawable
import com.bluestars.colorpicker.component.ColorEnvelope
import com.bluestars.colorpicker.component.ColorPickerController
import com.bluestars.colorpicker.component.ImageColorPicker
import com.bluestars.colorpicker.component.rememberColorPickerController
import com.bluestars.colorpicker.model.BitmapImage

@Composable
fun ImageColorPickerScreen(
    bitmapImage: BitmapImage?,
    onBackClick: () -> Unit,
    onImagePick: () -> Unit,
) {
    val controller = rememberColorPickerController()
    var hexCode by remember { mutableStateOf("") }
    var textColor by remember { mutableStateOf(Color.Transparent) }

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
                title = { Text(text = "이미지 픽커 테스트") }
            )
        },
        content = { paddingValues ->
            Column(
                modifier =
                Modifier.padding(paddingValues)
                    .verticalScroll(state = rememberScrollState())
            ) {

                if (bitmapImage != null) {
                    val imageBitmap = bitmapImage.toImageBitmap()
                    Spacer(modifier = Modifier.weight(1f))

                    PhotoPickerIcon(controller)

                    ImageColorPicker(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(10.dp),
                        controller = controller,
                        paletteImageBitmap =imageBitmap,//ImageBitmap.imageResource(R.drawable.palettebar),
                        onColorChanged = { colorEnvelope: ColorEnvelope ->
                            hexCode = colorEnvelope.hexCode
                            textColor = colorEnvelope.color
                        },
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "#$hexCode",
                        color = textColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )

                    AlphaTile(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .align(Alignment.CenterHorizontally),
                        controller = controller,
                    )
                }
            }
        }, bottomBar = {
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onImagePick
            ) {
                Text("이미지 선택")
            }
        }
    )
}

internal val defaultTileOddColor: Color = Color(0xFFFFFFFF)
internal val defaultTileEvenColor: Color = Color(0xFFCBCBCB)

private fun BitmapImage.toImageBitmap(): ImageBitmap {
    // Create an android.graphics.Bitmap
    val androidBitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888)
    androidBitmap.setPixels(buffer, 0, width, 0, 0, width, height)

    // Convert android.graphics.Bitmap to ImageBitmap
    return androidBitmap.asImageBitmap()
}
@Composable
public fun AlphaTile(
    modifier: Modifier,
    controller: ColorPickerController? = null,
    selectedColor: Color = Color.Transparent,
    tileOddColor: Color = defaultTileOddColor,
    tileEvenColor: Color = defaultTileEvenColor,
    tileSize: Dp = 12.dp,
) {
    val density = LocalDensity.current
    var backgroundBitmap: ImageBitmap? = null
    var bitmapSize = IntSize(0, 0)
    val colorPaint: Paint = Paint().apply {
        this.color = controller?.selectedColor?.value ?: selectedColor
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { newSize ->
                val size =
                    newSize.takeIf { it.width != 0 && it.height != 0 } ?: return@onSizeChanged
                val drawable =
                    AlphaTileDrawable(
                        with(density) { tileSize.toPx() },
                        tileOddColor,
                        tileEvenColor,
                    )
                backgroundBitmap
                    ?.asAndroidBitmap()
                    ?.recycle()
                backgroundBitmap =
                    ImageBitmap(size.width, size.height, ImageBitmapConfig.Argb8888).apply {
                        val backgroundCanvas = Canvas(this)
                        drawable.setBounds(
                            0,
                            0,
                            backgroundCanvas.nativeCanvas.width,
                            backgroundCanvas.nativeCanvas.height,
                        )
                        drawable.draw(backgroundCanvas.nativeCanvas)
                    }
                bitmapSize = size
            },
    ) {
        drawIntoCanvas { canvas ->
            backgroundBitmap?.let {
                canvas.drawImage(it, Offset.Zero, Paint())
                canvas.drawRect(
                    0f,
                    0f,
                    bitmapSize.width.toFloat(),
                    bitmapSize.height.toFloat(),
                    colorPaint,
                )
            }
        }
    }
}
