//
// Created by Rick4 on 13-9-2017.
//

#ifndef ANDROID_OPENCV_FUNCTIONS_H
#define ANDROID_OPENCV_FUNCTIONS_H

#define _USE_MATH_DEFINES

#include <vector>
#include <math.h>
#include <string>
#include "../types.h"

#include <opencv2/opencv.hpp>

int GetMedian(std::vector<int> val);
void GetAverageColor(const cv::Mat& frame, int avg[3]);
float InnerAngle(float px1, float py1, float px2, float py2, float cx1, float cy1);
double InnerAngle(const cv::Point& p1, const cv::Point& p2, const cv::Point& c1);

bool InRange(int value, int lower, int upper);
bool InRange(int value[3], int min[3], int max[3]);
bool Compare(int left, int right, int margin);

bool R1(int R, int G, int B);
bool R2(float Y, float Cr, float Cb);
bool R3(float H, float S, float V);

void SetMinMax(int v1, int v2, int v3, int min[3], int max[3]);

double Distance(const cv::Point& a, const cv::Point& b);
double Distance(int ax, int ay, int bx, int by);
double Angle(cv::Point left, cv::Point origin, cv::Point right);
cv::Point Angle(const cv::Point& left, const cv::Point& right, int distance, const cv::Point& origin);
double Rad2Deg(double radians);
double Deg2Rad(double degrees);

bool Intersects(cv::Point a1, cv::Point a2, cv::Point b1, cv::Point b2, cv::Point* intPnt = NULL);
bool Intersects(const cv::Rect& left, const cv::Rect& right);
bool Intersects(const cv::Point& left, const cv::Rect& right);

bool Inside(const cv::Rect& left, const cv::Rect& right);

double Cross(cv::Point p1, cv::Point p2);
double Magnitude(cv::Point& point);
double Magnitude(int x, int y);
std::pair<double, double> Normalize(int& x, int& y);

std::string BoolToString(bool data);

template <typename T>
bool InArray(std::vector<T> items, T item)
{
    for (size_t i = 0; i < items.size(); i++)
    {
        if (items[i] == item)
            return true;
    }
    return false;
}

template<typename T = int>
std::string NumberToString(T data)
{
    std::stringstream ss;
    ss << data;
    return ss.str();
}

#endif //ANDROID_OPENCV_FUNCTIONS_H
