package intern.expivi.detectionsdk;

import android.opengl.Matrix;
import intern.expivi.detectionsdk.GL.Mesh;

/**
 * Created by Rick4 on 17-11-2017.
 */

public abstract class IModel
{
    public static final int BYTES_PER_FLOAT = 4;
    protected Mesh mMesh;
    protected float[] mPosition = {0.0f, 0.0f, 0.0f};
    protected float   mRotation = 0.0f;
    protected float[] mScale = {1.0f, 1.0f, 1.0f};
    protected float[] mColor = {1.0f, 1.0f, 1.0f, 1.0f};
    protected float[] mAxis = {0.0f, 0.0f, 0.0f};
    protected float[] mModelMatrix = new float[] {
        1.0f, 0.0f, 0.0f, 0.0f,
        0.0f, 1.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 1.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 1.0f
    };


    public IModel(float[] position)
    {
        mPosition = position;

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, mPosition[0], mPosition[1], mPosition[2]);
    }

    public IModel(float[] position, float[] scale)
    {
        mPosition = position;
        mScale = scale;

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, mPosition[0], mPosition[1], mPosition[2]);
        Matrix.scaleM(mModelMatrix, 0, mScale[0], mScale[1], mScale[2]);
    }
    public IModel(float[] position, float[] scale, float rotation, float[] axis)
    {
        mPosition = position;
        mScale = scale;
        mRotation = rotation;
        mAxis = axis;

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, mPosition[0], mPosition[1], mPosition[2]);
        Matrix.rotateM(mModelMatrix, 0, mRotation, mAxis[0], mAxis[1], mAxis[2]);
        Matrix.scaleM(mModelMatrix, 0, mScale[0], mScale[1], mScale[2]);
    }

    public abstract void Update(final float[] viewMatrix, final float[] projectionMatrix);

    public void Draw()
    {
        mMesh.Draw();
    }

    public float GetPositionX() { return mPosition[0]; }
    public float GetPositionY() { return mPosition[1]; }
    public float GetPositionZ() { return mPosition[2]; }
    public float GetRotation() { return mRotation; }
    public float GetScaleX() { return mScale[0]; }
    public float GetScaleY() { return mScale[1]; }
    public float GetScaleZ() { return mScale[2]; }

    public float GetColorR() { return mColor[0]; }
    public float GetColorG() { return mColor[1]; }
    public float GetColorB() { return mColor[2]; }

    // Translate
    public void TranslateX(float dirX) {
        mPosition[0] += dirX;
        _Translate(dirX, 0.0f, 0.0f);
    }
    public void TranslateY(float dirY) {
        mPosition[1] += dirY;
        _Translate(0.0f, dirY, 0.0f);
    }
    public void TranslateZ(float dirZ) {
        mPosition[2] += dirZ;
        _Translate(0.0f, 0.0f, dirZ);
    }
    public void Translate(float dirX, float dirY) {
        mPosition[0] += dirX;
        mPosition[1] += dirY;
        _Translate(dirX, dirY, 0.0f);
    }
    public void Translate(float dirX, float dirY, float dirZ) {
        mPosition[0] += dirX;
        mPosition[1] += dirY;
        mPosition[2] += dirZ;
        _Translate(dirX, dirY, dirZ);
    }
    private void _Translate(float x, float y, float z)
    {
        Matrix.translateM(mModelMatrix, 0, x, y, z);
    }

    public void SetPosition(float x, float y)
    {
        mPosition[0] = x;
        mPosition[1] = y;

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, mPosition[0], mPosition[1], mPosition[2]);
        Matrix.rotateM(mModelMatrix, 0, mRotation, mAxis[0], mAxis[1], mAxis[2]);
        Matrix.scaleM(mModelMatrix, 0, mScale[0], mScale[1], mScale[2]);
    }
    public void SetPosition(float x, float y, float z)
    {
        mPosition[0] = x;
        mPosition[1] = y;
        mPosition[2] = z;
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, mPosition[0], mPosition[1], mPosition[2]);
        Matrix.rotateM(mModelMatrix, 0, mRotation, mAxis[0], mAxis[1], mAxis[2]);
        Matrix.scaleM(mModelMatrix, 0, mScale[0], mScale[1], mScale[2]);
    }

    // Rotate
    public void RotateX(float degrees) {
        Rotate(degrees, 1.0f, 0.0f, 0.0f);
    }
    public void RotateY(float degrees) {
        Rotate(degrees, 0.0f, 1.0f, 0.0f);
    }
    public void RotateZ(float degrees) {
        Rotate(degrees, 0.0f, 0.0f, 1.0f);
    }
    public void RotateXYZ(float degrees) {
        Rotate(degrees, 1.0f, 1.0f, 1.0f);
    }
    public void Rotate(float degrees, float axisX, float axisY, float axisZ) {
        mRotation = degrees;
        mAxis[0] = axisX;
        mAxis[1] = axisY;
        mAxis[2] = axisZ;
        Matrix.rotateM(mModelMatrix, 0, mRotation, axisX, axisY, axisZ);
    }

    // Scale
    public void ScaleX(float x) {
        mScale[0] = x;
        _Scale(mScale[0], 1.0f, 1.0f);
    }
    public void ScaleY(float y) {
        mScale[1] = y;
        _Scale(1.0f, mScale[1], 1.0f);
    }
    public void ScaleZ(float z) {
        mScale[2] = z;
        _Scale(1.0f, 1.0f, mScale[2]);
    }
    public void Scale(float x, float y, float z) {
        mScale[0] = x;
        mScale[1] = y;
        mScale[2] = z;
        _Scale(mScale[0], mScale[1], mScale[2]);
    }
    private void _Scale(float x, float y, float z)
    {
        Matrix.scaleM(mModelMatrix, 0, x, y, z);
    }
    protected float[] GetIdentityMatrix()
    {
        return new float[] {
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
        };
    }
    protected float[] GetTranslationMatrix(float x, float y, float z)
    {
        return new float[] {
                1.0f, 0.0f, 0.0f, x,
                0.0f, 1.0f, 0.0f, y,
                0.0f, 0.0f, 1.0f, z,
                0.0f, 0.0f, 0.0f, 1.0f
        };
    }
    protected float[] GetScalingMatrix(float x, float y, float z)
    {
        return new float[] {
                x, 0.0f, 0.0f, 0.0f,
                0.0f, y, 0.0f, 0.0f,
                0.0f, 0.0f, z, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
        };
    }
    @Override
    public void finalize() throws Throwable
    {
        if (mMesh != null)
            mMesh.finalize();
        mMesh = null;
    }

}
