package com.example.taskkeeper.Adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskkeeper.DeleteTaskDialog;
import com.example.taskkeeper.MainActivity;
import com.example.taskkeeper.Model.ToDoModel;
import com.example.taskkeeper.NewTaskDialog;
import com.example.taskkeeper.R;
import com.example.taskkeeper.Utils.DatabaseHandler;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private List<ToDoModel> todoList;
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

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(itemView);
    }

    public void onBindViewHolder(ViewHolder holder, int position){
        database.openDatabase();
        ToDoModel item = todoList.get(position);
        holder.task.setText(item.getTask());
        holder.task.setOnCheckedChangeListener(null);
        holder.task.setChecked(toBoolean(item.getStatus()));
        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

    private boolean toBoolean(int number){
        return number != 0;
    }

    public int getItemCount(){
        return todoList.size();
    }

    public void setTasks(List<ToDoModel> todoList){
        this.todoList = todoList;
        notifyDataSetChanged();
    }

    public void deleteItem(int position){
        ToDoModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());

        DeleteTaskDialog dialog = new DeleteTaskDialog(fragmentManager);
        dialog.setArguments(bundle);
        dialog.show(fragmentManager, "DeleteTaskDialog");

    }

    public void editItem(int position){
        ToDoModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());

        NewTaskDialog dialog = new NewTaskDialog(fragmentManager);
        dialog.setArguments(bundle);
        dialog.show(fragmentManager, "NewTaskDialog");
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox task;

        ViewHolder(View view){
            super(view);
            task = view.findViewById(R.id.todoCheckbox);
        }
    }
}
