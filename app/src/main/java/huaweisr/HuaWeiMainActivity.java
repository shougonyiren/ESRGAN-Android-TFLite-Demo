//package huaweisr;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.provider.MediaStore;
//
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.os.Build;
//import android.content.pm.PackageManager;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.huawei.hiai.vision.common.VisionImage;
//import com.huawei.hiai.vision.common.VisionCallback;
//import com.huawei.hiai.vision.visionkit.common.VisionConfiguration;
//import com.huawei.hiai.vision.visionkit.image.ImageResult;
//import com.huawei.hiai.vision.visionkit.image.sr.SISRConfiguration;
//import com.huawei.hiai.vision.image.sr.ImageSuperResolution;
//import com.huawei.hiai.vision.common.VisionBase;
//
//public class HuaWeiMainActivity extends AppCompatActivity {
//    private static final String TAG = "SISR MainActivity";
//    private static final int PHOTO_REQUEST_GALLERY = 2;
//    private static final int STORAGE_REQUEST = 0x0010;
//
//    private static final int TYPE_SHOW_SRC_IMG = 1;
//    private static final int TYPE_SHOW_SR_IMG =2;
//
//    private Bitmap mBitmap;
//    private Bitmap mBitmapSR;
//
//    private Button mBtnSrcImg;
//    private Button mBtnStartSR1x;
//    private Button mBtnStartSR3x;
//
//    private ImageView mImageViewSrc;
//    private ImageView mImageViewSR;
//
//    private TextView mTxtViewResult;
//
//    private Context mContext;
//    private boolean isRunning = false;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//
//        Log.d(TAG, "SISR MainActivity !!!! ");
//
//        super.onCreate(savedInstanceState);
//
//        mContext = getApplicationContext();
//        setContentView(R.layout.activity_sisr_layout);
//
//        mImageViewSrc = (ImageView) findViewById(R.id.imgViewSrc);
//        mImageViewSR = (ImageView) findViewById(R.id.imgViewSR);
//        mBtnSrcImg = (Button) findViewById(R.id.btnSrcImg);
//        mBtnStartSR1x = (Button) findViewById(R.id.btnStartSR1x);
//        mBtnStartSR3x = (Button) findViewById(R.id.btnStartSR3x);
//        mTxtViewResult = (TextView) findViewById(R.id.result);
//    }
//
//    public void onClickBtn(View v) {
//        switch (v.getId()) {
//            case R.id.btnSrcImg: {
//                int rslt = requestPermissions();
//
//                if ( 0 == rslt) {
//                    Intent intent = new Intent(Intent.ACTION_PICK);
//                    intent.setType("image/*");
//                    startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
//                }
//
//                break;
//            }
//            case R.id.btnStartSR1x: {
//                startSR(SISRConfiguration.SISR_SCALE_1X);
//                break;
//            }
//            case R.id.btnStartSR3x: {
//                startSR(SISRConfiguration.SISR_SCALE_3X);
//                break;
//            }
//            default:
//                break;
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode == Activity.RESULT_OK) {
//            if (data == null){
//                return;
//            }
//
//            Uri selectedImage = data.getData();
//            getBitmap(selectedImage);
//        }
//    }
//
//    private void getBitmap(Uri imageUri) {
//        String[] pathColumn = {MediaStore.Images.Media.DATA};
//
//        // 从系统表中查询指定Uri对应的照片
//        // Query the image corresponding to the URI from the system table.
//        Cursor cursor = getContentResolver().query(imageUri,pathColumn, null, null, null);
//        cursor.moveToFirst();
//
//        int columnIndex = cursor.getColumnIndex(pathColumn[0]);
//
//        // 获取照片路径
//        // Get the image path
//        String picturePath = cursor.getString(columnIndex);
//        cursor.close();
//
//        mBitmap = BitmapFactory.decodeFile(picturePath);
//        mHander.sendEmptyMessage(TYPE_SHOW_SRC_IMG);
//    }
//
//    private Handler mHander = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            int status = msg.what;
//
//            switch (status) {
//                case TYPE_SHOW_SRC_IMG: {
//                    if (mBitmap == null) {
//                        Log.e(TAG, "Input Bitmap is null!");
//
//                        mTxtViewResult.setText("Input Bitmap is null!");
//                        return;
//                    }
//
//                    mImageViewSrc.setImageBitmap(mBitmap);
//                    mTxtViewResult.setText("Ready to run SISR!");
//
//                    break;
//                }
//                case TYPE_SHOW_SR_IMG: {
//                    if (msg.obj == null) {
//                        Log.e(TAG, "SISR result is null!");
//
//                        mTxtViewResult.setText("SISR result is null!");
//                        return;
//                    }
//
//                    mTxtViewResult.setText("Succeed!");
//
//                    ImageResult result = (ImageResult) msg.obj;
//                    mBitmapSR = result.getBitmap();
//                    mImageViewSR.setImageBitmap(mBitmapSR);
//                    break;
//                }
//                default:
//                    break;
//            }
//        }
//    };
//
//    private void startSR(final float scale) {
//        if (!isRunning) {
//            isRunning = true;
//            mTxtViewResult.setText("Begin to run SISR.");
//
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//
//                    if (mBitmap == null) {
//                        Log.e(TAG, "Input Bitmap is null!");
//
//                        mTxtViewResult.setText("Input Bitmap is null!");
//                        return;
//                    }
//
//                    Log.d(TAG, "Start SISR");
//
//                    // 连接AI引擎
//                    // Connect to AI Engine
//                    VisionBase.init(getApplicationContext(), ConnectManager.getInstance().getmConnectionCallback());
//
//                    if (!ConnectManager.getInstance().isConnected()) {
//                        ConnectManager.getInstance().waitConnect();
//                    }
//
//                    if (!ConnectManager.getInstance().isConnected()) {
//                        Log.e(TAG, "Can't connect to server.");
//
//                        mTxtViewResult.setText("Can't connect to server!");
//
//                        return;
//                    }
//
//                    // 准备输入图片
//                    // Prepare input bitmap
//                    VisionImage image = VisionImage.fromBitmap(mBitmap);
//
//                    // 创建超分对象
//                    // Create SR object
//                    ImageSuperResolution superResolution = new ImageSuperResolution(mContext);
//
//                    // 准备超分配置
//                    // Prepare SR configuration
//                    SISRConfiguration paras = new SISRConfiguration
//                            .Builder()
//                            .setProcessMode(VisionConfiguration.MODE_OUT)
//                            .build();
//                    paras.setScale(scale);
//                    paras.setQuality(SISRConfiguration.SISR_QUALITY_HIGH);
//
//                    // 设置超分
//                    // Config SR
//                    superResolution.setSuperResolutionConfiguration(paras);
//
//                    // 执行超分
//                    // Run SR
//                    ImageResult result = new ImageResult();
//                    long startTime = System.currentTimeMillis();
//                    int resultCode = superResolution.doSuperResolution(image, result, visionCallback);
//                    long endTime = System.currentTimeMillis(); // 获取结束时间
//                    Log.e("TestTime", "Runtime: " + (endTime - startTime));
//
//                    if (resultCode == 700) {
//                            Log.d(TAG, "Wait for result.");
//
//                            return;
//                    } else if ( resultCode != 0 ) {
//                        Log.e(TAG, "Failed to run super-resolution, return : " + resultCode);
//
//                        mTxtViewResult.setText("Failed to run SISR!");
//                        return;
//                    }
//
//                    if (result == null) {
//                        Log.e(TAG, "Result is null!");
//
//                        mTxtViewResult.setText("SISR result is null!");
//
//                        return;
//                    }
//                    if (result.getBitmap() == null) {
//                        Log.e(TAG, "Result bitmap is null!");
//
//                        mTxtViewResult.setText("SISR result has null bitmap!");
//                        return;
//                    }
//
//                    Message msg = new Message();
//                    msg.what = TYPE_SHOW_SR_IMG;
//                    msg.obj = result;
//                    mHander.sendMessage(msg);
//                }
//            }).start();
//
//            isRunning = false;
//        }
//    }
//
//    VisionCallback<ImageResult> visionCallback = new VisionCallback<ImageResult>() {
//        @Override
//        public void onResult(ImageResult imageResult) {
//            if (imageResult.getBitmap() == null) {
//                Log.e(TAG, "onResult   Result bitmap is null!");
//
//                mTxtViewResult.setText("TxtSR result has null bitmap!");
//                return;
//            }
//
//            Message msg = new Message();
//            msg.what = TYPE_SHOW_SR_IMG;
//            msg.obj = imageResult;
//            mHander.sendMessage(msg);
//        }
//
//        @Override
//        public void onError(int i) {
//            Log.e(TAG, "onError: " + i);
//        }
//
//        @Override
//        public void onProcessing(float v) {
//            Log.e(TAG, "onProcessing: " + v);
//        }
//    };
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if ( (requestCode == STORAGE_REQUEST) &&
//                (grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED) ) {
//            Intent intent = new Intent(Intent.ACTION_PICK);
//            intent.setType("image/*");
//            startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
//        }
//    }
//
//    private int requestPermissions(){
//
//        int rslt = -1;
//
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
//                if(permission!= PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},0x0010);
//                }
//                else {
//                    rslt = 0;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return rslt;
//    }
//}
//
