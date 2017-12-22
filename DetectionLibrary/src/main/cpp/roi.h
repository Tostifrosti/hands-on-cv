//
// Created by Rick4 on 13-9-2017.
//

#ifndef ANDROID_OPENCV_ROI_H
#define ANDROID_OPENCV_ROI_H

#include <vector>
#include <numeric>
#include <cmath>
#include "types.h"

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
        /**
         * <p>ROI: This class represent a Region Of Interest for updating and validating the color within the given region.</p>
         * @param area: A rectangle that represent the region.
         */
        ROI(const cv::Rect& area);
        /**
         * <p>ROI: This class represent a Region Of Interest for updating and validating the color within the given region.</p>
         * @param area: A rectangle that represent the region.
         * @param frame_cols: An integer that represents the frame width
         * @param frame_rows: An integer that represents the frame height
         */
        ROI(const cv::Rect& area, int frame_cols, int frame_rows);

        /**
         * <p>Update: This method updates the new colors within the given region.</p>
         * <p>Note: This method must be called within a loop.</p>
         * @param data: A matrix that represent the region of this object.
         * @return void
         */
        void Update(const cv::Mat& data);

        /**
         * <p>Validate: This method validates the current color with given parameters.</p>
         * <p>Note: This method must be called within a loop.</p>
         * @param min: The minimum YCbCr values
         * @param max: The maximum YCbCr values
         * @param accepted_threshold: The tolerance of every minimum and maximum value.
         * @return boolean: returns true if the validation succeeded.
         */
        bool Validate(const cv::Scalar& min, const cv::Scalar& max, int accepted_threshold);

        /**
         * <p>Offset: This method calculates the offset between the current color and the given average color.</p>
         * @param avg: The average YCbCr values
         * @return double: The average offset of the YCbCr values
         */
        double Offset(const cv::Scalar& avg) const;

        /**
         * <p>IsValid: This method returns a boolean that represent the success of the validation.</p>
         * @return boolean
         */
        bool IsValid() const;

        /**
         * <p>Update: This method </p>
         * @return void
         */
        const cv::Rect& GetArea() const;

        /**
         * <p>GetColorModel: This method returns the current values of the color model.</p>
         * @return ColorModel
         */
        const ColorModel& GetColorModel() const;
    private:
        bool m_IsValid;
        cv::Rect m_Area;
        ColorModel m_Data;
    };
}

#endif //ANDROID_OPENCV_ROI_H
