package intern.expivi.detectionsdk.GL;

import android.opengl.GLES30;
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

    /**
     * Create: This method creates an instance of the vertex buffer
     * @param usage: The usage of the buffer
     * @return vertex buffer
     */
    public static VertexBuffer Create(BufferUsage usage)
    {
        return new VertexBuffer(usage);
    }

    private VertexBuffer(BufferUsage usage)
    {
        usage = (usage == null) ? BufferUsage.STATIC : usage;
        mBufferUsage = GetBufferUsage(usage);

        GLES30.glGenBuffers(1, mId, 0);
    }

    /**
     * SetData: This method is used for setting the data of the buffer
     * @param size: Amount of items in the data as bytes
     * @param data: The buffer data
     */
    public void SetData(int size, float[] data)
    {
        mSize = size;

        ByteBuffer byteBuf = ByteBuffer.allocateDirect(size);
        byteBuf.order(ByteOrder.nativeOrder());
        FloatBuffer dataBuffer = byteBuf.asFloatBuffer();
        dataBuffer.put(data);
        dataBuffer.position(0);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mId[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, mSize, dataBuffer, mBufferUsage);
    }

    /**
     * Bind: This method is used to bind the vertex buffer & enables the attributes
     */
    public void Bind()
    {
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mId[0]);
        SetLayout(mLayout);
    }

    /**
     * Unbind: This method is used to unbind the vertex buffer & disables the attributes
     */
    public void Unbind()
    {
        DisableAttributes();
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
    }

    /**
     * Draw: This method draws the current vertex buffer
     * @param count: Amount of triangle points
     */
    public void Draw(int count)
    {
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, count, GLES30.GL_UNSIGNED_BYTE, 0);
    }

    /**
     * SetLayout: This method sets the layout of the current vertex buffer
     * @param layout: The buffer layout that needs to be set
     */
    public void SetLayout(BufferLayout layout)
    {
        mLayout = layout;
        List<VertexAttribute> attributes = mLayout.GetLayout();

        for (int i=0; i < attributes.size(); i++)
        {
            GLES30.glEnableVertexAttribArray(i);
            GLES30.glVertexAttribPointer(
                    i,
                    attributes.get(i).Count,
                    attributes.get(i).GetGLType(),
                    attributes.get(i).Normalized,
                    layout.GetStride(),
                    attributes.get(i).Offset
            );
        }
    }

    /**
     * Resize: This method resizes the data of the vertex buffer by the given size
     * @param size: The new size of the buffer
     */
    public void Resize(int size)
    {
        mSize = size;
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mId[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, size, null, mBufferUsage);
    }

    /**
     * GetCount: This method returns the size of the vertex buffer data
     * @return count
     */
    public int GetCount()
    {
        return mSize;
    }

    /**
     * GetBufferUsage: This method returns the usage of the buffer in GLES30
     * @param usage: The usage of the buffer
     * @return GLES30 Buffer usage
     */
    private final int GetBufferUsage(BufferUsage usage)
    {
        switch (usage)
        {
            case STATIC:
                return GLES30.GL_STATIC_DRAW;
            case DYNAMIC:
                return GLES30.GL_DYNAMIC_DRAW;
            case STREAM:
                return GLES30.GL_STREAM_DRAW;
            default:
                return GLES30.GL_STATIC_DRAW;
        }
    }

    /**
     * EnableAttributes: This method enables all the attributes of the vertex buffer
     */
    public void EnableAttributes()
    {
        List<VertexAttribute> attributes = mLayout.GetLayout();

        for (int i=0; i < attributes.size(); i++)
        {
            GLES30.glEnableVertexAttribArray(i);
        }
    }

    /**
     * DisableAttributes: This method disables all the attributes of the vertex buffer
     */
    public void DisableAttributes()
    {
        List<VertexAttribute> attributes = mLayout.GetLayout();

        for (int i=0; i < attributes.size(); i++)
        {
            GLES30.glDisableVertexAttribArray(i);
        }
    }

    /**
     * finalize: This method finalizes the method by destroying all the objects within this instance.
     * @throws Throwable: Could throw an exception
     */
    @Override
    public void finalize() throws Throwable
    {
        if (mId[0] != -1) {
            GLES30.glDeleteBuffers(1, mId, 0);
        }
        mId[0] = -1;
        super.finalize();
    }
}
