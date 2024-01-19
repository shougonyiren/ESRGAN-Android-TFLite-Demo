package common.adapter


import android.content.Context
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import com.blankj.utilcode.util.LogUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.lh.SuperResolutionImage.HuaWeiObsUtils
import org.tensorflow.lite.examples.superresolution.R
import org.tensorflow.lite.examples.superresolution.databinding.RcCommonImageBinding

/**

 * @Author : liuhao02

 * @Time : On 2024/1/10 10:01

 * @Description : ImageListAdapter

 */
class ImageListAdapter(data: List<String>) : BaseQuickAdapter<String,ImageListAdapter.VH>(data) {

    var listener: PictureClickListener? = null
    class VH(
        parent: ViewGroup,
        val binding: RcCommonImageBinding = RcCommonImageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)
    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * 实现此方法，并使用 [holder] 完成 item 视图的操作
     *
     * @param holder A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    override fun onBindViewHolder(holder: ImageListAdapter.VH, position: Int, item: String?) {
        //holder.binding.image.loadSRImage(
       LogUtils.d("onBindViewHolder position"+position.toString()+" item:" +item.toString())
        item?.let {
            var url= HuaWeiObsUtils.thumbnailFromUrl(item,200,200)
            var startTime :Long= 0
            var endTime:Long= 0
            holder.binding.image.load(url){
                error(R.mipmap.ic_launcher)
                memoryCachePolicy(CachePolicy.DISABLED)
                networkCachePolicy(CachePolicy.ENABLED)
                diskCachePolicy(CachePolicy.DISABLED)
                listener(
                    onStart = { request ->
                        startTime = SystemClock.uptimeMillis()
                        LogUtils.d("TestTime", "onStart networkThumbnail" + startTime)
                    },
                    onError = { request, throwable ->
                        endTime = SystemClock.uptimeMillis() // 获取结束时间
                        LogUtils.e(
                            "TestTime",
                            "onError networkThumbnail Runtime: " + (endTime - startTime)+"throwable: " + throwable.throwable.toString()
                        )
                    },
                    onCancel = { request ->
                        endTime = SystemClock.uptimeMillis() // 获取结束时间
                        LogUtils.e(
                            "TestTime",
                            "onCancel networkThumbnail Runtime: " + (endTime - startTime)
                        )
                    },
                    onSuccess = { request, metadata ->
                        endTime = SystemClock.uptimeMillis() // 获取结束时间
                        LogUtils.e(
                            "TestTime",
                            "onSuccess networkThumbnail Runtime: " + (endTime - startTime)
                        )
                    }
                )
            }
            holder.binding.image.setOnClickListener {
                listener?.onClick(item,position)
            }
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): ImageListAdapter.VH {
        return VH(parent)
    }

    fun setPictureClickListener(addListener: PictureClickListener) {
        this.listener = addListener
    }

    interface PictureClickListener {
        fun onClick(path: String,  position: Int)
    }
}