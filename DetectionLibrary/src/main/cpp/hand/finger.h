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
        /**
         * <p>Finger: This class holds the information of one finger.</p>
         * @param index: The number that represents the finger.
         * @param fingerTop: The position of the fingertop.
         * @param defect: The position of the defect of the finger. (Defect: The lowest point between two fingers.)
         * @param length: The length of the finger.
         * @param angle: The angle of the finger in degrees.
         * @param thickness: The thickness of the finger.
         */
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
        /**
         * <p>GetAngle: returns the angle of the finger in degrees.</p>
         * @return angle
         */
        const double GetAngle() const;

        /**
         * <p>GetLength: returns the length of the finger.</p>
         * @return length
         */
        const double GetLength() const;

        /**
         * <p>GetThickness: returns the thickness of the finger.</p>
         * @return thickness
         */
        const double GetThickness() const;

        /**
         * <p>GetFingerTop: returns the position of the finger top.</p>
         * @return Point(x,y)
         */
        const cv::Point& GetFingerTop() const;

        /**
         * <p>GetKnuckle: returns the position of the knuckle.</p>
         * @return Point(x,y)
         */
        const cv::Point& GetKnuckle() const;

        /**
         * <p>GetDefect: returns the position of the defect (lowest point between 2 fingers).</p>
         * @return Point(x,y)
         */
        const cv::Point& GetDefect() const;

        /**
         * <p>RenderDebug: This method renders debug information about the finger on the given frame.</p>
         * @param frame: A matrix where the debug information can be drawn on.
         * @return void
         */
        void RenderDebug(cv::Mat& frame) const;
    private:
        void CalculateKnuckle();
    };
}

#endif //ANDROID_OPENCV_FINGER_H
