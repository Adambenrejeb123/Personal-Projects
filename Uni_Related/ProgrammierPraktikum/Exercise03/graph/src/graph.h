/**
 * @file graph.h
 */

#ifndef GRAPH_H_
#define GRAPH_H_
#include <string>
#include <vector>
#include <memory>
#include <iostream>

class Graph;

/**
 * Instances of class Vertex represent vertices of the Graph data structure.
 */
class Vertex
{
private:
    std::string name;
    size_t id;
    /** Vector of Edge's IDs pointing to this Vertex */
    std::vector<size_t> inEdgeIds;
    /** Vector of Edge's IDs pointing from this Vertex */
    std::vector<size_t> outEdgeIds;
    /**
     * Adds an Edge pointing towards this Vertex
     * @param id ID of the Edge pointing towards this Vertex
     */
    void addInEdgeId(size_t edgeId);

    /**
    * Adds an Edge which points out of this Vertex
    * @param id ID of an Edge which points out of this Vertex
    */
    void addOutEdgeId(size_t edgeId);

public:
    Vertex(std::string vertexName,
           size_t vertexId);
    const std::string& getName() const { return this->name; }
    size_t getId()   const { return this->id; }
    const std::vector<size_t>& getInEdgeIds()  const { return this->inEdgeIds; }
    const std::vector<size_t>& getOutEdgeIds() const { return this->outEdgeIds; }

    friend class Graph;
};

/**
 * Instances of class Edge represent connections between Vertexs.
 * An Edge points (or is directed) from its tail to its head.
 * Edges are handled by class Graph.
 */
class Edge
{
private:
    size_t id;
    /** ID of the Vertex connected to the tail of the Edge */
    size_t inVertex;
    /** ID of the Vertex connected to the head of the Edge */
    size_t outVertex;
public:
    Edge(size_t id, size_t inVertexId, size_t outVertexId);
    size_t getId()    const { return this->id; }
    size_t getInVertexId()  const { return this->inVertex; }
    size_t getOutVertexId() const { return this->outVertex; }

    friend class Graph;
};

class Graph
{
private:
    std::vector<Vertex> vertices;
    std::vector<Edge> edges;
public:
    const std::vector<Vertex>& getVertices() const {return this->vertices; }
    const std::vector<Edge>& getEdges() const {return this->edges; }
    size_t makeVertex(const std::string& name);
    size_t makeEdge(size_t inId, size_t outId);
    Vertex& getVertex(size_t id);
    Edge& getEdge(size_t id);
    const Vertex& getVertex(size_t id) const;
    const Edge& getEdge(size_t id) const;
};

void printGraph(std::ostream& os, const Graph& graph);

#endif /* GRAPH_H_ */


