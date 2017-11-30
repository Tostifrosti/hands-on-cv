//
// Created by Rick4 on 11-10-2017.
//

#include "finger.h"

namespace hdcv
{
    Finger::Finger(size_t index, const cv::Point& fingerTop, const cv::Point& defect, double length, double angle, double thickness)
            : m_Index(index), m_FingerTop(fingerTop), m_Defect(defect), m_Length(length), m_Angle(angle), m_Thickness(thickness), m_Knuckle(fingerTop)
    {
        this->CalculateKnuckle();
    }

    void Finger::CalculateKnuckle()
    {
        double dx = std::abs(m_FingerTop.x - m_Defect.x);
        double dy = std::abs(m_FingerTop.y - m_Defect.y);
        double rad = std::atan2(dx, dy);
        double dist = m_Thickness * 0.5;
        int px = m_Defect.x + (int)(dist * cos(rad));
        int py = m_Defect.y + (int)(dist * sin(rad));
        m_Knuckle = cv::Point(px, py);
    }


    void Finger::RenderDebug(cv::Mat& frame) const
    {
        cv::putText(frame, NumberToString<size_t>(m_Index + 1), cv::Point(m_FingerTop.x - 5, m_FingerTop.y - 10), CV_FONT_HERSHEY_PLAIN, 1.0, ColorScalar(0, 255, 0), 1);

        cv::circle(frame, m_FingerTop, 9, ColorScalar(0, 255, 0), 2);
        cv::line(frame, m_FingerTop, m_Defect, ColorScalar(0, 255, 0), 2);
        cv::circle(frame, m_Defect, 9, ColorScalar(0, 255, 0), 2);
    }


    const double Finger::GetAngle() const
    {
        return m_Angle;
    }
    const double Finger::GetLength() const
    {
        return m_Length;
    }
    const double Finger::GetThickness() const
    {
        return m_Thickness;
    }
    const cv::Point& Finger::GetFingerTop() const
    {
        return m_FingerTop;
    }
    const cv::Point& Finger::GetKnuckle() const
    {
        return m_Knuckle;
    }
    const cv::Point& Finger::GetDefect() const
    {
        return m_Defect;
    }
}