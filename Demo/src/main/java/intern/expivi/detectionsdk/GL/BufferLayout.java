package intern.expivi.detectionsdk.GL;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rick4 on 15-11-2017.
 */

public class BufferLayout
{
    private List<VertexAttribute> mAttributes;
    private int mSize;

    public BufferLayout()
    {
        mSize = 0;
        mAttributes = new ArrayList<VertexAttribute>();
    }

    // Size: bytes per item, count: amount of items per element
    // Example: { position, FLOAT, sizeof(float), 3, false};
    public void Push(String name, VertexAttribPointerType type, int size, int count, boolean normalized)
    {
        mAttributes.add(new VertexAttribute(name, type, size, count, mSize, normalized));
        mSize += size * count;
    }
    public List<VertexAttribute> GetLayout()
    {
        return mAttributes;
    }
    public int GetStride()
    {
        return mSize;
    }
}
