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

/**
 * Created by Rick4 on 14-11-2017.
 */

public class Test
{
    private static final String shaderSource =
        "#shader vertex                           \n"
                + "uniform mat4 u_MVPMatrix;      \n"
                + "attribute vec4 a_Position;     \n"
                + "attribute vec3 a_Color;        \n"
                + "varying vec4 v_Color;          \n"
                + "void main()                    \n"
                + "{                              \n"
                + "   v_Color = vec4(a_Color.r, a_Color.g, a_Color.b, 1.0f); \n"
                + "   gl_Position = u_MVPMatrix * a_Position; \n"
                + "}                              \n"
        + "#shader fragment                       \n"
                + "precision mediump float;       \n"
                + "varying vec4 v_Color;          \n"
                + "void main()                    \n"
                + "{                              \n"
                + "   gl_FragColor = v_Color;     \n"
                + "}                              \n";
    private final float[] vertices_data =
    {
            // X, Y, Z,
            // R, G, B, A
            -1.0f,  1.0f, 1.0f, // Position
            1.0f,  0.0f,  0.0f, // Color

            -1.0f, -1.0f, 1.0f,
            0.0f,  1.0f,  0.0f,

            1.0f,  1.0f, 1.0f,
            0.0f,  0.0f,  1.0f,

            1.0f, -1.0f,  1.0f,
            1.0f,  1.0f,  0.0f,

            1.0f,  1.0f, -1.0f,
            0.0f,  1.0f,  1.0f,

            1.0f,  -1.0f, -1.0f,
            1.0f,  0.0f,  1.0f,

            -1.0f,  1.0f, -1.0f,
            0.0f, 0.0f, 0.0f,

            -1.0f, -1.0f, -1.0f,
            1.0f, 1.0f, 1.0f,
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

    private static final int BYTES_PER_FLOAT = 4;
    private Mesh mMesh;
    private final float[] mDirection = {0.0f, 0.0f, -10.0f};
    private final float   mRotation = 0.0f;
    private final float[] mAxis = {1.0f, 1.0f, 1.0f};
    private final float[] mScale = {1.0f, 1.0f, 1.0f};

    private float[] mModelMatrix = new float[16];

    public Test()
    {
        Shader mCubeShader = Shader.CreateFromSource("CUBE", shaderSource);
        ShaderManager.Add(mCubeShader);

        VertexArray vArray = new VertexArray();

        VertexBuffer buffer = VertexBuffer.Create(BufferUsage.STATIC);

        BufferLayout layout = new BufferLayout();
        layout.Push("a_Position", VertexAttribPointerType.FLOAT, BYTES_PER_FLOAT, 3, false);
        layout.Push("a_Color", VertexAttribPointerType.FLOAT, BYTES_PER_FLOAT, 3, false);

        buffer.SetData(8 * 6 * BYTES_PER_FLOAT, vertices_data);
        buffer.SetLayout(layout);
        vArray.Push(buffer);

        IndexBuffer iBuffer = new IndexBuffer(12 * 3, index_data);

        mMesh = new Mesh(vArray, iBuffer, mCubeShader);
    }

    public void Update()
    {
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, mDirection[0], mDirection[1], mDirection[2]);
        Matrix.rotateM(mModelMatrix, 0, mRotation, mAxis[0], mAxis[1], mAxis[2]);
        Matrix.scaleM(mModelMatrix, 0, mScale[0], mScale[1], mScale[2]);
    }

    public void Draw(float[] viewMatrix, float[] projectionMatrix)
    {
        float[] MVPMatrix = new float[16];
        Matrix.multiplyMM(MVPMatrix, 0, viewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVPMatrix, 0);

        mMesh.GetShader().Bind();
        mMesh.GetShader().SetUniformMatrix4("u_MVPMatrix", MVPMatrix);
        mMesh.Draw();
    }

    @Override
    public void finalize() throws Throwable
    {
        mMesh.finalize();
        ShaderManager.Clean();
    }
}
