package com.gladys.cybuverse.Utils.GeneralUtils.collections;


import androidx.annotation.NonNull;

import static com.gladys.cybuverse.Utils.GeneralUtils.Funcs.format;

public class Variable<V> {
    private String KEY;
    private V VALUE;

    public Variable() {
    }

    public Variable(V value) {
        setValue(value);
    }

    public Variable(String name, V value) {
        setName(name);
        setValue(value);
    }

    public String getName() {
        return KEY;
    }

    public void setName(String key) {
        KEY = key;
    }

    public V getValue() {
        return VALUE;
    }

    public void setValue(V value) {
        VALUE = value;
    }

    public void reset() {
        VALUE = null;
    }

    @NonNull
    public String toString() {
        return format("Variable<<> : <>>", getName(), getValue());
    }

}