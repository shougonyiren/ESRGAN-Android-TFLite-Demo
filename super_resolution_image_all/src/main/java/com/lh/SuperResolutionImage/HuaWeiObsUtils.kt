package com.lh.SuperResolutionImage

import android.net.Uri
import android.text.TextUtils
import com.blankj.utilcode.util.LogUtils

/**

 * @Author : liuhao02

 * @Time : On 2024/1/9 15:11

 * @Description : HuaWeiObsUtils

 */
class HuaWeiObsUtils {
    companion object {
        fun Uri.addUriParameter(key: String, newValue: String): Uri {
            val params = queryParameterNames
            val newUri = buildUpon().clearQuery()
            var isSameParamPresent = false
            for (param in params) {
                // if same param is present override it, otherwise add the old param back
                newUri.appendQueryParameter(param,
                    if (param == key) newValue else getQueryParameter(param))
                if (param == key) {
                    // make sure we do not add new param again if already overridden
                    isSameParamPresent = true
                }
            }
            if (!isSameParamPresent) {
                // never overrode same param so add new passed value now
                newUri.appendQueryParameter(key,
                    newValue)
            }
            return newUri.build()
        }
        fun thumbnailFromUrl(url: String, width: Int, height: Int, quality: Int?=100): String {
            if (TextUtils.isEmpty(url)) {//todo 标识华为云域名
                return url;
            }
            val modifiedUri = Uri.parse(url).addUriParameter("x-image-process", "image/resize,w_${width},h_${height}/quality,Q_${quality}")
            LogUtils.d("thumbnailFromUrl: " + modifiedUri.toString())
            return modifiedUri.toString();
        }


        fun reductionThumbnailFromUrl(url: String, width: Int=200, height: Int=200, quality: Int=100): String {
            if (TextUtils.isEmpty(url)) {//todo 标识华为云域名
                return url;
            }
            val modifiedUri = Uri.parse(url).addUriParameter("x-image-process", "image/resize,w_${width},h_${height}/quality,Q_${quality}")
            return modifiedUri.toString();
        }
    }



//    static String thumbnailFromUrl(String url,
//    {int? width, int? height, int? quality}) {
//        if (url.isEmpty) return '';
//        String pwidth = ((width ?? 0) > 0) ? width.toString() : '64';
//        String pheight = ((height ?? 0) > 0) ? height.toString() : '64';
//        String pquality = ((quality ?? 0) > 0) ? quality.toString() : '100';
//        String parameters =
//        '?x-image-process=image/resize,w_${pwidth},h_${pheight}/quality,Q_${pquality}';
//        String res = url + parameters;
//        return res;
//    }
}