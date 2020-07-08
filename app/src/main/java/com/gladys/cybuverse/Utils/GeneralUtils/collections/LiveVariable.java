package com.gladys.cybuverse.Utils.GeneralUtils.collections;


import androidx.annotation.NonNull;

import static com.gladys.cybuverse.Utils.GeneralUtils.Funcs.format;

public class LiveVariable<V> extends Variable<V> {

    private OnValueTouchedListener<V> onValueTouchedListener;

    LiveVariable() {
    }

    LiveVariable(OnValueTouchedListener<V> valueTouchedListener) {
        this.onValueTouchedListener = valueTouchedListener;
    }

    @Override
    public V getValue() {
        V value = super.getValue();
        if (onValueTouchedListener != null)
            onValueTouchedListener.onGet(value);
        return value;
    }

    @Override
    public void setValue(V value) {
        if (onValueTouchedListener != null)
            onValueTouchedListener.onSet(value);
        super.setValue(value);
    }

    public OnValueTouchedListener getOnValueTouchedListener() {
        return onValueTouchedListener;
    }

    public void setOnValueTouchedListener(OnValueTouchedListener<V> onValueTouchedListener) {
        this.onValueTouchedListener = onValueTouchedListener;
    }

    @NonNull
    public String toString() {
        return format("LiveVariable<<> : <>>", getName(), getValue());
    }

    public interface OnValueTouchedListener<V> {
        void onSet(V value);

        void onGet(V value);
    }

}