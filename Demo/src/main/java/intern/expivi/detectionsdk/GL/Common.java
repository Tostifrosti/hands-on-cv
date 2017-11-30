package intern.expivi.detectionsdk.GL;

/**
 * Created by Rick4 on 16-11-2017.
 */

public class Common
{
    public static final boolean DEBUG = true;


    /**
     * Assert: This static method represent a simple assert
     * Note: This assertion will only work in DEBUG mode
     * @param condition: A boolean that represent if the condition has succeeded or not.
     */
    public static void Assert(boolean condition)
    {
        if (!DEBUG)
            return;

        Assert(condition, "");
    }

    /**
     * Assert: This static method represent a simple assert with message
     * Note: This assertion will only work in DEBUG mode
     * @param condition: A boolean that represent if the condition has succeeded or not.
     * @param message: A message that is passed to the AssertionError exception.
     */
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
