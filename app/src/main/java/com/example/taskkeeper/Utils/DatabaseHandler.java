package com.example.taskkeeper.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.taskkeeper.Model.Category;
import com.example.taskkeeper.Model.ToDoHeader;
import com.example.taskkeeper.Model.ToDoItem;
import com.example.taskkeeper.Model.ToDoTask;
import com.example.taskkeeper.R;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String NAME = "taskKeeperDatabase";
    private static final String MAINTASKS_TABLE = "maintasks";
    private static final String ID = "id";
    private static final String TASK = "task";
    private static final String STATUS = "status";
    private static final String CATEGORY = "category";
    private static final String CATEGORY_TABLE = "category";
    private static final String FRAGMENT = "fragment";
    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + MAINTASKS_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TASK + " TEXT, "
            + STATUS + " INTEGER, " + CATEGORY + " TEXT, " + FRAGMENT + " TEXT);";

    private static final String CREATE_CATEGORY_TABLE = "CREATE TABLE " + CATEGORY_TABLE + "("  + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CATEGORY + " TEXT);";

    private final String null_category = "Untagged";

    private SQLiteDatabase database;

    public DatabaseHandler(Context context){
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database){
        database.execSQL(CREATE_TODO_TABLE);
        database.execSQL(CREATE_CATEGORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){
        // Drop the older tables
        database.execSQL("DROP TABLE IF EXISTS " + MAINTASKS_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE);
        // Create tables again
        onCreate(database);
    }

    public void openDatabase(){
        database = this.getWritableDatabase();
    }

    public void insertTask(ToDoTask task, String fragmentName){
        ContentValues cv = new ContentValues();
        cv.put(TASK, task.getTask());
        cv.put(STATUS, 0);
        cv.put(CATEGORY, task.getCategory());
        cv.put(FRAGMENT, fragmentName);
        database.insert(MAINTASKS_TABLE, null, cv);
    }

    public List<ToDoItem> getFragmentTasks(String fragmentName){
        return getFragmentTasksHelper(null, fragmentName);
    }

    public List<ToDoItem> getFragmentTasksWithHeaders(String fragmentName){
        List<ToDoItem> databaseList = getFragmentTasksHelper(CATEGORY, fragmentName);
        if(databaseList.size() == 0){
            return databaseList;
        }



        List<ToDoItem> taskList = new ArrayList<>();
        String currentCategory = "";
        for(int i = 0; i < databaseList.size(); i++){
            ToDoTask task = (ToDoTask) databaseList.get(i);
            if(!currentCategory.equals(null_category) && task.getCategory() == null){
                taskList.add(new ToDoHeader(null_category));
                currentCategory = null_category;
            } else if(task.getCategory() != null && !currentCategory.equals(task.getCategory())){
                taskList.add(new ToDoHeader(task.getCategory()));
                currentCategory = task.getCategory();
            }
            taskList.add(task);
        }

        return taskList;
    }

    private boolean shouldAddHeader(String currentCategory, String taskCategory){
        if(currentCategory.equals("")){

        }

        if(currentCategory.equals(null_category)){

        }


        return true;
    }

    private List<ToDoItem> getFragmentTasksHelper(String orderBy, String fragmentName){
        List<ToDoItem> taskList = new ArrayList<>();
        Cursor cur = null;
        database.beginTransaction();
        try{
            cur = database.query(MAINTASKS_TABLE, null, FRAGMENT + " =?", new String[]{fragmentName}, null, null, orderBy, null);
            if(cur != null){
                if(cur.moveToFirst()){
                    do{
                        ToDoTask task = new ToDoTask();
                        task.setId(cur.getInt(cur.getColumnIndex(ID)));
                        task.setTask(cur.getString(cur.getColumnIndex(TASK)));
                        task.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
                        task.setCategory(cur.getString(cur.getColumnIndex(CATEGORY)));
                        taskList.add(task);
                    } while (cur.moveToNext());
                }
            }
        }
        finally {
            database.endTransaction();
            cur.close();
        }
        return taskList;
    }

    public List<String> getAllCategoriesInUse(){
        List<String> categoryList = new ArrayList<>();
        Cursor cur = null;
        database.beginTransaction();
        try{
            cur = database.query(true, MAINTASKS_TABLE, new String[]{CATEGORY}, null, null, null, null, null, null);
            if(cur != null){
                if(cur.moveToFirst()){
                    do{
                        String string = cur.getString(cur.getColumnIndex(CATEGORY));
                        categoryList.add(string);

                    } while (cur.moveToNext());
                }
            }
        }
        finally {
            database.endTransaction();
            cur.close();
        }
        return categoryList;
    }

    public boolean isCategoryInUse(String category){
        return getAllCategoriesInUse().contains(category);
    }

    public void updateStatus(int id, int status){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        database.update(MAINTASKS_TABLE, cv, ID + "=?", new String[]{String.valueOf(id)});
    }

    public void updateTask(int id, String task){
        ContentValues cv = new ContentValues();
        cv.put(TASK, task);
        database.update(MAINTASKS_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void updateTaskCategory(int id, String category){
        ContentValues cv = new ContentValues();
        cv.put(CATEGORY, category);
        database.update(MAINTASKS_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void deleteTask(int id){
        database.delete(MAINTASKS_TABLE, ID + "=?", new String[] {String.valueOf(id)});
    }

    public void insertCategory(String category){
        ContentValues cv = new ContentValues();
        cv.put(CATEGORY, category);
        database.insert(CATEGORY_TABLE, null, cv);
    }

    public List<Category> getALlCategories(){
        List<Category> categoryList = new ArrayList<>();
        Cursor cur = null;
        database.beginTransaction();
        try{
            cur = database.query(CATEGORY_TABLE, null, null, null, null, null, null, null);
            if(cur != null){
                if(cur.moveToFirst()){
                    do{
                        Category category = new Category();
                        category.setId(cur.getInt(cur.getColumnIndex(ID)));
                        category.setCategory(cur.getString(cur.getColumnIndex(CATEGORY)));
                        categoryList.add(category);
                    } while (cur.moveToNext());
                }
            }
        }
        finally {
            database.endTransaction();
            cur.close();
        }
        return categoryList;
    }

    public List<String> getAllCategoryNames(){
        List<String> categoryList = new ArrayList<>();
        Cursor cur = null;
        database.beginTransaction();
        try{
            cur = database.query(CATEGORY_TABLE, new String[]{CATEGORY}, null, null, null, null, null, null);
            if(cur != null){
                if(cur.moveToFirst()){
                    do{
                        categoryList.add(cur.getString(cur.getColumnIndex(CATEGORY)));
                    } while (cur.moveToNext());
                }
            }
        }
        finally {
            database.endTransaction();
            cur.close();
        }
        return categoryList;
    }

    public void updateCategory(int id, String category){
        ContentValues cv = new ContentValues();
        cv.put(CATEGORY, category);
        database.update(CATEGORY_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void updateCategoryName(String oldCategoryName, String newCategoryName){
        ContentValues cv = new ContentValues();
        cv.put(CATEGORY, newCategoryName);
        database.update(MAINTASKS_TABLE, cv, CATEGORY + "= ?", new String[] {String.valueOf(oldCategoryName)});
    }

    public void deleteCategory(int id){
        database.delete(CATEGORY_TABLE, ID + "=?", new String[] {String.valueOf(id)});
    }
}
