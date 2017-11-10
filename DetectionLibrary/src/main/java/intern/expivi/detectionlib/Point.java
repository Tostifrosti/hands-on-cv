package intern.expivi.detectionlib;

public class Point {
    public float x, y, z = 0.0f, w = 0.0f;

    public Point()
    {

    }

    public Point(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point(float x, float y, float z, float w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
}
