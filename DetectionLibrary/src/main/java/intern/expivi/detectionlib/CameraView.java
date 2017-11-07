package intern.expivi.detectionlib;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;

import org.opencv.android.JavaCameraView;

/**
 * Created by Rick4 on 6-11-2017.
 */

public class CameraView extends JavaCameraView
{
    public CameraView(Context c) {
        super(c, null);
        //initializeCamera(640, 480);
    }
    public CameraView(Context c, int cameraIndex) {
        super(c, cameraIndex);
        //initializeCamera(640, 480);
    }
    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //initializeCamera(640, 480);
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
}
