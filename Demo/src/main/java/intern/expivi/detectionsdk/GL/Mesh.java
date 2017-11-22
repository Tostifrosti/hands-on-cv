package intern.expivi.detectionsdk.GL;

import intern.expivi.detectionsdk.GL.shaders.Shader;

/**
 * Created by Rick4 on 14-11-2017.
 */

public class Mesh
{
    private VertexArray mVertexArray;
    private IndexBuffer mIndexBuffer;
    private Shader mShader;

    public Mesh(VertexArray vertexArray, IndexBuffer indexBuffer, Shader shader)
    {
        mVertexArray = vertexArray;
        mIndexBuffer = indexBuffer;
        mShader = shader;
    }

    public void Draw() {

        mShader.Bind();

        mVertexArray.Bind();
        mIndexBuffer.Bind();
        mVertexArray.Draw(mIndexBuffer.GetCount());
        mIndexBuffer.Unbind();
        mVertexArray.Unbind();

        mShader.Unbind();
    }

    public Shader GetShader()
    {
        return mShader;
    }

    @Override
    public void finalize() throws Throwable
    {
        if (mVertexArray != null)
            mVertexArray.finalize();
        if (mIndexBuffer != null)
            mIndexBuffer.finalize();
        mShader = null;

        super.finalize();
    }
}
