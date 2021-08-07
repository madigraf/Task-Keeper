package com.example.taskkeeper.Dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.taskkeeper.Model.ToDoTask;
import com.example.taskkeeper.R;
import com.example.taskkeeper.Utils.DatabaseHandler;

import java.util.List;

public class EditTaskDialog extends DialogFragment {

    private static final String TAG = "EditTaskDialog";
    private FragmentManager fragmentManager;
    private String fragmentName;

    private Spinner spinner;
    private EditText editTask;
    private Button cancelButton;
    private Button saveButton;
    private Button deleteButton;

    private String null_category;

    private DatabaseHandler database;


    public EditTaskDialog(FragmentManager fragmentManager, String fragmentName){
        this.fragmentManager = fragmentManager;
        this.fragmentName = fragmentName;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.EditTask);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_edittasks, container, false);
        null_category = getString(R.string.null_category);
        editTask = view.findViewById(R.id.taskname_edittasks);
        spinner = view.findViewById(R.id.category_edittasks);
        cancelButton = view.findViewById(R.id.cancel_edittasks);
        saveButton = view.findViewById(R.id.save_edittasks);
        deleteButton = view.findViewById(R.id.delete_edittasks);

        database = new DatabaseHandler(getActivity());
        database.openDatabase();

        List<String> options = database.getAllCategories();
        // we want null as a category, but we need an actual string to represent it,
        // so we replace/add it here
        if(options.contains(null)){
            options.set(options.indexOf(null), null_category);
        }
        else {
            options.add(0, null_category);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, options);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        boolean isUpdate = false;
        final Bundle bundle = getArguments();
        if(bundle != null){
            isUpdate = true;
            String task = bundle.getString("task");
            editTask.setText(task);
            String category = bundle.getString("category");
            if(category == null){ category = null_category; }
            spinner.setSelection(options.indexOf(category));
        }

        cancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        boolean finalIsUpdate = isUpdate;
        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String input = editTask.getText().toString();
                if(!input.equals("")){
                    // this means there is input

                    String text = editTask.getText().toString();
                    String category = spinner.getSelectedItem().toString();
                    if(category.equals(null_category)) { category = null; }

                    if(finalIsUpdate){
                        database.updateTask(bundle.getInt("id"), text);
                        database.updateCategory(bundle.getInt("id"), category);

                    }
                    else {
                        ToDoTask task = new ToDoTask();
                        task.setTask(text);
                        task.setStatus(0);
                        task.setCategory(category);
                        database.insertTask(task, fragmentName);
                    }
                    dismiss();
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final Bundle bundle = getArguments();
                database.deleteTask(bundle.getInt("id"));
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog){
        fragmentManager.setFragmentResult("EditTaskDialog", new Bundle());
    }
}
