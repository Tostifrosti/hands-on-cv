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
#include "opencv2/core/utility.hpp"

namespace hdcv
{
    enum ProgramState
    {
        CREATED = 0,
        INIT,
        CHECK,
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
        /**
         * <p>Application: This class represent a way to detect a hand gesture.</p>
         */
        Application();
        ~Application();

        /**
         * <p>Start: This method is used to start and initialization the application.</p>
         * @param image: The silhouette that is used for the initialization step.
         * @return void
         */
        void Start(const cv::Mat& image);

        /**
         * <p>Analyse: This method draws a silhouette on the given matrix and calculates the color within the silhouette.</p>
         * <p>Note: This method must be called within a loop.</p>
         * @param matAddr: The memory address of the camera frame.
         * @return boolean: Returns true if a hand is found within the silhouette.
         */
        bool Analyse(long matAddr);

        /**
         * <p>Detection: This method analyses the given frame and tries to detect a hand.</p>
         * <p>Note: This method must be called within a loop.</p>
         * @param matAddr: The memory address of the camera frame.
         * @return void
         */
        void Detection(long matAddr);

        /**
         * <p>GetInstance: This method returns the current instance of the Application class.</p>
         * @return static Application
         */
        static Application* GetInstance();

        /**
         * <p>Destroy: This method destroys the current instance of the Application class.</p>
         * @return void
         */
        static void Destroy();

        /**
         * <p>ShowBinaireFrame: This method sets the visualisation of the frame to binair or color.</p>
         * @param value: True to visualize the frame as black-white. False to visualize the frame as color.
         * @return void
         */
        void ShowBinaireFrame(bool value);

        /**
         * <p>Reset: This method resets all the current progress of the application.</p>
         * @return void
         */
        void Reset();

        /**
         * <p>GetCursorPosition: This method returns the current cursor position of the hand.</p>
         * @return {float, float}
         */
        std::pair<float, float> GetCursorPosition();

        /**
         * <p>GetHandState: This method returns the current state of the hand.</p>
         * @return HandState (NONE(-1), CLICKED(0) or PRESSED(1))
         */
        HandState GetHandState() const;

        /**
         * <p>GetHandSide: This method returns the current used side of the hand.</p>
         * @return HandSide (LEFT(0) or RIGHT(1))
         */
        HandSide GetHandSide() const;

        /**
         * <p>SwitchHand: This method toggles between the detection of LEFT or RIGHT hand.</p>
         * @return void
         */
        void SwitchHand();
    private:
        void AnalyseInit(cv::Mat* const source);
        void CheckCalibration(cv::Mat* const source);
        void AnalyseTrack(cv::Mat* const source);
        void Recalibrate(cv::Mat* const source, cv::Mat* const ybb, cv::Mat* const binair, const std::vector<cv::Point>& contour);
        void CheckResolution(int newWidth, int newHeight);
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
        const double m_AcceptCounterMax = 5.0;
        const int m_RecalibrationCounterMax = 0;
        double m_AcceptCounter = m_AcceptCounterMax;
        int m_RecalibrationCounter = m_RecalibrationCounterMax;
        RangeValues m_InRangeValues;
        RangeValues m_BaseInRangeValues;

        Hand m_Hand;
        cv::Point m_TrackingPoint;
        const double m_TrackingRadius = 75.0;
        bool m_IsTracking;

        cv::TickMeter* m_Timer;
        double m_CurrentTime;
        double m_LastTime;
    };
}

#endif //ANDROID_OPENCV_APPLICATION_H
