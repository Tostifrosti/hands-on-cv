package intern.expivi.detectionsdk;

import android.opengl.Matrix;
import intern.expivi.detectionsdk.GL.BufferLayout;
import intern.expivi.detectionsdk.GL.BufferUsage;
import intern.expivi.detectionsdk.GL.IndexBuffer;
import intern.expivi.detectionsdk.GL.Mesh;
import intern.expivi.detectionsdk.GL.VertexArray;
import intern.expivi.detectionsdk.GL.VertexAttribPointerType;
import intern.expivi.detectionsdk.GL.VertexBuffer;
import intern.expivi.detectionsdk.GL.shaders.Shader;
import intern.expivi.detectionsdk.GL.shaders.ShaderManager;

public class Arrow extends IModel
{
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

    public Arrow(String shaderName, float[] position)
    {
        super(position);
        Shader mShader = ShaderManager.Get(shaderName);
        mShader.Bind();

        VertexArray vArray = new VertexArray();
        VertexBuffer buffer = VertexBuffer.Create(BufferUsage.STATIC);

        BufferLayout layout = new BufferLayout();
        layout.Push("a_Position", VertexAttribPointerType.FLOAT, BYTES_PER_FLOAT, 3, false);
        layout.Push("a_Color", VertexAttribPointerType.FLOAT, BYTES_PER_FLOAT, 3, false);

        buffer.SetData(8 * 6 * BYTES_PER_FLOAT, vertices_data);
        buffer.SetLayout(layout);
        vArray.Push(buffer);

        IndexBuffer iBuffer = new IndexBuffer(6 * 6, index_data);
        mMesh = new Mesh(vArray, iBuffer, mShader);

        mShader.Unbind();
    }
    public void Update(final float[] viewMatrix, final float[] projectionMatrix)
    {
        // Calculate Model View Projection matrix
        float[] MVPMatrix = new float[16];
        Matrix.multiplyMM(MVPMatrix, 0, viewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVPMatrix, 0);

        // Update the MVP matrix
        mMesh.GetShader().Bind();
        mMesh.GetShader().SetUniformMatrix4("u_MVPMatrix", MVPMatrix);
        mMesh.GetShader().Unbind();
    }
    public void Reset()
    {
        mModelMatrix = GetIdentityMatrix();
        Translate(0.0f, 0.0f, -1.0f);
        Rotate(45.0f, 0.0f, 0.0f, 1.0f);
        Scale(0.125f, 0.125f, 0.125f);
    }
}