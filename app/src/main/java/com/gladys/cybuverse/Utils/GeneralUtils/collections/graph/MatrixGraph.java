package com.gladys.cybuverse.Utils.GeneralUtils.collections.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//TODO: optimize for undirected graph

public class MatrixGraph extends Graph<Integer> {

    private int verticeCount;
    private List<Edge<Integer>>[][] adjMatrix;
    private Map<Integer, Vertice<Integer>> verticeMap;

    public MatrixGraph(int verticeCount) {
        super();
        init(verticeCount);
    }

    public MatrixGraph(int verticeCount, String graphType) {
        super(graphType);
        init(verticeCount);
    }

    private void init(int verticeCount) {
        this.verticeCount = verticeCount;
        this.verticeMap = new HashMap<>();
        this.adjMatrix = new List[verticeCount][verticeCount];
        makeVertices();
    }

    private void makeVertices() {
        for (int i = 0; i < verticeCount; i++) {
            verticeMap.put(i, new Vertice<>(i));
        }
    }

    private void evaluateVertice(Vertice<Integer> vertice) throws InvalidVerticeException {
        evaluateVertice(vertice.getValue());
    }

    private void evaluateVertice(Integer vertice) throws InvalidVerticeException {
        if ((vertice < 0 || vertice >= verticeCount)) {
            throw new InvalidVerticeException("The vertice with value " + vertice + " does not exist!.");
        }
    }

    public void expandGraph(int new_vertice_count) {
        if (new_vertice_count > verticeCount) {
            List<Edge<Integer>>[][] newAdjMatrix = new ArrayList[verticeCount][verticeCount];
            for (int i = 0; i < verticeCount; i++) {
                System.arraycopy(adjMatrix[i], 0, newAdjMatrix[i], 0, verticeCount);
            }
            for (int i = verticeCount; i < new_vertice_count; i++) {
                verticeMap.put(i, new Vertice<>(i));
            }
            adjMatrix = newAdjMatrix;
            verticeCount = new_vertice_count;
        }
    }

    @Override
    public void addVertice(Vertice<Integer> vertice) {
        verticeMap.put(vertice.getValue(), vertice);
    }

    @Override
    public Vertice<Integer> getVertice(Integer vertice) {
        return verticeMap.get(vertice);
    }

    @Override
    public List<Vertice<Integer>> getAllVertice() {
        return new ArrayList<>(verticeMap.values());
    }

    @Override
    public void removeVertice(Vertice<Integer> vertice) {
        verticeMap.remove(vertice.getValue());
    }

    @Override
    public boolean hasVertice(Vertice<Integer> vertice) {
        return hasVertice(vertice.getValue()) && verticeMap.get(vertice.getValue()) == vertice;
    }

    @Override
    public boolean hasVertice(Integer value) {
        return verticeMap.containsKey(value);
    }

    @Override
    public void addEdge(Edge<Integer> edge) throws InvalidVerticeException {
        evaluateVertice(edge.getStartPoint());
        evaluateVertice(edge.getEndPoint());
        switch (getGraphType()) {
            case DIRECTED:
                edge.setDirected(true);
                break;
            case UNDIRECTED:
                edge.setDirected(false);
                break;
        }

        if (this.adjMatrix[edge.getStartPoint()][edge.getEndPoint()] == null) {
            this.adjMatrix[edge.getStartPoint()][edge.getEndPoint()] = new ArrayList<>();
        }

        //adding edge
        if (!edge.isDirected()) {
            this.adjMatrix[edge.getStartPoint()][edge.getEndPoint()].add(edge);
            this.adjMatrix[edge.getEndPoint()][edge.getStartPoint()].add(edge);
        } else if (edge.isDirected()) {
            this.adjMatrix[edge.getStartPoint()][edge.getEndPoint()].add(edge);
        }

    }

    @Override
    public void addEdge(Integer startpoint, Integer endpoint) throws InvalidVerticeException {
        addEdge(new Edge<>(startpoint, endpoint));
    }

    @Override
    public List<Edge<Integer>> getEdges(Integer vertice) throws InvalidVerticeException {
        evaluateVertice(vertice);
        List<Edge<Integer>> edges = new ArrayList<>();
        for (int i = 0; i < verticeCount; i++) {
            if (this.adjMatrix[vertice][i] != null) {
                edges.addAll(this.adjMatrix[vertice][i]);
            }
        }
        return edges;
    }

    @Override
    public List<Edge<Integer>> getAllEdges() {
        List<Edge<Integer>> edges = new ArrayList<>();
        for (int i = 0; i < verticeCount; i++) {
            for (int j = 0; j < verticeCount; j++)
                if (this.adjMatrix[i][j] != null) {
                    edges.addAll(this.adjMatrix[i][j]);
                }
        }
        return edges;
    }

    @Override
    public void removeEdge(Edge<Integer> edge) throws InvalidVerticeException {
        evaluateVertice(edge.getStartPoint());
        evaluateVertice(edge.getEndPoint());
        this.adjMatrix[edge.getStartPoint()][edge.getEndPoint()] = null;
    }

    @Override
    public void removeEdge(Integer startpoint, Integer endpoint) throws InvalidVerticeException {
        evaluateVertice(startpoint);
        evaluateVertice(endpoint);
        this.adjMatrix[startpoint][endpoint] = null;
    }

    @Override
    public boolean hasEdge(Edge<Integer> edge) throws InvalidVerticeException {
        evaluateVertice(edge.getStartPoint());
        evaluateVertice(edge.getEndPoint());
        return this.adjMatrix[edge.getStartPoint()][edge.getEndPoint()] != null;
    }

    @Override
    public boolean hasEdge(Integer startpoint, Integer endpoint) throws InvalidVerticeException {
        evaluateVertice(startpoint);
        evaluateVertice(endpoint);
        return this.adjMatrix[startpoint][endpoint] != null;
    }

    @Override
    public int getTotalVerticesCount() {
        return verticeCount;
    }

    @Override
    public int getTotalEdgesCount() {
        return getAllEdges().size();
    }

    @Override
    public int getDegree(Integer vertice) throws InvalidVerticeException {
        return 0;
    }

    @Override
    public int getInDegree(Integer vertice) {
        int count = 0;
        for (int i = 0; i < verticeCount; i++) {
            if (this.adjMatrix[i][vertice] != null) {
                count += this.adjMatrix[i][vertice].size();
            }
        }
        return count;
    }

    @Override
    public int getOutDegree(Integer vertice) throws InvalidVerticeException {
        int count = 0;
        for (int i = 0; i < verticeCount; i++) {
            if (this.adjMatrix[vertice][i] != null) {
                count += this.adjMatrix[vertice][i].size();
            }
        }
        return count;
    }

    @Override
    public long getEstimatedMaxEdge() {
        return verticeCount * (verticeCount - 1);
    }

    @Override
    public List<Vertice<Integer>> getNeighbours(Integer vertice) throws InvalidVerticeException {
        return getNeighbours(vertice, 1);
    }

    @Override
    public List<Vertice<Integer>> getNeighbours(Integer vertice, int dept) throws InvalidVerticeException {
        Set<Vertice<Integer>> neighbours = new HashSet<>();
        List<Edge<Integer>> edges = null;
        for (int i = 0; i < dept; i++) {
            if (i == 0) {
                edges = getEdges(vertice);
            } else {
                List<Edge<Integer>> temp = new ArrayList<>(edges);
                edges = new ArrayList<>();
                for (Edge<Integer> edge : temp) {
                    edges.addAll(getEdges(edge.getEndPoint()));
                }
            }
        }

        //getNeighbours
        if (edges != null && edges.size() > 0) {
            for (Edge<Integer> edge : edges) {
                neighbours.add(getVertice(edge.getEndPoint()));
            }
        }

        List<Vertice<Integer>> allNeighbours = new ArrayList<>(neighbours);
        allNeighbours.remove(getVertice(vertice));
        return allNeighbours;
    }

    @Override
    public List<List<Integer>> getPaths(Integer startpoint, Integer endpoint) {
        return null;
    }

    @Override
    public List<Integer> getShortestPath(Integer startpoint, Integer endpoint) {
        return null;
    }

    @Override
    public List<Integer> getLongestPath(Integer startpoint, Integer endpoint) {
        return null;
    }

    @Override
    public void traverse(VerticeOperation<Integer> operation) {

    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public boolean isStronglyConnected() {
        return false;
    }

    @Override
    public boolean isWeeklyConnected() {
        return false;
    }

    @Override
    public boolean isSparse() {
        return false;
    }

    @Override
    public boolean isDense() {
        return false;
    }

}
