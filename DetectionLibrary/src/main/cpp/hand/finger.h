//
// Created by Rick4 on 11-10-2017.
//

#ifndef ANDROID_OPENCV_FINGER_H
#define ANDROID_OPENCV_FINGER_H

#include <opencv2/opencv.hpp>

#include "../utils/functions.h"
#include "../types.h"

namespace hdcv
{
    class Finger
    {
    public:
        Finger(size_t index, const cv::Point& fingerTop, const cv::Point& defect, double length, double angle, double thickness);
    private:
        double m_Angle;
        double m_Length;
        double m_Thickness;
        size_t m_Index;
        cv::Point m_FingerTop;
        cv::Point m_Knuckle;
        cv::Point m_Defect;

    public:
        const double GetAngle() const;
        const double GetLength() const;
        const double GetThickness() const;
        const size_t GetIndex() const;
        const cv::Point& GetFingerTop() const;
        const cv::Point& GetKnuckle() const;
        const cv::Point& GetDefect() const;

        void RenderDebug(cv::Mat& frame) const;
    private:
        void CalculateKnuckle();
    };
}

#endif //ANDROID_OPENCV_FINGER_H
