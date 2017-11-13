package intern.expivi.detectionsdk;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import intern.expivi.detectionlib.Vector;

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
    private float[] mOrthogrpahicMatrix = new float[16];

    private Shader shader;
    public Cube mCube = new Cube();
    public Arrow mCursor = new Arrow();
    private String TAG = "GL2Renderer";

    private int mScreenWidth;
    private int mScreenHeight;
    private Vector mScreenPosition = new Vector(0, 0, 0);

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
        float aspectRatio = (float) width / (float) height;
        Matrix.orthoM(mOrthogrpahicMatrix, 0 , -aspectRatio, aspectRatio, -1.0f, 1.0f, 0.1f, 100.0f);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        mCube.Draw(shader, mViewMatrix, mProjectionMatrix);
        mCursor.Draw(shader, mViewMatrix, mOrthogrpahicMatrix);
    }

    void UpdateCursorPosition(Vector position) {
        if (this.mScreenPosition.x != position.x || this.mScreenPosition.y != position.y) {
            this.mScreenPosition = position;

            Vector clipping_space = new Vector();
            clipping_space.x = (float) ( position.x * 2.0f - 1.0);
            clipping_space.y = (float) ((1.0f -  position.y) * 2.0f - 1.0f);
            clipping_space.z = -1.0f;
            clipping_space.w = 1.0f;
            //Log.d(TAG, "UpdateCursorPosition: Clipping-space " + clipping_space.x + ", " + clipping_space.y + ", " + clipping_space.z + ", " + clipping_space.w);

            float[] viewProjection = new float[16];
            Matrix.multiplyMM(viewProjection, 0,
                    mOrthogrpahicMatrix, 0,
                    mViewMatrix, 0);
            float[] viewProjectionInvers = new float[16];
            Matrix.invertM(viewProjectionInvers, 0,
                    viewProjection, 0);

            float[] in = new float[4];
            in[0] = clipping_space.x;
            in[1] = clipping_space.y;
            in[2] = clipping_space.z;
            in[3] = clipping_space.w;

            float[] out = new float[4];
            Matrix.multiplyMV(out, 0,
                    viewProjectionInvers, 0,
                    in, 0);

            mCursor.mPosition[0] = out[0];
            mCursor.mPosition[1] = out[1];

            //Log.d(TAG, "UpdateCursorPosition: World-space " + mCursor.mPosition[0] + ", " + mCursor.mPosition[1]);
            //Log.d(TAG, "UpdateCursorPosition:");
            //translation[2] = outPoint[2] / outPoint[3];
        }
    }
}
