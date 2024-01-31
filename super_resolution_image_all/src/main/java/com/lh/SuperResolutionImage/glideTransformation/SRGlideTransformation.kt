package com.lh.SuperResolutionImage.glideTransformation

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.text.TextUtils
import com.blankj.utilcode.util.LogUtils
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.lh.SuperResolutionImage.SRHuaweiImage
import java.security.MessageDigest

/**

 * @Author : liuhao02

 * @Time : On 2024/1/29 14:47

 * @Description : SRGlideTransformation

 */
class SRGlideTransformation(var url: String, var SISR_SCALE: Float, var context: Context) :
    BitmapTransformation() {

    init {
        LogUtils.d("SRGlideTransformation url: " + url + " SISR_SCALE:" + SISR_SCALE + "");
        require(!TextUtils.isEmpty(url) && SISR_SCALE >= 0) {
            "SRGlideTransformation 数据不对 TextUtils.isEmpty(url) ｜｜ SISR_SCALE < 0"
        }
    }


    /**
     * Adds all uniquely identifying information to the given digest.
     *
     *
     * Note - Using [java.security.MessageDigest.reset] inside of this method will result
     * in undefined behavior.
     */
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES);
    }

    /**
     * Transforms the given [android.graphics.Bitmap] based on the given dimensions and returns
     * the transformed result.
     *
     *
     * The provided Bitmap, toTransform, should not be recycled or returned to the pool. Glide will
     * automatically recycle and/or reuse toTransform if the transformation returns a different
     * Bitmap. Similarly implementations should never recycle or return Bitmaps that are returned as
     * the result of this method. Recycling or returning the provided and/or the returned Bitmap to
     * the pool will lead to a variety of runtime exceptions and drawing errors. See #408 for an
     * example. If the implementation obtains and discards intermediate Bitmaps, they may safely be
     * returned to the BitmapPool and/or recycled.
     *
     *
     * outWidth and outHeight will never be [ ][com.bumptech.glide.request.target.Target.SIZE_ORIGINAL], this class converts them to be the
     * size of the Bitmap we're going to transform before calling this method.
     *
     * @param pool A [com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool] that can be used
     * to obtain and return intermediate [Bitmap]s used in this transformation. For every
     * [android.graphics.Bitmap] obtained from the pool during this transformation, a [     ] must also be returned.
     * @param toTransform The [android.graphics.Bitmap] to transform.
     * @param outWidth The ideal width of the transformed bitmap (the transformed width does not need
     * to match exactly).
     * @param outHeight The ideal height of the transformed bitmap (the transformed height does not
     * need to match exactly).
     */
    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        var startTime: Long = 0;
        if (SRHuaweiImage.isDebug) {
            startTime = SystemClock.uptimeMillis()
        }
        //todo 华为引擎不能转换过大的图片 目前过大的图片参数传过来 SISR_SCALE是1倍
        if (SISR_SCALE <= 1.0f) {
            return toTransform
        }
        var outBitmap = SRHuaweiImage.SRImageBitmap(toTransform, context, SISR_SCALE)
        if (SRHuaweiImage.isDebug) {
            val endTime = SystemClock.uptimeMillis()
            LogUtils.e(
                "TestTime",
                "transform  Runtime: " + (endTime - startTime) + "startTime: " + startTime.toString() + "endTime" + endTime.toString()
            )
        }
        if (outBitmap == null) {
            return toTransform;
        } else {
            return outBitmap;
        }
    }

    private val ID = "${javaClass.name}-$url,$SISR_SCALE"
    private val ID_BYTES: ByteArray = ID.toByteArray(charset(STRING_CHARSET_NAME))


    override fun equals(o: Any?): Boolean {
        if (o is SRGlideTransformation) {
            if (o.ID == ID) {
                return true
            }
            return false
        } else {
            return false
        }
    }

    override fun hashCode(): Int {
        return ID.hashCode()
    }


}