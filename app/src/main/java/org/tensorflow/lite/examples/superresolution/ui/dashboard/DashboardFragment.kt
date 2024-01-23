package org.tensorflow.lite.examples.superresolution.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.annotation.ExperimentalCoilApi
import common.adapter.ImageListAdapter
import org.tensorflow.lite.examples.superresolution.MyApplication
import org.tensorflow.lite.examples.superresolution.R
import org.tensorflow.lite.examples.superresolution.databinding.FragmentDashboardBinding
import org.tensorflow.lite.examples.superresolution.test.TestUtils


//1.写个列表 若干obs的图
//2.加载原图 加载缩放参数图  加载缩放参数加超分的图
//3.计算耗时
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @OptIn(ExperimentalCoilApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val list = TestUtils.list
        val imageListAdapter = ImageListAdapter(listOf<String>())
        with(binding) {
            cleanImageCache.setOnClickListener {
                imageListAdapter.imageLoader?.diskCache?.clear();
                imageListAdapter.imageLoader?.memoryCache?.clear();
            }
            rvTestImage.adapter = imageListAdapter;
            checkbox.setOnCheckedChangeListener { group, checkedId ->
                when(checkedId) {
                    //todo:
                    R.id.radio_image->{
                        var list:List<String> = TestUtils.list;
                        imageListAdapter.toSR=false;
                        imageListAdapter.items=list;
                        imageListAdapter.notifyDataSetChanged()
                    }
                    R.id.radio_thumbnail->{
                        var list:List<String> = TestUtils.getThumbnailList(200,200);
                        imageListAdapter.toSR=false;
                        imageListAdapter.items=list;
                        imageListAdapter.notifyDataSetChanged()
                    }
                    R.id.radio_sr_thumbnail->{
                        var list:List<String> = TestUtils.getThumbnailList(200,200);
                        imageListAdapter.toSR=true;
                        imageListAdapter.items=list;
                        imageListAdapter.notifyDataSetChanged()
                    }
                    else -> {

                    }
                }
            }
        }
//        val textView: TextView = binding.textDashboard
//        dashboardViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}