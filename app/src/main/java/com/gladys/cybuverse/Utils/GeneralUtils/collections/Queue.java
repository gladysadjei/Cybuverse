package com.gladys.cybuverse.Utils.GeneralUtils.collections;


import com.gladys.cybuverse.Utils.GeneralUtils.collections.exceptions.EmptyObjectException;

import java.util.Iterator;

public class Queue<T> implements Iterable<T> {

    private LinkedList<T> DATA = new LinkedList<>();

    Queue() {
    }

    public void enqueue(T object) {
        DATA.append(object);
    }

    public T dequeue() {
        T temp = peekFront();
        DATA.remove(0);
        return temp;
    }

    public T peekFront() {
        if (isEmpty()) {
            try {
                throw new EmptyObjectException("Queue");
            } catch (EmptyObjectException e) {
                e.printStackTrace();
            }
        }
        return DATA.get(0);
    }

    public boolean isEmpty() {
        return DATA.isEmpty();
    }

    public int size() {
        return DATA.size();
    }

    public String toString() {
        return DATA.toString().replace("LinkedList", "Queue");
    }

    @Override
    public Iterator<T> iterator() {
        return DATA.iterator();
    }
}