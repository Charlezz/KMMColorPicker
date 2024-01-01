package com.bluestars.colorpicker.component

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper
import androidx.core.os.BuildCompat
import java.util.ArrayList
import java.util.LinkedHashSet

@BuildCompat.PrereleaseSdkCheck
class PhotoPicker : ActivityResultContract<PhotoPicker.Args, List<Uri>>() {
    companion object {
        @JvmStatic
        fun isPhotoPickerAvailable(): Boolean {
            return BuildCompat.isAtLeastT()
        }

        private const val INTENT_PICK_IMAGES = "android.provider.action.PICK_IMAGES"
        private const val EXTRA_PICK_IMAGES_MAX = "android.provider.extra.PICK_IMAGES_MAX"
    }

    enum class Type {
        IMAGES_ONLY, VIDEO_ONLY, IMAGES_AND_VIDEO
    }

    class Args(val type: Type, val maxItems: Int)

    @CallSuper
    override fun createIntent(context: Context, input: Args): Intent {
        if (isPhotoPickerAvailable()) {
            val intent = Intent(INTENT_PICK_IMAGES).apply {
                if (input.maxItems > 1) {
                    putExtra(EXTRA_PICK_IMAGES_MAX, input.maxItems)
                }

                if (input.type == Type.IMAGES_ONLY) {
                    type = "image/*"
                } else if (input.type == Type.VIDEO_ONLY) {
                    type = "video/*"
                }
            }

            return intent
        } else {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "*/*"

                if (input.maxItems > 1) {
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                }

                when (input.type) {
                    Type.IMAGES_ONLY ->
                        putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
                    Type.VIDEO_ONLY ->
                        putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("video/*"))
                    Type.IMAGES_AND_VIDEO ->
                        putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
                }
            }

            return intent
        }
    }

    override fun getSynchronousResult(context: Context, input: Args): SynchronousResult<List<Uri>>? {
        return null
    }

    override fun parseResult(resultCode: Int, intent: Intent?): List<Uri> {
        return if (resultCode != Activity.RESULT_OK || intent == null) emptyList() else getClipDataUris(intent)
    }

    private fun getClipDataUris(intent: Intent): List<Uri> {
        // Use a LinkedHashSet to maintain any ordering that may be
        // present in the ClipData
        val resultSet = LinkedHashSet<Uri>()
        if (intent.data != null) {
            resultSet.add(intent.data!!)
        }
        val clipData = intent.clipData
        if (clipData == null && resultSet.isEmpty()) {
            return emptyList()
        } else if (clipData != null) {
            for (i in 0 until clipData.itemCount) {
                val uri = clipData.getItemAt(i).uri
                if (uri != null) {
                    resultSet.add(uri)
                }
            }
        }
        return ArrayList(resultSet)
    }
}
