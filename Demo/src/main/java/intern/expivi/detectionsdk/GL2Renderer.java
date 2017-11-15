package intern.expivi.detectionsdk;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import intern.expivi.detectionlib.Vector;


public class GL2Renderer implements GLSurfaceView.Renderer {
    private final String vertexShaderUniformColor =
                      "uniform mat4 u_MVPMatrix;      \n"        // A constant representing the combined model/view/projection matrix
                    + "uniform vec4 u_Color;          \n"

                    + "attribute vec4 a_Position;     \n"        // Per-vertex position information we will pass in.

                    + "varying vec4 v_Color;          \n"        // This will be passed into the fragment shader.

                    + "void main()                    \n"        // The entry point for our vertex shader.
                    + "{                              \n"
                    + "   v_Color = u_Color;          \n"        // Pass the color through to the fragment shader.
                    // It will be interpolated across the triangle.
                    + "   gl_Position = u_MVPMatrix   \n"    // gl_Position is a special variable used to store the final position.
                    + "               * a_Position;   \n"     // Multiply the vertex by the matrix to get the final point in
                    + "}                              \n";    // normalized screen coordinates.

    private final String baseVertexShader =
                    "uniform mat4 u_MVPMatrix;      \n"        // A constant representing the combined model/view/projection matrix.

                    + "attribute vec4 a_Position;     \n"        // Per-vertex position information we will pass in.
                    + "attribute vec4 a_Color;        \n"        // Per-vertex color information we will pass in.

                    + "varying vec4 v_Color;          \n"        // This will be passed into the fragment shader.

                    + "void main()                    \n"        // The entry point for our vertex shader.
                    + "{                              \n"
                    + "   v_Color = a_Color;          \n"        // Pass the color through to the fragment shader.
                    // It will be interpolated across the triangle.
                    + "   gl_Position = u_MVPMatrix   \n"    // gl_Position is a special variable used to store the final position.
                    + "               * a_Position;   \n"     // Multiply the vertex by the matrix to get the final point in
                    + "}                              \n";    // normalized screen coordinates.

    private final String baseFragmentShader =
            "precision mediump float;       \n"        // Set the default precision to medium. We don't need as high of a
                    // precision in the fragment shader.
                    + "varying vec4 v_Color;          \n"        // This is the color from the vertex shader interpolated across the
                    // triangle per fragment.
                    + "void main()                    \n"        // The entry point for our fragment shader.
                    + "{                              \n"
                    + "   gl_FragColor = v_Color;     \n"        // Pass the color directly through the pipeline.
                    + "}                              \n";

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

    private Shader baseShader, colorShader;
    public Cube mCube = new Cube();
    public Arrow mCursor = new Arrow();
    public Plane[] mPlanes = new Plane[8];
    private String TAG = "GL2Renderer";
    private float mAspectRatio;

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

        baseShader = new Shader(baseVertexShader, baseFragmentShader);
        colorShader = new Shader(vertexShaderUniformColor, baseFragmentShader);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        mScreenWidth = width;
        mScreenHeight = height;

        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);

        Matrix.perspectiveM(mProjectionMatrix, 0, 45.0f, (float) width / (float) height, 0.1f, 100.0f);
        mAspectRatio = (float) width / (float) height;
        Matrix.orthoM(mOrthogrpahicMatrix, 0, -mAspectRatio, mAspectRatio, -1.0f, 1.0f, 0.1f, 100.0f);

        mPlanes[0] = new Plane(new float[]{-(mAspectRatio*0.5f), 0.5f, -2.0f}, new float[]{1.0f, 0.0f, 0.0f, 1.0f});
        mPlanes[1] = new Plane(new float[]{0.0f, 0.5f, -2.0f}, new float[]{0.0f, 1.0f, 0.0f, 1.0f});
        mPlanes[2] = new Plane(new float[]{mAspectRatio*0.5f, 0.5f, -2.0f}, new float[]{0.0f, 0.0f, 1.0f, 1.0f});
        mPlanes[3] = new Plane(new float[]{-(mAspectRatio*0.5f), 0.0f, -2.0f}, new float[]{1.0f, 1.0f, 0.0f, 1.0f});
        mPlanes[4] = new Plane(new float[]{mAspectRatio*0.5f, 0.0f, -2.0f}, new float[]{1.0f, 0.0f, 1.0f, 1.0f});
        mPlanes[5] = new Plane(new float[]{-(mAspectRatio*0.5f), -0.5f, -2.0f}, new float[]{0.0f, 1.0f, 1.0f, 1.0f});
        mPlanes[6] = new Plane(new float[]{0.0f, -0.5f, -2.0f}, new float[]{1.0f, 1.0f, 1.0f, 1.0f});
        mPlanes[7] = new Plane(new float[]{mAspectRatio*0.5f, -0.5f, -2.0f}, new float[]{0.0f, 0.0f, 0.0f, 1.0f});
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        mCube.Draw(colorShader, mViewMatrix, mProjectionMatrix);
        mCursor.Draw(baseShader, mViewMatrix, mOrthogrpahicMatrix);

        for (Plane plane : mPlanes) {
            plane.Draw(colorShader, mViewMatrix, mOrthogrpahicMatrix);
        }


    }

    void UpdateCursorPosition(Vector position) {
        if (this.mScreenPosition.x != position.x || this.mScreenPosition.y != position.y) {
            this.mScreenPosition = position;

            Vector clipping_space = new Vector();
            clipping_space.x = (float) (position.x * 2.0f - 1.0);
            clipping_space.y = (float) ((1.0f - position.y) * 2.0f - 1.0f);
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

            for (Plane plane : mPlanes) {
                float[] bb = {plane.mPosition[0] - (0.5f* mAspectRatio * plane.mSize), plane.mPosition[1] - (0.5f* plane.mSize), plane.mPosition[0] + (0.5f* mAspectRatio* plane.mSize), plane.mPosition[1] + (0.5f* plane.mSize)};

                if(bb[0] <=  mCursor.mPosition[0]
                && mCursor.mPosition[0] <= bb[2]
                && bb[1] <=  mCursor.mPosition[1]
                && mCursor.mPosition[1] <= bb[3])
                {
                    // Collision;
                    mCube.mColor = plane.mColor;
                    break;
                }
            }
        }
    }
}
