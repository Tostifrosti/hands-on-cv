package intern.expivi.detectionsdk;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import intern.expivi.detectionlib.Point;

public class GL2Renderer implements GLSurfaceView.Renderer {
    /**
     * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
     * it positions things relative to our eye.
     */
    private float[] mViewMatrix = new float[16];

    /**
     * Store the projection matrix. This is used to project the scene onto a 2D viewport.
     */
    private float[] mProjectionMatrix = new float[16];

    private Shader shader;
    public Cube mCube = new Cube();
    public Arrow mCursor = new Arrow();
    private String TAG = "GL2Renderer";

    private int mScreenWidth;
    private int mScreenHeight;
    private Point mScreenPosition = new Point(0, 0, 0);

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        // Set the background clear color to gray.
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);

        // Position the eye behind the origin.
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 2.0f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = 0.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
        GLES20.glDepthMask(true);

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);

        shader = new Shader();
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        mScreenWidth = width;
        mScreenHeight = height;

        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);

        Matrix.perspectiveM(mProjectionMatrix, 0, 45.0f, (float) width / (float) height, 0.1f, 100.0f);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        mCube.Draw(shader, mViewMatrix, mProjectionMatrix);
        mCursor.Draw(shader, mViewMatrix, mProjectionMatrix);
    }

    void UpdateCursorPosition(Point position) {
        if (this.mScreenPosition.x != position.x || this.mScreenPosition.y != position.y) {
            this.mScreenPosition = position;

            // OpenCV Screen space to OpenGL Screen space
            Point screenSpace = new Point();
            screenSpace.x = position.x * mScreenWidth;
            screenSpace.y = position.y * mScreenHeight;

            float x = 2.0f * position.x - 1f;
            float y = - 2.0f * position.y + 1f;

            float[] viewProjection  = new float[16];
            float[] viewProjectionInverse  = new float[16];
            // Get the inverse of the transformation matrix.
            Matrix.multiplyMM(
                    viewProjection , 0,
                    mProjectionMatrix, 0,
                    mViewMatrix, 0);
            Matrix.invertM(viewProjectionInverse , 0,
                    viewProjection, 0);

            float[] in = new float[4];
            in[0] = x;
            in[1] = y;
            in[2] = 0.0f;
            in[3] = 0.0f;

            float[] out = new float[4];
            Matrix.multiplyMV(
                    out, 0,
                    viewProjectionInverse , 0,
                    in, 0);

            Log.d(TAG, "UpdateCursorPosition: " + out[0] + ", " + out[1]);

            mCursor.mPosition[0] = out[0];
            mCursor.mPosition[1] = out[1];
            //mCursor.mPosition[2] = out[2];
        }
    }
}
