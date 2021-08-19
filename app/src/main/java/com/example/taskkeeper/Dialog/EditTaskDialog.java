package com.example.taskkeeper.Dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.taskkeeper.R;
import com.example.taskkeeper.Utils.DatabaseHandler;
import com.example.taskkeeper.Utils.NullCategory;

import java.util.ArrayList;
import java.util.List;

public class EditTaskDialog extends DialogFragment {

    public static final String TAG = "EditTaskDialog";
    private final FragmentManager fragmentManager;
    private final String fragmentName;

    private AutoCompleteTextView categoryPicker;
    private String pickedCategory;

    private AutoCompleteTextView fragmentPicker;
    private String pickedFragment;

    private EditText editTask;

    private DatabaseHandler database;


    public EditTaskDialog(FragmentManager fragmentManager, String fragmentName){
        this.fragmentManager = fragmentManager;
        this.fragmentName = fragmentName;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_TaskKeeper);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_edittasks, container, false);
        editTask = view.findViewById(R.id.taskname_edittasks);
        categoryPicker = view.findViewById(R.id.category_edittasks);
        fragmentPicker = view.findViewById(R.id.tab_edittasks);
        Button cancelButton = view.findViewById(R.id.cancel_edittasks);
        Button saveButton = view.findViewById(R.id.save_edittasks);
        Button deleteButton = view.findViewById(R.id.delete_edittasks);

        database = new DatabaseHandler(getActivity());
        database.openDatabase();

        List<String> options = database.getAllCategoryNames();
        // we want null as a category, but we need an actual string to represent it,
        // so we add it here
        options.add(0, NullCategory.nullCategory);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(getContext(), R.layout.list_item, options);
        categoryPicker.setAdapter(categoryAdapter);
        categoryPicker.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pickedCategory = (String) parent.getItemAtPosition(position);

            }
        });

        List<String> fragments = new ArrayList<>();
        fragments.add("Today");
        fragments.add("Week");
        fragments.add("Tasks");
        fragments.add("Archive");
        ArrayAdapter<String> fragmentAdapter = new ArrayAdapter<String>(getContext(), R.layout.list_item, fragments);
        fragmentPicker.setAdapter(fragmentAdapter);
        fragmentPicker.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pickedFragment = (String) parent.getItemAtPosition(position);

            }
        });

        final Bundle bundle = getArguments();
        String task = bundle.getString("task");
        editTask.setText(task);
        pickedCategory = bundle.getString("category");
        if(pickedCategory == null){ pickedCategory = NullCategory.nullCategory; }
        categoryPicker.setText(pickedCategory, false);
        pickedFragment = fragmentName;
        fragmentPicker.setText(pickedFragment, false);


        cancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String input = editTask.getText().toString();
                if(!input.equals("")){
                    // this means there is input

                    String text = editTask.getText().toString();
                    if(pickedCategory.equals(NullCategory.nullCategory)) { pickedCategory = null; }

                    database.updateTask(bundle.getInt("id"), text);
                    database.updateTaskCategory(bundle.getInt("id"), pickedCategory);
                    database.updateFragment(bundle.getInt("id"), pickedFragment);

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
        super.onDismiss(dialog);
        fragmentManager.setFragmentResult(TAG, new Bundle());
    }
}
