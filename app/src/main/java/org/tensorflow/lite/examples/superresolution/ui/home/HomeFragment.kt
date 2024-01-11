package org.tensorflow.lite.examples.superresolution.ui.home

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.imageLoader
import coil.load
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.blankj.utilcode.util.LogUtils
import com.lh.SuperResolutionImage.HuaWeiObsUtils
import com.lh.SuperResolutionImage.SRHuaweiImage
import com.lh.SuperResolutionImage.SRHuaweiImage.Companion.loadSRImage
import common.adapter.ImageListAdapter
import org.tensorflow.lite.examples.superresolution.R
import org.tensorflow.lite.examples.superresolution.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {


    var selectUrl: String = ""

    //1.写个列表 若干obs的图
    //2.加载原图 加载缩放参数图  加载缩放参数加超分的图
    //3.计算耗时
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        SRHuaweiImage.getInstance(requireContext())
//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        //  var list:MutableList<String>
        val list = listOf(
            "https://huawei-vr-test.obs.cn-north-4.myhuaweicloud.com/mobile_test/3dian92mb.jpg",
            "https://image2a.5i5j.com/scm/HOUSE_CUSTOMER/aada58e53f4e4a6ead5a5457d9562848.jpg",
            "https://huawei-vr-test.obs.cn-north-4.myhuaweicloud.com/mobile_test/169kb.jpg",
            "https://huawei-vr-test.obs.cn-north-4.myhuaweicloud.com/mobile_test/7dian33mb.jpg",
            "https://huawei-vr-test.obs.cn-north-4.myhuaweicloud.com/mobile_test/705kb.jpg",
            "https://base-test-vr4cdn.5i5j.com/outputfiles/1a798326442256ac3b1e2cf095f72e48_17975/directionalPlan/NorthWest.jpg",
            "https://base-test-vr4cdn.5i5j.com/outputfiles/1a798326442256ac3b1e2cf095f72e48_17975/directionalPlan/West.jpg",
            "https://base-test-vr4cdn.5i5j.com/outputfiles/1a798326442256ac3b1e2cf095f72e48_17975/directionalPlan/East.jpg",
            "https://base-test-vr4cdn.5i5j.com/outputfiles/1a798326442256ac3b1e2cf095f72e48_17975/directionalPlan/SouthWest.jpg",
            "https://base-test-vr4cdn.5i5j.com/outputfiles/1a798326442256ac3b1e2cf095f72e48_17975/directionalPlan/South.jpg",
            "https://base-test-vr4cdn.5i5j.com/outputfiles/1a798326442256ac3b1e2cf095f72e48_17975/directionalPlan/SouthEast.jpg",
            "https://huawei-vr-test.obs.cn-north-4.myhuaweicloud.com/mobile_test/1dian61kb.png",
        )
        //"https://home.5i5j.com/static/png/values-daac8bcf.png",
        //"https://file-dmp.obs.cn-north-4.myhuaweicloud.com/portal-web/portal-web/20231128/1701165724121/419751/GONw5d636sHeBbXETYKh.png?AccessKeyId=WVQXKWAJLWTZNCOXJW2I&Expires=1704883713&Signature=GWzGSLpzRt6x5iklHeVPEnMwF1Q%3D",
        //
        selectUrl = list[0]
        val imageListAdapter = ImageListAdapter(list)
        binding.rvTestImage.adapter = imageListAdapter
        imageListAdapter.setPictureClickListener(object : ImageListAdapter.PictureClickListener{
            override fun onClick(path: String, position: Int) {
                selectUrl = list[position]
                LogUtils.d(" imageListAdapter.setOnItemClickListener position:$position selectUrl:$selectUrl")
            }
        })
        binding.networkImageStartLoadButton.setOnClickListener {
            var startTime: Long = 0
            var endTime: Long = 0
            binding.networkImage.load(selectUrl) {
                error(R.mipmap.ic_launcher)
                memoryCachePolicy(CachePolicy.DISABLED)
                networkCachePolicy(CachePolicy.ENABLED)
                diskCachePolicy(CachePolicy.DISABLED)
                listener(
                    onStart = { request ->
                        startTime = SystemClock.uptimeMillis()
                        LogUtils.d("TestTime", "onStart networkImage")
                    },
                    onError = { request, throwable ->
                        endTime = SystemClock.uptimeMillis() // 获取结束时间
                        LogUtils.e(
                            "TestTime",
                            "onError + networkImage Runtime: " + (endTime - startTime)+"throwable: " + throwable.throwable.toString()
                        )
                    },
                    onCancel = { request ->
                        endTime = SystemClock.uptimeMillis() // 获取结束时间
                        LogUtils.e(
                            "TestTime",
                            "onCancel networkImage Runtime: " + (endTime - startTime)
                        )
                    },
                    onSuccess = { request, metadata ->
                        endTime = SystemClock.uptimeMillis() // 获取结束时间
                        LogUtils.e(
                            "TestTime",
                            "onSuccess networkImage Runtime: " + (endTime - startTime)
                        )
                    }
                )
            }
        }
        binding.networkThumbnailStartLoadButton.setOnClickListener {
            var startTime: Long = 0
            var endTime: Long = 0
            var url = HuaWeiObsUtils.thumbnailFromUrl(selectUrl, 400, 400)
            binding.networkThumbnailImage.load(url) {
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
        }
        binding.huaweiImageStartLoadButton.setOnClickListener {
            binding.huaweiImage.loadSRImage(requireContext(), selectUrl, 400, 400)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}