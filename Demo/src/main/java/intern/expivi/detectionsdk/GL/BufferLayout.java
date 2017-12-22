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


    /**
     * BufferLayout: This class represents the layout of a buffer by attributes.
     */
    public BufferLayout()
    {
        mSize = 0;
        mAttributes = new ArrayList<VertexAttribute>();
    }

    /**
     * Push: This method creates an attribute with the given values
     * @param name: Name of the attribute
     * @param type: Type of the Attribute (BYTE, INT, FLOAT, ...)
     * @param size: The byte size of the type (BYTE = 1, INT = 1, FLOAT = 4)
     * @param count: The amount of values per element
     * @param normalized: The boolean that represent if the values need to be normalized
     * Example: { position, FLOAT, sizeof(float), 3, false};
     */
    public void Push(String name, VertexAttribPointerType type, int size, int count, boolean normalized)
    {
        mAttributes.add(new VertexAttribute(name, type, size, count, mSize, normalized));
        mSize += size * count;
    }

    /**
     * GetLayout: This method returns the current layout of the buffer.
     * @return Array of Attributes
     */
    public List<VertexAttribute> GetLayout()
    {
        return mAttributes;
    }

    /**
     * GetStride: This method returns the stride of the buffer that has been
     * calculated by the amount of attributes that has been pushed to the stack
     * @return stride
     */
    public int GetStride()
    {
        return mSize;
    }
}
