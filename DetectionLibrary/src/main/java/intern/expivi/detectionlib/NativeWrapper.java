package intern.expivi.detectionlib;

import android.content.res.AssetManager;

public class NativeWrapper {
    static {
        System.loadLibrary("native-lib");
    }

    public static native void Create(AssetManager ass);
    //public static native void Create(byte[] image, int width, int height);
    public static native void Destroy();

    public static native boolean Analyse(long nativeObjectAddress);
    public static native void Detection(long nativeObjectAddress);
    public static native Vector GetCursorPosition();
    public static native void Reset();
    public static native void ShowBinaire(boolean value);
    public static native int GetHandState();
}
