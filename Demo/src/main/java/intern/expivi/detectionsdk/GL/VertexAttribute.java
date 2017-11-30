package intern.expivi.detectionsdk.GL;

import android.opengl.GLES30;

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

    /**
     * VertexAttribute: This class holds the information of one attribute
     * @param name: Name of the attribute
     * @param type: Type of the attribute (BYTE, INT, FLOAT)
     * @param size: The byte size of the type (BYTE = 1, INT = 1, FLOAT = 4)
     * @param count: The amount of values per element
     * @param offset: The offset in bytes per element
     * @param normalized: The boolean that represent if the values need to be normalized
     * Example: { "position", FLOAT, sizeof(float), 3, 0, false}
     */
    public VertexAttribute(String name, VertexAttribPointerType type, int size, int count, int offset, boolean normalized)
    {
        this.Name = name;
        this.Type = type;
        this.Size = size;
        this.Count = count;
        this.Offset = offset;
        this.Normalized = normalized;
    }

    /**
     * GetGLType: This method returns the OpenGL ES type based on the local type
     * @return GLES30 type
     */
    public final int GetGLType()
    {
        switch (this.Type)
        {
            case BYTE:
                return GLES30.GL_BYTE;
            case INT:
                return GLES30.GL_INT;
            case FLOAT:
                return GLES30.GL_FLOAT;
            default:
                return -1;
        }
    }
}
