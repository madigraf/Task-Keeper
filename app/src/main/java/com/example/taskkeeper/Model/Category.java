package com.example.taskkeeper.Model;

import androidx.annotation.Nullable;

public class Category {
    private int id;
    private String category;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object obj) {
        super.equals(obj);
        if(obj == this){
            return true;
        }

        if(!(obj instanceof Category)){
            return false;
        }

        Category category = (Category) obj;

        return category.getId() == this.id;
    }
}
