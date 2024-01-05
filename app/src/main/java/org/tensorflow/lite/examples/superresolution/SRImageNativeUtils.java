package org.tensorflow.lite.examples.superresolution;

import android.util.Log;

import java.io.IOException;
import java.nio.MappedByteBuffer;

/**
 * @Author : liuhao02
 * @Time : On 2024/1/2 16:49
 * @Description : SRImageNativeUtils
 */
public class SRImageNativeUtils {
    static  {
        System.loadLibrary("SuperResolution");
    }
//    private long initTFLiteInterpreter(boolean useGPU) {
//        try {
//            model = loadModelFile();
//        } catch (IOException e) {
//            Log.e(TAG, "Fail to load model", e);
//        }
//        return initWithByteBufferFromJNI(model, useGPU);
//    }

//    private void deinit() {
//        deinitFromJNI(superResolutionNativeHandle);
//    }

    private native int[] superResolutionFromJNI(long superResolutionNativeHandle, int[] lowResRGB);

    private native long initWithByteBufferFromJNI(MappedByteBuffer modelBuffer, boolean useGPU);

    private native void deinitFromJNI(long superResolutionNativeHandle);
}
