#include <jni.h>
#include <android/log.h>

#define APPNAME "DetectionSDK"

int i = 0;

extern "C" {
    JNIEXPORT void JNICALL Java_intern_expivi_detectionlib_NativeWrapper_Test(JNIEnv*, jobject)
    {
        __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "Test method called", 1);
    }

    JNIEXPORT jboolean JNICALL Java_intern_expivi_detectionlib_NativeWrapper_Initialize(JNIEnv*, jobject, jlong addrFrame)
    {
//        Detection::GetInstance()->Analyse((long)addrFrame);
        i++;
        return jboolean(i >= 100);
    }

    JNIEXPORT void JNICALL Java_intern_expivi_detectionlib_NativeWrapper_Analyze(JNIEnv*, jobject, jlong addrFrame)
    {

    }

    JNIEXPORT void JNICALL Java_intern_expivi_detectionlib_NativeWrapper_Reset(JNIEnv*, jobject)
    {
        i = 0;
    }
}
