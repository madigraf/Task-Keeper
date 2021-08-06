package com.example.taskkeeper.Model;

public class ToDoHeader extends ToDoItem{
    private String header;

    @Override
    public int getType() {
        return TYPE_HEADER;
    }

    public ToDoHeader(String header){
        this.header = header;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }
}
