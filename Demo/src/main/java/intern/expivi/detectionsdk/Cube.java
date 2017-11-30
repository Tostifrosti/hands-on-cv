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

public class Cube extends IModel
{
    private final float[] vertices_data =
    {
        // X, Y, Z,
        // R, G, B, A
        -1.0f,  1.0f, 1.0f, // Position
        //1.0f,  0.0f,  0.0f, // Color

        -1.0f, -1.0f, 1.0f,
        //1.0f,  0.0f,  0.0f,

        1.0f,  1.0f, 1.0f,
        //1.0f,  0.0f,  1.0f,

        1.0f, -1.0f,  1.0f,
        //1.0f,  0.0f,  0.0f,

        1.0f,  1.0f, -1.0f,
        //1.0f,  0.0f,  1.0f,

        1.0f,  -1.0f, -1.0f,
        //1.0f,  0.0f,  1.0f,

        -1.0f,  1.0f, -1.0f,
        //1.0f, 0.0f, 0.0f,

        -1.0f, -1.0f, -1.0f,
        //1.0f, 0.0f, 1.0f,
    };

    private final byte[] index_data =
    {
        0, 3, 2,
        0, 1, 3,
        2, 5, 4,
        2, 3, 5,
        4, 7, 6,
        4, 5, 7,
        6, 1, 0,
        6, 7, 1,
        6, 0, 2,
        6, 2, 4,
        7, 3, 1,
        7, 5, 3,
    };

    public Cube(String shaderName, float[] position)
    {
        super(position);
        Shader mShader = ShaderManager.Get(shaderName);

        if (mShader == null)
            throw new IllegalArgumentException("The name of the given shader is invalid!");

        mShader.Bind();

        VertexArray vArray = VertexArray.Create();
        VertexBuffer buffer = VertexBuffer.Create(BufferUsage.STATIC);

        BufferLayout layout = new BufferLayout();
        layout.Push("a_Position", VertexAttribPointerType.FLOAT, BYTES_PER_FLOAT, 3, false);
        //layout.Push("a_Color", VertexAttribPointerType.FLOAT, BYTES_PER_FLOAT, 3, false);

        buffer.SetData(8 * 3 * BYTES_PER_FLOAT, vertices_data);
        buffer.SetLayout(layout);
        vArray.Push(buffer);

        IndexBuffer iBuffer = new IndexBuffer(6 * 6, index_data);
        mMesh = new Mesh(vArray, iBuffer, mShader);

        mShader.Unbind();
    }

    public void Update(final float[] viewMatrix, final float[] projectionMatrix)
    {
        Rotate(1.0f, 1.0f, 1.0f, 1.0f);

        // Calculate Model View Projection matrix
        float[] MVPMatrix = new float[16];
        Matrix.multiplyMM(MVPMatrix, 0, viewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVPMatrix, 0);

        // Update the MVP matrix
        mMesh.GetShader().Bind();
        mMesh.GetShader().SetUniformMatrix4("u_MVPMatrix", MVPMatrix);
        mMesh.GetShader().SetUniform4fv("u_Color", 1, mColor);
        mMesh.GetShader().Unbind();
    }

    public void SetColor(float r, float g, float b)
    {
        SetColor(r, g, b, mColor[3]);
    }

    public void SetColor(float r, float g, float b, float a)
    {
        mColor[0] = r;
        mColor[1] = g;
        mColor[2] = b;
        mColor[3] = a;
    }
}