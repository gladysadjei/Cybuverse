package com.gladys.cybuverse.Utils.GeneralUtils.collections;

import com.gladys.cybuverse.Utils.GeneralUtils.Funcs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;

import static com.gladys.cybuverse.Utils.GeneralUtils.Funcs.range;
import static com.gladys.cybuverse.Utils.GeneralUtils.Funcs.toDouble;

public class MasterList<T> implements Iterable<T> {

    private List<T> DATA = new ArrayList<>();

    public MasterList() {
    }

    MasterList(T[] obj) {
        DATA = changeToList(obj);
    }

    MasterList(List<T> obj) {
        DATA = obj;
    }

    MasterList(MasterList<T> obj) {
        DATA = changeToList(obj);
    }

    private List<T> changeToList(T[] obj) {
        List<T> n = new ArrayList<>();
        Collections.addAll(n, obj);
        return n;
    }

    private List<T> changeToList(MasterList<T> obj) {
        List<T> n = new ArrayList<>();
        for (int i : range(obj.size())) {
            n.add(obj.pop(i));
        }
        return n;
    }

    public MasterList join(List<T> obj) {
        DATA.addAll(obj);
        return clone();
    }

    public MasterList join(MasterList<T> obj) {
        Collections.addAll(DATA, obj.toArray());
        return clone();
    }

    public MasterList append(T... obj) {
        Collections.addAll(DATA, obj);
        return clone();
    }

    public MasterList appendleft(T obj) {
        DATA.add(0, obj);
        return clone();
    }

    public T pop(int index) {
        return DATA.get(index);
    }

    public T popfirst() {
        if (!isEmpty()) return DATA.get(0);
        return null;
    }

    public T poplast() {
        if (!isEmpty()) return DATA.get(size() - 1);
        return null;
    }

    public void assign(List<T> obj) {
        DATA = obj;
    }

    public void assign(MasterList<T> obj) {
        DATA = changeToList(obj);
    }

    public void assign(T[] obj) {
        DATA = changeToList(obj);
    }

    public MasterList insert(int index, T obj) {
        DATA.add(index, obj);
        return clone();
    }

    public MasterList update(int index, T obj) {
        DATA.set(index, obj);
        return clone();
    }

    public MasterList updateAll(T old_obj, T new_obj) {
        while (DATA.contains(old_obj)) {
            update(lastIndexOf(old_obj), new_obj);
        }
        return clone();
    }

    public MasterList remove(int index) {
        DATA.remove(index);
        return clone();
    }

    public MasterList removeAll(T obj) {
        while (DATA.contains(obj)) {
            remove(lastIndexOf(obj));
        }
        return clone();
    }

    public void clear() {
        DATA.clear();
    }

    public int count(T obj) {
        List<T> REMDATA = new ArrayList<>(DATA);
        int count = 0;
        while (REMDATA.contains(obj)) {
            count++;
            REMDATA.remove(REMDATA.lastIndexOf(obj));
        }
        return count;
    }

    public MasterList<T> reverse() {
        MasterList<T> n = new MasterList<>();
        for (int i : range(size())) {
            n.append(pop(size() - (i + 1)));
        }
        return n;
    }

    public MasterList<T> sample(int size) {
        return sample(0, size);
    }

    public MasterList<T> sample(int begin_index, int end_index) {
        MasterList<T> n = new MasterList<>();
        for (int i : range(begin_index, end_index)) {
            n.append(pop(i));
        }
        return n;
    }

    public MasterList<T> randsample(int size) {
        return new MasterList(Funcs.randsample(DATA.toArray(), size));
    }

    public int firstIndexOf(T obj) {
        return DATA.indexOf(obj);
    }

    public int lastIndexOf(T obj) {
        return DATA.lastIndexOf(obj);
    }

    public String toString() {
        return DATA.toString();
    }

    public int hashCode() {
        return DATA.hashCode();
    }

    public MasterList<T> clone() {
        return new MasterList(DATA);
    }

    public void forEach(Consumer<? super T> action) {
        for (T data : DATA) {
            action.accept(data);
        }
    }

    public Iterator<T> iterator() {
        return DATA.iterator();
    }

    public ListIterator<T> listiterator() {
        return DATA.listIterator();
    }

    public Iterator<T> listiterator(int index) {
        return DATA.listIterator(index);
    }

    public boolean equal(Object obj) {
        if (obj.getClass().getSimpleName().contains("List")) {
            return obj.toString().equals(toString());
        }
        return false;
    }

    public boolean equal(MasterList<T> obj) {
        return Arrays.equals(DATA.toArray(), obj.toArray());
    }

    public int size() {
        return DATA.size();
    }

    public boolean contains(T... obj) {
        if (obj.length > 1) {
            for (T i : obj) {
                if (!contains(i)) {
                    return false;
                }
            }
            return true;
        }
        return DATA.contains(obj[0]);
    }

    public boolean contains(MasterList<T> obj) {
        for (T i : obj.toArray()) {
            if (!contains(i)) {
                return false;
            }
        }
        return true;
    }

    public boolean contains(Collection<?> c) {
        return DATA.containsAll(c);
    }

    public boolean isEmpty() {
        return DATA.isEmpty();
    }

    public T[] toArray() {
        return (T[]) DATA.toArray();
    }

    public T[] toArray(T[] array) {
        return DATA.toArray(array);
    }

    public List<T> toList() {
        return DATA;
    }

    public T[] iter() {
        return toArray();
    }

    public MasterList<T> toSet() {
        return new MasterList<T>(asSet());
    }

    public List<T> asSet() {
        return new ArrayList<T>(new HashSet<T>(DATA));
    }

    public MasterList<T> sort() {
        return changeToMyList(new Funcs.SortedNumArray(toDouble(DATA.toArray())).toDouble());
    }

    private MasterList<T> changeToMyList(Double[] toDouble) {
        MasterList<T> n = new MasterList<>();
        for (Double i : toDouble) {
            n.append((T) i);
        }
        return n;
    }

    public boolean issupersetOf(List<T> obj) {
        return DATA.containsAll(obj);
    }

    public boolean issubsetOf(List<T> obj) {
        return obj.containsAll(DATA);
    }

    public boolean issupersetOf(MasterList<T> obj) {
        List<T> n = new ArrayList<>();
        Collections.addAll(n, obj.toArray());
        return DATA.containsAll(n);
    }

    public boolean issubsetOf(MasterList<T> obj) {
        List<T> n = new ArrayList<>();
        Collections.addAll(n, obj.toArray());
        return n.containsAll(DATA);
    }

    public Enumerator<T> getEnumerator() {
        return new Enumerator<>(this);
    }

    public void forEach(Funcs.Action<T> action) {
        Enumerator<T> enumerator = getEnumerator();
        while (enumerator.hasNext()) {
            action.operate(enumerator.getNext());
        }
    }

}