import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.interop.LocalUIViewController
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.window.ComposeUIViewController
import com.bluestars.colorpicker.AppScreen
import com.bluestars.colorpicker.GetDominantColorUseCase
import com.bluestars.colorpicker.GetColorByPositionUseCase
import com.bluestars.colorpicker.model.BSImage
import com.bluestars.colorpicker.usecase.ImageDecoder
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.get
import kotlinx.coroutines.launch
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import platform.CoreFoundation.CFDataGetBytePtr
import platform.CoreFoundation.CFDataGetLength
import platform.CoreFoundation.CFRelease
import platform.CoreGraphics.CGColorSpaceCreateDeviceRGB
import platform.CoreGraphics.CGDataProviderCopyData
import platform.CoreGraphics.CGImageAlphaInfo
import platform.CoreGraphics.CGImageCreateCopyWithColorSpace
import platform.CoreGraphics.CGImageGetAlphaInfo
import platform.CoreGraphics.CGImageGetBytesPerRow
import platform.CoreGraphics.CGImageGetDataProvider
import platform.CoreGraphics.CGImageGetHeight
import platform.CoreGraphics.CGImageGetWidth
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIImage
import platform.UniformTypeIdentifiers.UTTypeJPEG
import platform.darwin.NSObject

fun MainViewController() = ComposeUIViewController {
    val coroutineScope = rememberCoroutineScope()
    val uiViewController = LocalUIViewController.current
    var imageBitmap:ImageBitmap? by remember { mutableStateOf( null) }
    var randomSelectColor : Color? by remember { mutableStateOf(null) }
    var dominantColor:Color? = imageBitmap?.let {iBitmap->
        val buffer = IntArray(iBitmap.width * iBitmap.height)
        iBitmap.readPixels(buffer)
        val bSImage = BSImage(
            buffer = buffer,
            width = iBitmap.width,
            height = iBitmap.height
        )
        Color(GetDominantColorUseCase(bSImage))
    }

    val pickerDelegate = remember {
        object : NSObject(), PHPickerViewControllerDelegateProtocol {
            override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
                picker.dismissViewControllerAnimated(flag = false, completion = {})
                val phPickerResult = didFinishPicking.firstOrNull() as? PHPickerResult
                // 대표 픽셀 추출
                coroutineScope.launch {
                    val bsImage = ImageDecoder().decode(phPickerResult!!)
                    dominantColor = Color(GetDominantColorUseCase(bsImage))
//                    randomSelectColor = Color(GetColorByPositionUseCase(bsImage))
                }
                // 이미지
                phPickerResult?.run {
                    this.itemProvider.loadDataRepresentationForTypeIdentifier(UTTypeJPEG.identifier){ data, error->
                        data?.run {
                            val uiImage = UIImage(data = this)
                            // resize를 어떻게 해야하는지 모르겠음
                            imageBitmap = uiImage.toSkiaImage()?.toComposeImageBitmap()
                        }
                    }
                }
            }
        }
    }

    val configuration = PHPickerConfiguration()
    val pickerController = PHPickerViewController(configuration)
    pickerController.setDelegate(pickerDelegate)
    AppScreen(
        dominantColor = dominantColor,
        image = imageBitmap?.let { BitmapPainter(it) }?:ColorPainter(Color.White),
        onBackClick = { },
        onImagePick = {
            uiViewController.presentViewController(pickerController, animated = true, completion = null)
        },
        clickedColor =  null,
        clickedPosition =  null,
        imageSize = IntSize.Zero,
        imageAspectRatio = 1f,
        onImageSize = { },
        onImageClick = {}
    )
}

@OptIn(ExperimentalForeignApi::class)
internal fun UIImage.toSkiaImage(): Image? {
    val imageRef = CGImageCreateCopyWithColorSpace(this.CGImage, CGColorSpaceCreateDeviceRGB()) ?: return null

    val width = CGImageGetWidth(imageRef).toInt()
    val height = CGImageGetHeight(imageRef).toInt()

    val bytesPerRow = CGImageGetBytesPerRow(imageRef)
    val data = CGDataProviderCopyData(CGImageGetDataProvider(imageRef))
    val bytePointer = CFDataGetBytePtr(data)
    val length = CFDataGetLength(data)
    val alphaInfo = CGImageGetAlphaInfo(imageRef)

    val alphaType = when (alphaInfo) {
        CGImageAlphaInfo.kCGImageAlphaPremultipliedFirst, CGImageAlphaInfo.kCGImageAlphaPremultipliedLast -> ColorAlphaType.PREMUL
        CGImageAlphaInfo.kCGImageAlphaFirst, CGImageAlphaInfo.kCGImageAlphaLast -> ColorAlphaType.UNPREMUL
        CGImageAlphaInfo.kCGImageAlphaNone, CGImageAlphaInfo.kCGImageAlphaNoneSkipFirst, CGImageAlphaInfo.kCGImageAlphaNoneSkipLast -> ColorAlphaType.OPAQUE
        else -> ColorAlphaType.UNKNOWN
    }

    val byteArray = ByteArray(length.toInt()) { index ->
        bytePointer!![index].toByte()
    }
    CFRelease(data)
    CFRelease(imageRef)

    return Image.makeRaster(
        imageInfo = ImageInfo(width = width, height = height, colorType = ColorType.RGBA_8888, alphaType = alphaType),
        bytes = byteArray,
        rowBytes = bytesPerRow.toInt(),
    )
}