package com.gladys.cybuverse.Utils.GeneralUtils.collections.graph;

import java.util.HashMap;

public class Edge<T> {

    private Double weight;
    private boolean directed;
    private T startpoint, endpoint;
    private HashMap<Object, Object> properties;

    public Edge(Vertice<T> startpoint, Vertice<T> endpoint) {
        this.startpoint = startpoint.getValue();
        this.endpoint = endpoint.getValue();
        this.properties = new HashMap<>();
        this.directed = false;
    }

    public Edge(T startpoint, T endpoint) {
        this.startpoint = startpoint;
        this.endpoint = endpoint;
        this.properties = new HashMap<>();
        this.directed = false;
    }

    public T getStartPoint() {
        return startpoint;
    }

    public T getEndPoint() {
        return endpoint;
    }

    public Double getWeight() {
        return this.weight;
    }

    public Edge<T> setWeight(double weight) {
        this.weight = weight;
        return this;
    }

    public Edge<T> setProperty(Object key, Object value) {
        this.properties.put(key, value);
        return this;
    }

    public Object getProperty(Object key) {
        return this.properties.get(key);
    }

    public boolean hasPropertyKey(Object key) {
        return this.properties.containsKey(key);
    }

    public boolean hasPropertyValue(Object value) {
        return this.properties.containsValue(value);
    }

    public Edge<T> removeProperty(Object key) {
        this.properties.remove(key);
        return this;
    }

    public boolean isDirected() {
        return directed;
    }

    public Edge<T> setDirected(boolean isDirected) {
        this.directed = isDirected;
        return this;
    }

    @Override
    public String toString() {
        return "Edge<V(" + getStartPoint() + ") : V(" + getEndPoint() + ")" + ((getWeight() != null) ? " : [W=" + getWeight() + "]" : "") + ">";
    }
}
