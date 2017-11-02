//
// Created by Rick4 on 11-10-2017.
//

#ifndef ANDROID_OPENCV_CALIBRATIONOBJECT_H
#define ANDROID_OPENCV_CALIBRATIONOBJECT_H

#include <vector>
#include "Types.h"
#include <opencv2/opencv.hpp>

namespace hdcv
{
    class Window;
    class ROI;

    class CalibrationObject
    {
    public:
        CalibrationObject(const char* path, int frame_width, int frame_height);
        CalibrationObject(const cv::Mat& image, int frame_width, int frame_height);

        const std::vector<ROI>& Update(const cv::Mat& data);
        int Validate(const cv::Scalar& min, const cv::Scalar& max, int accepted_threshold);
        void Render(cv::Mat* const source) const;

        cv::Mat& GetData() const;
        int GetWidth() const;
        int GetHeight() const;
        void ChangeResolution(int newWidth, int newHeight);
    private:
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
