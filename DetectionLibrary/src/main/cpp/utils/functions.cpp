//
// Created by Rick4 on 13-9-2017.
//

#include "functions.h"

int GetMedian(std::vector<int> val)
{
	if (val.empty())
		return 0;

	int median = 0;
	int size = (int)val.size();
	std::sort(val.begin(), val.end());
	int index = 0;
	if (size % 2 == 0) {
		index = size / 2 - 1;
		if (index >= 0 && index < val.size())
			median = val[index];
	}
	else {
		index = size / 2;
		if (index >= 0 && index < val.size())
			median = val[index];
	}
	return median;
}

void GetAverageColor(const cv::Mat& frame, int avg[3])
{
	std::vector<int> hm;
	std::vector<int> sm;
	std::vector<int> vm;

	for (int i = 2; i < frame.rows - 2; i++)
	{
		for (int j = 2; j < frame.cols - 2; j++)
		{
			hm.push_back(frame.data[frame.channels() * (frame.cols * i + j) + 0]);
			sm.push_back(frame.data[frame.channels() * (frame.cols * i + j) + 1]);
			vm.push_back(frame.data[frame.channels() * (frame.cols * i + j) + 2]);
		}
	}
	avg[0] = GetMedian(hm);
	avg[1] = GetMedian(sm);
	avg[2] = GetMedian(vm);
}
float InnerAngle(float px1, float py1, float px2, float py2, float cx1, float cy1)
{
	// theta = arcos(x . y / |x||y|)

	float dist1 = std::sqrt((px1 - cx1) * (px1 - cx1) + (py1 - cy1) * (py1 - cy1));
	float dist2 = std::sqrt((px2 - cx1) * (px2 - cx1) + (py2 - cy1) * (py2 - cy1));

	float	Ax, Ay,
			Bx, By,
			Cx = cx1, Cy = cy1;

	if (dist1 < dist2)
	{
		Bx = px1;
		By = py1;
		Ax = px2;
		Ay = py2;
	}
	else {
		Bx = px2;
		By = py2;
		Ax = px1;
		Ay = py1;
	}

	float	Q1 = Cx - Ax,
			Q2 = Cy - Ay,
			P1 = Bx - Ax,
			P2 = By - Ay;

	float result = std::acos((P1 * Q1 + P2 * Q2) / (std::sqrt(P1 * P1 + P2 * P2) * std::sqrt(Q1 * Q1 + Q2 * Q2)));

	result = (float)(result * 180.0f / HDCV_PI);

	return result;
}
double InnerAngle(const cv::Point& p1, const cv::Point& p2, const cv::Point& c1)
{
	// theta = arcos(x . y / |x||y|)
	double dist1 = std::sqrt((p1.x - c1.x) * (p1.x - c1.x) + (p1.y - c1.y) * (p1.y - c1.y));
	double dist2 = std::sqrt((p2.x - c1.x) * (p2.x - c1.x) + (p2.y - c1.y) * (p2.y - c1.y));

	double	Ax, Ay,
			Bx, By,
			Cx = c1.x, Cy = c1.y;

	if (dist1 < dist2)
	{
		Bx = p1.x;
		By = p1.y;
		Ax = p2.x;
		Ay = p2.y;
	}
	else {
		Bx = p2.x;
		By = p2.y;
		Ax = p1.x;
		Ay = p1.y;
	}

	double	Q1 = Cx - Ax,
			Q2 = Cy - Ay,
			P1 = Bx - Ax,
			P2 = By - Ay;

	double result = std::acos((P1 * Q1 + P2 * Q2) / (std::sqrt(P1 * P1 + P2 * P2) * std::sqrt(Q1 * Q1 + Q2 * Q2)));

	result = (result * 180.0 / HDCV_PI);

	return result;
}

bool InRange(int value, int lower, int upper)
{
	return (value > lower && value < upper);
}
bool InRange(int value[3], int min[3], int max[3])
{
	if (value[0] > min[0] && value[0] < max[0] &&
		value[1] > min[1] && value[1] < max[1] &&
		value[2] > min[2] && value[2] < max[2])
		return true;

	return false;
}
bool Compare(int left, int right, int margin)
{
	if (left == right)
		return true;

	if ((left + margin > right && left - margin < right) ||
		(right + margin > left && right - margin < left))
		return true;

	return false;
}

bool R1(int R, int G, int B)
{
	bool e1 = false;
	if (R > 95)
		if (G > 40)
			if (B > 20)
				if ((cv::max(R, std::max(G, B)) - std::min(R, std::min(G, B))) > 15)
					if ((abs(R - G) > 15))
						if ((R > G) && (R > B))
							e1 = true;


	//bool e1 = (R>95) && (G>40) && (B>20) && ((cv::max(R, cv::max(G, B)) - cv::min(R, cv::min(G, B)))>15) && (abs(R - G)>15) && (R>G) && (R>B);

	bool e2 = false;
	if (R > 220)
		if (G > 210)
			if (B > 170)
				if ((std::abs(R - G) <= 15))
					if (R > B)
						if (G > B)
							e2 = true;

	//bool e2 = (R>220) && (G>210) && (B>170) && (abs(R - G) <= 15) && (R>B) && (G>B);
	return (e1 || e2);
}

bool R2(float Y, float Cr, float Cb)
{
	bool e3 = Cr <= 1.5862f*Cb + 20.0f;
	bool e4 = Cr >= 0.3448f*Cb + 76.2069f;
	bool e5 = Cr >= -4.5652f*Cb + 234.5652f;
	bool e6 = Cr <= -1.15f*Cb + 301.75f;
	bool e7 = Cr <= -2.2857f*Cb + 432.85f;

	return e3 && e4 && e5 && e6 && e7;
}

bool R3(float H, float S, float V)
{
	return (H < 25) || (H > 230);
}
void SetMinMax(int v1, int v2, int v3, int min[3], int max[3])
{
	if (min[0] > v1)
		min[0] = v1;
	if (min[1] > v2)
		min[1] = v2;
	if (min[2] > v3)
		min[2] = v3;

	if (max[0] < v1)
		max[0] = v1;
	if (max[1] < v2)
		max[1] = v2;
	if (max[2] < v3)
		max[2] = v3;
}

std::string BoolToString(bool data)
{
	return (data) ? "true" : "false";
}
double Distance(const cv::Point& a, const cv::Point& b)
{
	return std::sqrt(std::pow(a.x - b.x, 2) + std::pow(a.y - b.y, 2));
}
double Distance(int ax, int ay, int bx, int by)
{
	return std::sqrt(std::pow(ax - bx, 2) + std::pow(ay - by, 2));
}
double Angle(cv::Point s, cv::Point f, cv::Point e)
{
	double l1 = std::abs(Distance(f, s));
	double l2 = std::abs(Distance(f, e));

	double dot = ((s.x - f.x) * (e.x - f.x)) + ((s.y - f.y) * (e.y - f.y));
	double angle = std::acos(dot / (l1 * l2));
	angle = Rad2Deg(angle);
	return angle;
}
cv::Point Angle(const cv::Point& left, const cv::Point& right, int distance, const cv::Point& origin)
{
	double lx = right.x - left.x;
	double ly = right.y - left.y;
	double rad = std::atan2(ly, lx);
	double deg = Rad2Deg(rad) + 180.0;
	int px = int(distance * std::cos(Deg2Rad(deg))) + origin.x;
	int py = int(distance * std::sin(Deg2Rad(deg))) + origin.y;
	return cv::Point(px, py);
}
double Rad2Deg(double rad)
{
	return rad * (180.0 / HDCV_PI);
}
double Deg2Rad(double deg)
{
	return deg * (HDCV_PI / 180.0);
}
bool Intersects(cv::Point a1, cv::Point a2, cv::Point b1, cv::Point b2, cv::Point* intPnt)
{
	cv::Point r(a2 - a1);
	cv::Point s(b2 - b1);

	if (Cross(r, s) == 0)
		return false;

	double t = Cross(b1 - a1, s) / Cross(r, s);

	if (intPnt != NULL)
		*intPnt = a1 + t * r;

	return true;
}
bool Intersects(const cv::Rect& left, const cv::Rect& right)
{
	if (left.x + left.width > right.x && left.y + left.height > right.y &&
		left.x <= right.x + right.width && left.y <= right.y + right.height)
		return true;

	return false;
}
bool Intersects(const cv::Point& left, const cv::Rect& right)
{
	if (left.x > right.x && left.y > right.y &&
		left.x <= right.x + right.width && left.y <= right.y + right.height)
		return true;

	return false;
}
bool Inside(const cv::Rect& left, const cv::Rect& right)
{
	if (left.x > right.x && left.y > right.y &&
		left.x + left.width <= right.x + right.width &&
		left.y + left.height <= right.y + right.height)
		return true;

	return false;
}
double Cross(cv::Point p1, cv::Point p2)
{
	return (p1.x * p2.y) - (p1.y * p2.x);
}

double Magnitude(cv::Point& point)
{
	return std::sqrt(point.x * point.x + point.y * point.y);
}
double Magnitude(int x, int y)
{
	return std::sqrt(x * x + y * y);
}
std::pair<double, double> Normalize(int& x, int& y)
{
	double mag = Magnitude(x, y);
	return std::pair<double, double>(x / mag, y / mag);
}