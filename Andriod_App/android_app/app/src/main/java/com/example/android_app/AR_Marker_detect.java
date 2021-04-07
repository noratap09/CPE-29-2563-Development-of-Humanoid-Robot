package com.example.android_app;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import android.widget.ImageView;
import android.widget.ThemedSpinnerAdapter;

public class AR_Marker_detect extends AppCompatActivity implements CvCameraViewListener2 {

    Button btn_to_interaction_mode,btn_auto_shot,btn_rl,btn_rr;
    ImageView img_warning;
    AnimationDrawable Anime_warning;

    MediaPlayer mediaPlayer;
    String Shooting_list;

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

        img_warning = (ImageView) findViewById(R.id.img_warning);
        btn_to_interaction_mode = (Button) findViewById(R.id.btn_to_interaction_mode);
        btn_auto_shot = (Button) findViewById(R.id.btn_auto_shot);
        btn_rl = (Button) findViewById(R.id.btn_reload_L_2);
        btn_rr = (Button) findViewById(R.id.btn_reload_R_2);

        img_warning.setImageResource(R.drawable.anime_warning);
        Anime_warning = (AnimationDrawable)img_warning.getDrawable();

        btn_to_interaction_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.send_to_bt("interaction_mode");
                System.out.println("Mode A");
                Intent to_interaction = new Intent(getApplicationContext(), Interaction_control.class);
                startActivity(to_interaction);
                //AR_Marker_detect.this.finish();
            }
        });

        btn_auto_shot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Shooting_list != "") {
                    if(Shooting_list.charAt(0) == 'e') {
                        MainActivity.send_to_bt("reposition");
                    } else {
                        try {

                            Anime_warning.start();
                            img_warning.setVisibility(View.VISIBLE);

                            mediaPlayer = MediaPlayer.create(AR_Marker_detect.this, R.raw.warning);
                            mediaPlayer.start();

                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    Anime_warning.stop();
                                    img_warning.setVisibility(View.INVISIBLE);

                                    //DO ACTION
                                    if (Shooting_list.charAt(0) == 'r') {
                                        MainActivity.send_to_bt("gun_r_shot");
                                    } else if (Shooting_list.charAt(0) == 'l') {
                                        MainActivity.send_to_bt("gun_l_shot");
                                    }
                                    //End Action

                                    mediaPlayer = MediaPlayer.create(AR_Marker_detect.this, R.raw.clear);
                                    mediaPlayer.start();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        btn_rl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MainActivity.send_to_bt("gun_l_reload");
                return false;
            }
        });

        btn_rr.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MainActivity.send_to_bt("gun_r_reload");
                return false;
            }
        });

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


        Shooting_list = "";
        // System.out.println("Contours Size:"+mDetector.getContours2().size());
        // System.out.println("DetectMarker Size:"+detectedMarkers.size());
        //Draw Axis for each marker detected
        if (detectedMarkers.size() != 0) {
            for (int i = 0; i < detectedMarkers.size(); i++) {
                Marker marker = detectedMarkers.get(i);
                //detectedMarkers.get(i).draw3dAxis(rgba, camParams, new Scalar(0,255,0));
                double[] gun_vector_r = {-0.179826,0.04242,0.02595};
                double[] gun_vector_l = {0.04222,0.04247,0.02598};

                double[] marker_vector = new double[3];
                marker.getTvec().get(0,0,marker_vector);

                double[] target_vector_r = {gun_vector_r[0]+marker_vector[0],gun_vector_r[1]+marker_vector[1],gun_vector_r[2]+marker_vector[2]};
                double[] target_vector_l = {gun_vector_l[0]+marker_vector[0],gun_vector_l[1]+marker_vector[1],gun_vector_l[2]+marker_vector[2]};
                System.out.println("Target_vector_r:"+Arrays.toString(target_vector_r));
                System.out.println("Target_vector_l:"+Arrays.toString(target_vector_l));

                double target_vector_r_size = Math.sqrt( Math.pow(target_vector_r[0],2) + Math.pow(target_vector_r[1],2) + Math.pow(target_vector_r[2],2));
                double target_vector_l_size = Math.sqrt( Math.pow(target_vector_l[0],2) + Math.pow(target_vector_l[1],2) + Math.pow(target_vector_l[2],2));

                if(target_vector_r_size <= target_vector_l_size)
                {
                    double zeta_target_vector_x_r = Math.acos(target_vector_r[0] / target_vector_r_size);
                    double zeta_target_vector_y_r = Math.acos(target_vector_r[1] / target_vector_r_size);
                    zeta_target_vector_x_r = Math.toDegrees(zeta_target_vector_x_r);
                    zeta_target_vector_y_r = Math.toDegrees(zeta_target_vector_y_r)+10;
                    System.out.println("Target_Zeta_r:(" + zeta_target_vector_y_r + "," + zeta_target_vector_x_r + ")");
                    if(zeta_target_vector_x_r>=70) {
                        detectedMarkers.get(i).draw3dAxis(rgba, camParams, new Scalar(0,255,0));
                        Shooting_list += "r";
                        MainActivity.send_to_bt("gun_r_pos:" + String.valueOf(zeta_target_vector_x_r) + "," + String.valueOf(zeta_target_vector_y_r));
                    }
                    else {
                        detectedMarkers.get(i).draw3dAxis(rgba, camParams, new Scalar(255,0,0));
                        Shooting_list += "e";
                    }
                }
                else
                {
                    double zeta_target_vector_x_l = Math.acos(target_vector_l[0] / target_vector_l_size);
                    double zeta_target_vector_y_l = Math.acos(target_vector_l[1] / target_vector_l_size);
                    zeta_target_vector_x_l = Math.toDegrees(zeta_target_vector_x_l)-10;
                    zeta_target_vector_y_l = Math.toDegrees(zeta_target_vector_y_l)+20;
                    System.out.println("Target_Zeta_l:(" + zeta_target_vector_y_l + "," + zeta_target_vector_x_l + ")");
                    if(zeta_target_vector_x_l>=70) {
                        detectedMarkers.get(i).draw3dAxis(rgba, camParams, new Scalar(0,0,255));
                        Shooting_list += "l";
                        MainActivity.send_to_bt("gun_l_pos:" + String.valueOf(zeta_target_vector_x_l) + "," + String.valueOf(zeta_target_vector_y_l));
                    }
                    else
                    {
                        detectedMarkers.get(i).draw3dAxis(rgba, camParams, new Scalar(255,0,0));
                        Shooting_list += "e";
                    }
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