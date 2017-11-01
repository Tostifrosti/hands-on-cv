package intern.expivi.detectionlib;

public class NativeWrapper {
    static {
        System.loadLibrary("native-lib");
    }

    public static native void Create(byte[] image, int width, int height);
    public static native void Pause();
    public static native void Resume();
    public static native void Destroy();

    public static native boolean Analyse(long nativeObjectAddress);
    public static native void Detection(long nativeObjectAddress);
    public static native void Reset();
}
