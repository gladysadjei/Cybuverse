package com.gladys.cybuverse.Utils.GeneralUtils.collections;

import com.gladys.cybuverse.Utils.GeneralUtils.Funcs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Dictionary<K, V> implements Iterable<KeyValuePair<K, V>> {

    private Map<K, V> DATA = new HashMap<>();

    public Dictionary() {
    }

    public Dictionary(Map<K, V> obj) {
        DATA = obj;
    }

    public void assign(Map<K, V> obj) {
        DATA = obj;
    }

    public void join(Map<K, V> obj) {

        for (K key : keys()) {
            DATA.put(key, get(key));
        }
    }

    public void set(K key, V value) {
        DATA.put(key, value);
    }

    public V get(K key) {
        return DATA.get(key);
    }

    public void setIfAbsent(K key, V value) {
        if (!hasKey(key)) {
            set(key, value);
        }
    }

    public V getOrDefault(K key, V defaultValue) {
        if (hasKey(key)) {
            return get(key);
        }
        return defaultValue;
    }

    public void remove(K key) {
        DATA.remove(key);
    }

    public void remove(K key, V value) {
        if (hasKeyValue(key, value)) {
            remove(key);
        }
    }

    public void replace(K key, V value) {
        if (hasKey(key)) {
            set(key, value);
        }
    }

    public void replace(K key, V value, V new_value) {
        if (hasKeyValue(key, value)) {
            set(key, new_value);
        }
    }

    public void replace(K key, V value, K new_key, V new_value) {
        if (hasKeyValue(key, value)) {
            remove(key, value);
            set(key, new_value);
        }
    }

    public boolean hasKey(K key) {
        return DATA.containsKey(key);
    }

    public boolean hasValue(V value) {
        return DATA.containsValue(value);
    }

    public boolean hasKeyValue(K key, V value) {
        return DATA.containsKey(key) && get(key) == value;
    }

    public Set<K> keys() {
        return DATA.keySet();
    }

    public Collection<V> values() {
        return DATA.values();
    }

    public int count(V value) {
        int count = 0;
        for (V i : DATA.values()) {
            if (value == i) {
                count++;
            }
        }
        return count;
    }

    public Set<K> getKeysWithValue(V value) {
        Set<K> keys = new HashSet<>();
        for (KeyValuePair<K, V> kv : this) {
            if (kv.getValue() == value) {
                keys.add(kv.getKey());
            }
        }
        return keys;
    }

    public int size() {
        return DATA.size();
    }

    public void clear() {
        DATA.clear();
    }

    public void forEach(Funcs.MapAction<K, V> action) {
        for (K key : keys()) {
            action.operate(key, get(key));
        }
    }

    public Dictionary<K, V> clone() {
        return new Dictionary<K, V>(DATA);
    }

    public String toString() {
        return DATA.toString();
    }

    public int hashCode() {
        return DATA.hashCode();
    }

    public boolean isEmpty() {
        return DATA.isEmpty();
    }

    public List<KeyValuePair<K, V>> getList() {
        final List<KeyValuePair<K, V>> list = new ArrayList<>();
        for (K key : keys()) {
            list.add(new KeyValuePair<>(key, get(key)));
        }
        return list;
    }

    public Enumerator<KeyValuePair<K, V>> getEnumerator() {
        return new Enumerator<>(getList());
    }

    @Override
    public Iterator<KeyValuePair<K, V>> iterator() {
        return getList().iterator();
    }

}