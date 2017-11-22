package intern.expivi.detectionsdk;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

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
    private List<Plane> mClickPositions = new ArrayList<>();

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        String gles_version = GLES20.glGetString(GLES20.GL_VERSION);
        Log.e("OpenGL ES Version", (gles_version != null) ? gles_version : "");

        // Set the background clear color to gray.
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

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
        GLES20.glFrontFace(GLES20.GL_CCW); //CCW: select counterclockwise polygons as front-facing
        GLES20.glCullFace(GLES20.GL_BACK);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

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
        GLES20.glViewport(0, 0, width, height);

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
            mPlanes[i].Scale(0.15f, 0.15f, 1.0f);
            mPlanes[i].SetColor(colorPlanes[i][0], colorPlanes[i][1], colorPlanes[i][2], colorPlanes[i][3]);
        }
    }

    @Override
    public void onDrawFrame(GL10 glUnused)
    {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        // Update & Draw
        for (Plane plane : mPlanes) {
            plane.Update(mViewMatrix, mOrthogrpahicMatrix);
            plane.Draw();
        }
        mCube.Update(mViewMatrix, mProjectionMatrix);
        mCube.Draw();

        mCursor.Update(mViewMatrix, mOrthogrpahicMatrix);
        mCursor.Draw();

        for (int i=0; i < mClickPositions.size(); i++) {
            mClickPositions.get(i).Update(mViewMatrix, mOrthogrpahicMatrix);
            mClickPositions.get(i).Draw();
        }
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

            mCursor.SetPosition(out[0], out[1]);

            if (handState == 0)
            {
                float size = 0.025f;
                float[] bb = {
                    mCursor.GetPositionX() - (mAspectRatio * size),
                    mCursor.GetPositionY() - (size),
                    mCursor.GetPositionX() + (mAspectRatio* size),
                    mCursor.GetPositionY() + (size)
                };
                Plane p = new Plane("BASIC_COLOR", new float[] { bb[0], bb[1], 0.0f });
                p.Scale(size, size, 1.0f);
                p.SetColor(1.00f, 0.75f, 0.79f);
                mClickPositions.add(p);

                if (mClickPositions.size() > 20)
                    mClickPositions.remove(0);

                for (Plane plane : mPlanes)
                {
                    float[] plane_bb = {
                        plane.GetPositionX() - (mAspectRatio * plane.GetScaleX()),
                        plane.GetPositionY() - (plane.GetScaleY()),
                        plane.GetPositionX() + (mAspectRatio * plane.GetScaleX()),
                        plane.GetPositionY() + (plane.GetScaleY())
                    };

                    if(plane_bb[0] <= mCursor.GetPositionX() &&
                       plane_bb[1] <= mCursor.GetPositionY() &&
                       plane_bb[2] >= mCursor.GetPositionX() &&
                       plane_bb[3] >= mCursor.GetPositionY())
                    {
                        // Collision;
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
            if (mCube != null)
                mCube.finalize();
            if (mCursor != null)
                mCursor.finalize();

            for (int i=0; i < mPlanes.length; i++) {
                if (mPlanes[i] != null)
                    mPlanes[i].finalize();
            }
            ShaderManager.Clean();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
