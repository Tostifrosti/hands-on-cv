//
// Created by Rick4 on 11-10-2017.
//

#ifndef ANDROID_OPENCV_HAND_H
#define ANDROID_OPENCV_HAND_H

#include <vector>
#include <functional>
#include <algorithm>
#include <opencv2/opencv.hpp>

#include "../utils/functions.h"
#include "finger.h"
#include "../types.h"
#include "../utils/Vertex.h"

namespace hdcv
{
    enum HandSide
    {
        LEFT, RIGHT
    };

    enum HandState
    {
        NONE = -1,
        CLICKED,
        PRESSED
    };

    class Hand
    {
    public:
        /**
         * <p>Hand: This class collects information of one hand with the given contour.</p>
         * @param side: The hand side to start detecting.
         * @param callback: A function that needs to be called when the HandState is Pressed.
         */
        Hand(HandSide side, const std::function<void(cv::Point)>& callback);

        /**
         * <p>Validate: This method validates the basic patterns of a hand.</p>
         * <p>Note: This function needs to be called BEFORE the Update method.</p>
         * @param contour: An array of positions that represents a shape.
         * @return boolean: Returns true if a hand is detected.
         */
        bool Validate(std::vector<cv::Point> contour);

        /**
         * <p>Update: This method updates the values of the hand, fingers and gestures.</p>
         * @return void
         */
        void Update();

        /**
         * <p>RenderDebug: This method renders debug information about the hand on the given frame.</p>
         * @param frame: A matrix where the debug information can be drawn on.
         * @param scale: The scale for the debug information on the frame.
         * @return void
         */
        void RenderDebug(cv::Mat& frame, double scale) const;

        /**
         * <p>SetFrameSize: This method sets the width and height of the frame.</p>
         * <p>Note: This method should be called when the size of the frame is changed!</p>
         * @param width: The new width of the frame.
         * @param height: The new height of the frame.
         * @return void
         */
        void SetFrameSize(int width, int height);

        /**
         * <p>IsHand: This method returns true if a shape of a hand is found.</p>
         * @return boolean
         */
        bool IsHand() const;

        /**
         * <p>GetBoundingBox: This method returns a rect around the hand.</p>
         * @return Rect(x,y,width,height)
         */
        const cv::Rect& GetBoundingBox() const;

        /**
         * <p>GetFingerTips: This method returns an array of the fingertips.</p>
         * @return Array of Point(x,y)
         */
        const std::vector<cv::Point> GetFingerTips() const;

        /**
         * <p>GetConvexHull: This method returns the convex hull that has been created during the validation process.</p>
         * <p>Convex hull: Convex hull can be explained simply as a curve joining all the most outer points using the Sklansky's algorithm( O(N logN) ).</p>
         * @return Array of Point(x,y)
         */
        const std::vector<cv::Point> GetConvexHull() const;

        /**
         * <p>GetContour: This method returns the contour that has been given during the validation process.</p>
         * <p>Contour: Contours can be explained simply as a curve joining all the continuous points (along the boundary).</p>
         * @return Array of Point(x,y)
         */
        const std::vector<cv::Point> GetContour() const;

        /**
         * <p>GetDefectPoints: This method returns the position of every defect that has been created during the validation process.</p>
         * <p>Convexity Defect Points: Convexity Defect Points are the most inner points between two convex hull points.</p>
         * @return Array of Point(x,y)
         */
        const std::vector<cv::Point>& GetDefectPoints() const;
        /**
         * <p>GetDefectVextors: This method returns a Vector4i that represents the defect point, 2 convex hull points and a distance between the farthest contour point and the hull.</p>
         * @return Array of Point(x,y)
         */
        const std::vector<cv::Vec4i>& GetDefectVectors() const;

        /**
         * <p>GetFingers: This method returns an array of the fingers.</p>
         * <p>Note: Currently the application support max 2 fingers.</p>
         * @return Array of the Finger class
         */
        const std::vector<Finger>& GetFingers() const;

        /**
         * <p>GetAmountFingers: This method returns an integer that represent the amount of fingers.</p>
         * @return size_t (unsigned int)
         */
        size_t GetAmountFingers() const;

        /**
         * <p>GetHandSide: This method returns the handside that is used to detect a hand.</p>
         * @return HandSide (LEFT or RIGHT)
         */
        HandSide GetHandSide() const;

        /**
         * <p>GetPosition: This method returns the defect point between the index finger and thumb. This represent the position of the hand gesture.</p>
         * @return Point(x,y)
         */
        const cv::Point& GetPosition() const;

        /**
         * <p>GetCursorPosition: This method returns the position of the cursor. This is currently bound to the top of the thumb.</p>
         * @return Point(x,y)
         */
        const cv::Point& GetCursorPosition() const;

        /**
         * <p>IsPressed: This method returns true is 'close' gesture is detected. This is equal as if the left mouse button is pressed.</p>
         * @return boolean
         */
        bool IsPressed() const;

        /**
         * <p>GetState: This method returns the current state of the hand.</p>
         * @return HandState (NONE, CLICKED or PRESSED)
         */
        HandState GetState() const;

        /**
         * <p>SetHandSide: This method sets the side of the hand that is used for detecting a hand.</p>
         * @param side: The new hand side.
         * @return void
         */
        void SetHandSide(HandSide side);
    private:
        void UpdateLH();
        void UpdateRH();
        bool ValidateLH();
        bool ValidateRH();

        void CalculateDefects();

        void OptimizeContour();
        void RemoveRedundantDefects();
        void RemoveRedundantEndPoints();
        void Clear();
    private:
        std::vector<cv::Point> m_ConvexHull;
        std::vector<int> m_HullIndexes;
        //std::vector<cv::Point> m_HullPoints;
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
        bool m_ShapeFound;
        bool m_IsHandOpen,
             m_IsHandClosed;
        HandState m_HandState;
        std::vector<std::vector<cv::Point>> m_LShapedPoints;
        std::function<void(cv::Point)> m_ClickCallback;
        HandSide m_HandSide;
    };
}

#endif //ANDROID_OPENCV_HAND_H
