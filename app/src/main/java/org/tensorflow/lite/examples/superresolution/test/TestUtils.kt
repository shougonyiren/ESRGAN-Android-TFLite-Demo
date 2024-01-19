package org.tensorflow.lite.examples.superresolution.test

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger

/**

 * @Author : liuhao02

 * @Time : On 2024/1/19 10:55

 * @Description : TestUtils

 */
class TestUtils {
    companion object{
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
    }

    fun newImageCacheLoader( context: Context): ImageLoader {
        //在这里可以自定义我们的ImageLoader
        return ImageLoader(context).newBuilder()
            .memoryCachePolicy(CachePolicy.ENABLED)//开启内存缓存策略
            .memoryCache {//构建内存缓存
                MemoryCache.Builder(context)
                    .maxSizePercent(0.1)//最大只能使用剩余内存空间的10%
                    .strongReferencesEnabled(true)//开启强引用
                    .build()
            }
            .diskCachePolicy(CachePolicy.ENABLED)//开启磁盘缓存策略
            .diskCache { //构建磁盘缓存
                DiskCache.Builder()
                    .maxSizePercent(0.03)//最大只能使用剩余内存空间的3%
                    .directory(context.cacheDir)//存放在缓存目录
                    .build()
            }
            //.callFactory {
            //      //允许你拦截Coil发出的请求，假设你请求一张需要身份验证的图片
            //      //因此你只需要附加token到http请求的标头即可，默认情况下Coil是不会拦截的
            //      Call.Factory{
            //            it.newBuilder()
            //                  .addHeader("Authorization", "")
            //                  .build()
            //      }
            //}
            .logger(DebugLogger())//开启调试记录
            .build()
    }

    fun newImageDiskCacheLoader( context: Context): ImageLoader {
        //在这里可以自定义我们的ImageLoader
        return ImageLoader(context).newBuilder()
            .memoryCachePolicy(CachePolicy.DISABLED)//开启内存缓存策略
            .memoryCache {//构建内存缓存
                MemoryCache.Builder(context)
                    .maxSizePercent(0.1)//最大只能使用剩余内存空间的10%
                    .strongReferencesEnabled(true)//开启强引用
                    .build()
            }
            .diskCachePolicy(CachePolicy.ENABLED)//开启磁盘缓存策略
            .diskCache { //构建磁盘缓存
                DiskCache.Builder()
                    .maxSizePercent(0.03)//最大只能使用剩余内存空间的3%
                    .directory(context.cacheDir)//存放在缓存目录
                    .build()
            }
            //.callFactory {
            //      //允许你拦截Coil发出的请求，假设你请求一张需要身份验证的图片
            //      //因此你只需要附加token到http请求的标头即可，默认情况下Coil是不会拦截的
            //      Call.Factory{
            //            it.newBuilder()
            //                  .addHeader("Authorization", "")
            //                  .build()
            //      }
            //}
            .logger(DebugLogger())//开启调试记录
            .build()
    }

    fun newImageMemoryCacheLoader( context: Context): ImageLoader {
        //在这里可以自定义我们的ImageLoader
        return ImageLoader(context).newBuilder()
            .memoryCachePolicy(CachePolicy.DISABLED)//开启内存缓存策略
            .memoryCache {//构建内存缓存
                MemoryCache.Builder(context)
                    .maxSizePercent(0.1)//最大只能使用剩余内存空间的10%
                    .strongReferencesEnabled(true)//开启强引用
                    .build()
            }
//            .diskCachePolicy(CachePolicy.ENABLED)//开启磁盘缓存策略
//            .diskCache { //构建磁盘缓存
//                DiskCache.Builder()
//                    .maxSizePercent(0.03)//最大只能使用剩余内存空间的3%
//                    .directory(context.cacheDir)//存放在缓存目录
//                    .build()
//            }
            //.callFactory {
            //      //允许你拦截Coil发出的请求，假设你请求一张需要身份验证的图片
            //      //因此你只需要附加token到http请求的标头即可，默认情况下Coil是不会拦截的
            //      Call.Factory{
            //            it.newBuilder()
            //                  .addHeader("Authorization", "")
            //                  .build()
            //      }
            //}
            .logger(DebugLogger())//开启调试记录
            .build()
    }
}