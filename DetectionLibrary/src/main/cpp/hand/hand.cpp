//
// Created by Rick4 on 11-10-2017.
//

#include "hand.h"

namespace hdcv
{
    Hand::Hand(HandSide side, const std::function<void(cv::Point)>& callback)
            : m_HandSide(side), m_ClickCallback(callback), m_FrameWidth(0), m_FrameHeight(0),
              m_HasClicked(false), m_IsPressed(false),
              m_Position(0, 0), m_IsHandOpen(false), m_IsHandClosed(false)
    {
    }

    void Hand::RemoveRedundantEndPoints()
    {
        cv::Vec4i temp;
        int tolerance = m_BoundingBox.width / 6;
        int startidx, endidx;
        int startidx2, endidx2;

        for (size_t i = 0; i < m_Defects.size(); i++)
        {
            for (size_t j = i; j < m_Defects.size(); j++)
            {
                startidx = m_Defects[i][0];
                endidx = m_Defects[i][1];
                startidx2 = m_Defects[j][0];
                endidx2 = m_Defects[j][1];

                if (Distance(m_Contour[startidx], m_Contour[endidx2]) < tolerance) {
                    m_Contour[startidx] = m_Contour[endidx2];
                    break;
                }
                if (Distance(m_Contour[endidx], m_Contour[startidx2]) < tolerance) {
                    m_Contour[startidx2] = m_Contour[endidx];
                }
            }
        }
    }
    void Hand::RemoveRedundantDefects()
    {
        int tolerance = m_BoundingBox.height / 20;
        double angleMax = 120.0;
        std::vector<cv::Vec4i> newDefects;
        int startidx, endidx, faridx;
        std::vector<cv::Vec4i>::iterator d = m_RawDefects.begin();

        for (size_t i = 0; i < m_RawDefects.size(); i++)
        {
            startidx = m_RawDefects[i][0];
            endidx = m_RawDefects[i][1];
            faridx = m_RawDefects[i][2];

            if (Distance(m_Contour[startidx], m_Contour[faridx]) > tolerance &&
                Distance(m_Contour[endidx], m_Contour[faridx]) > tolerance &&
                Angle(m_Contour[startidx], m_Contour[faridx], m_Contour[endidx]) < angleMax)
            {
                if (m_Contour[endidx].y <= (m_BoundingBox.y + m_BoundingBox.height - m_BoundingBox.height / 10) &&
                    m_Contour[startidx].y <= (m_BoundingBox.y + m_BoundingBox.height - m_BoundingBox.height / 10))
                {
                    m_Defects.emplace_back(m_RawDefects[i]);
                }
            }
        }
    }
    void Hand::CalculateDefects()
    {
        m_Defects.clear();
        m_RawDefects.clear();

        // Get Defects
        cv::convexityDefects(cv::Mat(m_Contour), m_HullIndexes, m_RawDefects);

        // Remove Redundant Defects/End points
        RemoveRedundantDefects();
        RemoveRedundantEndPoints();
    }

    bool Hand::Validate(std::vector<cv::Point> contour)
    {
        m_Contour.clear();
        m_ConvexHull.clear();
        m_DefectsPoints.clear();
        m_FingerTips.clear();
        m_Fingers.clear();
        m_Radius = 0;
        m_LShapedPoints.clear();
        m_Point = cv::Point(-1, -1);

        m_Contour = contour;
        cv::convexHull(cv::Mat(contour), m_ConvexHull, false);

        if (m_ConvexHull.size() <= 2)
            return false;

        m_BoundingBox = cv::boundingRect(m_ConvexHull);

        // Get Hull Points/Indices
        //cv::approxPolyDP(m_Contour, m_Contour, 18, true);
        cv::convexHull(cv::Mat(m_Contour), m_HullPoints, true, true);
        cv::convexHull(cv::Mat(m_Contour), m_HullIndexes, true, false);

        // Defects
        this->CalculateDefects();

        m_LShapeFound = false;

        for (size_t i = 0; i < m_Defects.size(); i++)
        {
            // Finger points
            cv::Point p1 = m_Contour[m_Defects[i][0]];
            cv::Point p2 = m_Contour[m_Defects[i][1]];
            cv::Point p3 = m_Contour[m_Defects[i][2]];

            // Calculate finger tops
            cv::Point diffLeft(p3.x - p2.x, p3.y - p2.y);
            cv::Point diffRight(p3.x - p1.x, p3.y - p1.y);
            double lengthLeft = std::abs(std::sqrt(std::pow(diffLeft.x, 2) + std::pow(diffLeft.y, 2)));
            double lengthRight = std::abs(std::sqrt(std::pow(diffRight.x, 2) + std::pow(diffRight.y, 2)));
            double diff = lengthRight - lengthLeft;
            //double fingerTopAngle = Rad2Deg(std::atan2(p1.x - p2.x, p1.y - p2.y)); // p2 is origin -> p1
            double angleThumb = Rad2Deg(std::atan2(p3.y - p2.y, p3.x - p2.x)) + 270;
            //double angleIndexFinger = Rad2Deg(std::atan2(p3.y - p1.y, p3.x - p1.x)) + 270;
            double inAngle = Angle(p2, p3, p1);
            //double thicknessRight = lengthRight * 0.4;
            //double thicknessLeft = lengthLeft * 0.4;

            m_DefectsPoints.emplace_back(p3);

            //  P2 \ P3 / P1

            // Not Clicked _!
            if (angleThumb > 200 && angleThumb < 340 &&
                lengthLeft > 25.0 && lengthRight > 25.0 && lengthLeft < lengthRight &&
                diff > 0 && std::abs(diff) < lengthRight * 0.5 && lengthLeft * 1.20 < lengthRight &&
                inAngle > 5 && inAngle < 130 &&
                p2.x < p1.x && p2.y > p1.y && p2.x < p3.x && p1.y < p3.y)
            {
                m_IsHandOpen = true;
                m_IsHandClosed = false;
                m_LShapeFound = true;
                m_Position = p3;
                m_CursorPosition = p2;

                std::vector<cv::Point> points;
                points.push_back(p1);
                points.push_back(p2);
                points.push_back(p3);
                m_LShapedPoints.push_back(points);
                m_LShapedPoints.push_back(points);
                break;
            }
                // Clicked _.
            else if ((m_IsHandOpen || m_IsHandClosed) && (angleThumb > 200 && angleThumb < 340 &&
                     lengthLeft > 25.0 && lengthLeft >= lengthRight && lengthLeft < lengthRight * 3 &&
                     diff < 0 && std::abs(diff) < lengthLeft && std::abs(diff) < lengthRight * 0.5 &&
                     inAngle > 5 && inAngle < 130 &&
                     p2.x < p3.x && p1.y < p3.y && p2.x < p1.x))
            {
                m_IsHandOpen = false;
                m_IsHandClosed = true;
                m_LShapeFound = true;
                m_Position = p3;
                m_CursorPosition = p2;

                std::vector<cv::Point> points;
                points.push_back(p1);
                points.push_back(p2);
                points.push_back(p3);
                m_LShapedPoints.push_back(points);
                break;
            }
        }

        if (!m_LShapeFound)
        {
            m_Fingers.clear();
            m_Defects.clear();
            m_DefectsPoints.clear();
            m_LShapedPoints.clear();
            m_Contour.clear();
            m_IsHandOpen = false;
            m_IsHandClosed = false;
        }

        return m_LShapeFound;
    }

    void Hand::Update()
    {
        if (!IsHand())
            return;

        if (!m_Defects.empty())
        {
            // Finger points
            cv::Point p1 = m_LShapedPoints[0][0];
            cv::Point p2 = m_LShapedPoints[0][1];
            cv::Point p3 = m_LShapedPoints[0][2];

            // Calculate finger tops
            cv::Point diffLeft(p3.x - p2.x, p3.y - p2.y);
            cv::Point diffRight(p3.x - p1.x, p3.y - p1.y);
            double lengthLeft = std::abs(std::sqrt(std::pow(diffLeft.x, 2) + std::pow(diffLeft.y, 2)));
            double lengthRight = std::abs(std::sqrt(std::pow(diffRight.x, 2) + std::pow(diffRight.y, 2)));
            double diff = lengthRight - lengthLeft;
            //double fingerTopAngle = Rad2Deg(std::atan2(p1.x - p2.x, p1.y - p2.y)); // p2 is origin -> p1
            double angleThumb = Rad2Deg(std::atan2(p3.y - p2.y, p3.x - p2.x)) + 270; // Rad2Deg(std::acos((diffLeft.x * diffRight.x + diffLeft.y * diffRight.y) / (lengthLeft * lengthRight)));
            double angleIndexFinger = Rad2Deg(std::atan2(p3.y - p1.y, p3.x - p1.x)) + 270;
            //double inAngle = Angle(p2, p3, p1);
            double thicknessRight = lengthRight * 0.4;
            double thicknessLeft = lengthLeft * 0.4;

            if (m_LShapedPoints.size() > 1)
            {
                if (m_IsPressed) {
                    m_HasClicked = true;
                    m_ClickCallback(p2);
                }
                else
                    m_HasClicked = false;
                m_IsPressed = false;
                m_Point = p3;
                m_Position = p3;
                m_CursorPosition = p2;
                m_Radius = (int)(lengthLeft + std::abs(diff * 0.5));
                m_Fingers.emplace_back(Finger(m_Fingers.size(), p2, p3, lengthLeft, angleThumb, thicknessLeft)); // Thumb
                m_Fingers.emplace_back(Finger(m_Fingers.size(), p1, p3, lengthRight, angleIndexFinger, thicknessRight)); // Index Finger
            }
            else
            {
                m_HasClicked = false;
                m_IsPressed = true;
                m_Point = p3;
                m_Position = p3;
                m_CursorPosition = p2;
                m_Radius = (int)(lengthLeft + std::abs(diff * 0.5));
                m_Fingers.emplace_back(Finger(m_Fingers.size(), p2, p3, lengthLeft, angleThumb, thicknessLeft)); // Thumb
            }
        }
    }

    void Hand::RenderDebug(cv::Mat& frame, double scale) const
    {
        // cv::rectangle(frame, m_BoundingBox, cv::Scalar(255, 0, 0));
        /* cv::circle(frame, cv::Point(m_BoundingBox.x + (m_BoundingBox.width / 2), m_BoundingBox.y + (m_BoundingBox.height / 2)),
        std::min(m_BoundingBox.width, m_BoundingBox.height) / 2, cv::Scalar(255, 0, 0), 1);*/

        if (!m_Contour.empty())
        {
            if (!m_IsPressed)
                cv::circle(frame, m_Point, m_Radius, ColorScalar(255, 0, 0), 2);
            else
                cv::circle(frame, m_Point, m_Radius, ColorScalar(0, 0, 255), 2);
            cv::circle(frame, m_Point, 3, ColorScalar(255, 0, 0), -1);

            for (size_t i = 0; i < m_Fingers.size(); i++) {
                m_Fingers[i].RenderDebug(frame);
            }

            for (size_t i = 0; i < m_DefectsPoints.size(); i++) {
                cv::circle(frame, m_DefectsPoints[i], 3, ColorScalar(0, 255, 255), -1);
            }
        }

        // User Interface
        std::string clicked_str = "Clicked: ";
        std::string pressed_str = "Pressed: ";
        cv::putText(frame, clicked_str.append(BoolToString(m_HasClicked)).c_str(), cv::Point(0, 20), CV_FONT_HERSHEY_PLAIN, 1.0 * scale, ColorScalar(255, 255, 255), 1);
        cv::putText(frame, pressed_str.append(BoolToString(m_IsPressed)).c_str(), cv::Point(0, 40), CV_FONT_HERSHEY_PLAIN, 1.0 * scale, ColorScalar(255, 255, 255), 1);


        // Gradenboog
        cv::Point point(m_FrameWidth - 75, 75);
        cv::circle(frame, point, 30, ColorScalar(255, 0, 0), 1);

        if (!m_Fingers.empty()) {
            cv::line(frame, point, Angle(m_Fingers[0].GetFingerTop(), m_Fingers[0].GetDefect(), 30, point), ColorScalar(0, 0, 255), 1, cv::LINE_AA);
            if (m_Fingers.size() > 1)
                cv::line(frame, point, Angle(m_Fingers[1].GetFingerTop(), m_Fingers[1].GetDefect(), 30, point), ColorScalar(0, 0, 255), 1, cv::LINE_AA);
        }
        cv::line(frame, point, cv::Point(point.x, point.y - 35), ColorScalar(255, 0, 0), 1); // North
        cv::line(frame, point, cv::Point(point.x + 35, point.y), ColorScalar(255, 0, 0), 1); // East
        cv::line(frame, point, cv::Point(point.x, point.y + 35), ColorScalar(255, 0, 0), 1); // South
        cv::line(frame, point, cv::Point(point.x - 35, point.y), ColorScalar(255, 0, 0), 1); // West

        cv::putText(frame, "0",   cv::Point(point.x, point.y - 35), CV_FONT_HERSHEY_PLAIN, 1.0, ColorScalar(255, 0, 0), 1);
        cv::putText(frame, "90",  cv::Point(point.x + 35, point.y), CV_FONT_HERSHEY_PLAIN, 1.0, ColorScalar(255, 0, 0), 1);
        cv::putText(frame, "180", cv::Point(point.x, point.y + 40), CV_FONT_HERSHEY_PLAIN, 1.0, ColorScalar(255, 0, 0), 1);
        cv::putText(frame, "270", cv::Point(point.x - 45, point.y), CV_FONT_HERSHEY_PLAIN, 1.0, ColorScalar(255, 0, 0), 1);
    }

    bool Hand::IsHand() const
    {
        if (m_Contour.empty() || m_Defects.empty())
            return false;

        if (m_Fingers.size() > 5)
            return false;

        if (m_BoundingBox.width <= 0 || m_BoundingBox.height <= 0)
            return false;

        if (m_BoundingBox.width / m_BoundingBox.height > 4 || m_BoundingBox.height / m_BoundingBox.width > 4)
            return false;

        if ((m_BoundingBox.width > s_MaxHandSize && m_BoundingBox.height > s_MaxHandSize) ||
            (m_BoundingBox.width < s_MinHandSize && m_BoundingBox.height < s_MinHandSize))
            return false;

        if (m_BoundingBox.height > m_FrameHeight * 0.90f ||
            m_BoundingBox.width > m_FrameWidth * 0.90f)
            return false;

        // Only for this project (max: 2 fingers)
        if (!m_LShapeFound)
            return false;

        return true;
    }

    void Hand::SetFrameSize(int width, int height)
    {
        m_FrameWidth = width;
        m_FrameHeight = height;
    }

    const cv::Rect& Hand::GetBoundingBox() const
    {
        return m_BoundingBox;
    }
    const std::vector<cv::Point> Hand::GetContour() const
    {
        return m_Contour;
    }
    const std::vector<cv::Point> Hand::GetConvexHull() const
    {
        return m_ConvexHull;
    }
    const std::vector<cv::Point> Hand::GetFingerTips() const
    {
        return m_FingerTips;
    }
    const std::vector<cv::Point>& Hand::GetDefectPoints() const
    {
        return m_DefectsPoints;
    }
    const std::vector<cv::Vec4i>& Hand::GetDefectVectors() const
    {
        return m_Defects;
    }
    size_t Hand::GetAmountFingers() const
    {
        return m_Fingers.size();
    }
    const std::vector<Finger>& Hand::GetFingers() const
    {
        return m_Fingers;
    }

    const cv::Point& Hand::GetPosition() const
    {
        return m_Position;
    }
    const cv::Point& Hand::GetCursorPosition() const
    {
        return m_CursorPosition;
    }
    bool Hand::IsPressed() const
    {
        return m_IsPressed;
    }
    HandSide Hand::GetHandSide() const
    {
        return m_HandSide;
    }
}