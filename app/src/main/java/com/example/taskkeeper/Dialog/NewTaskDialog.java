package com.example.taskkeeper.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.taskkeeper.Model.ToDoTask;
import com.example.taskkeeper.R;
import com.example.taskkeeper.Utils.DatabaseHandler;
import com.example.taskkeeper.Utils.NullCategory;

import java.util.List;

public class NewTaskDialog extends DialogFragment {
    public static final String TAG = "NewTaskDialog";

    private final FragmentManager fragmentManager;
    private final String fragmentName;

    private EditText editNewTask;

    private AutoCompleteTextView categoryPicker;
    private String pickedCategory;

    private DatabaseHandler database;

    public NewTaskDialog (FragmentManager fragmentManager, String fragmentName){
        this.fragmentManager = fragmentManager;
        this.fragmentName = fragmentName;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("New Task");
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_newtasks, null);
        builder.setView(view);

        editNewTask = view.findViewById(R.id.edittext_newtask);
        categoryPicker = view.findViewById(R.id.autocomplete_newtask);

        database = new DatabaseHandler(getActivity());
        database.openDatabase();

        List<String> options = database.getAllCategoryNames();
        // we want null as a category, but we need an actual string to represent it,
        // so we add it here
        options.add(0, NullCategory.nullCategory);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.list_item, options);
        categoryPicker.setAdapter(adapter);
        pickedCategory = NullCategory.nullCategory;
        categoryPicker.setText(pickedCategory, false);
        categoryPicker.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pickedCategory = (String) parent.getItemAtPosition(position);

            }
        });

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String input = editNewTask.getText().toString();
                if(!input.equals("")){
                    // this means there is input

                    String text = editNewTask.getText().toString();
                    if(pickedCategory.equals(NullCategory.nullCategory)) { pickedCategory = null; }

                    ToDoTask task = new ToDoTask();
                    task.setTask(text);
                    task.setStatus(0);
                    task.setCategory(pickedCategory);
                    database.insertTask(task, fragmentName);

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
        fragmentManager.setFragmentResult(TAG, new Bundle());
    }

}
