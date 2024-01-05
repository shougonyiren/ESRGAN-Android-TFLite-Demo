import android.content.Context
import android.util.Log
import android.widget.ImageView
import coil.imageLoader
import coil.request.ImageRequest
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.LogUtils
import com.lh.SuperResolutionImage.AssetsUtil
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class SRImageUtils{

    companion object{
        fun init(context: Context){
             this.context = context
        }

        private lateinit var context: Context
        private const val MAX_BITMAP_SIZE =
            100 * 1024 * 1024 // 100 MB 最大的图片 trying to draw too large(159907840bytes) bitmap.

        private const val MODEL_NAME = "ESRGAN.tflite"
        private const val IN_HEIGHT = 50
        private const val IN_WIDTH = 50
        private const val UPSCALE_FACTOR = 4
        private val OUT_HEIGHT: Int = IN_HEIGHT * UPSCALE_FACTOR
        private val OUT_WIDTH: Int = IN_WIDTH * UPSCALE_FACTOR

        /**
         *     todo
         *     改造为uri
         *     考虑 列表复用 对每个imageview进行map last处理 节流并取最后一个
         */
        suspend fun ImageView.loadSRImage(context: Context,url: String){
            val imageLoader = context.imageLoader
            val request = ImageRequest.Builder(context)
                .data(url)//"https://img-blog.csdnimg.cn/20210124002108308.png")
                .target(
                    onStart = { placeholder ->
                              LogUtils.d("onStart$url")
                        // Handle the placeholder drawable.
                    },
                    onSuccess = { result ->
                        LogUtils.d("onSuccess$url")
                        // Handle the successful result.
                    },
                    onError = { error ->
                        LogUtils.d("onError$url")
                        // Handle the error drawable.
                    }
                )
                .build()
            flow{
               emit(imageLoader.execute(request).drawable)
            }.filter {
                if (drawable != null) {
                    true
                } else {
                    false
                }
            }.map {
                ConvertUtils.drawable2Bitmap(it)
            }.collect(){

            }
        }
        private var model: MappedByteBuffer? = null
        private const val superResolutionNativeHandle: Long = 0
        private external fun superResolutionFromJNI(
            superResolutionNativeHandle: Long,
            lowResRGB: IntArray
        ): IntArray?

        private external fun initWithByteBufferFromJNI(
            modelBuffer: MappedByteBuffer,
            useGPU: Boolean
        ): Long

        private external fun deinitFromJNI(superResolutionNativeHandle: Long)

        private fun initTFLiteInterpreter(useGPU: Boolean): Long {
            try {
                model = loadModelFile()
            } catch (e: IOException) {
                LogUtils.e("Fail to load model", e)
            }
            return initWithByteBufferFromJNI(model!!, useGPU)
        }

        private fun deinit() {
            deinitFromJNI(superResolutionNativeHandle)
        }

        @Throws(IOException::class)
        private fun loadModelFile(): MappedByteBuffer? {
            AssetsUtil.getAssetFileDescriptorOrCached(
                context,
                MODEL_NAME
            ).use { fileDescriptor ->
                FileInputStream(fileDescriptor.fileDescriptor).use { inputStream ->
                    val fileChannel: FileChannel = inputStream.channel
                    val startOffset: Long = fileDescriptor.startOffset
                    val declaredLength: Long = fileDescriptor.declaredLength
                    return fileChannel.map(
                        FileChannel.MapMode.READ_ONLY,
                        startOffset,
                        declaredLength
                    )
                }
            }
        }
    }

}