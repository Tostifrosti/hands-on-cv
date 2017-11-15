#include <jni.h>
#include <vector>
#include <android/log.h>
#include <assert.h>

#include "application.h"
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

#include <opencv2/videoio.hpp>
#include <opencv2/highgui.hpp>
#include <opencv2/imgproc.hpp>

#define APPNAME "DetectionSDK"

#define LOG(...) __android_log_print(ANDROID_LOG_DEBUG, APPNAME, __VA_ARGS__)
#define VERBOSE(...) __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, __VA_ARGS__)
#define ERROR(...) __android_log_print(ANDROID_LOG_ERROR, APPNAME, __VA_ARGS__)

#ifdef __cplusplus
extern "C"
{
#endif
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

        jclass clazz = env->FindClass("intern/expivi/detectionlib/Vector");

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

    JNIEXPORT void JNICALL Java_intern_expivi_detectionlib_NativeWrapper_Create(JNIEnv* env, jobject, jobject assetManager)
    {
        AAssetManager* ass = AAssetManager_fromJava(env, assetManager);
        assert(ass != nullptr);

        AAsset* asset = AAssetManager_open(ass, "hand.png", AASSET_MODE_UNKNOWN);
        assert(asset != nullptr);

        //size_t assetLength = AAsset_getLength(asset);
        unsigned char* imgBuffer = (unsigned char*) AAsset_getBuffer(asset);

        assert(imgBuffer != nullptr);

        int height = 400,
            width = 400;

        cv::Mat imgData(height, width, CV_8UC4, (unsigned char*)imgBuffer);
        cv::Mat img = cv::imdecode(imgData, 1);
        cv::cvtColor(img, img, CV_RGB2RGBA);
        hdcv::Application::GetInstance()->Start(img);

        AAsset_close(asset);
    }
    JNIEXPORT void JNICALL Java_intern_expivi_detectionlib_NativeWrapper_Destroy(JNIEnv*, jobject)
    {
        hdcv::Application::Destroy();
    }
    JNIEXPORT void JNICALL Java_intern_expivi_detectionlib_NativeWrapper_Reset(JNIEnv* env, jobject)
    {
        hdcv::Application::GetInstance()->Reset();
    }
    JNIEXPORT void JNICALL Java_intern_expivi_detectionlib_NativeWrapper_ShowBinaire(JNIEnv*, jobject, jboolean show_binaire)
    {
        hdcv::Application::GetInstance()->ShowBinaireFrame((bool)show_binaire);
    }

    JNIEXPORT int JNICALL Java_intern_expivi_detectionlib_NativeWrapper_GetHandState(JNIEnv*, jobject)
    {
        return hdcv::Application::GetInstance()->GetHandState();
    }
#ifdef __cplusplus
};
#endif
