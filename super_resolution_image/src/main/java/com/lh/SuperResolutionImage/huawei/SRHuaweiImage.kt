package com.lh.SuperResolutionImage

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import android.widget.ImageView
import coil.load
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.ViewSizeResolver
import com.blankj.utilcode.util.LogUtils
import com.huawei.hiai.vision.common.VisionBase
import com.huawei.hiai.vision.common.VisionImage
import com.huawei.hiai.vision.image.sr.ImageSuperResolution
import com.huawei.hiai.vision.visionkit.common.VisionConfiguration
import com.huawei.hiai.vision.visionkit.image.ImageResult
import com.huawei.hiai.vision.visionkit.image.sr.SISRConfiguration
import com.lh.SuperResolutionImage.coilTransformation.SRTransformation
import com.lh.SuperResolutionImage.huawei.ConnectManager


/**

 * @Author : liuhao02

 * @Time : On 2024/1/9 15:44

 * @Description : SRHuaweiImage

 */
class SRHuaweiImage {



    private var isConnected: Boolean = false

    private var connecting: Boolean = false
    private var context: Context

    private constructor(context: Context) {
        LogUtils.d("Start SISR")
        this.context = context
        connectHuaweiAIEngine(context)
    }

    private fun connectHuaweiAIEngine(context: Context) {
        connecting = true
        isConnected = false
        // 连接AI引擎
        // Connect to AI Engine
        VisionBase.init(
            context,
            ConnectManager.getInstance().getmConnectionCallback()
        )
        if (!ConnectManager.getInstance().isConnected) {
            ConnectManager.getInstance().waitConnect()
        }
        isConnected = ConnectManager.getInstance().isConnected
        if (!ConnectManager.getInstance().isConnected) {
            LogUtils.d("Can't connect to server.")
//            mHuaweiTxtViewResult.setText("Can't connect to server!")
            return
        }
        connecting = false
    }

    companion object {
        public  var isDebug: Boolean = true

        @Volatile
        private var instance: SRHuaweiImage? = null

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: SRHuaweiImage(context).also { instance = it }
            }

        public val SRMaxWidth = 800;
        public val SRMaxHeight = 600;


        //默认图片来源华为云  使用华为obs
        //分为三部分 数据处理 数据加载 超分
        fun ImageView.loadSRThumbnailImage(
            context: Context,
            url: String,
            width: Int,
            height: Int,
            quality: Int? = 100
        ) {
            LogUtils.d("url: " + url+" width: " + width+" height: "+height+" quality: " + quality)
            var reductionWidth = width?.div(3);
            var reductionHeight = height?.div(3);
            var reductionQuality = quality;
            var url = url
            var scale = SISRConfiguration.SISR_SCALE_1X
            if (SRMaxWidth > reductionWidth!! && SRMaxHeight > reductionHeight!!) {
                scale = SISRConfiguration.SISR_SCALE_3X
                url = HuaWeiObsUtils.thumbnailFromUrl(url, reductionWidth!!, reductionHeight!!)
            } else {
                scale = SISRConfiguration.SISR_SCALE_1X
            }
            this.load(url) {
                transformations(
                    SRTransformation(
                        url,
                        scale,context
                    )
                )
            }
        }

        //默认图片来源华为云  使用华为obs
        //分为三部分 数据处理 数据加载 超分
        //todo 可以传listener
        fun ImageView.loadSRImage(
            context: Context,
            url: String,
            width: Int,
            height: Int,
            quality: Int? = 100

        ) {
            LogUtils.d("url: " + url+" width: " + width+" height: "+height+" quality: " + quality)
//            var reductionWidth = width?.div(3);
//            var reductionHeight = height?.div(3);
            var reductionQuality = quality;
            var url = url
            var scale = SISRConfiguration.SISR_SCALE_1X
            if (SRMaxWidth > width!! && SRMaxHeight > height!!) {
                scale = SISRConfiguration.SISR_SCALE_3X
                url = HuaWeiObsUtils.thumbnailFromUrl(url, width!!, height!!)
            } else {
                scale = SISRConfiguration.SISR_SCALE_1X
            }
            var startTime :Long= 0
            var endTime:Long= 0
            this.load(url) {
                memoryCachePolicy(CachePolicy.DISABLED)
                networkCachePolicy(CachePolicy.ENABLED)
                diskCachePolicy(CachePolicy.DISABLED)
                size(ViewSizeResolver(this@loadSRImage))
                listener(
                    onStart = { request ->
                        startTime = SystemClock.uptimeMillis()
                        LogUtils.d("TestTime", "onStart huaweiImage"+startTime)
                    },
                    onError = { request, throwable ->
                        endTime = SystemClock.uptimeMillis() // 获取结束时间
                        LogUtils.e("TestTime", "onError huaweiImage Runtime: " + (endTime - startTime)+"throwable: " + throwable.throwable.toString())
                    },
                    onCancel = { request ->
                        endTime = SystemClock.uptimeMillis() // 获取结束时间
                        LogUtils.e("TestTime", "onCancel huaweiImage Runtime: " + (endTime - startTime))
                    },
                    onSuccess = { request, metadata ->
                        endTime = SystemClock.uptimeMillis() // 获取结束时间
                        LogUtils.e("TestTime", "onSuccess huaweiImage Runtime: " + (endTime - startTime))
                    }
                )
                transformations(
                    SRTransformation(
                        url,
                        scale,context
                    )
                )
            }
        }

        fun SRImage(bitmap: Bitmap, context: Context, scale: Float): Bitmap? {

            // 准备输入图片
            // Prepare input bitmap
            val image = VisionImage.fromBitmap(bitmap)


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
            val resultCode = superResolution.doSuperResolution(image, result, null)// visionCallback
            val endTime = SystemClock.uptimeMillis() // 获取结束时间

            Log.e("TestTime", "Runtime: " + (endTime - startTime))
//           val msg = Message()
//           msg.what = MainActivity.TYPE_SHOW_SR_TEXT
//           msg.obj = "Runtime: " + (endTime - startTime)
//           mHander.sendMessage(msg)
            if (resultCode == 700) {
                LogUtils.e("Wait for result.")
                return null;
            } else if (resultCode != 0) {
                //Log.e(MainActivity.TAG, "Failed to run super-resolution, return : $resultCode")
                LogUtils.e("Failed to run super-resolution, return : $resultCode")
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
            return result.bitmap
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

        fun getHuaweiReductionUrl(url: String, width: Int, height: Int, quality: Int): Boolean {
            var reductionWidth = width / 3;
            var reductionHeight = height / 3;
            var reductionQuality = quality;
            if (SRMaxWidth > reductionWidth && SRMaxHeight > reductionHeight) {
                return true
            } else {
                return false
            }
        }
    }
}
