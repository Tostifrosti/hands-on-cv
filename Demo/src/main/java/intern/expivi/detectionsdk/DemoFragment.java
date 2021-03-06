package intern.expivi.detectionsdk;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
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
import intern.expivi.detectionlib.NativeWrapper;

public class DemoFragment extends Fragment implements CameraBridgeViewBase.CvCameraViewListener2 {

    private String TAG = "DemoFragment";
    private CommunicationInterface callback;
    private GL2Renderer mRenderer;
    private JavaCameraView mOpenCvCameraView;
    private Mat mRgba;

    private GLSurfaceView mGLSurfaceView;

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
        mGLSurfaceView = view.findViewById(R.id.demo_glsurface_view);

        // Check if the system supports OpenGL ES 3.0.
        final ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
        final boolean supportsEs3 = configurationInfo.reqGlEsVersion >= 0x30000;

        if (supportsEs3) {
            // Request an OpenGL ES 3.0 compatible context.
            mGLSurfaceView.setEGLContextClientVersion(3);

            // Set the renderer to our demo renderer, defined below.
            mGLSurfaceView = view.findViewById(R.id.demo_glsurface_view);
            mRenderer = new GL2Renderer();
            mGLSurfaceView.setRenderer(mRenderer);
        } else  if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context.
            mGLSurfaceView.setEGLContextClientVersion(2);

            // Set the renderer to our demo renderer, defined below.
            mGLSurfaceView = view.findViewById(R.id.demo_glsurface_view);
            mRenderer = new GL2Renderer();
            mGLSurfaceView.setRenderer(mRenderer);
        }

        mOpenCvCameraView = view.findViewById(R.id.demo_clsurface_view);
        //mOpenCvCameraView = new JavaCameraView(getContext(), CameraBridgeViewBase.CAMERA_ID_ANY);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setMaxFrameSize(640, 480);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_demo, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_recalibrate:
                callback.Initialize();
                break;
            case R.id.menu_show_binaire:
                callback.ShowBinaire();
                break;
            case R.id.menu_reset_cursor:
                mRenderer.ResetCursor();
                break;
            case R.id.menu_switch_hand:
                callback.SwitchHand();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: called");
        super.onResume();
        mGLSurfaceView.onResume();

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_3_0, this.getActivity(), mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: called");
        super.onPause();
        mGLSurfaceView.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: called");
        super.onDetach();

        NativeWrapper.Reset();
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
        mRgba = inputFrame.rgba();
        NativeWrapper.Detection(mRgba.getNativeObjAddr());
        mRenderer.UpdateCursorPosition(NativeWrapper.GetCursorPosition(), NativeWrapper.GetHandState());
        return mRgba;
    }
}
