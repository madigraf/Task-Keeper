package com.example.taskkeeper.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskkeeper.Adapter.ToDoAdapter;
import com.example.taskkeeper.Dialog.NewTaskDialog;
import com.example.taskkeeper.Model.ToDoItem;
import com.example.taskkeeper.Model.ToDoTask;
import com.example.taskkeeper.R;
import com.example.taskkeeper.RecyclerItemTouchHelper;
import com.example.taskkeeper.Utils.DatabaseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public abstract class TaskFragment extends Fragment {

    private RecyclerView recyclerView;
    private ToDoAdapter adapter;
    private DatabaseHandler database;
    private List<ToDoItem> taskList;

    private FloatingActionButton newTaskButton;

    private final String fragmentName;

    protected TaskFragment(String fragmentName) {
        this.fragmentName = fragmentName;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maintasks, container, false);

        database = new DatabaseHandler(view.getContext());
        database.openDatabase();

        newTaskButton = view.findViewById(R.id.maintasksFAB);

        taskList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.maintasksRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapter = new ToDoAdapter(view.getContext(), getParentFragmentManager(), fragmentName, database);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        taskList = database.getFragmentTasksWithHeaders(fragmentName);
        //Collections.reverse(maintaskList);
        adapter.setTasks(taskList);


        // update list of tasks after NewTaskDialog closes
        getParentFragmentManager().setFragmentResultListener("NewTaskDialog", TaskFragment.this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                refreshList();
            }
        });

        getParentFragmentManager().setFragmentResultListener("DeleteTaskDialog", TaskFragment.this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                refreshList();
            }
        });

        getParentFragmentManager().setFragmentResultListener("EditTaskDialog", TaskFragment.this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                refreshList();
            }
        });

        newTaskButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                NewTaskDialog dialog = new NewTaskDialog(getParentFragmentManager(), fragmentName);
                dialog.show(getParentFragmentManager(), "NewTaskDialog");

            }
        });

        return view;

    }

    public void prune(){
        refreshList();

        for (ToDoItem toDoItem: taskList) {
            if(toDoItem.getType() == ToDoItem.TYPE_TASK){
                ToDoTask toDoTask = (ToDoTask) toDoItem;
                if(toDoTask.getStatus() == 1){
                    database.deleteTask(toDoTask.getId());
                }
            }
        }

        refreshList();
    }

    public void refreshList(){
        taskList = database.getFragmentTasksWithHeaders(fragmentName);
        //Collections.reverse(maintaskList);
        adapter.setTasks(taskList);
        adapter.notifyDataSetChanged();
    }
}
