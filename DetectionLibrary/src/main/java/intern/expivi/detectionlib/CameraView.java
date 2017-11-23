package intern.expivi.detectionlib;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.hardware.Camera.Size;

import org.opencv.android.JavaCameraView;

import java.util.List;

/**
 * Created by Rick4 on 6-11-2017.
 */

public class CameraView extends JavaCameraView
{
    public CameraView(Context c) {
        super(c, null);
    }
    public CameraView(Context c, int cameraIndex) {
        super(c, cameraIndex);
    }
    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean initializeCamera(int frame_width, int frame_height)
    {
        return super.initializeCamera(frame_width, frame_height);
    }
    public void releaseCamera()
    {
        super.releaseCamera();
    }
    public boolean connectCamera(int width, int height)
    {
        /* 1. We need to instantiate camera
         * 2. We need to start thread which will be getting frames
         */
        return super.connectCamera(width, height);
    }
    public void disconnectCamera()
    {
        /* 1. We need to stop thread which updating the frames
         * 2. Stop camera and release it
         */
        super.disconnectCamera();
    }

    public boolean setWhiteBalance(String whiteBalance)
    {
        if (whiteBalance.isEmpty() || mCamera == null)
            return false;

        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters != null)
        {
            parameters.setWhiteBalance(whiteBalance);
            mCamera.setParameters(parameters);
            return true;
        } else {
            return false;
        }
    }
    public boolean setAutoWhiteBalanceLock(boolean toggle)
    {
        if (mCamera == null)
            return false;

        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters != null)
        {
            if (!parameters.isAutoWhiteBalanceLockSupported())
                return false;

            parameters.setAutoWhiteBalanceLock(toggle);
            mCamera.setParameters(parameters);
            return true;
        } else {
            return false;
        }
    }
    public boolean setFlashMode(String option)
    {
        if (option.isEmpty() || mCamera == null)
            return false;

        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters != null)
        {
            parameters.setFlashMode(option);
            mCamera.setParameters(parameters);
            return true;
        } else {
            return false;
        }
    }
    public List<Size> getResolutionList() {
        if (mCamera == null)
            return null;
        return mCamera.getParameters().getSupportedPreviewSizes();
    }

    /*public void setResolution(int h, int w){
        Camera.Parameters params = mCamera.getParameters();
        params.setPreviewSize(mFrameWidth, mFrameHeight);
        mCamera.setParameters(params);
    }*/

    public Size getResolution() {
        return mCamera.getParameters().getPreviewSize();
    }
}
