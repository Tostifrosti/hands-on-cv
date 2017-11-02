package intern.expivi.detectionlib;

import android.util.Log;
import java.util.LinkedList;

public class FPSMeter {

    private String TAG;
    private LinkedList<Long> times = new LinkedList<Long>() {{
        add(System.nanoTime());
    }};

    public FPSMeter(String tag)
    {
        TAG = tag;
        TAG += " FPSMeter";
    }

    /**
     * Calculates and returns frames per second
     */
    public void tick() {

        long lastTime = System.nanoTime();
        double NANOS = 1000000000.0;
        int MAX_SIZE = 100;
        double difference = (lastTime - times.getFirst()) / NANOS;
        times.addLast(lastTime);
        int size = times.size();
        if (size > MAX_SIZE) {
            times.removeFirst();
        }
        double fps = difference > 0 ? times.size() / difference : 0.0;
        Log.d(TAG, ": " + fps);
    }
}
