package intern.expivi.detectionsdk.GL;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rick4 on 14-11-2017.
 */

public class VertexArray
{
    private List<VertexBuffer> mBuffers;

    public static VertexArray Create()
    {
        return new VertexArray();
    }

    public VertexArray()
    {
        mBuffers = new ArrayList<VertexBuffer>();
    }

    public void Bind()
    {
        if (!mBuffers.isEmpty())
            mBuffers.get(0).Bind();
    }

    public void Unbind()
    {
        if (!mBuffers.isEmpty())
            mBuffers.get(0).Unbind();
    }

    public void Draw(int count)
    {
        if (!mBuffers.isEmpty())
            mBuffers.get(0).Draw(count);
    }
    public void Push(VertexBuffer buffer)
    {
        mBuffers.add(buffer);
    }

    public VertexBuffer GetBuffer(int index)
    {
        if (!mBuffers.isEmpty())
            return mBuffers.get(index);
        return null;
    }

    @Override
    public void finalize() throws Throwable
    {
        for (int i=0; i < mBuffers.size(); i++)
            mBuffers.get(i).finalize();
        mBuffers.clear();
        super.finalize();
    }
}
