package intern.expivi.detectionlib;

/**
 * Created by Rick4 on 11-12-2017.
 */

public class Rect
{
    public int x, y, width, height;

    public Rect()
    {
        x = y = width = height = 0;
    }

    public Rect(int width, int height)
    {
        this.x = this.y = 0;
        this.width = width;
        this.height = height;
    }

    public Rect(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean Intersects(Rect other)
    {
        if (width == 0 || height == 0 ||
            other.width == 0 || other.height == 0)
            return false;

        if (x + width <= other.x)
            return false;

        if (x >= other.x + other.width)
            return false;

        if (y + height <= other.y)
            return false;

        if (y >= other.y + other.height)
            return false;

        return true;
    }
}
