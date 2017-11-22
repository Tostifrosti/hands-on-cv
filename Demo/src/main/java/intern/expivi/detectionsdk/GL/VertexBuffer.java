package intern.expivi.detectionsdk.GL;

import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

/**
 * Created by Rick4 on 13-11-2017.
 */

public class VertexBuffer
{
    private final int[] mId = new int[1];
    private int mSize;
    private final int mBufferUsage;
    private BufferLayout mLayout;

    public static VertexBuffer Create(BufferUsage usage)
    {
        return new VertexBuffer(usage);
    }

    public VertexBuffer(BufferUsage usage)
    {
        usage = (usage == null) ? BufferUsage.STATIC : usage;
        mBufferUsage = GetBufferUsage(usage);

        GLES20.glGenBuffers(1, mId, 0);
    }

    public void SetData(int size, float[] data)
    {
        mSize = size;

        ByteBuffer byteBuf = ByteBuffer.allocateDirect(size);
        byteBuf.order(ByteOrder.nativeOrder());
        FloatBuffer dataBuffer = byteBuf.asFloatBuffer();
        dataBuffer.put(data);
        dataBuffer.position(0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mId[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mSize, dataBuffer, mBufferUsage);
    }

    public void Bind()
    {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mId[0]);
        SetLayout(mLayout);
    }
    public void Unbind()
    {
        DisableAttributes();
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    public void Draw(int count)
    {
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, count, GLES20.GL_UNSIGNED_BYTE, 0);
    }

    public void SetLayout(BufferLayout layout)
    {
        mLayout = layout;
        List<VertexAttribute> attributes = mLayout.GetLayout();

        for (int i=0; i < attributes.size(); i++)
        {
            GLES20.glEnableVertexAttribArray(i);
            GLES20.glVertexAttribPointer(
                    i,
                    attributes.get(i).Count,
                    attributes.get(i).GetGLType(),
                    attributes.get(i).Normalized,
                    layout.GetStride(),
                    attributes.get(i).Offset
            );
        }
    }

    public void Resize(int size)
    {
        mSize = size;
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mId[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, size, null, mBufferUsage);
    }

    public int GetCount()
    {
        return mSize;
    }
    private final int GetBufferUsage(BufferUsage usage)
    {
        switch (usage)
        {
            case STATIC:
                return GLES20.GL_STATIC_DRAW;
            case DYNAMIC:
                return GLES20.GL_DYNAMIC_DRAW;
            case STREAM:
                return GLES20.GL_STREAM_DRAW;
            default:
                return GLES20.GL_STATIC_DRAW;
        }
    }
    public void EnableAttributes()
    {
        List<VertexAttribute> attributes = mLayout.GetLayout();

        for (int i=0; i < attributes.size(); i++)
        {
            GLES20.glEnableVertexAttribArray(i);
        }
    }
    public void DisableAttributes()
    {
        List<VertexAttribute> attributes = mLayout.GetLayout();

        for (int i=0; i < attributes.size(); i++)
        {
            GLES20.glDisableVertexAttribArray(i);
        }
    }

    @Override
    public void finalize() throws Throwable
    {
        if (mId[0] != -1) {
            GLES20.glDeleteBuffers(1, mId, 0);
        }
        mId[0] = -1;
        super.finalize();
    }
}
