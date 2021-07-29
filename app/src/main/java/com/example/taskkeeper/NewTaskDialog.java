package com.example.taskkeeper;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.taskkeeper.Model.ToDoModel;
import com.example.taskkeeper.Utils.DatabaseHandler;

public class NewTaskDialog extends DialogFragment {
    private static final String TAG = "NewTaskDialog";

    public interface OnInputSelected{
        void sendInput(String input);
    }
    public OnInputSelected onInputSelected;

    private EditText editNewTask;
    private Button actionOk, actionCancel;
    private DatabaseHandler database;

    public static NewTaskDialog newInstance(){
        return new NewTaskDialog();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_newtask_dialog, container, false);
        actionOk = view.findViewById(R.id.action_ok);
        actionCancel = view.findViewById(R.id.action_cancel);
        editNewTask = view.findViewById(R.id.edit_newtask);

        //!!!
        database = new DatabaseHandler(getActivity());
        database.openDatabase();

        boolean isUpdate = false;
        final Bundle bundle = getArguments();
        if(bundle != null){
            isUpdate = true;
            String task = bundle.getString("task");
            editNewTask.setText(task);
        }
        //!!!

        actionCancel.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing dialog");
                getDialog().dismiss();
            }
        });

        boolean finalIsUpdate = isUpdate;
        actionOk.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: capturing input");
                
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

                    onInputSelected.sendInput(input);
                    getDialog().dismiss();

                }
            }
        });


        return view;

    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            onInputSelected = (OnInputSelected) getTargetFragment();
        } catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException : " + e.getMessage());
        }
    }

}
