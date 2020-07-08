package com.gladys.cybuverse.Utils.GeneralUtils.collections;


import com.gladys.cybuverse.Utils.GeneralUtils.collections.exceptions.EmptyObjectException;

import java.util.Iterator;

public class MasterQueue<T> implements Iterable<T> {

    private LinkedList<T> DATA = new LinkedList<>();

    MasterQueue() {
    }

    public void add(T object) {
        DATA.append(object);
    }

    public T getNext() {
        T temp = peekNext();
        DATA.remove(0);
        return temp;
    }

    public T peekNext() {
        if (isEmpty()) {
            try {
                throw new EmptyObjectException("MasterQueue");
            } catch (EmptyObjectException e) {
                e.printStackTrace();
            }
        }
        return DATA.get(0);
    }

    public boolean hasNext() {
        return !isEmpty();
    }

    public boolean isEmpty() {
        return DATA.isEmpty();
    }

    public int size() {
        return DATA.size();
    }

    public String toString() {
        return DATA.toString().replace("LinkedList", "MasterQueue");
    }

    @Override
    public Iterator<T> iterator() {
        return DATA.iterator();
    }
}