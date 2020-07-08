package com.gladys.cybuverse.Utils.GeneralUtils.collections;

import com.gladys.cybuverse.Utils.GeneralUtils.Funcs;

import java.util.Iterator;

import static com.gladys.cybuverse.Utils.GeneralUtils.Funcs.format;
import static com.gladys.cybuverse.Utils.GeneralUtils.Funcs.range;

public class LinkedList<T> implements Iterable<T> {

    private int SIZE = 0;
    private Node head = null;

    LinkedList() {
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            Enumerator<T> enumerator = getEnumerator();

            @Override
            public boolean hasNext() {
                return enumerator.hasNext();
            }

            @Override
            public T next() {
                return enumerator.getNext();
            }
        };
    }

    public void forEach(Funcs.Action<T> action) {
        Enumerator<T> enumerator = getEnumerator();
        while (enumerator.hasNext()) {
            action.operate(enumerator.getNext());
        }
    }

    public void append(T value) {
        if (isEmpty()) {
            head = new Node(value);
            SIZE++;
        } else {
            getTempAt(size() - 1).next = new Node(value);
            SIZE++;
        }
    }

    public void insert(int index, T value) {
        if (validateIndex(index)) {

            if (index == 0) {
                Node temp = new Node(value);
                temp.next = head;
                head = temp;
                SIZE++;
                return;
            }

            Node new_node = new Node(value);
            new_node.next = getTempAt(index - 1).next;
            getTempAt(index - 1).next = new_node;

            SIZE++;
        }
    }

    public void replace(int index, T value) {
        if (validateIndex(index)) {
            getTempAt(index).value = value;
        }
    }

    public void replaceFirst(T object, T value) {
        int found = findFirstIndexOf(object);
        if (found != -1) {
            replace(found, value);
        }
    }

    public void replaceLast(T object, T value) {
        int found = findLastIndexOf(object);
        if (found != -1) {
            replace(found, value);
        }
    }

    public void replaceAll(T object, T value) {

        if (findLastIndexOf(object) != -1 && get(findFirstIndexOf(object)) == value) {
            return;
        }

        while (findFirstIndexOf(object) != -1) {
            replace(findFirstIndexOf(object), value);
        }
    }

    public void remove(int index) {
        if (validateIndex(index)) {
            if (index == 0) {
                head = head.next;
                SIZE--;
                return;
            }
            getTempAt(index - 1).next = getTempAt(index - 1).next.next;
            SIZE--;
        }
    }

    public void removeObject(T object) {
        int found = findFirstIndexOf(object);
        if (found == -1) {
            return;
        } else {
            remove(found);
        }
    }

    public void removeAllOf(T object) {
        while (findFirstIndexOf(object) != -1) {
            removeObject(object);
        }
    }

    public T get(int index) {
        if (validateIndex(index)) {
            return getTempAt(index).value;
        }
        return null;
    }

    public int findFirstIndexOf(T object) {
        if (!isEmpty()) {
            for (int i : range(size())) {
                if (get(i) == object) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int findLastIndexOf(T object) {
        if (!isEmpty()) {
            for (int i : range(size())) {
                if (get((SIZE - 1) - i) == object) {
                    return (SIZE - 1) - i;
                }
            }
        }
        return -1;
    }

    public int count(T object) {
        int counter = 0;
        for (int i : range(size())) {
            if (get(i) == object) {
                counter++;
            }
        }
        return counter;
    }

    public int size() {
        return SIZE;
    }

    public void clear() {
        head = null;
        SIZE = 0;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public Enumerator<T> getEnumerator() {
        return new Enumerator<>(this);
    }

    public String toString() {
        StringBuilder list = new StringBuilder("LinkedList[");
        for (int i : range(size())) {
            list.append(get(i)).append((i == size() - 1) ? "" : ", ");
        }
        return list + "]";
    }

    private boolean validateIndex(int index) {
        if (index < 0 || index >= SIZE) {
            throw new IndexOutOfBoundsException();
        } else {
            return true;
        }
    }

    private Node getTempAt(int index) {
        Node temp = head;

        for (int i : range(index)) {
            temp = temp.next;
        }

        return temp;
    }

    private class Node {

        public T value;
        Node previous;
        Node next;

        Node(T value) {
            this.value = value;
        }

        public String toString() {
            return format("Node(value:<>)", value);
        }

        public Node clone() {
            Node n = new Node(value);
            n.previous = previous;
            n.next = next;
            return n;
        }

    }

}