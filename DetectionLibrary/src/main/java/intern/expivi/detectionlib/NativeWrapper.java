package intern.expivi.detectionlib;

public class NativeWrapper {
    static {
        System.loadLibrary("native-lib");
    }

    public static native boolean Initialize(long nativeObjectAddress);
    public static native void Reset();
    public static native void Analyse(long nativeObjectAddress);
}
