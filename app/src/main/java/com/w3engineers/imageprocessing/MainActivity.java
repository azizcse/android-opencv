package com.w3engineers.imageprocessing;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private JavaCameraView javaCameraView;
    private Mat mMat, imageGray, imageCanny;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (OpenCVLoader.initDebug()) {
            Toast.makeText(this, "Opencv init success", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Opencv init failed", Toast.LENGTH_LONG).show();
        }
        if(PermissionUtil.init(this).request(Manifest.permission.CAMERA)) {
            javaCameraView = (JavaCameraView) findViewById(R.id.java_camera_view);
            javaCameraView.setVisibility(SurfaceView.VISIBLE);

            javaCameraView.setCvCameraViewListener(this);
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    @Override
    protected void onPause() {
        super.onPause();
        if (javaCameraView != null) {
            javaCameraView.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (javaCameraView != null) {
            javaCameraView.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            mLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        } else {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);
        }
    }

    @Override
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
    }
}
