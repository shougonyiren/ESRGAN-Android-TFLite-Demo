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
import coil.load
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

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        //  var list:MutableList<String>
        val list = listOf(
            "https://base-test-vr4cdn.5i5j.com/outputfiles/1a798326442256ac3b1e2cf095f72e48_17975/directionalPlan/NorthWest.jpg",
            "https://base-test-vr4cdn.5i5j.com/outputfiles/1a798326442256ac3b1e2cf095f72e48_17975/directionalPlan/North.jpg",
            "https://base-test-vr4cdn.5i5j.com/outputfiles/1a798326442256ac3b1e2cf095f72e48_17975/directionalPlan/NorthEast.jpg",
            "https://base-test-vr4cdn.5i5j.com/outputfiles/1a798326442256ac3b1e2cf095f72e48_17975/directionalPlan/West.jpg",
            "https://base-test-vr4cdn.5i5j.com/outputfiles/1a798326442256ac3b1e2cf095f72e48_17975/directionalPlan/East.jpg",
            "https://base-test-vr4cdn.5i5j.com/outputfiles/1a798326442256ac3b1e2cf095f72e48_17975/directionalPlan/SouthWest.jpg",
            "https://base-test-vr4cdn.5i5j.com/outputfiles/1a798326442256ac3b1e2cf095f72e48_17975/directionalPlan/South.jpg",
            "https://base-test-vr4cdn.5i5j.com/outputfiles/1a798326442256ac3b1e2cf095f72e48_17975/directionalPlan/SouthEast.jpg"
        )
        val imageListAdapter = ImageListAdapter(list)
        binding.rvTestImage.adapter = imageListAdapter
        imageListAdapter.setOnItemClickListener { adapter, view, position ->
            selectUrl = list[position]
        }
        binding.networkImage.setOnClickListener {
            val startTime = SystemClock.uptimeMillis()

            binding.networkImage.load(selectUrl)

            val endTime = SystemClock.uptimeMillis() // 获取结束时间
            LogUtils.e("TestTime", "networkImage Runtime: " + (endTime - startTime))
        }
        binding.networkThumbnailImage.setOnClickListener {
            val startTime = SystemClock.uptimeMillis()

            var url = HuaWeiObsUtils.thumbnailFromUrl(selectUrl, 400, 400)
            binding.networkThumbnailImage.load(url) {
                error(R.mipmap.ic_launcher)
            }

            val endTime = SystemClock.uptimeMillis() // 获取结束时间
            LogUtils.e("TestTime", "networkThumbnai Runtime: " + (endTime - startTime))
        }
        binding.huaweiImageStartLoadButton.setOnClickListener {
            val startTime = SystemClock.uptimeMillis()

            binding.huaweiImage.loadSRImage(requireContext(), selectUrl, 400, 400)

            val endTime = SystemClock.uptimeMillis() // 获取结束时间
            LogUtils.e("TestTime", "huaweiImage Runtime: " + (endTime - startTime))
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}