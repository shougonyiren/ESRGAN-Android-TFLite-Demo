package com.lh.SuperResolutionImage.coilTransformation

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.text.TextUtils
import coil.size.Size
import coil.transform.Transformation
import com.blankj.utilcode.util.LogUtils
import com.lh.SuperResolutionImage.SRHuaweiImage

/**

 * @Author : liuhao02

 * @Time : On 2024/1/9 17:40

 * @Description : SRTransformation

 */
class SRTransformation(var url: String, var SISR_SCALE: Float, var context: Context) :
    Transformation {
    init {
        LogUtils.d("SRTransformation url: "+url +" SISR_SCALE:"+ SISR_SCALE+"");
        require(!TextUtils.isEmpty(url) && SISR_SCALE >= 0) {
            "SRTransformation 数据不对 TextUtils.isEmpty(url) ｜｜ SISR_SCALE < 0"
        }
    }


    /**
     * The unique cache key for this transformation.
     *
     * The key is added to the image request's memory cache key and should contain any params that
     * are part of this transformation (e.g. size, scale, color, radius, etc.).
     */
    override val cacheKey = "${javaClass.name}-$url,$SISR_SCALE"

    /**
     * Apply the transformation to [input] and return the transformed [Bitmap].
     *
     * @param input The input [Bitmap] to transform.
     *  Its config will always be [ARGB_8888] or [RGBA_F16].
     * @param size The size of the image request.
     * @return The transformed [Bitmap].
     */
    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        var startTime: Long = 0;
        if (SRHuaweiImage.isDebug) {
            startTime = SystemClock.uptimeMillis()
        }
        //todo 华为引擎不能转换过大的图片 目前过大的图片参数传过来 SISR_SCALE是1倍
        if (SISR_SCALE <= 1.0f) {
            return input
        }
        var outBitmap = SRHuaweiImage.SRImageBitmap(input, context, SISR_SCALE)
        if (SRHuaweiImage.isDebug) {
            val endTime = SystemClock.uptimeMillis()
            LogUtils.e("TestTime","transform  Runtime: " + (endTime - startTime) + "startTime: " + startTime.toString() + "endTime" + endTime.toString())
        }
        if (outBitmap == null) {
            return input;
        } else {
            return outBitmap;
        }
    }
}