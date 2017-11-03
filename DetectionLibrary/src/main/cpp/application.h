//
// Created by Rick4 on 11-10-2017.
//

#ifndef ANDROID_OPENCV_APPLICATION_H
#define ANDROID_OPENCV_APPLICATION_H

#include <iostream>
#include <numeric>
#include <cmath>
#include <functional>

#include <opencv2/videoio.hpp>
#include <opencv2/highgui.hpp>
#include <opencv2/imgproc.hpp>

#include "hand/hand.h"
#include "utils/functions.h"
#include "types.h"

namespace hdcv
{
    enum ProgramState
    {
        CREATED = 0,
        INIT,
        TRACK,
        INVALID = 100,
    };
    struct RangeValues
    {
        cv::Scalar Min, Max;
    };

    class Window;
    class CalibrationObject;

    class Application
    {
    public:
        Application();
        ~Application();

        void Start(const cv::Mat& image);
        bool Analyse(long matAddr);
        void Detection(long matAddr);
        static Application* GetInstance();
        static void Destroy();

        void ShowBinaireFrame(bool value);
        void Reset();
    private:
        void AnalyseInit(cv::Mat* const source);
        void AnalyseTrack(cv::Mat* const source);
        void Recalibrate(cv::Mat* const source, cv::Mat* const ybb, cv::Mat* const binair, const std::vector<cv::Point>& contour);
        void CheckResolution(int newWidth, int newHeight);
    public:
    private:
        static Application* s_Instance;
        bool m_IsRunning;
        bool m_IsDebug;
        ProgramState m_ProgramState;
        CalibrationObject* m_CalibrationObject;
        cv::Mat m_ImageHand;
        bool m_ShowBinaireFrame;

        cv::Point m_Resolution;
        double m_Scale;
        int m_BlurSize = 5;
        int m_DialiteSize = 5;
        int m_RangeThreshold = 3;
        int m_AcceptedThreshold = 10;
        const int m_AcceptROIValue = 9;
        const int m_AcceptCounterMax = 2 * 30;
        const int m_RecalibrationCounterMax = 0;
        int m_AcceptCounter = m_AcceptCounterMax;
        int m_RecalibrationCounter = m_RecalibrationCounterMax;
        RangeValues m_InRangeValues;
        RangeValues m_BaseInRangeValues;

        Hand m_Hand;
        cv::Point m_TrackingPoint;
        const double m_TrackingRadius = 75.0;
        bool m_IsTracking;

        // Test
        cv::Rect m_Rect;
        bool m_IsRectGrabbed;
        std::vector<cv::Point> m_ClickPoints;
        int m_ClickTimer;
        const int m_ClickTimerMax = 60;
    };
}

#endif //ANDROID_OPENCV_APPLICATION_H
