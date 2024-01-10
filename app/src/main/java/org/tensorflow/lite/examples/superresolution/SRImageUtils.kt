//import android.content.Context
//import android.graphics.Bitmap
//import android.os.Bundle
//import android.os.Message
//import android.os.SystemClock
//import android.widget.ImageView
//import androidx.annotation.WorkerThread
//import coil.imageLoader
//import coil.request.CachePolicy
//import coil.request.ImageRequest
//import coil.size.Size
//import coil.transform.Transformation
//import com.blankj.utilcode.util.ConvertUtils
//import com.blankj.utilcode.util.LogUtils
//import kotlinx.coroutines.flow.filter
//import kotlinx.coroutines.flow.flow
//import kotlinx.coroutines.flow.map
//import org.tensorflow.lite.examples.superresolution.AssetsUtil
//import java.io.FileInputStream
//import java.io.IOException
//import java.nio.MappedByteBuffer
//import java.nio.channels.FileChannel
//import java.util.concurrent.ExecutorService
//import java.util.concurrent.SynchronousQueue
//import java.util.concurrent.ThreadPoolExecutor
//import java.util.concurrent.TimeUnit
//
//
//class SRImageUtils{
//
//    companion object{
//
//       lateinit  var  executorService: ExecutorService
//        init {
//            System.loadLibrary("SuperResolution")
//            //Java线程池的拒绝策略 后续开放
//            executorService = ThreadPoolExecutor(
//                0, Int.MAX_VALUE,
//                30L, TimeUnit.SECONDS,
//                SynchronousQueue(),ThreadPoolExecutor.CallerRunsPolicy()
//            );// Executors.newCachedThreadPool()
//        }
//        fun init(context: Context){
//             this.context = context
//        }
//
//        private lateinit var context: Context
//        private const val MAX_BITMAP_SIZE =
//            100 * 1024 * 1024 // 100 MB 最大的图片 trying to draw too large(159907840bytes) bitmap.
//
//        private const val MODEL_NAME = "ESRGAN.tflite"
//        private const val IN_HEIGHT = 50
//        private const val IN_WIDTH = 50
//        private const val UPSCALE_FACTOR = 4
//        private val OUT_HEIGHT: Int = IN_HEIGHT * UPSCALE_FACTOR
//        private val OUT_WIDTH: Int = IN_WIDTH * UPSCALE_FACTOR
//
//        /**
//         *     todo
//         *     改造为uri
//         *     考虑 列表复用 对每个imageview进行map last处理 节流并取最后一个
//         */
//        suspend fun ImageView.loadSRImage(context: Context,url: String){
//
//            val imageLoader = context.imageLoader.newBuilder().diskCachePolicy(CachePolicy.ENABLED).build()
//            val request = ImageRequest.Builder(context)
//                .data(url)//"https://img-blog.csdnimg.cn/20210124002108308.png")
////                .diskCache(
////                    DiskCache.Builder()
////                        .directory(context.cacheDir.resolve("image_cache"))
////                        .maxSizePercent(0.02)
////                        .build()
////                )
//                .target(
//                    onStart = { placeholder ->
//                              LogUtils.d("onStart$url")
//                        // Handle the placeholder drawable.
//                    },
//                    onSuccess = { result ->
//                        LogUtils.d("onSuccess$url")
//                        // Handle the successful result.
//                    },
//                    onError = { error ->
//                        LogUtils.d("onError$url")
//                        // Handle the error drawable.
//                    }
//                )
//                .build()
//            flow{
//               emit(imageLoader.execute(request).drawable)
//            }.filter {
//                if (drawable != null) {
//                    true
//                } else {
//                    false
//                }
//            }.map {
//                ConvertUtils.drawable2Bitmap(it)
//            }.map{
//                LogUtils.d("start superResolution")
//                initSuperResolutionNative()
//                if (superResolutionNativeHandle == 0L) {
//                    LogUtils.e("superResolutionNativeHandle == 0L")
//                }
//                buildRunnable(it)
//            }.collect {
//                executorService.execute(it)
//            }
//        }
//
//        suspend fun ImageView.loadSRImage(context: Context,url: String,byInterceptor: Boolean){
//
//        }
//        class SRImageTransformation(
//            private  val url :String ="",
//            private val UPSCALE_FACTOR: Float = 1.2f,
//        ) : Transformation {
//
//
//            /**
//             * The unique cache key for this transformation.
//             *
//             * The key is added to the image request's memory cache key and should contain any params that
//             * are part of this transformation (e.g. size, scale, color, radius, etc.).
//             */
//            override val cacheKey: String
//                get() = "${javaClass.name}-$url,$UPSCALE_FACTOR"
//
//            /**
//             * Apply the transformation to [input] and return the transformed [Bitmap].
//             *
//             * @param input The input [Bitmap] to transform.
//             *  Its config will always be [ARGB_8888] or [RGBA_F16].
//             * @param size The size of the image request.
//             * @return The transformed [Bitmap].
//             */
//            override suspend fun transform(input: Bitmap, size: Size): Bitmap {
//                return doSuperResolution(input,UPSCALE_FACTOR) ?: input
//            }
//        }
//
//
//
//        fun buildRunnable(bitmap: Bitmap) :Runnable{
//          return  Runnable{
//               // doSuperResolution(bitmap, UPSCALE_FACTOR)
//            }
//        }
//
//        private fun doSuperResolution(bitmap: Bitmap, UPSCALE_FACTOR: Float) :Bitmap?{
//            val w: Int = bitmap.width
//            val h: Int = bitmap.height
//            val inputBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h)
//            val lowResRGB = IntArray(h * w)
//            val max_a = Math.ceil((w.toFloat() / IN_WIDTH).toDouble()).toInt()
//            val max_b = Math.ceil((h.toFloat() / IN_HEIGHT).toDouble()).toInt()
//            val superResRGB: IntArray = superResolutionFromJNI(superResolutionNativeHandle, lowResRGB)
//            if (superResRGB == null) {
//                LogUtils.e("Super resolution failed!")
//                return null
//            }
//            val srImgBitmap = Bitmap.createBitmap(
//                superResRGB,
//                w * this.UPSCALE_FACTOR,
//                h * this.UPSCALE_FACTOR,
//                Bitmap.Config.ARGB_8888
//            )
//            return srImgBitmap;
//        }
//
//
//        /**
//         * 碎片化的超分辨率
//         */
//        @WorkerThread
//        @Synchronized
//        fun doSuperResolutionChips(bitmap: Bitmap) {
//            var processingTimeMs = SystemClock.uptimeMillis()
//            val startTime = SystemClock.uptimeMillis()
//            var progress = 0
//            val w: Int = bitmap.width
//            val h: Int = bitmap.height
//            val max_a = Math.ceil((w.toFloat() / IN_WIDTH).toDouble()).toInt()
//            val max_b = Math.ceil((h.toFloat() / IN_HEIGHT).toDouble()).toInt()
//            val max_w = max_a * IN_WIDTH
//            val max_h = max_b * IN_HEIGHT
//            var  srBitmap = Bitmap.createBitmap(
//                w * UPSCALE_FACTOR,
//                h * UPSCALE_FACTOR,
//                Bitmap.Config.ARGB_8888
//            )
//            val inputBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h)
//            for (a in 0 until max_a) {
//                val msg = Message()
//                if (a != 0) {
//                    val bundle = Bundle()
//                    bundle.putString(
//                        "progress", ("progress: " + a + "/" + max_a
//                                + ", need " + ((SystemClock.uptimeMillis() - startTime) / a * (max_a - a)) + "ms")
//                    )
//                    msg.data = bundle
//                }
////      int in_width = (max_a-a==1)?w-a*IN_WIDTH:IN_WIDTH;
//                val x =
//                    if ((max_a - a == 1)) (w - IN_WIDTH) else IN_WIDTH * a
//                for (b in 0 until max_b) {
////        int in_height = (max_b-b==1)?h-b*IN_HEIGHT:IN_HEIGHT;
//                    val y =
//                        if ((max_b - b == 1)) h - IN_HEIGHT else IN_HEIGHT * b
//                    val lowResRGB = IntArray(IN_WIDTH * IN_HEIGHT)
//                    inputBitmap.getPixels(
//                        lowResRGB,
//                        0,
//                        IN_WIDTH,
//                        x,
//                        y,
//                        IN_WIDTH,
//                        IN_HEIGHT
//                    )
//                    srBitmap.setPixels(
//                        superResolutionFromJNI(superResolutionNativeHandle, lowResRGB),
//                        0,
//                        OUT_WIDTH,
//                        UPSCALE_FACTOR * x,
//                        UPSCALE_FACTOR * y,
//                        OUT_WIDTH,
//                        OUT_HEIGHT
//                    )
//                    if (progress < 0) {
//                        processingTimeMs = -1
//                        return
//                    }
//                    progress++
//                }
//            }
//            processingTimeMs = SystemClock.uptimeMillis() - startTime
////            val msg = Message()
////            val bundle = Bundle()
////            bundle.putString("progress", "Inference time: " + processingTimeMs + "ms")
////            msg.data = bundle
//           // this@UIhandler.sendMessage(msg)
//        }
//
//        var gpuSwitch = false;// gpuSwitch.isChecked()
//        var useGPU = false;
//        fun initSuperResolutionNative(){
//            if (superResolutionNativeHandle == 0L)
//            {
//                superResolutionNativeHandle = initTFLiteInterpreter(gpuSwitch)
//            } else if (useGPU != gpuSwitch)
//            {
//                // We need to reinitialize interpreter when execution hardware is changed
//                deinit()
//                superResolutionNativeHandle = initTFLiteInterpreter(gpuSwitch)
//            }
//        }
//        private var model: MappedByteBuffer? = null
//        private  var superResolutionNativeHandle: Long = 0
//        private external fun superResolutionFromJNI(
//            superResolutionNativeHandle: Long,
//            lowResRGB: IntArray
//        ): IntArray
//
//        private external fun initWithByteBufferFromJNI(
//            modelBuffer: MappedByteBuffer,
//            useGPU: Boolean
//        ): Long
//
//        private external fun deinitFromJNI(superResolutionNativeHandle: Long)
//
//        private fun initTFLiteInterpreter(useGPU: Boolean): Long {
//            try {
//                model = loadModelFile()
//            } catch (e: IOException) {
//                LogUtils.e("Fail to load model", e)
//            }
//            return initWithByteBufferFromJNI(model!!, useGPU)
//        }
//
//        private fun deinit() {
//            deinitFromJNI(superResolutionNativeHandle)
//        }
//
//        @Throws(IOException::class)
//        private fun loadModelFile(): MappedByteBuffer? {
//            AssetsUtil.getAssetFileDescriptorOrCached(
//                context,
//                MODEL_NAME
//            ).use { fileDescriptor ->
//                FileInputStream(fileDescriptor.fileDescriptor).use { inputStream ->
//                    val fileChannel: FileChannel = inputStream.channel
//                    val startOffset: Long = fileDescriptor.startOffset
//                    val declaredLength: Long = fileDescriptor.declaredLength
//                    return fileChannel.map(
//                        FileChannel.MapMode.READ_ONLY,
//                        startOffset,
//                        declaredLength
//                    )
//                }
//            }
//        }
//    }
//
//}