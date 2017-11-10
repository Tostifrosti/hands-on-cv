#include <jni.h>
#include <vector>
#include <android/log.h>

#include "application.h"

#define APPNAME "DetectionSDK"

extern "C"
{
    JNIEXPORT void JNICALL Java_intern_expivi_detectionlib_NativeWrapper_Test(JNIEnv*, jobject)
    {
        __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "Test method called", 1);
    }

    JNIEXPORT jboolean JNICALL Java_intern_expivi_detectionlib_NativeWrapper_Analyse(JNIEnv*, jobject, jlong addrFrame)
    {
        return jboolean(hdcv::Application::GetInstance()->Analyse((long)addrFrame));
    }
    JNIEXPORT void JNICALL Java_intern_expivi_detectionlib_NativeWrapper_Detection(JNIEnv*, jobject, jlong addrFrame)
    {
        hdcv::Application::GetInstance()->Detection((long)addrFrame);
    }

    JNIEXPORT jobject JNICALL Java_intern_expivi_detectionlib_NativeWrapper_GetCursorPosition(JNIEnv *env, jobject)
    {
        std::pair<float, float> point = hdcv::Application::GetInstance()->GetCursorPosition();

        // Get the class we wish to return an instance of

        jclass clazz = env->FindClass("intern/expivi/detectionlib/Point");

        // Get the method id of an empty constructor in clazz
        jmethodID constructor = env->GetMethodID(clazz, "<init>", "()V");

        // Create an instance of clazz
        jobject obj = env->NewObject(clazz, constructor);

        // Get Field references
        jfieldID x = env->GetFieldID(clazz, "x", "F");
        jfieldID y = env->GetFieldID(clazz, "y", "F");

        // Set fields for object
        env->SetFloatField(obj, x, point.first);
        env->SetFloatField(obj, y, point.second);

        // return object
        return obj;
    }

    JNIEXPORT void JNICALL Java_intern_expivi_detectionlib_NativeWrapper_Create(JNIEnv* env, jobject, jbyteArray byteArray, jint width, jint height)
    {
        jbyte* imgArray  = env->GetByteArrayElements(byteArray, 0);
        cv::Mat imgData(height, width, CV_8UC4, (unsigned char*)imgArray);
        cv::Mat img = cv::imdecode(imgData, 1);
        cv::cvtColor(img, img, CV_RGB2RGBA);
        hdcv::Application::GetInstance()->Start(img);

        env->ReleaseByteArrayElements(byteArray, imgArray, 0);
    }
    JNIEXPORT void JNICALL Java_intern_expivi_detectionlib_NativeWrapper_Destroy(JNIEnv*, jobject)
    {
        hdcv::Application::Destroy();
    }
    JNIEXPORT void JNICALL Java_intern_expivi_detectionlib_NativeWrapper_Reset(JNIEnv* env, jobject)
    {
        hdcv::Application::GetInstance()->Reset();

    }
}
