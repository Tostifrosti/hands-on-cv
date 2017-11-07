package intern.expivi.detectionsdk;

import android.content.Context;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import intern.expivi.detectionlib.CommunicationInterface;
import intern.expivi.detectionlib.FPSMeter;
import intern.expivi.detectionlib.CameraView;
import intern.expivi.detectionlib.NativeWrapper;

public class DemoFragment extends Fragment  implements CameraBridgeViewBase.CvCameraViewListener2 {

    // newInstance constructor for creating fragment with arguments
    private static DemoFragment instance;

    public static DemoFragment newInstance() {
        if (instance == null) {
            instance = new DemoFragment();
        }
        return instance;
    }

    private String TAG = "DemoFragment";
    private CommunicationInterface callback;
    private JavaCameraView mOpenCvCameraView;
    private Mat mRgba;
    private FPSMeter meter;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this.getActivity()) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };
    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach: called");
        super.onAttach(context);
        callback = (CommunicationInterface) context;
        meter = new FPSMeter(TAG);
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: called");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: called");
        View view = inflater.inflate(R.layout.fragment_demo, container, false);
        GLSurfaceView glView = view.findViewById(R.id.demo_glsurface_view);
        glView.setRenderer(new OpenGLRenderer());

        mOpenCvCameraView = (JavaCameraView) view.findViewById(R.id.demo_clsurface_view);
        EnableView();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_demo, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_recalibrate:
                callback.Initialize();
                break;
            case R.id.menu_show_binaire:
                callback.ShowBinaire();
                break;
            default:
                break;
        }

        return false;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: called");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: called");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: called");
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this.getActivity(), mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: called");
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop: called");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView: called");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: called");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: called");
        super.onDetach();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        meter.tick();
        mRgba = inputFrame.rgba();
        NativeWrapper.Detection(mRgba.getNativeObjAddr());
        return mRgba;
    }

    private void EnableView()
    {
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.enableFpsMeter();
        mOpenCvCameraView.setMaxFrameSize(640, 480);
        //mOpenCvCameraView.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_FLUORESCENT);
    }
}
