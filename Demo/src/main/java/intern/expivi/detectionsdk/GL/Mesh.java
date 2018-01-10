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

    /**
     * Mesh: This class couples the vertex array, index buffer and the shader
     * @param vertexArray: Instance of the vertex array
     * @param indexBuffer: Instance of the index buffer
     * @param shader: Instance of the shader
     */
    public Mesh(VertexArray vertexArray, IndexBuffer indexBuffer, Shader shader)
    {
        mVertexArray = vertexArray;
        mIndexBuffer = indexBuffer;
        mShader = shader;
    }

    /**
     * Draw: This method binds the buffers in the correct order and draws the shader.
     */
    public void Draw()
    {
        mShader.Bind();

        mVertexArray.Bind();
        mIndexBuffer.Bind();
        mVertexArray.Draw(mIndexBuffer.GetCount());
        mIndexBuffer.Unbind();
        mVertexArray.Unbind();

        mShader.Unbind();
    }

    /**
     * GetShader: This method returns the instance of the shader
     * @return shader
     */
    public Shader GetShader()
    {
        return mShader;
    }

    /**
     * finalize: This method finalizes the method by destroying all the objects within this instance.
     * @throws Throwable: Could throw an exception
     */
    @Override
    public void finalize() throws Throwable
    {
        if (mVertexArray != null)
            mVertexArray.finalize();
        mVertexArray = null;
        if (mIndexBuffer != null)
            mIndexBuffer.finalize();
        mIndexBuffer = null;
        mShader = null; // Shader gets finalized in the ShaderManager
    }
}
