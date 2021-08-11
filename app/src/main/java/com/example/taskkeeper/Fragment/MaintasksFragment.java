package com.example.taskkeeper.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.taskkeeper.Adapter.ToDoAdapter;
import com.example.taskkeeper.Model.ToDoItem;
import com.example.taskkeeper.Model.ToDoTask;
import com.example.taskkeeper.Dialog.NewTaskDialog;
import com.example.taskkeeper.R;
import com.example.taskkeeper.RecyclerItemTouchHelper;
import com.example.taskkeeper.Utils.DatabaseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MaintasksFragment extends TaskFragment {

    private static final String TAG = "MaintasksFragment";

    public MaintasksFragment() {
        super("Tasks");
    }
}