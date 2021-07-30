package com.example.taskkeeper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.taskkeeper.Model.ToDoModel;
import com.example.taskkeeper.Utils.DatabaseHandler;

public class NewTaskDialog extends DialogFragment {
    private static final String TAG = "NewTaskDialog";

    private FragmentManager fragmentManager;

    private EditText editNewTask;
    private DatabaseHandler database;

    public NewTaskDialog (FragmentManager fragmentManager){
        this.fragmentManager = fragmentManager;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("New Task");

        editNewTask = new EditText(getContext());
        editNewTask.setHint("Enter a task");
        builder.setView(editNewTask);

        database = new DatabaseHandler(getActivity());
        database.openDatabase();

        boolean isUpdate = false;
        final Bundle bundle = getArguments();
        if(bundle != null){
            isUpdate = true;
            String task = bundle.getString("task");
            editNewTask.setText(task);
        }

        boolean finalIsUpdate = isUpdate;
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String input = editNewTask.getText().toString();
                if(!input.equals("")){
                    // this means there is input

                    String text = editNewTask.getText().toString();
                    if(finalIsUpdate){
                        database.updateTask(bundle.getInt("id"), text);

                    }
                    else {
                        ToDoModel task = new ToDoModel();
                        task.setTask(text);
                        task.setStatus(0);
                        database.insertTask(task);
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
