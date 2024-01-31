package com.lh.SuperResolutionImage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.SystemClock
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import coil.ImageLoader
import coil.imageLoader
import coil.load
import coil.size.ViewSizeResolver
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.EncodeUtils
import com.blankj.utilcode.util.LogUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.huawei.hiai.vision.common.VisionBase
import com.huawei.hiai.vision.common.VisionImage
import com.huawei.hiai.vision.image.sr.ImageSuperResolution
import com.huawei.hiai.vision.visionkit.common.VisionConfiguration
import com.huawei.hiai.vision.visionkit.image.ImageResult
import com.huawei.hiai.vision.visionkit.image.sr.SISRConfiguration
import com.lh.SuperResolutionImage.coilTransformation.SRTransformation
import com.lh.SuperResolutionImage.glideTransformation.SRGlideTransformation
import com.lh.SuperResolutionImage.huawei.VisionBaseConnectManager


/**

 * @Author : liuhao02

 * @Time : On 2024/1/9 15:44

 * @Description : SRHuaweiImage

 */
class SRHuaweiImage private constructor(private var context: Context) {


    private var isConnected: Boolean = false

    private var connecting: Boolean = false


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

    companion object {
        const val IMAGE_LOADING_METHOD_GLIDE = 0;
        const val IMAGE_LOADING_METHOD_COIL = 1;

        private var imageMethod: Int = IMAGE_LOADING_METHOD_GLIDE;


        public var isDebug: Boolean = true

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
            LogUtils.d("url: $url width: $width height: $height quality: $quality")
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
                        scale, context
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
            width: Int? = null,
            height: Int? = null,
            quality: Int? = 100,
            toThumbnail: Boolean = false,
            imageLoader: ImageLoader = context.imageLoader,
        ) {

            var mWidth = width
            var mHeight = height

//            var reductionWidth = width?.div(3);
//            var reductionHeight = height?.div(3);
            var reductionQuality = quality;
            var url = url
            var scale: Float
            if (mWidth == null) {
                mWidth = this.width
            }
            if (mHeight == null) {
                mHeight = this.height
            }
            scale = SISRConfiguration.SISR_SCALE_3X
//            if (SRMaxWidth > mHeight!! && SRMaxHeight > mHeight!!) {
//                scale = SISRConfiguration.SISR_SCALE_3X
//                if (toThumbnail) {
//                    url = HuaWeiObsUtils.thumbnailFromUrl(url, mWidth!!, mHeight!!)
//                }
//            } else {
//                scale = SISRConfiguration.SISR_SCALE_1X
//            }
            LogUtils.d(
                "SRMaxWidth" + SRMaxWidth + " mHeight" + mHeight + "SRMaxHeight" + SRMaxHeight + "mHeight" + mHeight,
                "url: $url width: $width height: $height quality: $quality  SISRConfiguration : $scale"
            )
            var startTime: Long = 0
            var endTime: Long = 0
            if (imageMethod == IMAGE_LOADING_METHOD_COIL) {
                this.load(url, imageLoader = imageLoader) {
                    size(ViewSizeResolver(this@loadSRImage))
                    listener(
                        onStart = { request ->
                            startTime = SystemClock.uptimeMillis()
//                        LogUtils.d("TestTime", "onStart huaweiImage" + startTime)
                        },
                        onError = { request, throwable ->
                            endTime = SystemClock.uptimeMillis() // 获取结束时间
                            LogUtils.e(
                                "TestTime",
                                "onError huaweiImage Runtime: " + (endTime - startTime) + "throwable: " + throwable.throwable.toString()
                            )
                        },
                        onCancel = {
                            endTime = SystemClock.uptimeMillis() // 获取结束时间
                            LogUtils.e(
                                "TestTime",
                                "onCancel huaweiImage Runtime: " + (endTime - startTime)
                            )
                        },
                        onSuccess = { request, metadata ->
                            endTime = SystemClock.uptimeMillis() // 获取结束时间
                            LogUtils.e(
                                "TestTime",
                                "onSuccess huaweiImage Runtime: " + (endTime - startTime)
                            )
                        }
                    )
                    transformations(
                        SRTransformation(
                            url,
                            scale, context
                        )
                    )
                }
            } else {
                startTime = SystemClock.uptimeMillis()
//                val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
//                val thumbnailRequest: DrawableRequestBuilder<String> = Glide.with(context).load(url)
                Glide.with(context)
                    .asBitmap()
                    .load(url)
                    .transform(
                        SRGlideTransformation(
                            url,
                            scale, context
                        )
                    )
                    //.thumbnail(thumbnailRequest)缩略图
                    //之后对过渡进行控制 .transition(withCrossFade(factory))
                    .into(object : CustomTarget<Bitmap>() {
                        /**
                         * The method that will be called when the resource load has finished.
                         *
                         * @param resource the loaded resource.
                         */
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            endTime = SystemClock.uptimeMillis() // 获取结束时间
                            LogUtils.e(
                                "TestTime  startTime$startTime endTime:$endTime",
                                "onSuccess glide onResourceReady  Runtime: " + (endTime - startTime) + "url:$url scale:$scale"
                            )
                            this@loadSRImage.setImageBitmap(resource)
                        }

                        /**
                         * A **mandatory** lifecycle callback that is called when a load is cancelled and its resources
                         * are freed.
                         *
                         *
                         * You **must** ensure that any current Drawable received in [.onResourceReady] is no longer used before redrawing the container (usually a View) or changing its
                         * visibility.
                         *
                         * @param placeholder The placeholder drawable to optionally show, or null.
                         */
                        override fun onLoadCleared(placeholder: Drawable?) {

                        }
                    })
            }
        }


        fun SRBase64Image(base64: String, context: Context, scale: Float): String {
            var startTime: Long = 0;
            if (SRHuaweiImage.isDebug) {
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
            if (SRHuaweiImage.isDebug) {
                val endTime = SystemClock.uptimeMillis()
                LogUtils.e(
                    "TestTime",
                    "transform  Runtime: " + (endTime - startTime) + "startTime: " + startTime.toString() + "endTime" + endTime.toString()
                )
            }
            return string;
        }


        fun SRImageBitmap(bitmap: Bitmap, context: Context, scale: Float): Bitmap? {
            SRImage(VisionImage.fromBitmap(bitmap), context, scale).let {
                if (it == null) return it
                return it?.bitmap
            }
        }

        fun SRImage(image: VisionImage, context: Context, scale: Float): ImageResult? {
            if (!VisionBaseConnectManager.getInstance().isConnected) {
                LogUtils.d("SRImage VisionBaseConnectManager notConnected ")
                return null
            }
            // 准备输入图片
            // Prepare input bitmap
            //image

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
                LogUtils.e("SISR Wait for result.")
                return null;
            } else if (resultCode != 0) {
                //Log.e(MainActivity.TAG, "Failed to run super-resolution, return : $resultCode")
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
