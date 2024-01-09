package com.bluestars.colorpicker.usecase

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.bluestars.colorpicker.model.BSImage
import platform.PhotosUI.PHPickerResult
import platform.UIKit.UIImage
import platform.UniformTypeIdentifiers.UTTypeJPEG
import toSkiaImage
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * @author soohwan.ok
 */
actual class ImageDecoder {
    actual suspend fun decode(target:Any, size : Int): BSImage {
        val image = target as? PHPickerResult
        var imageBitmap: ImageBitmap? = null
        var buffer:IntArray? = null

        return suspendCoroutine<BSImage> { continuation->
            image?.run {
                this.itemProvider.loadDataRepresentationForTypeIdentifier(UTTypeJPEG.identifier){ data, error->
                    data?.run {
                        val uiImage = UIImage(data = this)
                        // resize를 어떻게 해야하는지 모르겠음
                        imageBitmap = uiImage.toSkiaImage()?.toComposeImageBitmap()
                        buffer = IntArray(imageBitmap!!.width * imageBitmap!!.height)
                        imageBitmap!!.readPixels(buffer!!)
                        continuation.resume(
                            BSImage(
                                buffer = buffer!!,
                                width = imageBitmap!!.width,
                                height = imageBitmap!!.height
                            )
                        )
                    }
                }
            }
        }
    }
}