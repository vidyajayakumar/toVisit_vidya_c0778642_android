package com.vidya.toVisit_vidya_c0778642_android;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public
class SwipeToDelete extends ItemTouchHelper.SimpleCallback {

    recyclerAdapter adapter;

    public
    SwipeToDelete(int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
    }

    public
    SwipeToDelete(recyclerAdapter adapter) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT);
        this.adapter = adapter;
    }

    @Override
    public
    boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public
    void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.deleteFav(viewHolder.getAdapterPosition());
    }
}