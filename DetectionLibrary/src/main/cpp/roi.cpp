#include "roi.h"

namespace hdcv
{
    ROI::ROI(const cv::Rect& area)
            : m_Area(area), m_IsValid(true)
    {

    }

    ROI::ROI(const cv::Rect& area, int frame_cols, int frame_rows)
            : m_Area(std::max(std::min(area.x, frame_cols - area.width-1), 0), std::max(0, std::min(area.y, frame_rows - area.height-1)), area.width, area.height),
              m_IsValid(true)
    {

    }

    void ROI::Update(const cv::Mat& data)
    {
        m_Data.Channels.clear();

        cv::Mat ycrcb(data.rows, data.cols, data.type());
        cv::cvtColor(data, ycrcb, RGB2YCrCb); //CV_BGR2YCrCb

        std::vector<Channel> channels(ycrcb.channels());

        int chan1 = data.channels();
        int chan2 = ycrcb.channels();

        for (int i = 0; i < ycrcb.rows; i++)
        {
            for (int j = 0; j < ycrcb.cols; j++)
            {
                for (int c = 0; c < ycrcb.channels(); c++)
                    channels[c].Values.emplace_back(ycrcb.data[ycrcb.channels() * (ycrcb.cols * i + j) + c]);
            }
        }

        for (int i = 0; i < channels.size(); i++)
        {
            Channel ch = channels[i];
            std::vector<float> sorted_values = ch.Values;
            std::sort(sorted_values.begin(), sorted_values.end());

            size_t size = sorted_values.size();
            ch.Min = (int)sorted_values[0];
            ch.Max = (int)sorted_values[size - 1];
            ch.Range = ch.Max - ch.Min;

            if (size % 2 == 0)
                ch.Median = (int)sorted_values[(size - 1) / 2];
            else
                ch.Median = (int)sorted_values[size / 2];

            ch.Mean = (int)std::ceil(std::accumulate(sorted_values.begin(), sorted_values.end(), 0.0) / size);

            channels[i] = ch;
        }
        m_Data.Channels = channels;
        m_Data.ColorModelType = ColorModels::YCrCb;
    }

    bool ROI::Validate(const cv::Scalar& min, const cv::Scalar& max, int accepted_threshold)
    {
        bool isValid = true;
        // Y
        isValid = (m_Data.Channels[0].Median + accepted_threshold >= min.val[0]) ? ((m_Data.Channels[0].Median - accepted_threshold <= max.val[0]) ? true : false) : false;
        // Cr
        isValid = isValid ? ((m_Data.Channels[1].Median + accepted_threshold >= min.val[1]) ? ((m_Data.Channels[1].Median - accepted_threshold <= max.val[1]) ? true : false) : false) : isValid;
        // Cb
        isValid = isValid ? ((m_Data.Channels[2].Median + accepted_threshold >= min.val[2]) ? ((m_Data.Channels[2].Median - accepted_threshold <= max.val[2]) ? true : false) : false) : isValid;

        return m_IsValid = isValid;
    }

    double ROI::Offset(const cv::Scalar& avg) const
    {
        double offset_y =  (std::min(avg[0], (double)m_Data.Channels[0].Median) / std::max(avg[0], (double)m_Data.Channels[0].Median)) * 100;
        double offset_cr = (std::min(avg[1], (double)m_Data.Channels[1].Median) / std::max(avg[1], (double)m_Data.Channels[1].Median)) * 100;
        double offset_cb = (std::min(avg[2], (double)m_Data.Channels[2].Median) / std::max(avg[2], (double)m_Data.Channels[2].Median)) * 100;

        return (offset_y + offset_cr + offset_cb) / 3;
    }

    bool ROI::IsValid() const
    {
        return m_IsValid;
    }
    const cv::Rect& ROI::GetArea() const
    {
        return m_Area;
    }
    const ColorModel& ROI::GetColorModel() const
    {
        return m_Data;
    }
}