package com.lh.SuperResolutionImage

import android.text.TextUtils

/**

 * @Author : liuhao02

 * @Time : On 2024/1/9 15:11

 * @Description : HuaWeiObsUtils

 */
class HuaWeiObsUtils {
    companion object {
        fun thumbnailFromUrl(url: String, width: Int, height: Int, quality: Int?=100): String {
            if (TextUtils.isEmpty(url)) {//todo 标识华为云域名
                return url;
            }
            val mUrl =
                url.plus("?x-image-process=image/resize,w_${width},h_${height}/quality,Q_${quality}")
            return mUrl;
        }



        fun reductionThumbnailFromUrl(url: String, width: Int, height: Int, quality: Int): String {
            if (TextUtils.isEmpty(url)) {//todo 标识华为云域名
                return url;
            }
            val mUrl =
                url.plus("?x-image-process=image/resize,w_${width},h_${height}/quality,Q_${quality}")
            return mUrl;
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