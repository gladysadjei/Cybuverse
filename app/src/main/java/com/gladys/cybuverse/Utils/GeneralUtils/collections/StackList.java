package com.gladys.cybuverse.Utils.GeneralUtils.collections;

import com.gladys.cybuverse.Utils.GeneralUtils.Funcs;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;

import static com.gladys.cybuverse.Utils.GeneralUtils.Funcs.range;


public class StackList<T> extends Stack<T> implements Iterable<T> {

    public StackList() {
    }

    public StackList(Stack<T> stack) {
        for (int i : range(stack.size())) {
            push(stack.pop());
        }
    }

    public StackList(List<T> list) {
        for (int i : range(list.size())) {
            push(list.get(i));
        }
    }

    public T peekElementAt(int index) {
        return super.list.pop(index);
    }

    public T pop(int index) {
        T temp = super.list.pop(index);
        super.list.remove(index);
        return temp;
    }

    public void replace(int index, T object) {
        super.list.update(index, object);
    }

    public void remove(int index) {
        super.list.remove(index);
    }

    public void remove(T object) {
        super.list.remove(super.list.firstIndexOf(object));
    }

    public void insert(int index, T object) {
        super.list.insert(index, object);
    }

    public boolean contains(T object) {
        return super.list.contains(object);
    }

    public int findFirstIndexOf(T object) {
        return super.list.firstIndexOf(object);
    }

    public int findLastIndexOf(T object) {
        return super.list.lastIndexOf(object);
    }

    public List<T> toList() {
        return super.list.toList();
    }

    public Stack<T> toStack() {
        return this;
    }

    public Enumerator<T> getEnumerator() {
        return new Enumerator<>(toList());
    }

    @Override
    public Iterator<T> iterator() {
        return super.list.iterator();
    }

    public void forEach(Funcs.Action<T> action) {
        for (T data : super.list) {
            action.operate(data);
        }
    }

    @Override
    public Spliterator<T> spliterator() {
        return list.spliterator();
    }
}
