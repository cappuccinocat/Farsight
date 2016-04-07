package com.nutrons.farsight.farsight;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class CameraActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{
    Mat hsv;
    Mat mask;
    double[] colorLow;
    double[] colorHigh;
    private CameraBridgeViewBase cameraView;
    BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    System.out.println("Success");
                    cameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0,
                this, mLoaderCallback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        cameraView = (CameraBridgeViewBase) findViewById(R.id.camera_view);
        cameraView.setCvCameraViewListener(this);
        cameraView.setVisibility(View.VISIBLE);

        //threshold values for green, !!LATER CHANGE THESE TO SLIDERS!!
        colorLow = new double[]{50, 100, 100};
        colorHigh = new double[]{90, 255, 255};
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        hsv = new Mat(height, width, CvType.CV_8U);
        mask = new Mat(height, width, CvType.CV_8U);
    }

    @Override
    public void onCameraViewStopped() {
        hsv.release();
        mask.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        return getProcessedMat(inputFrame.rgba(), colorLow, colorHigh);
    }

    public Mat getProcessedMat(Mat image, double[] colorLow, double[] colorHigh){
        Imgproc.cvtColor(image, hsv, Imgproc.COLOR_BGR2HSV);
        Scalar lThresh = new Scalar(colorLow);
        Scalar hThresh = new Scalar(colorHigh);
        Core.inRange(hsv, lThresh, hThresh, mask);
        return mask;
    }
}
