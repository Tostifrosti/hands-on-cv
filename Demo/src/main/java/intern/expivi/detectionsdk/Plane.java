package intern.expivi.detectionsdk;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Plane {
    /** How many bytes per float. */
    private final int mBytesPerFloat = 4;

    /** How many elements per vertex. */
    private final int mStrideBytes = 3 * mBytesPerFloat;

    /** Offset of the position data. */
    private final int mPositionOffset = 0;

    /** Size of the position data in elements. */
    private final int mPositionDataSize = 3;

    // This triangle is red, green, and blue.
    private final float[] vertices_data =
            {
                    // X, Y, Z,
                    -1.0f,  1.0f, 1.0f, // Position

                    -1.0f, -1.0f, 1.0f,

                    1.0f,  1.0f, 1.0f,

                    1.0f, -1.0f,  1.0f,
            };

    private final byte[] index_data =
            {
                    0, 3, 2,
                    0, 1, 3,
            };

    private FloatBuffer mVertexBuffer;
    private ByteBuffer mIndexBuffer;

    public float   mSize = 0.15f;
    public float[] mPosition = {0.0f, 0.0f, -2.0f};
    public float   mRotation = 0.0f;
    public float[] mAxis = {1.0f, 1.0f, 1.0f};
    public float[] mScales = {mSize, mSize, 1.0f};

    public float[] mColor = {1.0f, 0.0f, 0.0f, 1.0f};

    private float[] mModelMatrix = new float[16];

    Plane(float[] position, float[] color) {
        mPosition = position;
        mColor = color;
        // Initialize the buffers.
        ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices_data.length * mBytesPerFloat);
        byteBuf.order(ByteOrder.nativeOrder());
        mVertexBuffer = byteBuf.asFloatBuffer();
        mVertexBuffer.put(vertices_data);
        mVertexBuffer.position(0);

        mIndexBuffer = ByteBuffer.allocateDirect(index_data.length);
        mIndexBuffer.put(index_data);
        mIndexBuffer.position(0);

        UpdateModelView();
    }

    void Draw(Shader shader, float[] viewMatrix, float[] projectionMatrix) {

        GLES20.glUseProgram(shader.mProgramHandle);
        float[] MVPMatrix = new float[16];

        //mRotation = mRotation < 360 ? mRotation+1 : 0;
        // Pass in the position information
        mVertexBuffer.position(mPositionOffset);
        GLES20.glVertexAttribPointer(shader.mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, mStrideBytes, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(shader.mPositionHandle);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(MVPMatrix, 0, viewMatrix, 0, mModelMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVPMatrix, 0);

        GLES20.glUniformMatrix4fv(shader.mMVPMatrixHandle, 1, false, MVPMatrix, 0);

        int loc = GLES20.glGetUniformLocation(shader.mProgramHandle, "u_Color");
        GLES20.glUniform4f(loc, mColor[0], mColor[1], mColor[2], mColor[3]);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_BYTE, mIndexBuffer);

    }

    public void UpdateModelView() {
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, mPosition[0], mPosition[1], mPosition[2]);
        Matrix.rotateM(mModelMatrix, 0, mRotation, mAxis[0], mAxis[1], mAxis[2]);
        Matrix.scaleM(mModelMatrix, 0, mScales[0], mScales[1], mScales[2]);
    }
}