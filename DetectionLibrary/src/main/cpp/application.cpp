//
// Created by Rick4 on 11-10-2017.
//

#include "application.h"

#include "calibration_object.h"
#include "roi.h"
#include <android/log.h>
namespace hdcv
{
    Application* Application::s_Instance = nullptr;

    Application::Application()
            : m_IsRunning(false), m_IsDebug(false),
              m_ProgramState(ProgramState::CREATED),
              m_Hand(HandSide::LEFT, (std::function<void(cv::Point)>)[&](cv::Point p) -> void {
                  m_ClickPoints.emplace_back(p);
              }),
              m_TrackingPoint(320, 240), m_Rect(120, 150, 75, 75), m_IsRectGrabbed(false),
              m_CalibrationObject(nullptr), m_ShowBinaireFrame(false),
              m_Scale(1.0), m_Resolution(0, 0), m_IsTracking(false),
              m_Timer(new cv::TickMeter()),
              m_CurrentTime(0.0),
              m_LastTime(0.0)
    {

    }

    Application::~Application()
    {
        if (m_CalibrationObject)
            delete m_CalibrationObject;
        m_CalibrationObject = nullptr;
    }
    void Application::Destroy()
    {
        if (s_Instance != nullptr)
            delete s_Instance;
        s_Instance = nullptr;
    }
    Application* Application::GetInstance()
    {
        if (s_Instance == nullptr)
            s_Instance = new Application();

        return s_Instance;
    }


    void Application::Start(const cv::Mat& image)
    {
        m_ImageHand = image.clone();
        m_ProgramState = ProgramState::INIT;
        m_IsRunning = true;
        m_Timer->start();
        m_LastTime = m_Timer->getTimeMilli();
    }

    bool Application::Analyse(long matAddr) {
        cv::Mat *src = (cv::Mat *) matAddr;
        CheckResolution(src->cols, src->rows);

        m_Hand.SetFrameSize(src->cols, src->rows);
        if (m_CalibrationObject == nullptr)
            m_CalibrationObject = new CalibrationObject(m_ImageHand, src->cols, src->rows);

        if (!m_IsDebug)
        {
            switch (m_ProgramState)
            {
                case ProgramState::INIT:
                    AnalyseInit(src);
                    break;
                case ProgramState::CHECK:
                    CheckCalibration(src);
                    break;
                default:
                    m_ProgramState = ProgramState::INIT;
                    break;
            }
        }

        return m_ProgramState == ProgramState::TRACK;
    }

    void Application::Detection(long matAddr)
    {
        cv::Mat* src = (cv::Mat*)matAddr;
        CheckResolution(src->cols, src->rows);

        m_Hand.SetFrameSize(src->cols, src->rows);

        if (!m_IsDebug)
            AnalyseTrack(src);
    }

    void Application::AnalyseInit(cv::Mat* const source)
    {
        if (source == nullptr || source->empty()) {
            m_ProgramState = ProgramState::INVALID;
            return;
        }

        // Local copy
        cv::Mat frame = source->clone();

        // Create roi from the middle of the source image
        int x = std::max(source->cols / 2 - m_CalibrationObject->GetWidth() / 2, 0);
        int y = std::max(source->rows / 2 - m_CalibrationObject->GetHeight() / 2, 0);
        cv::Rect middle = cv::Rect(x, y, m_CalibrationObject->GetWidth(), m_CalibrationObject->GetHeight());

        // Merge the silhouette and frame
        cv::Mat dst_roi = (*source)(middle);
        cv::addWeighted(dst_roi, 1.0, m_CalibrationObject->GetData(), 0.5, 0.0, dst_roi);
        cv::putText(*source, "Place hand in on the silhouette", cv::Point(10, 20), CV_FONT_HERSHEY_PLAIN, m_Scale, ColorScalar(0, 255, 0), 1);

        // Add blur
        //cv::blur(dst_roi, dst_roi, cv::Size(m_BlurSize, m_BlurSize));
        cv::medianBlur(frame, frame, m_BlurSize);

        // Update ROIs of hand
        const std::vector<ROI>& rois = m_CalibrationObject->Update(cv::Mat(frame(middle)));

        // Calculate min/max of YCrCb
        int min_y = 255, max_y = 0;
        int min_cr = 255, max_cr = 0;
        int min_cb = 255, max_cb = 0;

        std::vector<int> vector_min_y, vector_max_y;
        std::vector<int> vector_min_cr, vector_max_cr;
        std::vector<int> vector_min_cb, vector_max_cb;

        for (const ROI& roi : rois)
        {
            const ColorModel& data = roi.GetColorModel();

            vector_min_y.push_back(data.Channels[0].Median - m_AcceptedThreshold);
            vector_max_y.push_back(data.Channels[0].Median + m_AcceptedThreshold);
            vector_min_cr.push_back(data.Channels[1].Median - m_AcceptedThreshold);
            vector_max_cr.push_back(data.Channels[1].Median + m_AcceptedThreshold);
            vector_min_cb.push_back(data.Channels[2].Median - m_AcceptedThreshold);
            vector_max_cb.push_back(data.Channels[2].Median + m_AcceptedThreshold);

            min_y = data.Channels[0].Median - m_RangeThreshold < min_y ? data.Channels[0].Median - m_RangeThreshold : min_y;   // minY
            max_y = data.Channels[0].Median + m_RangeThreshold > max_y ? data.Channels[0].Median + m_RangeThreshold : max_y;   // maxY
            min_cr = data.Channels[1].Median - m_RangeThreshold < min_cr ? data.Channels[1].Median - m_RangeThreshold : min_cr; // minCr
            max_cr = data.Channels[1].Median + m_RangeThreshold > max_cr ? data.Channels[1].Median + m_RangeThreshold : max_cr; // maxCr
            min_cb = data.Channels[2].Median - m_RangeThreshold < min_cb ? data.Channels[2].Median - m_RangeThreshold : min_cb; // minCb
            max_cb = data.Channels[2].Median + m_RangeThreshold > max_cb ? data.Channels[2].Median + m_RangeThreshold : max_cb; // maxCb
        }

        int min_y_avg = (int)std::ceil(std::accumulate(vector_min_y.begin(), vector_min_y.end(), 0.0) / vector_min_y.size());
        int max_y_avg = (int)std::ceil(std::accumulate(vector_max_y.begin(), vector_max_y.end(), 0.0) / vector_max_y.size());
        int min_cr_avg = (int)std::ceil(std::accumulate(vector_min_cr.begin(), vector_min_cr.end(), 0.0) / vector_min_cr.size());
        int max_cr_avg = (int)std::ceil(std::accumulate(vector_max_cr.begin(), vector_max_cr.end(), 0.0) / vector_max_cr.size());
        int min_cb_avg = (int)std::ceil(std::accumulate(vector_min_cb.begin(), vector_min_cb.end(), 0.0) / vector_min_cb.size());
        int max_cb_avg = (int)std::ceil(std::accumulate(vector_max_cb.begin(), vector_max_cb.end(), 0.0) / vector_max_cb.size());

        // Validate ROIs based on min/max YCrCb
        int valid_count = m_CalibrationObject->Validate(cv::Scalar(min_y_avg, min_cr_avg, min_cb_avg), cv::Scalar(max_y_avg, max_cr_avg, max_cb_avg), m_AcceptedThreshold);

        // Render ROis & Hand
        m_CalibrationObject->Render(&dst_roi);

        // Start Counting if most ROIs are valid
        std::string roi_string = NumberToString<int>(valid_count);
        roi_string.append(" valid roi's");
        cv::putText(*source, roi_string, cv::Point(10, 40), CV_FONT_HERSHEY_PLAIN, m_Scale, ColorScalar(0, 255, 0), 1);

        if (valid_count >= m_AcceptROIValue)
        {
            m_Timer->stop();
            m_CurrentTime = m_Timer->getTimeSec();
            m_AcceptCounter -= (m_CurrentTime - m_LastTime);
            m_LastTime = m_CurrentTime;
            m_Timer->start();


            std::string timer_string("Hold still for ");
            timer_string.append(NumberToString<double>(m_AcceptCounter));
            timer_string.append(" seconds.");
            cv::putText(*source, timer_string, cv::Point(10, 60), CV_FONT_HERSHEY_PLAIN, m_Scale, ColorScalar(0, 255, 0), 1);

            if (m_AcceptCounter <= 0)
            {
                m_ProgramState = ProgramState::CHECK;
                m_InRangeValues.Min = cv::Scalar(min_y, min_cr, min_cb);
                m_InRangeValues.Max = cv::Scalar(max_y, max_cr, max_cb);
            }
        }
        else {
            m_AcceptCounter = m_AcceptCounterMax;
        }

        dst_roi.copyTo((*source)(middle));
    }

    void Application::CheckCalibration(cv::Mat* const source)
    {
        if (source == nullptr || source->empty()) {
            m_ProgramState = ProgramState::INIT;
            return;
        }

        cv::Mat frame = source->clone();
        cv::Mat ybb_frame, binair_frame;
        cv::cvtColor(frame, ybb_frame, RGB2YCrCb); //CV_BGR2YCrCb
        cv::inRange(ybb_frame, m_InRangeValues.Min, m_InRangeValues.Max, binair_frame);

        // Blur
        cv::medianBlur(binair_frame, binair_frame, m_BlurSize);

        /// Apply the dilation operation
        cv::Mat element = cv::getStructuringElement(cv::MORPH_ELLIPSE, cv::Size(2 * m_DialiteSize + 1, 2 * m_DialiteSize + 1), cv::Point(m_DialiteSize, m_DialiteSize));
        cv::dilate(binair_frame, binair_frame, element); // scale hand back up

        int total = binair_frame.rows * binair_frame.cols * binair_frame.channels();

        int amountDetected = 0;
        for (int y = 0; y < binair_frame.rows; y++)
        {
            for (int x = 0; x < binair_frame.cols; x++)
            {
                if (binair_frame.at<unsigned char>(y, x) > 0)
                    amountDetected++;
            }
        }

        double percentage = (amountDetected / (double)total) * 100;

        // ~20% = best case scenario
        if (percentage >= 25)
        {
            m_ProgramState = ProgramState::INIT;
            m_AcceptCounter = m_AcceptCounterMax;
            m_CalibrationObject->Initialize();
        }
        else {
            m_ProgramState = ProgramState::TRACK;
        }
    }

    void Application::AnalyseTrack(cv::Mat* const source)
    {
        if (source == nullptr || source->empty()) {
            m_ProgramState = ProgramState::INIT;
            return;
        }

        // Local copy
        cv::Mat frame = source->clone();

        cv::Mat ybb_frame, binair_frame;
        cv::cvtColor(frame, ybb_frame, RGB2YCrCb); //CV_BGR2YCrCb
        cv::inRange(ybb_frame, m_InRangeValues.Min, m_InRangeValues.Max, binair_frame);

        /// Blur
        //cv::blur(binair_frame, binair_frame, cv::Size(m_BlurSize, m_BlurSize));
        cv::medianBlur(binair_frame, binair_frame, m_BlurSize);

        /// Apply the dilation operation
        cv::Mat element = cv::getStructuringElement(cv::MORPH_ELLIPSE, cv::Size(2 * m_DialiteSize + 1, 2 * m_DialiteSize + 1), cv::Point(m_DialiteSize, m_DialiteSize));
        cv::dilate(binair_frame, binair_frame, element);

        /// Find contours
        std::vector<std::vector<cv::Point>> contours;
        std::vector<cv::Vec4i> hierarchy;
        int largestContourIdx = 0;
        cv::findContours(binair_frame, contours, hierarchy, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_SIMPLE, cv::Point(0, 0));
        m_IsRectGrabbed = false;

        if (!contours.empty())
        {
            // Find top 5 of largest contours
            std::vector<int> contourIdxHierarchy;
            contourIdxHierarchy.reserve(contours.size());
            for (size_t i = 0; i < std::min((size_t)5, contours.size()); i++)
            {
                largestContourIdx = (int)i;
                for (size_t j = i + 1; j < contours.size(); j++)
                {
                    if (largestContourIdx != j && !InArray<int>(contourIdxHierarchy, (int)j) && cv::contourArea(contours[j]) > cv::contourArea(contours[largestContourIdx]))
                        largestContourIdx = (int)j;
                }
                contourIdxHierarchy.push_back(largestContourIdx);
            }

            bool foundHand = false;

            // Search for a hand in the top 5 of largest contours
            for (size_t i = 0; i < contourIdxHierarchy.size(); i++)
            {
                largestContourIdx = contourIdxHierarchy[i];

                // Fill empty spaces within contour
                cv::drawContours(binair_frame, contours, (int)largestContourIdx, ColorScalar(255, 255, 255), -1);
                if (m_Hand.Validate(contours[largestContourIdx]))
                {
                    if (m_Hand.IsHand() && (!m_IsTracking || std::abs(Distance(m_TrackingPoint, m_Hand.GetPosition())) < m_TrackingRadius))
                    {
                        // Frame cut-out
                        cv::Mat tmp = binair_frame.clone();
                        cv::Mat tmpFrame = cv::Mat::zeros(binair_frame.rows, binair_frame.cols, binair_frame.type());
                        cv::Rect box = cv::boundingRect(contours[largestContourIdx]);
                        cv::Mat smallerFrame = tmp(box);
                        smallerFrame.copyTo(tmpFrame(box));
                        binair_frame = tmpFrame;
                        cv::rectangle(binair_frame, box, ColorScalar(255, 0, 0), 1);

                        // Update
                        m_Hand.Update();

                        // Recalibrate
                        if (m_RecalibrationCounter <= 0) {
                            m_RecalibrationCounter = m_RecalibrationCounterMax;
                            Recalibrate(source, &ybb_frame, &binair_frame, contours[largestContourIdx]);
                        }
                        else {
                            m_RecalibrationCounter--;
                        }

                        // Update Tracking point
                        m_TrackingPoint.x = m_Hand.GetPosition().x;
                        m_TrackingPoint.y = m_Hand.GetPosition().y;

                        m_IsTracking = true;
                        foundHand = true;
                        break;
                    }
                }
            }

            if (!foundHand)
            {
                m_IsTracking = false;
            }
        }

        m_Hand.RenderDebug(*source, m_Scale);

        if (m_ShowBinaireFrame)
            binair_frame.copyTo(*source);
    }

    void Application::Recalibrate(cv::Mat* const source, cv::Mat* const ybb, cv::Mat* const binair, const std::vector<cv::Point>& contour)
    {
        if (source->empty())
            return;

        if (m_Hand.GetFingers().empty())
            return;

        std::vector<ROI> rois;

        int roiSize = 20;

        // Thumb
        int distThumbX = (m_Hand.GetFingers()[0].GetDefect().x - m_Hand.GetFingers()[0].GetFingerTop().x);
        int distThumbY = (m_Hand.GetFingers()[0].GetDefect().y - m_Hand.GetFingers()[0].GetFingerTop().y);

        //int thumbX = m_Hand.GetFingers()[0].GetFingerTop().x - (roiSize / 2);
        //int thumbY = m_Hand.GetFingers()[0].GetFingerTop().y - (roiSize / 2);

        std::pair<double, double> normThumbPoint = Normalize(distThumbX, distThumbY);

        // Too risky
        /*rois.emplace_back(ROI(cv::Rect(((m_Hand.GetFingers()[0].GetFingerTop().x - (roiSize / 2)) + int(10 * normThumbPoint.first)),
                                        ((m_Hand.GetFingers()[0].GetFingerTop().y - (roiSize / 2)) + int(10 * normThumbPoint.second)),
                                        roiSize, roiSize), source->cols, source->rows));*/
        rois.emplace_back(ROI(cv::Rect(((m_Hand.GetFingers()[0].GetFingerTop().x + distThumbX / 2) - (roiSize / 2)),
                                       ((m_Hand.GetFingers()[0].GetFingerTop().y + distThumbY / 2) - (roiSize / 2)) + int(10 * normThumbPoint.second),
                                       roiSize, roiSize), source->cols, source->rows));

        // Defect
        rois.emplace_back(ROI(cv::Rect((m_Hand.GetFingers()[0].GetDefect().x - (roiSize / 2)) + int(10 * normThumbPoint.first),
                                       (m_Hand.GetFingers()[0].GetDefect().y - (roiSize / 2)) + int(10 * normThumbPoint.second),
                                       roiSize, roiSize), source->cols, source->rows));

        int midIndexX = 0,
                midIndexY = 0;
        if (m_Hand.GetFingers().size() > 1)
        {
            // Index
            int distIndexX = (m_Hand.GetFingers()[1].GetDefect().x - m_Hand.GetFingers()[1].GetFingerTop().x);
            int distIndexY = (m_Hand.GetFingers()[1].GetDefect().y - m_Hand.GetFingers()[1].GetFingerTop().y);

            int indexX = m_Hand.GetFingers()[1].GetFingerTop().x - (roiSize / 2);
            int indexY = m_Hand.GetFingers()[1].GetFingerTop().y - (roiSize / 2);

            //std::pair<double, double> normIndexPoint = Normalize(distIndexX, distIndexY);

            // Too risky
            /*rois.emplace_back(ROI(cv::Rect(((m_Hand.GetFingers()[1].GetFingerTop().x - (roiSize / 2)) + int(10 * normIndexPoint.first)),
                                            ((m_Hand.GetFingers()[1].GetFingerTop().y - (roiSize / 2)) + int(10 * normIndexPoint.second)),
                                            roiSize, roiSize) ));*/
            rois.emplace_back(ROI(cv::Rect(((m_Hand.GetFingers()[1].GetFingerTop().x + distIndexX / 2) - (roiSize / 2)),
                                           ((m_Hand.GetFingers()[1].GetFingerTop().y + distIndexY / 2) - (roiSize / 2)),
                                           roiSize, roiSize), source->cols, source->rows));
        }

        // Hand palm
        cv::Mat skel,
                contourMat = cv::Mat::zeros(cv::Size(binair->cols, binair->rows), binair->type()),
                input = (*binair)(m_Hand.GetBoundingBox());
        input.copyTo(contourMat);
        cv::distanceTransform(input, skel, CV_DIST_L2, 3); // CV_DIST_L2 = Euclidean distance
        cv::normalize(skel, skel, 0, 1., CV_MINMAX);
        cv::Point maxIdx;
        cv::minMaxLoc(skel, NULL, NULL, NULL, &maxIdx);
        cv::Point m_HandPalmCenter = cv::Point(m_Hand.GetBoundingBox().x + maxIdx.x, m_Hand.GetBoundingBox().y + maxIdx.y);

        rois.emplace_back(ROI(cv::Rect(m_HandPalmCenter.x - (roiSize / 2),
                                       m_HandPalmCenter.y - (roiSize * 2), roiSize, roiSize), source->cols, source->rows));
        rois.emplace_back(ROI(cv::Rect(m_HandPalmCenter.x - (roiSize / 2),
                                       m_HandPalmCenter.y + roiSize, roiSize, roiSize), source->cols, source->rows));

        rois.emplace_back(ROI(cv::Rect(m_HandPalmCenter.x - (roiSize / 2),
                                       m_HandPalmCenter.y - (roiSize / 2), roiSize, roiSize), source->cols, source->rows));

        rois.emplace_back(ROI(cv::Rect(m_HandPalmCenter.x - (roiSize * 2),
                                       m_HandPalmCenter.y - (roiSize / 2), roiSize, roiSize), source->cols, source->rows));
        rois.emplace_back(ROI(cv::Rect(m_HandPalmCenter.x + roiSize,
                                       m_HandPalmCenter.y - (roiSize / 2), roiSize, roiSize), source->cols, source->rows));

        // Update
        for (ROI& r : rois)
            r.Update( cv::Mat((*source)(r.GetArea())) );

        int min_y = 255, max_y = 0;
        int min_cr = 255, max_cr = 0;
        int min_cb = 255, max_cb = 0;

        std::vector<int> vector_min_y, vector_max_y;
        std::vector<int> vector_min_cr, vector_max_cr;
        std::vector<int> vector_min_cb, vector_max_cb;

        for (const ROI& roi : rois)
        {
            const ColorModel& data = roi.GetColorModel();

            vector_min_y.push_back(data.Channels[0].Median - m_AcceptedThreshold);
            vector_max_y.push_back(data.Channels[0].Median + m_AcceptedThreshold);
            vector_min_cr.push_back(data.Channels[1].Median - m_AcceptedThreshold);
            vector_max_cr.push_back(data.Channels[1].Median + m_AcceptedThreshold);
            vector_min_cb.push_back(data.Channels[2].Median - m_AcceptedThreshold);
            vector_max_cb.push_back(data.Channels[2].Median + m_AcceptedThreshold);

            min_y = (data.Channels[0].Median - m_RangeThreshold < min_y) ? (data.Channels[0].Median - m_RangeThreshold) : min_y;   // minY
            max_y = (data.Channels[0].Median + m_RangeThreshold > max_y) ? (data.Channels[0].Median + m_RangeThreshold) : max_y;   // maxY
            min_cr = (data.Channels[1].Median - m_RangeThreshold < min_cr) ? (data.Channels[1].Median - m_RangeThreshold) : min_cr; // minCr
            max_cr = (data.Channels[1].Median + m_RangeThreshold > max_cr) ? (data.Channels[1].Median + m_RangeThreshold) : max_cr; // maxCr
            min_cb = (data.Channels[2].Median - m_RangeThreshold < min_cb) ? (data.Channels[2].Median - m_RangeThreshold) : min_cb; // minCb
            max_cb = (data.Channels[2].Median + m_RangeThreshold > max_cb) ? (data.Channels[2].Median + m_RangeThreshold) : max_cb; // maxCb
        }

        int min_y_avg =  (int)std::ceil(std::accumulate(vector_min_y.begin(),  vector_min_y.end(), 0.0)  / vector_min_y.size());
        int max_y_avg =  (int)std::ceil(std::accumulate(vector_max_y.begin(),  vector_max_y.end(), 0.0)  / vector_max_y.size());
        int min_cr_avg = (int)std::ceil(std::accumulate(vector_min_cr.begin(), vector_min_cr.end(), 0.0) / vector_min_cr.size());
        int max_cr_avg = (int)std::ceil(std::accumulate(vector_max_cr.begin(), vector_max_cr.end(), 0.0) / vector_max_cr.size());
        int min_cb_avg = (int)std::ceil(std::accumulate(vector_min_cb.begin(), vector_min_cb.end(), 0.0) / vector_min_cb.size());
        int max_cb_avg = (int)std::ceil(std::accumulate(vector_max_cb.begin(), vector_max_cb.end(), 0.0) / vector_max_cb.size());

        double percentageMin = 0.0;
        double percentageMax = 0.0;
        for (const ROI& r : rois)
        {
            percentageMin += r.Offset(m_InRangeValues.Min);
            percentageMax += r.Offset(m_InRangeValues.Max);
        }
        percentageMin /= rois.size();
        percentageMax /= rois.size();

        int inRange = 10;

        if (percentageMin <= 92.0)
        {
            int min_step_y  = min_y  > m_InRangeValues.Min[0] ? 1 : -1;
            int min_step_cr = min_cr > m_InRangeValues.Min[1] ? 1 : -1;
            int min_step_cb = min_cb > m_InRangeValues.Min[2] ? 1 : -1;

            if (m_BaseInRangeValues.Min[0] + inRange < m_InRangeValues.Min[0] + min_step_y && m_BaseInRangeValues.Min[0] - inRange > m_InRangeValues.Min[0] + min_step_y)
                min_step_y = 0;
            if (m_BaseInRangeValues.Min[1] + inRange < m_InRangeValues.Min[1] + min_step_cr && m_BaseInRangeValues.Min[1] - inRange > m_InRangeValues.Min[1] + min_step_cr)
                min_step_cr = 0;
            if (m_BaseInRangeValues.Min[2] + inRange < m_InRangeValues.Min[2] + min_step_cb && m_BaseInRangeValues.Min[2] - inRange > m_InRangeValues.Min[2] + min_step_cb)
                min_step_cb = 0;

            m_InRangeValues.Min += cv::Scalar(std::max(min_step_y, 0), std::max(min_step_cr, 0), std::max(min_step_cb, 0));
        }
        if (percentageMax <= 92.0)
        {
            int max_step_y =  (max_y > m_InRangeValues.Max[0])  ? 1 : (max_y > m_InRangeValues.Min[0] + 10  ? -1 : 0);
            int max_step_cr = (max_cr > m_InRangeValues.Max[1]) ? 1 : (max_cr > m_InRangeValues.Min[1] + 10 ? -1 : 0);
            int max_step_cb = (max_cb > m_InRangeValues.Max[2]) ? 1 : (max_cb > m_InRangeValues.Min[2] + 10 ? -1 : 0);

            if (m_BaseInRangeValues.Max[0] + inRange < m_InRangeValues.Max[0] + max_step_y && m_BaseInRangeValues.Max[0] - inRange > m_InRangeValues.Max[0] + max_step_y)
                max_step_y = 0;
            if (m_BaseInRangeValues.Max[1] + inRange < m_InRangeValues.Max[1] + max_step_cr && m_BaseInRangeValues.Max[1] - inRange > m_InRangeValues.Max[1] + max_step_cr)
                max_step_cr = 0;
            if (m_BaseInRangeValues.Max[2] + inRange < m_InRangeValues.Max[2] + max_step_cb && m_BaseInRangeValues.Max[2] - inRange > m_InRangeValues.Max[2] + max_step_cb)
                max_step_cb = 0;

            m_InRangeValues.Max += cv::Scalar(std::min(max_step_y, 255), std::min(max_step_cr, 255), std::min(max_step_cb, 255));
        }


        //if (percentageMin <= 92.0 || percentageMax <= 92.0)
            //std::cout << "UPDATE!  (min: " << m_InRangeValues.Min << ", max: " << m_InRangeValues.Max << ") - percentage: (" << percentageMin << "/" << percentageMax << ")" << std::endl;
        //else
            //std::cout << "-- SKIP (percentage min: " << percentageMin << ", max: " << percentageMax << ") - percentage: (" << percentageMin << "/" << percentageMax << ")" << std::endl;

        //for (const ROI& r : rois)
            //cv::rectangle(*source, r.GetArea(), cv::Scalar(255, 255, 255), 1);
    }

    void Application::ShowBinaireFrame(bool value)
    {
        m_ShowBinaireFrame = value;
    }
    void Application::CheckResolution(int newWidth, int newHeight)
    {
        if (m_Resolution.x == newWidth && m_Resolution.y == newHeight)
            return;

        m_Resolution.x = newWidth;
        m_Resolution.y = newHeight;
        m_Scale = (newHeight / 480.0);
        if (m_CalibrationObject != nullptr)
            m_CalibrationObject->ChangeResolution(newWidth, newHeight);

        m_TrackingPoint = cv::Point(m_TrackingPoint.x * m_Scale, m_TrackingPoint.y * m_Scale);
        m_Rect = cv::Rect(m_Rect.x * m_Scale, m_Rect.y * m_Scale, 75 * m_Scale, 75 * m_Scale);
    }
    void Application::Reset()
    {
        m_AcceptCounter = m_AcceptCounterMax;
        m_RecalibrationCounter = m_RecalibrationCounterMax;
        m_ProgramState = ProgramState::INIT;
        m_RangeThreshold = 3;
        m_AcceptedThreshold = 10;
        m_IsRectGrabbed = false;
        m_TrackingPoint = cv::Point(320, 240);
        m_Rect = cv::Rect(120, 150, 75, 75);
        m_CalibrationObject = nullptr;
        m_ShowBinaireFrame = false;
        m_Scale = 1.0;
        m_Resolution = cv::Point(0, 0);
        m_ClickPoints.clear();
        m_ClickTimer = m_ClickTimerMax;
        m_IsTracking = false;
        m_Timer->reset();
    }

    std::pair<float, float> Application::GetCursorPosition(){
        cv::Point point = m_Hand.GetCursorPosition();
        return std::make_pair((float)point.x / m_Resolution.x, (float)point.y / m_Resolution.y);
    };

    HandState Application::GetHandState() const
    {
        return m_Hand.GetState();
    }
    void Application::SwitchHand()
    {
        if (m_Hand.GetHandSide() == HandSide::LEFT)
            m_Hand.SetHandSide(HandSide::RIGHT);
        else
            m_Hand.SetHandSide(HandSide::LEFT);
    }
}