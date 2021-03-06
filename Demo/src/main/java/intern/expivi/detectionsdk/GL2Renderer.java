package intern.expivi.detectionsdk;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import intern.expivi.detectionlib.NativeWrapper;
import intern.expivi.detectionlib.Vector;
import intern.expivi.detectionsdk.GL.shaders.ShaderFactory;
import intern.expivi.detectionsdk.GL.shaders.ShaderManager;


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

    private Cube mCube;
    private Arrow mCursor;
    private Plane[] mPlanes = new Plane[8];
    private float mAspectRatio;

    private int mScreenWidth;
    private int mScreenHeight;
    private final Vector mScreenPosition = new Vector(0, 0, 0);
    private final float[] mRHcursorarea = new float[] { -1.00f, -1.50f, 2.25f, 1.25f };
    private final float[] mLHcursorarea = new float[] { -2.25f, -1.50f, 1.00f, 1.25f };
    //private List<Plane> mClickPositions = new ArrayList<>();

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        String gles_version = GLES30.glGetString(GLES30.GL_VERSION);
        Log.e("OpenGL ES Version", (gles_version != null) ? gles_version : "");
        String gles_extensions = GLES30.glGetString(GLES30.GL_EXTENSIONS);
        Log.e("OpenGL ES Extensions", (gles_extensions != null) ? gles_extensions : "");

        // Set the background clear color to gray.
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

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

        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glDepthFunc(GLES30.GL_LEQUAL);
        GLES30.glDepthMask(true);

        GLES30.glEnable(GLES30.GL_CULL_FACE);
        GLES30.glFrontFace(GLES30.GL_CCW); //CCW: select counterclockwise polygons as front-facing
        GLES30.glCullFace(GLES30.GL_BACK);
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);

        ShaderFactory.CreateBasicShader("BASIC");
        ShaderFactory.CreateBasicColorShader("BASIC_COLOR");

        mCursor = new Arrow("BASIC", new float[] { 0.0f, 0.0f, -1.0f });
        mCursor.Scale(0.125f, 0.125f, 0.125f);
        mCursor.RotateZ(45.0f);
        mCube = new Cube("BASIC_COLOR", new float[] {0.0f, 0.0f, -10.0f});
        mCube.SetColor(1.0f, 0.0f, 0.0f, 1.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height)
    {
        mScreenWidth = width;
        mScreenHeight = height;

        // Set the OpenGL viewport to the same size as the surface.
        GLES30.glViewport(0, 0, width, height);

        Matrix.perspectiveM(mProjectionMatrix, 0, 45.0f, (float) width / (float) height, 0.1f, 100.0f);
        mAspectRatio = (float) width / (float) height;
        Matrix.orthoM(mOrthogrpahicMatrix, 0, -mAspectRatio, mAspectRatio, -1.0f, 1.0f, 0.1f, 100.0f);


        float dist = 0.5f;
        float[] posPlanes[] = {
                // X, Y, Z
            { -(mAspectRatio*dist), dist, -2.0f },      // 0
            { 0.0f, dist, -2.0f },                      // 1
            { mAspectRatio*dist, dist, -2.0f },         // 2
            { -(mAspectRatio*dist), 0.0f, -2.0f },      // 3
            { mAspectRatio*dist, 0.0f, -2.0f },         // 4
            { -(mAspectRatio*dist), -dist, -2.0f },     // 5
            { 0.0f, -dist, -2.0f },                     // 6
            { mAspectRatio*dist, -dist, -2.0f }         // 7
        };
        float[] colorPlanes[] = {
            // R, G, B, A
            { 1.0f, 0.0f, 0.0f, 1.0f }, // 0
            { 0.0f, 1.0f, 0.0f, 1.0f }, // 1
            { 0.0f, 0.0f, 1.0f, 1.0f }, // 2
            { 1.0f, 1.0f, 0.0f, 1.0f }, // 3
            { 1.0f, 0.0f, 1.0f, 1.0f }, // 4
            { 0.0f, 1.0f, 1.0f, 1.0f }, // 5
            { 1.0f, 1.0f, 1.0f, 1.0f }, // 6
            { 0.0f, 0.0f, 0.0f, 1.0f }  // 7
        };

        for (int i=0; i < 8; i++) {
            mPlanes[i] = new Plane("BASIC_COLOR", posPlanes[i]);
            mPlanes[i].Scale(0.15f, 0.15f, 0.15f);
            mPlanes[i].SetColor(colorPlanes[i][0], colorPlanes[i][1], colorPlanes[i][2], colorPlanes[i][3]);
        }
    }

    @Override
    public void onDrawFrame(GL10 glUnused)
    {
        GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);

        // Update & Draw
        for (Plane plane : mPlanes) {
            plane.Update(mViewMatrix, mOrthogrpahicMatrix);
            plane.Draw();
        }
        mCube.Update(mViewMatrix, mProjectionMatrix);
        mCube.Draw();

        /*for (int i=0; i < mClickPositions.size(); i++) {
            mClickPositions.get(i).Update(mViewMatrix, mOrthogrpahicMatrix);
            mClickPositions.get(i).Draw();
        }*/

        mCursor.Update(mViewMatrix, mOrthogrpahicMatrix);
        mCursor.Draw();
    }

    void UpdateCursorPosition(Vector position, int handState)
    {
        if (this.mScreenPosition.x != position.x || this.mScreenPosition.y != position.y)
        {
            this.mScreenPosition.x = position.x;
            this.mScreenPosition.y = position.y;

            Vector clipping_space = new Vector();
            clipping_space.x = (position.x * 2.0f - 1.0f);
            clipping_space.y = ((1.0f - position.y) * 2.0f - 1.0f);
            clipping_space.z = -1.0f;
            clipping_space.w = 1.0f;

            float[] viewProjection = new float[16];
            Matrix.multiplyMM(viewProjection, 0, mOrthogrpahicMatrix, 0, mViewMatrix, 0);
            float[] viewProjectionInvers = new float[16];
            Matrix.invertM(viewProjectionInvers, 0, viewProjection, 0);

            float[] in = new float[] { clipping_space.x, clipping_space.y, clipping_space.z, clipping_space.w };
            float[] out = new float[] { 0.0f, 0.0f, 0.0f, 0.0f };
            Matrix.multiplyMV(out, 0, viewProjectionInvers, 0, in, 0);

            // Stretch out positions to encounter deadzone
            float newX = out[0];
            float newY = out[1];

            if (NativeWrapper.GetHandSide() == 0) // LEFT handside
            {
                newX = (out[0] < 0.0f) ? -(out[0] * mLHcursorarea[0]) : (out[0] * mLHcursorarea[2]);
                newY = (out[1] < 0.0f) ? -(out[1] * mLHcursorarea[1]) : (out[1] * mLHcursorarea[3]);
            }
            else if (NativeWrapper.GetHandSide() == 1) // RIGHT handside
            {
                newX = (out[0] < 0.0f) ? -(out[0] * mRHcursorarea[0]) : (out[0] * mRHcursorarea[2]);
                newY = (out[1] < 0.0f) ? -(out[1] * mRHcursorarea[1]) : (out[1] * mRHcursorarea[3]);
            }

            if (newX > 1.0f)
                newX = 1.0f;
            else if (newX < -1.0f)
                newX = -1.0f;

            if (newY > 1.0f)
                newY = 1.0f;
            else if (newY < -1.0f)
                newY = -1.0f;

            // Set cursor position
            mCursor.SetPosition(newX, newY);

            if (handState == 0)
            {
                /*Plane cp = new Plane("BASIC_COLOR", new float[] { mCursor.GetPositionX(), mCursor.GetPositionY(), -1.0f });
                cp.Scale(0.025f, 0.025f, 0.025f);
                cp.SetColor(1.00f, 0.75f, 0.79f, 1.0f);
                mClickPositions.add(cp);

                if (mClickPositions.size() > 20)
                    mClickPositions.remove(0);*/

                for (Plane plane : mPlanes)
                {
                    float[] plane_pos = {
                        plane.GetPositionX() - (mAspectRatio * plane.GetScaleX()),
                        plane.GetPositionY() - (plane.GetScaleY()),
                        plane.GetPositionX() + (mAspectRatio * plane.GetScaleX()),
                        plane.GetPositionY() + (plane.GetScaleY())
                    };

                    if (plane_pos[0] <= mCursor.GetPositionX() &&
                        plane_pos[1] <= mCursor.GetPositionY() &&
                        plane_pos[2] >= mCursor.GetPositionX() &&
                        plane_pos[3] >= mCursor.GetPositionY())
                    {
                        // Collision
                        mCube.SetColor(plane.GetColorR(), plane.GetColorG(), plane.GetColorB());
                        break;
                    }
                }
            }
        }
    }

    public void ResetCursor()
    {
        mCursor.Reset();
    }

    @Override
    public void finalize() throws Throwable
    {
        try {
            if (mCube != null) {
                mCube.finalize();
                mCube = null;
            }
            if (mCursor != null) {
                mCursor.finalize();
                mCursor = null;
            }

            if (mPlanes != null) {
                for (int i=0; i < mPlanes.length; i++) {
                    if (mPlanes[i] != null)
                        mPlanes[i].finalize();
                }
                mPlanes = null;
            }
            ShaderManager.Clean();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
