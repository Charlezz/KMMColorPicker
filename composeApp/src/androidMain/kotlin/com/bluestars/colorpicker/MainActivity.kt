package com.bluestars.colorpicker

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import com.bluestars.colorpicker.usecase.ImageDecoder
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
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
            AppScreen(
                dominantColor = dominantColor,
                image = rememberAsyncImagePainter(model = uri),
                onBackClick = {finish()},
                onImagePick = { pickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
            )

        }
    }
}