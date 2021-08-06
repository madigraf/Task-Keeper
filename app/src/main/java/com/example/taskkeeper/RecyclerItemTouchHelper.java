package com.example.taskkeeper;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskkeeper.Adapter.ToDoAdapter;

public class RecyclerItemTouchHelper  extends ItemTouchHelper.SimpleCallback {

    private ToDoAdapter adapter;

    public RecyclerItemTouchHelper(ToDoAdapter adapter){
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
    }

    @Override
    public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        // this prevents the headers from being swiped
        if (viewHolder instanceof ToDoAdapter.HeaderViewHolder) return 0;
        return super.getSwipeDirs(recyclerView, viewHolder);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction){
        final int position = viewHolder.getBindingAdapterPosition();
        if(direction == ItemTouchHelper.RIGHT){
            adapter.deleteItem(position);
        }
        else{
            adapter.editItem(position);
        }
    }

}
