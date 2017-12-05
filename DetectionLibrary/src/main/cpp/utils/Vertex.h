//
// Created by Rick4 on 5-12-2017.
//

#ifndef HANDS_ON_CV_VERTEX_H
#define HANDS_ON_CV_VERTEX_H

#include <vector>

struct Vertex
{
    const size_t Index;
    int Distance;
    int TotalDistance;
    Vertex* Previous;
    std::vector<Vertex*> Adjacent;

    Vertex(size_t index, int distance, Vertex* previous = nullptr)
        : Index{ index }
    {
        Distance = distance;
        TotalDistance = distance;
        Previous = previous;
    }
};

#endif //HANDS_ON_CV_VERTEX_H
