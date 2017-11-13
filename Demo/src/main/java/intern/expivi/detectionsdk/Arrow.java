package intern.expivi.detectionsdk;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Arrow {
    /** How many bytes per float. */
    private final int mBytesPerFloat = 4;

    /** How many elements per vertex. */
    private final int mStrideBytes = 6 * mBytesPerFloat;

    /** Offset of the position data. */
    private final int mPositionOffset = 0;

    /** Size of the position data in elements. */
    private final int mPositionDataSize = 3;

    /** Offset of the color data. */
    private final int mColorOffset = 3;

    /** Size of the color data in elements. */
    private final int mColorDataSize = 3;

    // This triangle is red, green, and blue.
    private final float[] vertices_data =
            {
                    0.0f,  0.0f,  0.5f,
                    1.0f,  0.0f,  0.0f,

                    0.0f, -1.5f,  0.5f,
                    0.0f,  0.0f,  1.0f,

                    -1.0f, -2.0f,  0.5f,
                    0.0f,  1.0f,  0.0f,

                    1.0f, -2.0f,  0.5f,
                    0.0f,  1.0f,  1.0f,

                    0.0f,  0.0f, -0.5f,
                    1.0f,  0.0f,  0.0f,

                    0.0f, -1.5f, -0.5f,
                    0.0f,  0.0f,  1.0f,

                    1.0f, -2.0f, -0.5f,
                    0.0f,  1.0f,  1.0f,

                    -1.0f, -2.0f, -0.5f,
                    0.0f,  1.0f,  0.0f,
            };

    private final byte[] index_data =
            {
                    0, 2, 1,
                    0, 1, 3,

                    4, 6, 5,
                    4, 5, 7,

                    0, 4, 2,
                    4, 7, 2,

                    0, 6, 4,
                    0, 3, 6,

                    2, 7, 5,
                    2, 5, 1,

                    3, 5, 6,
                    3, 1, 5,

            };

    private FloatBuffer mVertexBuffer;
    private ByteBuffer mIndexBuffer;

    public float[] mPosition = {0.0f, 0.0f, -1.0f};
    public float   mRotation = 45.0f;
    public float[] mAxis = {0.0f, 0.0f, 1.0f};
    public float[] mScales = {0.125f, 0.125f, 0.125f};

    public float[] mModelMatrix = new float[16];

    Arrow() {
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

    void Draw(Shader shader, float[] viewMatrix, float[] orthographicMatrix) {
        float[] MVPMatrix = new float[16];
        UpdateModelView();

        //mRotation++;

        // Pass in the position information
        mVertexBuffer.position(mPositionOffset);
        GLES20.glVertexAttribPointer(shader.mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, mStrideBytes, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(shader.mPositionHandle);

        // Pass in the color information
        mVertexBuffer.position(mColorOffset);
        GLES20.glVertexAttribPointer(shader.mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false, mStrideBytes, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(shader.mColorHandle);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(MVPMatrix, 0, viewMatrix, 0, mModelMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(MVPMatrix, 0, orthographicMatrix, 0, MVPMatrix, 0);

        GLES20.glUniformMatrix4fv(shader.mMVPMatrixHandle, 1, false, MVPMatrix, 0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 36, GLES20.GL_UNSIGNED_BYTE, mIndexBuffer);

    }

    private void UpdateModelView() {
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, mPosition[0], mPosition[1], mPosition[2]);
        Matrix.rotateM(mModelMatrix, 0, mRotation, mAxis[0], mAxis[1], mAxis[2]);
        Matrix.scaleM(mModelMatrix, 0, mScales[0], mScales[1], mScales[2]);
    }
}