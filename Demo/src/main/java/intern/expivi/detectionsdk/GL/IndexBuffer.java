package intern.expivi.detectionsdk.GL;

/**
 * Created by Rick4 on 13-11-2017.
 */

import android.opengl.GLES20;
import java.nio.ByteBuffer;

public class IndexBuffer
{
    private final int[] mId = new int[1];
    private int mCount;

    public IndexBuffer(int count, byte[] data)
    {
        mCount = count;

        ByteBuffer dataBuffer = ByteBuffer.allocateDirect(data.length);
        dataBuffer.put(data);
        dataBuffer.position(0);

        GLES20.glGenBuffers(1, mId, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mId[0]);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, mCount, dataBuffer, GLES20.GL_STATIC_DRAW);
    }

    public void Bind()
    {
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mId[0]);
    }
    public void Unbind()
    {
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public int GetCount()
    {
        return mCount;
    }
    @Override
    public void finalize() throws Throwable
    {
        if (mId[0] != -1)
            GLES20.glDeleteBuffers(1, mId, 0);
        mId[0] = -1;
        super.finalize();
    }
}
