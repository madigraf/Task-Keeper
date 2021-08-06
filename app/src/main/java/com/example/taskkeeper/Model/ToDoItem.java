package com.example.taskkeeper.Model;

public abstract class ToDoItem {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_TASK = 1;

    abstract public int getType();
}
