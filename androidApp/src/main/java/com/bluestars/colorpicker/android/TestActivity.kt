package com.bluestars.colorpicker.android

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter.State.Empty.painter
import com.bluestars.colorpicker.usecase.GetDominantColorUseCase
import com.bluestars.colorpicker.usecase.ImageDecoder
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.internal.toHexString

/**
 * @author soohwan.ok
 */
class TestActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val coroutineScope = rememberCoroutineScope()
            val localContext = LocalContext.current
            var uri: Uri? by remember { mutableStateOf(null) }
            var dominantColor: Color? by remember { mutableStateOf(null) }
            val pickerLauncher =
                rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { newUri ->
                    if(newUri!=null){
                        uri = newUri
                        coroutineScope.launch {
                            val bitmapImage = ImageDecoder(localContext).decode(newUri)
                            val colorInt = GetDominantColorUseCase.invoke(bitmapImage)
                            dominantColor = Color(colorInt)
                        }
                    }
                }
            MaterialTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            navigationIcon = {
                                IconButton(onClick = {
                                    finish()
                                }) {
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
                            AsyncImage(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f),
                                model = uri,
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
                                        modifier = Modifier.background(color = Color.Black).align(Alignment.Center),
                                        text = dominantColor!!.toArgb().toHexString(),
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
                            onClick = {
                                pickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            }
                        ) {
                            Text("이미지 선택")
                        }
                    }
                )
            }
        }
    }
}