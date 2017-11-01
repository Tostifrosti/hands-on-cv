//
// Created by Rick4 on 13-9-2017.
//

#ifndef ANDROID_OPENCV_ROI_H
#define ANDROID_OPENCV_ROI_H

#include <vector>
#include <numeric>
#include <cmath>
#include "Types.h"

#include <opencv2/opencv.hpp>

namespace hdcv
{
    enum ColorModels
    {
        BGR,
        HSV,
        YCrCb
    };

    struct Channel
    {
        std::vector<float> Values;
        int Min,
                Max,
                Mean,
                Median,
                Range;
    };
    struct ColorModel
    {
        std::vector<Channel> Channels;
        ColorModels ColorModelType;
    };

    class ROI
    {
    public:
        ROI(const cv::Rect& area);
        ROI(const cv::Rect& area, int frame_cols, int frame_rows);

        void Update(const cv::Mat& data);
        bool Validate(const cv::Scalar& min, const cv::Scalar& max, int accepted_threshold);
        double Offset(const cv::Scalar& avg) const;

        bool IsValid() const;
        const cv::Rect& GetArea() const;
        const ColorModel& GetColorModel() const;
    private:
        bool m_IsValid;
        cv::Rect m_Area;
        ColorModel m_Data;
    };
}

#endif //ANDROID_OPENCV_ROI_H
