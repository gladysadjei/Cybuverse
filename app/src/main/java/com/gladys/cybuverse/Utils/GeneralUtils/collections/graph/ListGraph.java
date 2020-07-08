package com.gladys.cybuverse.Utils.GeneralUtils.collections.graph;


import com.gladys.cybuverse.Utils.GeneralUtils.collections.KeyValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ListGraph<T> extends Graph<T> {

    private Map<T, VerticeEdge> adjList;

    public ListGraph() {
        super();
        adjList = new HashMap<>();
    }

    public ListGraph(String graphType) {
        super(graphType);
        adjList = new HashMap<>();
    }

    @Override
    public void addVertice(Vertice<T> vertice) {
        adjList.put(vertice.getValue(), new VerticeEdge(vertice, new ArrayList<Edge<T>>()));
    }

    @Override
    public Vertice<T> getVertice(T vertice) {
        return getVerticeEdge(vertice).getVertice();
    }

    @Override
    public List<Vertice<T>> getAllVertice() {
        List<Vertice<T>> vertices = new ArrayList<>();
        for (T key : adjList.keySet()) {
            vertices.add(getVertice(key));
        }
        return vertices;
    }

    private void evaluateVertice(Vertice<T> vertice) throws InvalidVerticeException {
        evaluateVertice(vertice.getValue());
    }

    private void evaluateVertice(T vertice) throws InvalidVerticeException {
        if (!hasVertice(vertice)) {
            throw new InvalidVerticeException("The vertice with value " + vertice + " does not exist!.");
        }
    }

    public VerticeEdge getVerticeEdge(T verticeValue) {
        return adjList.get(verticeValue);
    }

    @Override
    public void removeVertice(Vertice<T> vertice) {
        adjList.remove(vertice.getValue());
    }

    @Override
    public boolean hasVertice(Vertice<T> vertice) {
        return hasVertice(vertice.getValue());
    }

    @Override
    public boolean hasVertice(T value) {
        return adjList.containsKey(value);
    }

    @Override
    public void addEdge(Edge<T> edge) throws InvalidVerticeException {
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

        //adding edge
        if (!edge.isDirected()) {
            adjList.get(edge.getStartPoint()).getEdges().add(edge);
            adjList.get(edge.getEndPoint()).getEdges().add(edge);
        } else if (edge.isDirected()) {
            adjList.get(edge.getStartPoint()).getEdges().add(edge);
        }
    }

    @Override
    public void addEdge(T startpoint, T endpoint) throws InvalidVerticeException {
        addEdge(new Edge<>(startpoint, endpoint));
    }

    @Override
    public List<Edge<T>> getEdges(T vertice) throws InvalidVerticeException {
        evaluateVertice(vertice);
        return getVerticeEdge(vertice).getEdges();
    }

    @Override
    public List<Edge<T>> getAllEdges() {
        List<Edge<T>> allEdges = new ArrayList<>();
        for (T vertice : adjList.keySet()) {
            try {
                allEdges.addAll(getEdges(vertice));
            } catch (InvalidVerticeException e) {
                e.printStackTrace();
            }
        }
        return allEdges;
    }

    @Override
    public void removeEdge(Edge<T> edge) throws InvalidVerticeException {
        evaluateVertice(edge.getStartPoint());
        evaluateVertice(edge.getEndPoint());
        adjList.get(edge.getStartPoint()).getEdges().remove(edge);
    }

    @Override
    public void removeEdge(T startpoint, T endpoint) throws InvalidVerticeException {
        evaluateVertice(startpoint);
        evaluateVertice(endpoint);
        for (int i = 0; i < adjList.get(startpoint).getEdges().size(); i++) {
            if (adjList.get(startpoint).getEdges().get(i).getEndPoint() == endpoint) {
                adjList.get(startpoint).getEdges().remove(i);
                break;
            }
        }
    }

    @Override
    public boolean hasEdge(Edge<T> edge) throws InvalidVerticeException {
        return getEdges(edge.getStartPoint()).contains(edge);
    }

    @Override
    public boolean hasEdge(T startpoint, T endpoint) throws InvalidVerticeException {
        evaluateVertice(startpoint);
        evaluateVertice(endpoint);
        for (int i = 0; i < adjList.get(startpoint).getEdges().size(); i++) {
            if (adjList.get(startpoint).getEdges().get(i).getEndPoint() == endpoint) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getTotalVerticesCount() {
        return adjList.size();
    }

    @Override
    public int getTotalEdgesCount() {
        return getAllEdges().size();
    }

    @Override
    public int getDegree(T vertice) throws InvalidVerticeException {
        return getEdges(vertice).size();
    }

    @Override
    public int getInDegree(T vertice) {
        int count = 0;
        for (VerticeEdge value : adjList.values()) {
            for (Edge<T> edge : value.getEdges()) {
                if (edge.getEndPoint() == vertice) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public int getOutDegree(T vertice) throws InvalidVerticeException {
        return getEdges(vertice).size();
    }

    @Override
    public long getEstimatedMaxEdge() {
        int n = adjList.size();
        return n * (n - 1);
    }

    @Override
    public List<Vertice<T>> getNeighbours(T vertice) throws InvalidVerticeException {
        return getNeighbours(vertice, 1);
    }

    @Override
    public List<Vertice<T>> getNeighbours(T vertice, int dept) throws InvalidVerticeException {
        Set<Vertice<T>> neighbours = new HashSet<>();
        List<Edge<T>> edges = null;
        for (int i = 0; i < dept; i++) {
            if (i == 0) {
                edges = getEdges(vertice);
            } else {
                List<Edge<T>> temp = new ArrayList<>(edges);
                edges = new ArrayList<>();
                for (Edge<T> edge : temp) {
                    edges.addAll(getEdges(edge.getEndPoint()));
                }
            }
        }

        //getNeighbours
        if (edges != null && edges.size() > 0) {
            for (Edge<T> edge : edges) {
                neighbours.add(getVertice(edge.getEndPoint()));
            }
        }

        List<Vertice<T>> allNeighbours = new ArrayList<>(neighbours);
        allNeighbours.remove(getVertice(vertice));
        return allNeighbours;
    }

    @Override
    public List<List<T>> getPaths(T startpoint, T endpoint) {
        //getEdges(endpoint)
        return null;
    }

    @Override
    public List<T> getShortestPath(T startpoint, T endpoint) {
        return null;
    }

    @Override
    public List<T> getLongestPath(T startpoint, T endpoint) {
        return null;
    }

    @Override
    public void traverse(VerticeOperation<T> operation) {
        for (VerticeEdge verticeEdge : adjList.values()) {
            operation.operate(verticeEdge.getVertice());
        }
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

    @Override
    public String toString() {
        return "ListGraph<V: " + getTotalVerticesCount() + ", E: " + getTotalEdgesCount() + ">";
    }

    public class VerticeEdge {

        private KeyValuePair<Vertice<T>, List<Edge<T>>> data;

        public VerticeEdge(Vertice<T> key, List<Edge<T>> value) {
            data = new KeyValuePair<>(key, value);
        }

        public Vertice<T> getVertice() {
            return data.getKey();
        }

        public List<Edge<T>> getEdges() {
            return data.getValue();
        }

        @Override
        public String toString() {
            return "VerticeEdge<" + getVertice() + ": " + getEdges() + ">";
        }
    }
}
