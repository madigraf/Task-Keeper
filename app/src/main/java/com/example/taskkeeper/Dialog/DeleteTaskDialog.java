package com.example.taskkeeper.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.taskkeeper.Utils.DatabaseHandler;

public class DeleteTaskDialog extends DialogFragment {
    private static final String TAG = "DeleteTaskDialog";

    private FragmentManager fragmentManager;
    private DatabaseHandler database;

    public DeleteTaskDialog (FragmentManager fragmentManager){
        this.fragmentManager = fragmentManager;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Task");

        database = new DatabaseHandler(getActivity());
        database.openDatabase();

        builder.setMessage("Are you sure you want to delete this task?");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final Bundle bundle = getArguments();
                database.deleteTask(bundle.getInt("id"));
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
        fragmentManager.setFragmentResult("DeleteTaskDialog", new Bundle());
    }
}
