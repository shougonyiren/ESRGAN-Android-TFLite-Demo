import android.content.Context
import android.widget.ImageView
import coil.imageLoader
import coil.request.ImageRequest
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.LogUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import java.net.URL
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
class SRImageUtils{

    companion object{
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

    }

}