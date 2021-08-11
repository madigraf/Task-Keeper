package com.example.taskkeeper.Adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskkeeper.Model.Category;
import com.example.taskkeeper.R;
import com.example.taskkeeper.Utils.DatabaseHandler;

import java.util.List;

import static android.content.ContentValues.TAG;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categoryList;
    private DatabaseHandler database;

    public CategoryAdapter(DatabaseHandler database){
        this.database = database;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_layout, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        database.openDatabase();

        holder.editText.setTag(categoryList.get(position).getId());
        holder.editText.setText(categoryList.get(position).getCategory());
        holder.deleteCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!database.isCategoryInUse(categoryList.get(position).getCategory())){
                    deleteItem(position);
                    setCategories(database.getALlCategories());
                    notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void setCategories(List<Category> categoryList){
        this.categoryList = categoryList;
        notifyDataSetChanged();
    }

    public void deleteItem(int position){
        database.deleteCategory(categoryList.get(position).getId());
    }

    public void updateItem(int position, String name){
        Category category = categoryList.get(position);
        database.updateCategory(category.getId(), name);
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder{
        ImageButton deleteCategoryButton;
        EditText editText;
        DatabaseHandler database;

        CategoryViewHolder(View view){
            super(view);
            database = new DatabaseHandler(view.getContext());
            deleteCategoryButton = view.findViewById(R.id.button_category);
            editText = view.findViewById(R.id.editText_category);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    int id = (int) editText.getTag();
                    String name = editText.getText().toString();
                    database.openDatabase();
                    database.updateCategory(id, name);
                }
            });
        }
    }
}

