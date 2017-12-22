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
        // Analyse the colors of the given camera frame
        return jboolean(hdcv::Application::GetInstance()->Analyse((long)addrFrame));
    }
    JNIEXPORT void JNICALL Java_intern_expivi_detectionlib_NativeWrapper_Detection(JNIEnv*, jobject, jlong addrFrame)
    {
        // Detect a hand in the given camera frame
        hdcv::Application::GetInstance()->Detection((long)addrFrame);
    }

    JNIEXPORT jobject JNICALL Java_intern_expivi_detectionlib_NativeWrapper_GetCursorPosition(JNIEnv *env, jobject)
    {
        // Retrieve the position of the cursor
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
        // Convert the AssetManager from Java to C++
        AAssetManager* ass = AAssetManager_fromJava(env, assetManager);
        assert(ass != nullptr);

        // Load the image
        AAsset* asset = AAssetManager_open(ass, "hand.png", AASSET_MODE_UNKNOWN);
        assert(asset != nullptr);

        // Retrieve the buffer of the image
        //size_t assetLength = AAsset_getLength(asset);
        unsigned char* imgBuffer = (unsigned char*) AAsset_getBuffer(asset);
        assert(imgBuffer != nullptr);

        int height = 400,
            width = 400;

        // Convert the buffer to an OpenCV Matrix object
        cv::Mat imgData(height, width, CV_8UC4, (unsigned char*)imgBuffer);

        // Decode the buffer data to an image.
        cv::Mat img = cv::imdecode(imgData, 1);

        // Add the ALPHA channel to RGB
        cv::cvtColor(img, img, CV_RGB2RGBA);

        // Start the application
        hdcv::Application::GetInstance()->Start(img);

        // Close unnecessary assets & free their resources
        AAsset_close(asset);
    }
    JNIEXPORT void JNICALL Java_intern_expivi_detectionlib_NativeWrapper_Destroy(JNIEnv*, jobject)
    {
        // Close the application
        hdcv::Application::Destroy();
    }
    JNIEXPORT void JNICALL Java_intern_expivi_detectionlib_NativeWrapper_Reset(JNIEnv* env, jobject)
    {
        // Reset the Application
        hdcv::Application::GetInstance()->Reset();
    }
    JNIEXPORT void JNICALL Java_intern_expivi_detectionlib_NativeWrapper_ShowBinaire(JNIEnv*, jobject, jboolean show_binaire)
    {
        // Toggle the visualization of the frame
        hdcv::Application::GetInstance()->ShowBinaireFrame((bool)show_binaire);
    }

    JNIEXPORT int JNICALL Java_intern_expivi_detectionlib_NativeWrapper_GetHandState(JNIEnv*, jobject)
    {
        // Retrieve the current hand state of the application
        return hdcv::Application::GetInstance()->GetHandState();
    }
    JNIEXPORT int JNICALL Java_intern_expivi_detectionlib_NativeWrapper_GetHandSide(JNIEnv*, jobject)
    {
        // Retrieve the current hand state of the application
        return hdcv::Application::GetInstance()->GetHandSide();
    }
    JNIEXPORT void JNICALL Java_intern_expivi_detectionlib_NativeWrapper_SwitchHand(JNIEnv*, jobject)
    {
        // Switch hands (LEFT or RIGHT)
        hdcv::Application::GetInstance()->SwitchHand();
    }
#ifdef __cplusplus
};
#endif
