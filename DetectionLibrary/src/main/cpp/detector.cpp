#ifndef HANDS_ON_CV_DETECTOR_H
#include "detector.h"
#endif //HANDS_ON_CV_DETECTOR_H

#include "opencv2/imgproc.hpp"
#include "opencv2/objdetect.hpp"

Detector* Detector::m_pInstance = nullptr;

Detector* Detector::GetInstance() {
    if (m_pInstance == nullptr) {
        m_pInstance = new Detector();
    }
    return m_pInstance;
}

Detector::Detector()
{

}
Detector::~Detector()
{

}

bool Detector::Initialize(long a_lMatAddr)
{
    cv::Mat* frame = (cv::Mat*) a_lMatAddr;

    i++;
    return (i > 100);
}
void Detector::Analyse(long a_lMatAddr)
{
    cv::Mat* frame = (cv::Mat*) a_lMatAddr;
}
void Detector::Reset()
{
    i = 0;
}