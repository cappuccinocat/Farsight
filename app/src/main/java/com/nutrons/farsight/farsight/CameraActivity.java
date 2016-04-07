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
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CameraActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{
    Mat hsv;
    Mat mask;
    Mat hierarchy;
    Scalar CONTOUR_COLOR;
    List<MatOfPoint> largestContour = new ArrayList<MatOfPoint>();
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
        hierarchy = new Mat(height, width, CvType.CV_8U);
        CONTOUR_COLOR = new Scalar(255, 0, 0, 255);
    }

    @Override
    public void onCameraViewStopped() {
        hsv.release();
        mask.release();
        hierarchy.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        getProcessedMat(inputFrame.rgba(), colorLow, colorHigh);
        getLargestContour(mask);
        Imgproc.drawContours(mask, largestContour, -1, CONTOUR_COLOR);
        return mask;
    }

    public Mat getProcessedMat(Mat image, double[] colorLow, double[] colorHigh){
        Imgproc.cvtColor(image, hsv, Imgproc.COLOR_BGR2HSV);
        Scalar lThresh = new Scalar(colorLow);
        Scalar hThresh = new Scalar(colorHigh);
        Core.inRange(hsv, lThresh, hThresh, mask);
        return mask;
    }

    public List<MatOfPoint> getLargestContour(Mat threshedImage){
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(threshedImage, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        //finding biggest area
        Iterator<MatOfPoint> iterator = contours.iterator();
        double maxArea = 0.0;
        while(iterator.hasNext()){
            double area = Imgproc.contourArea(iterator.next());
            if(area > maxArea){
                maxArea = area;
            };
        }

        //filter out only biggest contour
        largestContour.clear();
        while(iterator.hasNext()){
            if(Imgproc.contourArea(iterator.next()) == maxArea){
                largestContour.add(iterator.next());
            }
        }

        return largestContour;
    }
}
