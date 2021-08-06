package com.example.taskkeeper.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskkeeper.DeleteTaskDialog;
import com.example.taskkeeper.Model.ToDoHeader;
import com.example.taskkeeper.Model.ToDoItem;
import com.example.taskkeeper.Model.ToDoTask;
import com.example.taskkeeper.NewTaskDialog;
import com.example.taskkeeper.R;
import com.example.taskkeeper.Utils.DatabaseHandler;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ToDoItem> todoList;
    private Context context;
    private FragmentManager fragmentManager;

    private DatabaseHandler database;

    public ToDoAdapter(Context context, FragmentManager fragmentManager, DatabaseHandler database){
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.database = database;
    }

    public Context getContext(){
        return context;
    }

    @Override
    public int getItemViewType(int position){
        return todoList.get(position).getType();
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView;
        if(viewType == ToDoItem.TYPE_HEADER){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.header_layout, parent, false);
            return new HeaderViewHolder(itemView);
        }
        else{
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.task_layout, parent, false);
            return new TaskViewHolder(itemView);
        }
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        int type = getItemViewType(position);

        if(type == ToDoItem.TYPE_HEADER){
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            ToDoHeader item = (ToDoHeader) todoList.get(position);
            headerViewHolder.header.setText(item.getHeader());
        } else {
            TaskViewHolder taskViewHolder = (TaskViewHolder) holder;
            database.openDatabase();
            ToDoTask item = (ToDoTask) todoList.get(position);
            taskViewHolder.task.setText(item.getTask());
            taskViewHolder.task.setOnCheckedChangeListener(null);
            taskViewHolder.task.setChecked(toBoolean(item.getStatus()));
            taskViewHolder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        database.updateStatus(item.getId(), 1);
                    }
                    else {
                        database.updateStatus(item.getId(), 0);
                    }
                }
            });
        }
    }

    private boolean toBoolean(int number){
        return number != 0;
    }

    public int getItemCount(){
        return todoList.size();
    }

    public void setTasks(List<ToDoItem> todoList){
        this.todoList = todoList;
        notifyDataSetChanged();
    }

    public void deleteItem(int position){
        ToDoTask item = (ToDoTask) todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());

        DeleteTaskDialog dialog = new DeleteTaskDialog(fragmentManager);
        dialog.setArguments(bundle);
        dialog.show(fragmentManager, "DeleteTaskDialog");

    }

    public void editItem(int position){
        ToDoTask item = (ToDoTask) todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());
        bundle.putString("category", item.getCategory());

        NewTaskDialog dialog = new NewTaskDialog(fragmentManager);
        dialog.setArguments(bundle);
        dialog.show(fragmentManager, "NewTaskDialog");
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView header;

        HeaderViewHolder(View view){
            super(view);
            header = view.findViewById(R.id.todoHeader);
        }
    }

    private static class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox task;

        TaskViewHolder(View view){
            super(view);
            task = view.findViewById(R.id.todoCheckbox);
        }
    }
}
