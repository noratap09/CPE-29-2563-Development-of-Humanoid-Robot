package com.example.android_app;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.example.android_app.es.ava.aruco.CameraParameters;
import com.example.android_app.es.ava.aruco.Marker;
import com.example.android_app.es.ava.aruco.MarkerDetector;

import android.bluetooth.BluetoothSocket;
import android.widget.Button;
import android.widget.ThemedSpinnerAdapter;

public class AR_Marker_detect extends AppCompatActivity implements CvCameraViewListener2 {

    BluetoothSocket bt_socket;
    Button btn_to_interaction_mode;

    //Constants
    private static final String TAG = "Aruco";
    private static final float MARKER_SIZE = (float) 0.10;

    //Preferences
    private static final boolean SHOW_MARKERID = false;

    //You must run a calibration prior to detection
    // The activity to run calibration is provided in the repository
    private static final String DATA_FILEPATH = "";

    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean              mIsJavaCamera = true;
    private MenuItem             mItemSwitchCamera = null;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public AR_Marker_detect() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_r__marker_detect);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        btn_to_interaction_mode = (Button) findViewById(R.id.btn_to_interaction_mode);

        btn_to_interaction_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    bt_socket.getOutputStream().write("interaction_mode\n".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println("Mode A");
                Intent to_interaction = new Intent(getApplicationContext(), Interaction_control.class);
                startActivity(to_interaction);
            }
        });

        bt_socket = MainActivity.bt_socket;

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.camera_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCameraIndex(1);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.enableFpsMeter();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        //Convert input to rgba
        Mat rgba = inputFrame.rgba();

        //Setup required parameters for detect method
        MarkerDetector mDetector = new MarkerDetector();
        Vector<Marker> detectedMarkers = new Vector<>();
        CameraParameters camParams = new CameraParameters();

        Mat cam_mat = new  Mat(3,3, CvType.CV_32FC1);
        MatOfDouble dist_mat = new MatOfDouble();
        double[] distArray =  { 0.09789569,
                -0.24227678,
                -0.00488104,
                0.0047146,
                0.06488531};

        cam_mat.put(0,0,628.05440626,0.0,447.70861701,0.0,463.78478254 ,236.35913529,0.0,0.0,1.0);
        dist_mat.fromArray(distArray);

        camParams.setCameraMatrix(cam_mat);
        camParams.setDistorsionMatrix(dist_mat);

        //System.out.println("Camera Param:"+camParams.getDistCoeff().dump());
        //camParams.readFromFile(Environment.getExternalStorageDirectory().toString() + DATA_FILEPATH);

        Core.flip(rgba,rgba,1);
        Core.rotate(rgba,rgba,90);

        //Size sz = new Size(640,480);
        //Imgproc.resize( rgba, rgba, sz ,0,0, Imgproc.INTER_AREA);

        //Populate detectedMarkers
        mDetector.detect(rgba, detectedMarkers, camParams, MARKER_SIZE);

        // System.out.println("Contours Size:"+mDetector.getContours2().size());
        // System.out.println("DetectMarker Size:"+detectedMarkers.size());
        //Draw Axis for each marker detected
        if (detectedMarkers.size() != 0) {
            for (int i = 0; i < detectedMarkers.size(); i++) {
                Marker marker = detectedMarkers.get(i);
                detectedMarkers.get(i).draw3dAxis(rgba, camParams, new Scalar(255,0,0));
                double[] gun_vector = {-0.179826,0.04242,0.02595};
                double[] marker_vector = new double[3];
                marker.getTvec().get(0,0,marker_vector);
                double[] target_vector = {gun_vector[0]+marker_vector[0],gun_vector[1]+marker_vector[1],gun_vector[2]+marker_vector[2]};
                System.out.println("Target_vector:"+Arrays.toString(target_vector));

                double target_vector_size = Math.sqrt( Math.pow(target_vector[0],2) + Math.pow(target_vector[1],2) + Math.pow(target_vector[2],2));
                double zeta_target_vector_x = Math.acos(target_vector[0]/target_vector_size);
                double zeta_target_vector_y = Math.acos(target_vector[1]/target_vector_size);
                zeta_target_vector_x = Math.toDegrees(zeta_target_vector_x);
                zeta_target_vector_y = Math.toDegrees(zeta_target_vector_y);
                System.out.println("Target_Zeta:("+zeta_target_vector_y+","+zeta_target_vector_x+")");
                try {
                    bt_socket.getOutputStream().write(("gun_r_pos:"+String.valueOf(zeta_target_vector_x)+","+String.valueOf(zeta_target_vector_y)+"\n").getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                /*
                if (SHOW_MARKERID) {
                    //Setup
                    int idValue = detectedMarkers.get(i).getMarkerId();
                    Vector<Point3> points = new Vector<>();
                    points.add(new Point3(0, 0, 0));
                    MatOfPoint3f pointMat = new MatOfPoint3f();
                    pointMat.fromList(points);
                    MatOfPoint2f outputPoints = new MatOfPoint2f();

                    //Project point to marker origin
                    Calib3d.projectPoints(pointMat, marker.getRvec(), marker.getTvec(), camParams.getCameraMatrix(), camParams.getDistCoeff(), outputPoints);
                    List<Point> pts = new Vector<>();
                    pts = outputPoints.toList();

                    Imgproc.putText(rgba, marker.getTvec().dump() , pts.get(0), Imgproc.FONT_HERSHEY_SIMPLEX, 2, new Scalar(0,255,0));
                }
                */

            }
        }

        return rgba;
    }
}