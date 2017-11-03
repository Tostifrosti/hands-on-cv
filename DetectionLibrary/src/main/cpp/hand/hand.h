//
// Created by Rick4 on 11-10-2017.
//

#ifndef ANDROID_OPENCV_HAND_H
#define ANDROID_OPENCV_HAND_H

#include <vector>
#include <functional>
#include <opencv2/opencv.hpp>

#include "../utils/functions.h"
#include "finger.h"
#include "../types.h"

namespace hdcv
{
    enum HandSide // TODO
    {
        LEFT, RIGHT
    };

    class Hand
    {
    public:
        Hand(HandSide side, const std::function<void(cv::Point)>& callback);

        bool Validate(std::vector<cv::Point> contour);
        void Update();
        void RenderDebug(cv::Mat& frame, double scale) const;

        void SetFrameSize(int width, int height);
        bool IsHand() const;

        const cv::Rect& GetBoundingBox() const;
        const std::vector<cv::Point> GetFingerTips() const;
        const std::vector<cv::Point> GetConvexHull() const;
        const std::vector<cv::Point> GetContour() const;
        const std::vector<cv::Point>& GetDefectPoints() const;
        const std::vector<cv::Vec4i>& GetDefectVectors() const;
        const std::vector<Finger>& GetFingers() const;
        size_t GetAmountFingers() const;
        HandSide GetHandSide() const;

        const cv::Point& GetPosition() const;
        const cv::Point& GetCursorPosition() const;
        bool IsPressed() const;
    private:
        void CalculateDefects();

        void RemoveRedundantDefects();
        void RemoveRedundantEndPoints();
    private:
        std::vector<cv::Point> m_ConvexHull;
        std::vector<int> m_HullIndexes;
        std::vector<cv::Point> m_HullPoints;
        std::vector<cv::Vec4i> m_Defects;
        std::vector<cv::Point> m_Contour;
        std::vector<cv::Point> m_FingerTips;
        std::vector<cv::Point> m_DefectsPoints;
        std::vector<cv::Vec4i> m_RawDefects;
        std::vector<Finger> m_Fingers;
        cv::Rect m_BoundingBox;
        int m_FrameWidth, m_FrameHeight;

        static const int s_MinHandSize = 100,
                         s_MaxHandSize = 400;
        cv::Point m_Point;
        cv::Point m_Position;
        cv::Point m_CursorPosition;
        int m_Radius;
        bool m_HasClicked;
        bool m_IsPressed;
        bool m_LShapeFound;
        bool m_IsHandOpen,
             m_IsHandClosed;
        std::vector<std::vector<cv::Point>> m_LShapedPoints;
        std::function<void(cv::Point)> m_ClickCallback;
        HandSide m_HandSide;
    };
}

#endif //ANDROID_OPENCV_HAND_H
