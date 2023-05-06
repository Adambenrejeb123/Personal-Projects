/**
 * @file test.cpp
 */
 
#include <cassert>
#include "../src/graph.h"

int main()
{
    Graph graph;
    size_t vertex1Id = graph.makeVertex("Vertex1");
    size_t vertex2Id = graph.makeVertex("Vertex2");
    size_t vertex3Id = graph.makeVertex("Vertex3");
    graph.makeEdge(vertex1Id, vertex2Id);
    graph.makeEdge(vertex1Id, vertex3Id);
    graph.makeEdge(vertex3Id, vertex1Id);

    const std::vector<Vertex>& vertices = graph.getVertices();
    assert(vertices.size() == 3);
    const std::vector<Edge>& edges = graph.getEdges();
    assert(edges.size() == 3);
    assert(graph.getEdge(0).getInVertexId() == vertex1Id);
    assert(graph.getEdge(0).getOutVertexId() == vertex2Id);
    assert(graph.getEdge(1).getInVertexId() == vertex1Id);
    assert(graph.getEdge(1).getOutVertexId() == vertex3Id);
    assert(graph.getEdge(2).getInVertexId() == vertex3Id);
    assert(graph.getEdge(2).getOutVertexId() == vertex1Id);
}

