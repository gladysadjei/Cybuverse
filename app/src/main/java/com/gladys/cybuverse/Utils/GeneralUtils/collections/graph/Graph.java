package com.gladys.cybuverse.Utils.GeneralUtils.collections.graph;

import java.util.List;

public abstract class Graph<T> {

    public static final String DIRECTED = "DIRECTED";
    public static final String UNDIRECTED = "UNDIRECTED";
    public static final String DIRECTED_AND_UNDIRECTED = "DIRECTED_AND_UNDIRECTED";
    private String graphType;


    public Graph() {
        setGraphType(DIRECTED_AND_UNDIRECTED);
    }

    public Graph(String graphType) {
        setGraphType(graphType);
    }

    public final String getGraphType() {
        return this.graphType;
    }

    private void setGraphType(String graphType) {
        if (graphType.equals(DIRECTED) || graphType.equals(UNDIRECTED) || graphType.equals(DIRECTED_AND_UNDIRECTED)) {
            this.graphType = graphType;
        }
    }

    public abstract void addVertice(Vertice<T> vertice);

    public abstract Vertice<T> getVertice(T vertice);

    public abstract List<Vertice<T>> getAllVertice();

    public abstract void removeVertice(Vertice<T> vertice);

    public abstract boolean hasVertice(Vertice<T> vertice);

    public abstract boolean hasVertice(T value);

    public abstract void addEdge(Edge<T> edge) throws InvalidVerticeException;

    public abstract void addEdge(T startpoint, T endpoint) throws InvalidVerticeException;

    public abstract List<Edge<T>> getEdges(T vertice) throws InvalidVerticeException;

    public abstract List<Edge<T>> getAllEdges();

    public abstract void removeEdge(Edge<T> edge) throws InvalidVerticeException;

    public abstract void removeEdge(T startpoint, T endpoint) throws InvalidVerticeException;

    public abstract boolean hasEdge(Edge<T> edge) throws InvalidVerticeException;

    public abstract boolean hasEdge(T startpoint, T endpoint) throws InvalidVerticeException;

    public abstract int getTotalVerticesCount();

    public abstract int getTotalEdgesCount();

    public abstract List<Vertice<T>> getNeighbours(T vertice) throws InvalidVerticeException;

    public abstract List<Vertice<T>> getNeighbours(T vertice, int dept) throws InvalidVerticeException;

    public abstract void traverse(VerticeOperation<T> operation);

    public abstract long getEstimatedMaxEdge();

    public abstract List<List<T>> getPaths(T startpoint, T endpoint);

    public abstract int getDegree(T vertice) throws InvalidVerticeException;

    public abstract int getInDegree(T vertice);

    public abstract int getOutDegree(T vertice) throws InvalidVerticeException;

    public abstract List<T> getShortestPath(T startpoint, T endpoint);

    public abstract List<T> getLongestPath(T startpoint, T endpoint);

    public boolean isConnected() {
        return false;
    }

    public boolean isStronglyConnected() {
        return false;
    }

    public boolean isWeeklyConnected() {
        return false;
    }

    public boolean isSparse() {
        return false;
    }

    public boolean isDense() {
        return false;
    }

    @Override
    public String toString() {
        return "Graph<" + getGraphType() + ">";
    }

    public interface VerticeOperation<T> {
        void operate(Vertice<T> vertice);
    }

    public static class InvalidVerticeException extends Exception {
        InvalidVerticeException() {
            super("You are referencing a vertice that does not exist in the Graph!.");
        }

        InvalidVerticeException(String message) {
            super(message);
        }
    }
}
