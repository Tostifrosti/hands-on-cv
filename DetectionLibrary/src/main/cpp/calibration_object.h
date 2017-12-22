//
// Created by Rick4 on 11-10-2017.
//

#ifndef ANDROID_OPENCV_CALIBRATIONOBJECT_H
#define ANDROID_OPENCV_CALIBRATIONOBJECT_H

#include <vector>
#include "types.h"
#include <opencv2/opencv.hpp>

namespace hdcv
{
    class Window;
    class ROI;

    class CalibrationObject
    {
    public:
        /**
         * <p>CalibrationObject: This class can be used as a silhouette to detect RGB colors.</p>
         * @param path: The path to an image that represents a silhouette.
         * @param frame_width: The width of the frame.
         * @param frame_height: The height of the frame.
         */
        CalibrationObject(const char* path, int frame_width, int frame_height);
        /**
         * <p>CalibrationObject: This class can be used as a silhouette to detect RGB colors.</p>
         * @param image: The matrix of an image that represents a silhouette.
         * @param frame_width: The width of the frame.
         * @param frame_height: The height of the frame.
         */
        CalibrationObject(const cv::Mat& image, int frame_width, int frame_height);

        /**
         * <p>Update: This method is used to calculate the new colors within their region. It returns the new calculated colors as ROI objects.</p>
         * <p>Note: This method must be called within a loop.</p>
         * @param data: A matrix of the region of the camera frame.
         * @return Array of the ROI class
         */
        const std::vector<ROI>& Update(const cv::Mat& data);

        /**
         * <p>Validate: This method is used to validate the color within every ROI region.</p>
         * <p>Note: This method must be called within a loop.</p>
         * @param min: The minimum YCbCr values
         * @param max: The maximum YCbCr values
         * @param accepted_threshold: The tolerance of every minimum and maximum value.
         * @return int: The integer represent the amount of validation passes of every ROI object.
         */
        int Validate(const cv::Scalar& min, const cv::Scalar& max, int accepted_threshold);

        /**
         * <p>Render: This method renders the silhouette and ROI positions to the given frame.</p>
         * @param source: A matrix that represents the current camera frame.
         * @return void
         */
        void Render(cv::Mat* const source) const;

        /**
         * <p>GetData: This method returns a matrix that represent the silhouette image.</p>
         * @return Matrix
         */
        cv::Mat& GetData() const;

        /**
         * <p>GetWidth: This method returns the current width of the silhouette image.</p>
         * @return int
         */
        int GetWidth() const;
        /**
         * <p>GetHeight: This method returns the current height of the silhouette image.</p>
         * @return int
         */
        int GetHeight() const;
        /**
         * <p>ChangeResolution: This method sets the new frame width & height and calculates the new scale.</p>
         * @param newWidth: An integer that represents the new frame width
         * @param newHeight: An integer that represents the new frame height
         * @return void
         */
        void ChangeResolution(int newWidth, int newHeight);

        /**
         * <p>Initialize: This method initializes the class by creating the every ROI object with their respected position.</p>
         * @return void
         */
        void Initialize();
    private:
        cv::Mat* m_Data;
        int m_Width, m_Height;
        int m_OriginalWidth, m_OriginalHeight;

        float m_BoxSize = 0.05f;
        std::vector<ROI> m_ROIs;
    };
}

#endif //ANDROID_OPENCV_CALIBRATIONOBJECT_H
