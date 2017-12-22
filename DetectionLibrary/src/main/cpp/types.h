//
// Created by Rick4 on 12-10-2017.
//

#include <opencv2/imgproc.hpp>
#include <opencv2/opencv.hpp>
#include <android/log.h>

#ifndef ANDROID_OPENCV_TYPES_H
#define ANDROID_OPENCV_TYPES_H

#define HDCV_PI     3.1415926535897932384626433832795
#define HDCV_2PI    6.283185307179586476925286766559
#define HDCV_LOG2   0.69314718055994530941723212145818

#define LOG(...) __android_log_print(ANDROID_LOG_DEBUG, "DetectionSDK", __VA_ARGS__)
#define VERBOSE(...) __android_log_print(ANDROID_LOG_VERBOSE, "DetectionSDK", __VA_ARGS__)
#define ERROR(...) __android_log_print(ANDROID_LOG_ERROR, "DetectionSDK", __VA_ARGS__)

#define USE_RGB 1

#if USE_RGB > 0
    #define RGB2YCrCb   CV_RGB2YCrCb
    #define RGB2HSV     CV_RGB2HSV
    #define RGB2RGBA    CV_RGB2RGBA
    #define ColorScalar(b, g, r) cv::Scalar(r, g, b)
#else
    #define RGB2YCrCb   CV_BGR2YCrCb
    #define RGB2HSV     CV_BGR2HSV
    #define RGB2RGBA    CV_BGR2RGBA
    #define ColorScalar(b, g, r) cv::Scalar(b, g, r)
#endif


#endif //ANDROID_OPENCV_TYPES_H