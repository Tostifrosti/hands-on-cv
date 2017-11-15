package intern.expivi.detectionsdk.GL;

import android.opengl.GLES20;

/**
 * Created by Rick4 on 14-11-2017.
 */

public class VertexAttribute
{
    public String Name;
    public VertexAttribPointerType Type;
    public int Size;
    public int Count;
    public int Offset;
    public boolean Normalized;

    // Size: bytes per item, Count: items per element
    // Example: { "position", FLOAT, sizeof(float), 3, 0, false}
    public VertexAttribute(String name, VertexAttribPointerType type, int size, int count, int offset, boolean normalized)
    {
        this.Name = name;
        this.Type = type;
        this.Size = size;
        this.Count = count;
        this.Offset = offset;
        this.Normalized = normalized;
    }

    public final int GetGLType()
    {
        switch (this.Type)
        {
            case BYTE:
                return GLES20.GL_BYTE;
            case INT:
                return GLES20.GL_INT;
            case FLOAT:
                return GLES20.GL_FLOAT;
            default:
                return GLES20.GL_FLOAT;
        }
    }
}
