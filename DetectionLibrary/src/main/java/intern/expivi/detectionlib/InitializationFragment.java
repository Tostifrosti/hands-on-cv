package intern.expivi.detectionlib;

import android.content.Context;
import android.content.res.AssetManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class InitializationFragment extends Fragment implements CameraBridgeViewBase.CvCameraViewListener2 {

   private String TAG = "InitFragment";
    private CommunicationInterface callback;
    private JavaCameraView mOpenCvCameraView;
    private Mat mRgba;
    private FPSMeter meter;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this.getActivity()) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    AssetManager ass = getResources().getAssets();
                    NativeWrapper.Create(ass);
                break;
                default:
                    super.onManagerConnected(status);
                break;
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach: called");
        super.onAttach(context);
        callback = (CommunicationInterface) context;
        NativeWrapper.Reset();
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
        View view = inflater.inflate(R.layout.fragment_initialization, container, false);
        mOpenCvCameraView = (JavaCameraView) view.findViewById(R.id.initialization_surface_view);
        EnableView();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_initialize, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_cancel) {
            callback.Detect();
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
        if (mOpenCvCameraView != null )
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
        if (mOpenCvCameraView != null )
            mOpenCvCameraView.disableView();
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
        if(NativeWrapper.Analyse(mRgba.getNativeObjAddr()))
            callback.Detect();
        return mRgba;
    }

    private void EnableView()
    {
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.enableFpsMeter();
        mOpenCvCameraView.setMaxFrameSize(640, 480);
        //mOpenCvCameraView.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_FLUORESCENT);
        //mCamera.setAutoWhiteBalanceLock(true);
        //mOpenCvCameraView.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
    }
}
