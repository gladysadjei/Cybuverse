package com.gladys.cybuverse.Utils.GeneralUtils.collections;

import java.util.Collection;
import java.util.List;

public class Enumerator<T> {

    private int index = 0;
    private String TYPE;

    private T[] ARRAY;
    private List<T> LIST;
    private Collection<T> COLLECTION;
    private LinkedList<T> LINKEDLIST;
    private MasterList<T> MASTERLIST;

    Enumerator(T[] list) {
        ARRAY = list;
        TYPE = "array";
    }

    Enumerator(List<T> list) {
        LIST = list;
        TYPE = "list";
    }

    Enumerator(Collection<T> list) {
        COLLECTION = list;
        TYPE = "collection";
    }

    Enumerator(LinkedList<T> list) {
        LINKEDLIST = list;
        TYPE = "linkedlist";
    }

    Enumerator(MasterList<T> list) {
        MASTERLIST = list;
        TYPE = "masterlist";
    }


    public boolean hasNext() {
        switch (TYPE) {
            case "array":
                return (ARRAY.length > index);
            case "list":
                return (LIST.size() > index);
            case "collection":
                return (COLLECTION.size() > index);
            case "linkedlist":
                return (LINKEDLIST.size() > index);
            case "masterlist":
                return (MASTERLIST.size() > index);
        }
        return false;
    }

    public T getNext() {
        if (hasNext()) {
            index++;
            switch (TYPE) {
                case "array":
                    return ARRAY[index - 1];
                case "list":
                    return LIST.get(index - 1);
                case "collection":
                    List<T> l = (List<T>) COLLECTION;
                    return l.get(index - 1);
                case "linkedlist":
                    return LINKEDLIST.get(index - 1);
                case "masterlist":
                    return MASTERLIST.pop(index - 1);
            }
        }
        return null;
    }

    public int size() {
        switch (TYPE) {
            case "array":
                return ARRAY.length;
            case "list":
                return LIST.size();
            case "collection":
                return COLLECTION.size();
            case "linkedlist":
                return LINKEDLIST.size();
            case "masterlist":
                return MASTERLIST.size();
        }
        return -1;
    }

    public void reset() {
        index = 0;
    }

    public int enumerationsRemaining() {
        return size() - index;
    }

    public int enumerationsPassed() {
        return index;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

}