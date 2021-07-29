package com.example.taskkeeper.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.taskkeeper.Adapter.ToDoAdapter;
import com.example.taskkeeper.Model.ToDoModel;
import com.example.taskkeeper.NewTaskDialog;
import com.example.taskkeeper.R;
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
public class MaintasksFragment extends Fragment implements NewTaskDialog.OnInputSelected {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "MaintasksFragment";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView maintasksRecyclerView;
    private ToDoAdapter maintasksAdapter;

    private DatabaseHandler database;

    private List<ToDoModel> maintaskList;

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
        maintasksAdapter = new ToDoAdapter(database);
        maintasksRecyclerView.setAdapter(maintasksAdapter);

        maintaskList = database.getAllTasks();
        Collections.reverse(maintaskList);
        maintasksAdapter.setTasks(maintaskList);



        newTaskButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: opening dialog");

                NewTaskDialog dialog = new NewTaskDialog();
                dialog.setTargetFragment(MaintasksFragment.this, 1);
                dialog.show(getFragmentManager(), "NewTaskDialog");
            }
        });

        return view;
    }

    @Override
    public void sendInput(String input) {
        Log.d(TAG, "sendInput: found incoming input: " + input);

        maintaskList = database.getAllTasks();
        Collections.reverse(maintaskList);
        maintasksAdapter.setTasks(maintaskList);
        maintasksAdapter.notifyDataSetChanged();

    }
}