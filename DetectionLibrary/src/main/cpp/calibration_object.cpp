//
// Created by Rick4 on 11-10-2017.
//

#include "calibration_object.h"
#include "roi.h"

namespace hdcv
{
    CalibrationObject::CalibrationObject(const char* path, int frame_width, int frame_height)
        : m_Width(0), m_Height(0)
    {
        m_Data = new cv::Mat(cv::imread(path, cv::IMREAD_UNCHANGED));
        if (m_Data->empty())
        {
            std::cout << "Cannot read image path." << std::endl;
            delete m_Data;
            m_Data = nullptr;
            return;
        }
        m_OriginalWidth = m_Data->cols;
        m_OriginalHeight = m_Data->rows;

        if (m_Data->cols > frame_width || m_Data->rows > frame_height)
            cv::resize(*m_Data, *m_Data, cv::Size(((frame_width / 2) / m_Data->cols) * frame_width, ((frame_height / 2) / m_Data->rows) * frame_height));

        m_Width = m_Data->cols;
        m_Height = m_Data->rows;

        this->Initialize();
    }

    CalibrationObject::CalibrationObject(const cv::Mat& image, int frame_width, int frame_height)
            : m_Width(0), m_Height(0), m_Data(new cv::Mat(0, 0, CV_8UC4))
    {
        if (image.empty())
        {
            std::cout << "Cannot read image path." << std::endl;
            delete m_Data;
            m_Data = nullptr;
            return;
        }
        image.copyTo(*m_Data);

        m_OriginalWidth = m_Data->cols;
        m_OriginalHeight = m_Data->rows;

        if (m_Data->cols > frame_width || m_Data->rows > frame_height) {
            double margin = std::min((frame_height * 0.75) / m_Data->rows, 0.80);
            int width = (int)(m_Data->cols * margin);
            int height = (int)(m_Data->rows * margin);
            cv::resize(*m_Data, *m_Data, cv::Size(width, height));
        }

        m_Width = m_Data->cols;
        m_Height = m_Data->rows;

        this->Initialize();
    }

    void CalibrationObject::Initialize()
    {
        m_ROIs.clear();

        // Thumb
        m_ROIs.emplace_back(ROI(cv::Rect((int)(0.1875f * m_Width - (m_BoxSize * m_Width) / 2.0f),
                                         (int)(0.5625f * m_Height - (m_BoxSize * m_Height) / 2.0f),
                                         (int)(m_BoxSize * m_Width),
                                         (int)(m_BoxSize * m_Height))));

        m_ROIs.emplace_back(ROI(cv::Rect((int)(0.375f * m_Width - (m_BoxSize * m_Width) / 2.0f),
                                         (int)(0.75f * m_Height - (m_BoxSize * m_Height) / 2.0f),
                                         (int)(m_BoxSize * m_Width),
                                         (int)(m_BoxSize * m_Height))));

        // Index
        m_ROIs.emplace_back(ROI(cv::Rect((int)(0.35f * m_Width - (m_BoxSize * m_Width) / 2.0f),
                                         (int)(0.1875f * m_Height - (m_BoxSize * m_Height) / 2.0f),
                                         (int)(m_BoxSize * m_Width),
                                         (int)(m_BoxSize * m_Height))));

        m_ROIs.emplace_back(ROI(cv::Rect((int)(0.4375f * m_Width - (m_BoxSize * m_Width) / 2.0f),
                                         (int)(0.55f * m_Height - (m_BoxSize * m_Height) / 2.0f),
                                         (int)(m_BoxSize * m_Width),
                                         (int)(m_BoxSize * m_Height))));

        // Middle
        m_ROIs.emplace_back(ROI(cv::Rect((int)(0.5375f * m_Width - (m_BoxSize * m_Width) / 2.0f),
                                         (int)(0.1875f * m_Height - (m_BoxSize * m_Height) / 2.0f),
                                         (int)(m_BoxSize * m_Width),
                                         (int)(m_BoxSize * m_Height))));

        m_ROIs.emplace_back(ROI(cv::Rect((int)(0.5625f * m_Width - (m_BoxSize * m_Width) / 2.0f),
                                         (int)(0.55f * m_Height - (m_BoxSize * m_Height) / 2.0f),
                                         (int)(m_BoxSize * m_Width),
                                         (int)(m_BoxSize * m_Height))));

        // Ring
        m_ROIs.emplace_back(ROI(cv::Rect((int)(0.7f * m_Width - (m_BoxSize * m_Width) / 2.0f),
                                         (int)(0.2f * m_Height - (m_BoxSize * m_Height) / 2.0f),
                                         (int)(m_BoxSize * m_Width),
                                         (int)(m_BoxSize * m_Height))));

        m_ROIs.emplace_back(ROI(cv::Rect((int)(0.665f * m_Width - (m_BoxSize * m_Width) / 2.0f),
                                         (int)(0.555f * m_Height - (m_BoxSize * m_Height) / 2.0f),
                                         (int)(m_BoxSize * m_Width),
                                         (int)(m_BoxSize * m_Height))));

        // Pinky
        m_ROIs.emplace_back(ROI(cv::Rect((int)(0.875f * m_Width - ((m_BoxSize * 0.75f) * m_Width) / 2.0f),
                                         (int)(0.35f * m_Height - ((m_BoxSize * 0.75f) * m_Height) / 2.0f),
                                         (int)((m_BoxSize * 0.75f) * m_Width),
                                         (int)((m_BoxSize * 0.75f) * m_Height))));

        m_ROIs.emplace_back(ROI(cv::Rect((int)(0.75f * m_Width - (m_BoxSize * m_Width) / 2.0f),
                                         (int)(0.575f * m_Height - (m_BoxSize * m_Height) / 2.0f),
                                         (int)(m_BoxSize * m_Width),
                                         (int)(m_BoxSize * m_Height))));

        // Palm
        m_ROIs.emplace_back(ROI(cv::Rect((int)(0.5625f * m_Width - (m_BoxSize * m_Width) / 2.0f),
                                         (int)(0.875f * m_Height - (m_BoxSize * m_Height) / 2.0f),
                                         (int)(m_BoxSize * m_Width),
                                         (int)(m_BoxSize * m_Height))));

        m_ROIs.emplace_back(ROI(cv::Rect((int)(0.5625f * m_Width - (m_BoxSize * m_Width) / 2.0f),
                                         (int)(0.6875f * m_Height - (m_BoxSize * m_Height) / 2.0f),
                                         (int)(m_BoxSize * m_Width),
                                         (int)(m_BoxSize * m_Height))));

        m_ROIs.emplace_back(ROI(cv::Rect((int)(0.6875f * m_Width - (m_BoxSize * m_Width) / 2.0f),
                                         (int)(0.75f * m_Height - (m_BoxSize * m_Height) / 2.0f),
                                         (int)(m_BoxSize * m_Width),
                                         (int)(m_BoxSize * m_Height))));
    }

    const std::vector<ROI>& CalibrationObject::Update(const cv::Mat& data)
    {
        for (ROI& roi : m_ROIs)
            roi.Update(cv::Mat(data(roi.GetArea())));

        return m_ROIs;
    }

    int CalibrationObject::Validate(const cv::Scalar& min, const cv::Scalar& max, int accepted_threshold)
    {
        int valid_count = 0;
        for (ROI& roi : m_ROIs)
        {
            if (roi.Validate(cv::Scalar(min[0], min[1], min[2]), cv::Scalar(max[0], max[1], max[2]), accepted_threshold))
                valid_count++;
        }
        return valid_count;
    }

    void CalibrationObject::Render(cv::Mat* const source) const
    {
        for (const ROI& roi : m_ROIs)
        {
            if (roi.IsValid())
                cv::rectangle(*source, roi.GetArea(), ColorScalar(0, 0, 255), -1);
            else
                cv::rectangle(*source, roi.GetArea(), ColorScalar(0, 0, 0), -1);
        }
    }

    cv::Mat& CalibrationObject::GetData() const
    {
        return *m_Data;
    }
    int CalibrationObject::GetWidth() const
    {
        return m_Width;
    }
    int CalibrationObject::GetHeight() const
    {
        return m_Height;
    }
    void CalibrationObject::ChangeResolution(int newWidth, int newHeight)
    {
        double margin = std::min((newHeight * 0.75) / m_OriginalHeight, 0.80);
        int width = (int)(m_OriginalWidth * margin);
        int height = (int)(m_OriginalHeight * margin);
        cv::resize(*m_Data, *m_Data, cv::Size(width, height));

        m_Width = m_Data->cols;
        m_Height = m_Data->rows;

        this->Initialize();
    }
}