package intern.expivi.detectionlib;

public class Vector
{
    public float x, y, z, w;

    public Vector()
    {
        x = y = z = w = 0.0f;
    }

    public Vector(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = 0.0f;
    }

    public Vector(float x, float y, float z, float w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
}
