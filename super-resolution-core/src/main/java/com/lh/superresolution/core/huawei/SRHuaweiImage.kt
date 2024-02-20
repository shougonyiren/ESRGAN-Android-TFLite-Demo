package com.lh.superresolution.core.huawei

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.SystemClock
import android.util.Base64
import android.util.Log
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.EncodeUtils
import com.blankj.utilcode.util.LogUtils
import com.huawei.hiai.vision.common.VisionBase
import com.huawei.hiai.vision.common.VisionCallback
import com.huawei.hiai.vision.common.VisionImage
import com.huawei.hiai.vision.image.sr.ImageSuperResolution
import com.huawei.hiai.vision.visionkit.common.VisionConfiguration
import com.huawei.hiai.vision.visionkit.image.ImageResult
import com.huawei.hiai.vision.visionkit.image.sr.SISRConfiguration
import com.lh.superresolution.core.SRImage

/**

 * @Author : liuhao02

 * @Time : On 2024/1/31 14:56

 * @Description : SRImage

 */
class SRHuaweiImage private constructor(private var context: Context) : SRImage(context) {


    private var isConnected: Boolean = false

    private var connecting: Boolean = false
    public var isDebug: Boolean = true


    companion object{
        @Volatile
        private var instance: SRHuaweiImage? = null

        fun getInstance()=instance

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: SRHuaweiImage(context).also { instance = it }
            }
    }
    init {
        LogUtils.d("Start SISR")
        connectHuaweiAIEngine(context)
    }

    private fun connectHuaweiAIEngine(context: Context) {
        connecting = true
        isConnected = false
        // 连接AI引擎
        // Connect to AI Engine
        VisionBase.init(
            context,
            VisionBaseConnectManager.getInstance().getmConnectionCallback()
        )
        if (!VisionBaseConnectManager.getInstance().isConnected) {
            VisionBaseConnectManager.getInstance().waitConnect()
        }
        isConnected = VisionBaseConnectManager.getInstance().isConnected
        if (!VisionBaseConnectManager.getInstance().isConnected) {
            LogUtils.d("Can't connect to server.")
//            mHuaweiTxtViewResult.setText("Can't connect to server!")
            return
        }
        connecting = false
    }

    override fun loadBase64ToBitmap(base64: String, context: Context, scale: Float): String {
        var startTime: Long = 0;
        if (isDebug) {
            startTime = SystemClock.uptimeMillis()
        }

        val byteArray: ByteArray = Base64.decode(base64, Base64.DEFAULT)
        //todo  需要VisionImageMetadata metadata
//            VisionImage.fromByteArray(byteArray)
//            SRImage(VisionImage.fromByteArray(byteArray), context, scale).let {
//                if (it == null) return it
//                return it?.bitmap
//            }
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        var srBitmap =
            SRImage(VisionImage.fromBitmap(bitmap), context, scale).let {
                if (it == null) return ""
                return@let it?.bitmap
            }
        var string = EncodeUtils.base64Encode2String(ConvertUtils.bitmap2Bytes(srBitmap))
        if (isDebug) {
            val endTime = SystemClock.uptimeMillis()
            LogUtils.e(
                "TestTime",
                "transform  Runtime: " + (endTime - startTime) + "startTime: " + startTime.toString() + "endTime" + endTime.toString()
            )
        }
        return string;
    }

    override fun loadBitmap(bitmap: Bitmap, context: Context, scale: Float): Bitmap? {
        SRImage(VisionImage.fromBitmap(bitmap), context, scale).let {
            if (it == null) return it
            return it?.bitmap
        }
    }


    /**
     *  如果华为版本不支持就返回空
     */
    private fun SRImage(
        image: VisionImage,
        context: Context,
        scale: Float,
        callback: VisionCallback<ImageResult>? = null
    ): ImageResult? {
        if (!VisionBaseConnectManager.getInstance().isConnected) {
            LogUtils.d("SRImage VisionBaseConnectManager notConnected ")
            return null
        }

        // 创建超分对象
        // Create SR object
        val superResolution = ImageSuperResolution(context)


        // 准备超分配置
        // Prepare SR configuration
        // 构造和设置超分参数。
        // 其中，MODE_OUT指定使用进程间通信模式，如果该参数为MODE_IN，则程序将以同进程模式运行。
        // scale指定了超分倍数，
        // SISR_QUALITY_HIGH参数则指定了超分质量。最后，将配置好的参数设置到超分对象中。
        val paras = SISRConfiguration.Builder()
            .setProcessMode(VisionConfiguration.MODE_OUT)
            .build()
        paras.scale = scale
        paras.quality = SISRConfiguration.SISR_QUALITY_HIGH


        // 设置超分
        // Config SR
        superResolution.setSuperResolutionConfiguration(paras)

        // 执行超分
        // Run SR
        var result = ImageResult()

        val startTime = SystemClock.uptimeMillis()
        /*
        doSuperResolution函数接受三个参数，
        第一个参数表示输入的图片；
        第三个参数如果不为空，则doSuperResolution将以异步模式调用，而该参数则指定了异步模式时的回调函数，结果将以回调的方式传回；
        如果第三个参数为空，则doSuperResolution将为同步调用，
        结果从第二个参数输出；
        该函数返回结果码。
         */
        val resultCode =
            superResolution.doSuperResolution(image, result, callback)// visionCallback
        val endTime = SystemClock.uptimeMillis() // 获取结束时间

        Log.e("TestTime", "Runtime: " + (endTime - startTime))
        if (resultCode == 700) {
            LogUtils.e("SISR Wait for result.")
            return null;
        } else if (resultCode != 0) {
            LogUtils.e("SISR Failed to run super-resolution, return : $resultCode")
            return null;
        }

        if (result == null) {
            LogUtils.e(" SISR Result is null!")
            return null;
        }
        if (result.bitmap == null) {
            LogUtils.e("SISR result has null bitmap!")
            return null;
        }
        return result
    }

//        var visionCallback: VisionCallback<ImageResult> = object : VisionCallback<ImageResult> {
//            override fun onResult(imageResult: ImageResult) {
//                if (imageResult.bitmap == null) {
//                    LogUtils.d("visionCallbackonResult   Result bitmap is null!")
//                    return
//                }
//
//            }
//
//            override fun onError(i: Int) {
//                LogUtils.d("visionCallback onError: $i")
//
//            }
//
//            override fun onProcessing(v: Float) {
//                LogUtils.d("visionCallback onProcessing: $v")
//            }
//        }


}
