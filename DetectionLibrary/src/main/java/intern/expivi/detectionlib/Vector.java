package intern.expivi.detectionlib;

public class Vector {
    public float x, y, z = 0.0f, w = 0.0f;

    public Vector()
    {

    }

    public Vector(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(float x, float y, float z, float w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
}
