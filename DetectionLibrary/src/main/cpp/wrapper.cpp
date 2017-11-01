#include <jni.h>
#include "detector.h"

extern "C" {
    JNIEXPORT jboolean JNICALL Java_intern_expivi_detectionlib_NativeWrapper_Initialize(JNIEnv*, jobject, jlong addrFrame)
    {
        return jboolean(Detector::GetInstance()->Initialize((long)addrFrame));
    }

    JNIEXPORT void JNICALL Java_intern_expivi_detectionlib_NativeWrapper_Analyse(JNIEnv*, jobject, jlong addrFrame)
    {
        Detector::GetInstance()->Analyse((long)addrFrame);
    }

    JNIEXPORT void JNICALL Java_intern_expivi_detectionlib_NativeWrapper_Reset(JNIEnv*, jobject)
    {
        Detector::GetInstance()->Reset();
    }
}
