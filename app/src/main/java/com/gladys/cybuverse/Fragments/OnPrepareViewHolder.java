package com.gladys.cybuverse.Fragments;

import androidx.recyclerview.widget.RecyclerView;

public interface OnPrepareViewHolder<T> {

    void onPrepare(RecyclerView.ViewHolder holder, T item, int position);

}

