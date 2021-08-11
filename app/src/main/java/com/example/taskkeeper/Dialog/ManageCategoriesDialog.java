package com.example.taskkeeper.Dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskkeeper.Adapter.CategoryAdapter;
import com.example.taskkeeper.Model.Category;
import com.example.taskkeeper.R;
import com.example.taskkeeper.Utils.DatabaseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ManageCategoriesDialog extends DialogFragment {
    private RecyclerView categoriesRecyclerView;
    private CategoryAdapter categoriesAdapter;
    private DatabaseHandler database;
    private List<Category> categoryList;
    private List<Category> beforeModificationsList;
    private FloatingActionButton newCategoryButton;
    private Button doneButton;

    public static final String TAG = "ManageCategoriesDialog";

    private String null_category;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_categories, container, false);
        null_category = "Untagged";
        newCategoryButton = view.findViewById(R.id.fab_categories);
        doneButton = view.findViewById(R.id.done_button_categories);
        categoryList = new ArrayList<>();

        database = new DatabaseHandler(getActivity());
        database.openDatabase();

        categoriesRecyclerView = view.findViewById(R.id.categoriesRecyclerView);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        categoriesAdapter = new CategoryAdapter(database);
        categoriesRecyclerView.setAdapter(categoriesAdapter);

        beforeModificationsList = database.getALlCategories();
        categoryList = database.getALlCategories();
        categoriesAdapter.setCategories(categoryList);

        newCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // need to add a new field to the recyclerView
                database.insertCategory("");
                refreshList();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeIllegalCategories();
                updateTaskCategories();
                dismiss();
            }
        });

        return view;
    }

    private void refreshList(){
        categoryList = database.getALlCategories();
        categoriesAdapter.setCategories(categoryList);
        categoriesAdapter.notifyDataSetChanged();
    }

    // Illegal categories include: only whitespace, duplicate of other categories, including null_category
    private void removeIllegalCategories(){
        categoryList = database.getALlCategories();
        // first, revert or remove empty strings, or categories with the same name as the null category
        // reversion is only possible if they were not created during this session
        for (Category category: categoryList) {
            if(category.getCategory().trim().length() == 0 || category.getCategory().equals(null_category)){
                // no string content, revert it or delete it from database
                if(beforeModificationsList.contains(category)){
                    Category reversion = beforeModificationsList.get(beforeModificationsList.indexOf(category));
                    database.updateCategory(reversion.getId(), reversion.getCategory());
                } else {
                    database.deleteCategory(category.getId());
                }
            }
        }

        // determine duplicates using sets, since sets return false if a duplicate is added
        categoryList = database.getALlCategories();
        Set<String> helperSet = new HashSet<>();
        Set<String> duplicates = new HashSet<>();
        for (Category category: categoryList) {
            if(!helperSet.add(category.getCategory())){
                duplicates.add(category.getCategory());
            }
        }

        for (Category category: categoryList) {
            // if duplicate, revert it or delete from database
            if(duplicates.contains(category.getCategory())){
                if(beforeModificationsList.contains(category)){
                    Category reversion = beforeModificationsList.get(beforeModificationsList.indexOf(category));
                    database.updateCategory(reversion.getId(), reversion.getCategory());
                } else {
                    database.deleteCategory(category.getId());
                }
            }
        }

    }

    private void updateTaskCategories(){
        categoryList = database.getALlCategories();

        for (Category beforeCategory: beforeModificationsList) {
            // if same ID is in the new categoryList, we need to check if there has been a change
            // to category name
            if(categoryList.contains(beforeCategory)){
                Category afterCategory = categoryList.get(categoryList.indexOf(beforeCategory));
                if(!afterCategory.getCategory().equals(beforeCategory.getCategory())){
                    // means there has been a change
                    database.updateCategoryName(beforeCategory.getCategory(), afterCategory.getCategory());
                }

            }
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        getParentFragmentManager().setFragmentResult(TAG, new Bundle());
    }
}
