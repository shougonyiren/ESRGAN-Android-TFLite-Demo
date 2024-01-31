package com.lh.superresolution.core

import android.content.Context
import android.graphics.Bitmap

/**

 * @Author : liuhao02

 * @Time : On 2024/1/31 15:35

 * @Description : SRImage

 */
abstract class SRImage(private var context: Context)  {
    init {

    }


    abstract  fun loadBitmap(bitmap: Bitmap, context: Context, scale: Float): Bitmap?


    abstract fun loadBase64ToBitmap(base64: String, context: Context, scale: Float): String

}