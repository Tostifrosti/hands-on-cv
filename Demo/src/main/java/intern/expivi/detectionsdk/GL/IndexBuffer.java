package intern.expivi.detectionsdk.GL;

/**
 * Created by Rick4 on 13-11-2017.
 */

import android.opengl.GLES30;
import java.nio.ByteBuffer;

public class IndexBuffer
{
    private final int[] mId = new int[1];
    private int mCount;

    /**
     * IndexBuffer: This class represents the indecies for the vertex buffer
     * @param count: The amount of elements in the array (NOT the size in bytes!)
     * @param data: The indecies as byte array
     */
    public IndexBuffer(int count, byte[] data)
    {
        mCount = count;

        ByteBuffer dataBuffer = ByteBuffer.allocateDirect(data.length);
        dataBuffer.put(data);
        dataBuffer.position(0);

        GLES30.glGenBuffers(1, mId, 0);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, mId[0]);
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, mCount, dataBuffer, GLES30.GL_STATIC_DRAW);
    }

    /**
     * Bind: This method is used to bind the element array buffer
     */
    public void Bind()
    {
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, mId[0]);
    }

    /**
     * Unbind: This method is used to unbind the element array buffer
     */
    public void Unbind()
    {
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    /**
     * GetCount: This method returns the amount of elements in the indecies buffer
     * @return count
     */
    public int GetCount()
    {
        return mCount;
    }

    /**
     * finalize: This method finalizes the method by destroying all the objects within this instance.
     * @throws Throwable: Could throw an exception
     */
    @Override
    public void finalize() throws Throwable
    {
        if (mId[0] != -1)
            GLES30.glDeleteBuffers(1, mId, 0);
        mId[0] = -1;
        super.finalize();
    }
}
