/**
 * Main file. Should not be modified.
 * @file main.cpp
 */

#include "graph.h"

int main()
{
    Graph graph;
    size_t vertex1Id = graph.makeVertex("Vertex1");
    size_t vertex2Id = graph.makeVertex("Vertex2");
    size_t vertex3Id = graph.makeVertex("Vertex3");
    graph.makeEdge(vertex1Id, vertex2Id);
    graph.makeEdge(vertex1Id, vertex3Id);
    graph.makeEdge(vertex3Id, vertex1Id);

    printGraph(std::cout, graph);

    return 0;
}

