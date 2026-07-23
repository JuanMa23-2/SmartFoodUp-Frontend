package com.smartfoodup.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.*
import platform.Foundation.*
import platform.darwin.*
import kotlinx.cinterop.*
import platform.posix.memcpy

@Composable
actual fun registrarImagePicker(
    origen: OrigenImagen,
    onImageSelected: (ImagenSeleccionada) -> Unit
): () -> Unit {
    val delegate = remember {
        object : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {
            override fun imagePickerController(
                picker: UIImagePickerController,
                didFinishPickingMediaWithInfo: Map<Any?, *>
            ) {
                val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
                if (image != null) {
                    val imageData = UIImageJPEGRepresentation(image, 0.9)
                    if (imageData != null) {
                        val base64String = imageData.base64EncodedStringWithOptions(0u)
                        onImageSelected(
                            ImagenSeleccionada(
                                nombre = "ios_image_${NSDate().timeIntervalSince1970}.jpg",
                                bytesBase64 = base64String
                            )
                        )
                    }
                }
                picker.dismissViewControllerAnimated(true, null)
            }

            override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
                picker.dismissViewControllerAnimated(true, null)
            }
        }
    }

    return remember {
        {
            val picker = UIImagePickerController()
            picker.delegate = delegate
            picker.sourceType = if (origen == OrigenImagen.GALERIA) {
                UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
            } else {
                UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
            }

            val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
            rootViewController?.presentViewController(picker, true, null)
        }
    }
}
