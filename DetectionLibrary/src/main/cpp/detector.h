//
// Created by bschu on 01-Nov-17.
//

#ifndef HANDS_ON_CV_DETECTOR_H
#define HANDS_ON_CV_DETECTOR_H

class Detector
{
public:
    static Detector* GetInstance();
    ~Detector();

    bool Initialize(long a_lMatAddr);
    void Analyse(long a_lMatAddr);
    void Reset();
protected:
private:
    int i = 0;
    static Detector* m_pInstance;
    Detector();
};

#endif //HANDS_ON_CV_DETECTOR_H
