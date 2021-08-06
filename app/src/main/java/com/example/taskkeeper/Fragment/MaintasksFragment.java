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
import com.example.taskkeeper.NewTaskDialog;
import com.example.taskkeeper.R;
import com.example.taskkeeper.RecyclerItemTouchHelper;
import com.example.taskkeeper.Utils.DatabaseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MaintasksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MaintasksFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "MaintasksFragment";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView maintasksRecyclerView;
    private ToDoAdapter maintasksAdapter;
    private DatabaseHandler database;
    private List<ToDoItem> maintaskList;
    public FloatingActionButton newTaskButton;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MaintasksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MaintasksFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MaintasksFragment newInstance(String param1, String param2) {
        MaintasksFragment fragment = new MaintasksFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_maintasks, container, false);

        database = new DatabaseHandler(view.getContext());
        database.openDatabase();

        newTaskButton = view.findViewById(R.id.maintasksFAB);

        maintaskList = new ArrayList<>();

        maintasksRecyclerView = view.findViewById(R.id.maintasksRecyclerView);
        maintasksRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        maintasksAdapter = new ToDoAdapter(view.getContext(), getParentFragmentManager(), database);
        maintasksRecyclerView.setAdapter(maintasksAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(maintasksAdapter));
        itemTouchHelper.attachToRecyclerView(maintasksRecyclerView);

        maintaskList = database.getAllTasksWithHeaders();
        //Collections.reverse(maintaskList);
        maintasksAdapter.setTasks(maintaskList);


        // update list of tasks after NewTaskDialog closes
        getParentFragmentManager().setFragmentResultListener("NewTaskDialog", MaintasksFragment.this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                maintaskList = database.getAllTasksWithHeaders();
                //Collections.reverse(maintaskList);
                maintasksAdapter.setTasks(maintaskList);
                maintasksAdapter.notifyDataSetChanged();
            }
        });

        getParentFragmentManager().setFragmentResultListener("DeleteTaskDialog", MaintasksFragment.this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                maintaskList = database.getAllTasksWithHeaders();
                //Collections.reverse(maintaskList);
                maintasksAdapter.setTasks(maintaskList);
                maintasksAdapter.notifyDataSetChanged();
            }
        });

        newTaskButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: opening dialog");
                NewTaskDialog dialog = new NewTaskDialog(getParentFragmentManager());
                dialog.show(getParentFragmentManager(), "NewTaskDialog");

            }
        });

        return view;
    }
}