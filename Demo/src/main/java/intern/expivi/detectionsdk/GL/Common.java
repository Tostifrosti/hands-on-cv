package intern.expivi.detectionsdk.GL;

/**
 * Created by Rick4 on 16-11-2017.
 */

public class Common
{
    public static final boolean DEBUG = true;

    public static void Assert(boolean condition)
    {
        if (!DEBUG)
            return;

        Assert(condition, "");
    }

    public static void Assert(boolean condition, String message)
    {
        if (!DEBUG)
            return;

        if (!condition)
        {
            throw new AssertionError(message);
        }
    }
}
