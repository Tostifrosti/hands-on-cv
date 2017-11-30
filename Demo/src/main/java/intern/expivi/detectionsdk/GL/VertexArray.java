package intern.expivi.detectionsdk.GL;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rick4 on 14-11-2017.
 */

public class VertexArray
{
    private List<VertexBuffer> mBuffers;


    /**
     * Create: This static method is used to create an instance for the vertex array
     * @return vertex array
     */
    public static VertexArray Create()
    {
        return new VertexArray();
    }

    private VertexArray()
    {
        mBuffers = new ArrayList<VertexBuffer>();
    }

    /**
     * Bind: This method is used to bind the vertex array
     */
    public void Bind()
    {
        Common.Assert(!mBuffers.isEmpty(), "Add a buffer before binding!");
        mBuffers.get(0).Bind();
    }

    /**
     * Unbind: This method is used to unbind the vertex array
     */
    public void Unbind()
    {
        Common.Assert(!mBuffers.isEmpty(), "Add a buffer before unbinding!");
        mBuffers.get(0).Unbind();
    }

    /**
     * Draw: This method is used to draw the vertex array
     * @param count: The amount of indecies
     */
    public void Draw(int count)
    {
        Common.Assert(!mBuffers.isEmpty(), "Add a buffer before drawing!");
        mBuffers.get(0).Draw(count);
    }

    /**
     * Push: This method is used to add a vertex buffer
     * @param buffer: Instance of vertex buffer
     */
    public void Push(VertexBuffer buffer)
    {
        mBuffers.add(buffer);
    }

    /**
     * GetBuffer: This method returns the buffer of the array by the given index
     * @param index: Index of the array
     * @return vertex buffer
     */
    public VertexBuffer GetBuffer(int index)
    {
        if (!mBuffers.isEmpty())
            return mBuffers.get(index);
        return null;
    }

    /**
     * finalize: This method finalizes the method by destroying all the objects within this instance.
     * @throws Throwable: Could throw an exception
     */
    @Override
    public void finalize() throws Throwable
    {
        for (int i=0; i < mBuffers.size(); i++)
            mBuffers.get(i).finalize();
        mBuffers.clear();
        super.finalize();
    }
}
