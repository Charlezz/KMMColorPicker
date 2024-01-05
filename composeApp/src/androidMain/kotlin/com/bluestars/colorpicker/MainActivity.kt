package com.bluestars.colorpicker

import android.net.Uri
import android.os.Bundle
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.bluestars.colorpicker.model.BSImage
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
            var BSImageT: BSImage? by remember { mutableStateOf(null) }
            var hexString by remember { mutableStateOf("") }

            val pickerLauncher =
                rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { newUri ->
                    if (newUri != null) {
                        uri = newUri
                        coroutineScope.launch {
                            val bitmapImage = ImageDecoder(localContext).decode(newUri)
                            val colorInt = GetDominantColorUseCase.invoke(bitmapImage)
                            BSImageT = bitmapImage
                            dominantColor = Color(colorInt)
                            hexString = String.format("#%02X%02X%02X", (dominantColor!!.red * 255).toInt(), (dominantColor!!.green * 255).toInt(), (dominantColor!!.blue * 255).toInt())
                        }
                    }
                }
                NavScreen(
                    uri = uri,
                    BSImage =BSImageT,
                    dominantColor = dominantColor,
                    onBackClick = {finish()},
                    onImagePick = { pickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
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
    BSImage: BSImage?,
    uri : Uri?,
    onBackClick :()->Unit,
    onImagePick :()->Unit
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
                image = rememberAsyncImagePainter(model = uri),
                onBackClick = onBackClick , // finish()
                onImagePick = onImagePick
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