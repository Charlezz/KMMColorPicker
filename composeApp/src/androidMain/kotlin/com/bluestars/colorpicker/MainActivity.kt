package com.bluestars.colorpicker

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.bluestars.colorpicker.model.BSImage
import com.bluestars.colorpicker.usecase.ImageDecoder
import kotlinx.coroutines.launch
import java.net.URL
import kotlin.math.max

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val coroutineScope = rememberCoroutineScope()
            val localContext = LocalContext.current
            var uri : Uri?  by remember { mutableStateOf(null) }

            var dominantColor: Color? by remember { mutableStateOf(null) }
            var bsImage: BSImage? by remember { mutableStateOf(null) }

            var clickedPosition by remember { mutableStateOf(Offset(0f, 0f)) }
            var imageSize by remember { mutableStateOf(IntSize.Zero) }
            var clickedColor by remember { mutableStateOf<Color?>(null) }

            var offSet by remember { mutableStateOf<Offset?>(null) }
            var intSize by remember { mutableStateOf<IntSize?>(null) }
            Log.d("MainActivity", "offSet : $offSet, intSize : $intSize")
            val pickerLauncher =
                rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { newUri ->
                    if (newUri != null) {
                        uri = newUri
                        coroutineScope.launch {
                            val bitmapImage = ImageDecoder(localContext).decode(newUri)
                            val colorInt = GetDominantColorUseCase(bitmapImage)
                            bsImage = bitmapImage
                            dominantColor = Color(colorInt)

                        }
                    }
                }

                NavScreen(
                    uri = uri,
                    BSImage =bsImage,
                    dominantColor = dominantColor,
                    clickedPosition = clickedPosition,
                    imageSize = imageSize,
                    clickedColor = clickedColor,
                    onBackClick = {finish()},
                    onImagePick = { pickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    onImageClick = {
                        offSet = it
                        coroutineScope.launch {
                        if(offSet != null && intSize != null && uri != null && intSize!!.width != 0){
                                var image =  ImageDecoder(localContext).decode(uri!!, max(intSize!!.width ,intSize!!.height) )
                                var ratio =  intSize!!.width.toFloat() / image!!.width.toFloat()
                                var x =  offSet!!.x.toFloat() * ratio
                                var y = offSet!!.y.toFloat() * ratio
                                dominantColor = Color(GetColorByPositionUseCase(image!!, x.toInt(), y.toInt()))
                            }
                        }
                    },
                    onImageSize = {
                        intSize = it
                    }
                )

//            AppScreen(
//                dominantColor = dominantColor,
//                image = rememberAsyncImagePainter(model = uri),
//                onBackClick = { finish() },
//                onImagePick = { pickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
//            )

        }
    }
}

sealed class Screen(val route: String, val name: String) {
    data object ImagePicker : Screen("image_picker", "Image")
    data object ColorPicker : Screen("color_picker", "Color")

    data object Choice : Screen("choice" ,"choice")
}

@Composable
fun NavScreen(
    dominantColor : Color?,
    clickedColor: Color?,
    clickedPosition: Offset,
    imageSize: IntSize,
    BSImage: BSImage?,
    uri : Uri?,

    onBackClick :()->Unit,
    onImagePick :()->Unit,
    onImageClick : (Offset) -> Unit,
    onImageSize : (IntSize) -> Unit
//    updateClickedColor: (Offset, IntSize, Painter) -> Unit,
    ) {
    val navController = rememberNavController()
    NavHost(
        navController,
        startDestination = Screen.Choice.route
    ) {
        composable(Screen.Choice.route){
            ChoiceScreen(
               onImageRoute =  {navController.navigate(Screen.ImagePicker.route)},
                onColorRoute =  {navController.navigate(Screen.ColorPicker.route)}
            )
        }
        composable(Screen.ImagePicker.route) {
            AppScreen(
                dominantColor = dominantColor,
                clickedPosition = clickedPosition,
                imageSize = imageSize,
                clickedColor =clickedColor,
                image = rememberAsyncImagePainter(uri),
                onBackClick = onBackClick , // finish()
                onImagePick = onImagePick,
                onImageClick = onImageClick,
                onImageSize =onImageSize,
//                updateClickedColor = updateClickedColor
                imageAspectRatio = BSImage?.let { it.width.toFloat() / it.height.toFloat()  } ?: 1f
            )
        }
        composable(Screen.ColorPicker.route) {
            ImageColorPickerScreen(
                BSImage =  BSImage,
                onBackClick = onBackClick , // finish()
                onImagePick = onImagePick,
            )
        }
    }
}