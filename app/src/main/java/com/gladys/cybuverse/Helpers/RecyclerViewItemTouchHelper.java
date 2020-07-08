package com.gladys.cybuverse.Helpers;

import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private RecyclerViewItemTouchListener listener;

    public RecyclerViewItemTouchHelper(int dragDirs, int swipeDirs, RecyclerViewItemTouchListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (listener != null) {
            listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
        }
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        View foreGround = viewHolder.itemView;
        getDefaultUIUtil().clearView(foreGround);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        View foreGround = recyclerView.getRootView();
        getDefaultUIUtil().clearView(foreGround);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View foreGround = viewHolder.itemView;
        getDefaultUIUtil().onDraw(c, recyclerView, foreGround, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View foreGround = viewHolder.itemView;
        getDefaultUIUtil().onDrawOver(c, recyclerView, foreGround, dX, dY, actionState, isCurrentlyActive);
    }

    public static interface RecyclerViewItemTouchListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }

}
