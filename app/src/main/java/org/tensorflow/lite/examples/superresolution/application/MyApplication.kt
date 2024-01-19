import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger

class MyApplication : Application()
  //  ,
 //   ImageLoaderFactory
{

//    override fun newImageLoader(): ImageLoader {
//        //在这里可以自定义我们的ImageLoader
//        return ImageLoader(this).newBuilder()
//            .memoryCachePolicy(CachePolicy.ENABLED)//开启内存缓存策略
//            .memoryCache {//构建内存缓存
//                MemoryCache.Builder(this)
//                    .maxSizePercent(0.1)//最大只能使用剩余内存空间的10%
//                    .strongReferencesEnabled(true)//开启强引用
//                    .build()
//            }
//            .diskCachePolicy(CachePolicy.ENABLED)//开启磁盘缓存策略
//            .diskCache { //构建磁盘缓存
//                DiskCache.Builder()
//                    .maxSizePercent(0.03)//最大只能使用剩余内存空间的3%
//                    .directory(cacheDir)//存放在缓存目录
//                    .build()
//            }
//            //.callFactory {
//            //      //允许你拦截Coil发出的请求，假设你请求一张需要身份验证的图片
//            //      //因此你只需要附加token到http请求的标头即可，默认情况下Coil是不会拦截的
//            //      Call.Factory{
//            //            it.newBuilder()
//            //                  .addHeader("Authorization", "")
//            //                  .build()
//            //      }
//            //}
//            .logger(DebugLogger())//开启调试记录
//            .build()
//    }
}
