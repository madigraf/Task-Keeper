package com.example.taskkeeper.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.taskkeeper.Model.ToDoTask;
import com.example.taskkeeper.R;
import com.example.taskkeeper.Utils.DatabaseHandler;

import java.util.List;

public class NewTaskDialog extends DialogFragment {
    private static final String TAG = "NewTaskDialog";

    private FragmentManager fragmentManager;
    private String fragmentName;

    private EditText editNewTask;
    private Spinner spinner;
    private DatabaseHandler database;

    private String null_category;

    public NewTaskDialog (FragmentManager fragmentManager, String fragmentName){
        this.fragmentManager = fragmentManager;
        this.fragmentName = fragmentName;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        null_category = getString(R.string.null_category);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("New Task");

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_newtasks, null);
        builder.setView(view);

        editNewTask = view.findViewById(R.id.edittext_newtask);
        spinner = view.findViewById(R.id.spinner_newtask);

        database = new DatabaseHandler(getActivity());
        database.openDatabase();

        List<String> options = database.getAllCategoryNames();
        // we want null as a category, but we need an actual string to represent it,
        // so we add it here
        options.add(0, null_category);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, options);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        boolean isUpdate = false;
        final Bundle bundle = getArguments();
        if(bundle != null){
            isUpdate = true;
            String task = bundle.getString("task");
            editNewTask.setText(task);
            String category = bundle.getString("category");
            if(category == null){ category = null_category; }
            spinner.setSelection(options.indexOf(category));
        }

        boolean finalIsUpdate = isUpdate;
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String input = editNewTask.getText().toString();
                if(!input.equals("")){
                    // this means there is input

                    String text = editNewTask.getText().toString();
                    String category = spinner.getSelectedItem().toString();
                    if(category.equals(null_category)) { category = null; }

                    if(finalIsUpdate){
                        database.updateTask(bundle.getInt("id"), text);
                        database.updateTaskCategory(bundle.getInt("id"), category);

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
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog){
        fragmentManager.setFragmentResult("NewTaskDialog", new Bundle());
    }

}
