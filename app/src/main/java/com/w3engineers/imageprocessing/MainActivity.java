package com.w3engineers.imageprocessing;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity /*implements CameraBridgeViewBase.CvCameraViewListener2*/ {

    private JavaCameraView javaCameraView;
    private Mat mMat, imageGray, imageCanny;
    private static final String DATA_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/com.w3engineers.imageprocessing/";

    BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case BaseLoaderCallback.SUCCESS:
                    javaCameraView.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }

        }
    };
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_picker_view);

        imageView = findViewById(R.id.imageView);
        if (OpenCVLoader.initDebug()) {
            Toast.makeText(this, "Opencv init success", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Opencv init failed", Toast.LENGTH_LONG).show();
        }
        /*if(PermissionUtil.init(this).request(Manifest.permission.CAMERA)) {
            javaCameraView = (JavaCameraView) findViewById(R.id.java_camera_view);
            javaCameraView.setVisibility(SurfaceView.VISIBLE);

            javaCameraView.setCvCameraViewListener(this);
        }*/
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public void openGlarrey(View view){
        if(PermissionUtil.init(this).request(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            File file = new File(DATA_PATH);

            if(!file.exists()){
                file.mkdirs();
            }
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 100);
        }
    }



    public void convertGray(View view){
        Mat rgb = new Mat();
        Mat gray = new Mat();
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inDither = false;
        options.inSampleSize = 4;

        int width = imageBitmap.getWidth();
        int hight = imageBitmap.getHeight();
        grayBitmap = Bitmap.createBitmap(width, hight, Bitmap.Config.RGB_565);

        Utils.bitmapToMat(imageBitmap, rgb);
        Imgproc.cvtColor(rgb, gray, Imgproc.COLOR_RGB2GRAY);
        Imgproc.Canny(gray, gray, 50,150);
        Utils.matToBitmap(gray, grayBitmap);

        imageView.setImageBitmap(grayBitmap);
        new ocrTask(grayBitmap).execute();
    }

    Uri imageUri;
    Bitmap grayBitmap, imageBitmap;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(imageBitmap);


        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*if (javaCameraView != null) {
            javaCameraView.disableView();
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*if (javaCameraView != null) {
            javaCameraView.disableView();
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if (OpenCVLoader.initDebug()) {
            mLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        } else {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);
        }*/
    }

    /*@Override
    public void onCameraViewStarted(int width, int height) {
        mMat = new Mat(width, height, CvType.CV_8UC4);
        imageGray = new Mat(width, height, CvType.CV_8UC1);
        imageCanny = new Mat(width, height, CvType.CV_8UC1);
    }

    @Override
    public void onCameraViewStopped() {
        mMat.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mMat = inputFrame.rgba();
        Imgproc.cvtColor(mMat, imageGray, Imgproc.COLOR_RGB2GRAY);
        Imgproc.Canny(imageGray, imageCanny, 50,150);
        return imageCanny;
    }*/


    private class ocrTask extends AsyncTask<Void, Void, Void> {
        Bitmap bitmap;
        TessBaseAPI baseApi;
        public ocrTask(Bitmap grayBitmap){
            this.bitmap = grayBitmap;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("OCRTask","onPreExecute..");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.i("OCRTask","onPostExecute..");

        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i("OCRTask","extracting..");
            String text = recognizeText();
            Log.i("OCRTask","extracting = "+text);
            return null;
        }

        private String recognizeText(){
            String language = "";

            baseApi = new TessBaseAPI();
            baseApi.init(DATA_PATH, "eng",TessBaseAPI.OEM_TESSERACT_ONLY);
            baseApi.setImage(bitmap);
            return baseApi.getUTF8Text();

        }
    }


}
