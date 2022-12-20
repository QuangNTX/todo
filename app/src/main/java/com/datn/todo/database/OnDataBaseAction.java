package com.datn.todo.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.datn.todo.model.Task;

import java.util.List;

@Dao
public interface OnDataBaseAction {

    @Query("SELECT * FROM Task")
    List<Task> getAllTasksList();

    @Insert
    void insertDataIntoTaskList(Task task);

    @Query("DELETE FROM Task WHERE taskId = :taskId")
    void deleteTaskFromId(int taskId);

    @Query("SELECT * FROM Task WHERE taskId = :taskId")
    Task selectDataFromAnId(int taskId);

    @Query("UPDATE Task SET taskTitle = :taskTitle, taskDescription = :taskDescription, date = :taskDate, " +
            "time = :taskTime, isComplete = :isComplete WHERE taskId = :taskId")
    void updateAnExistingRow(int taskId, String taskTitle, String taskDescription, String taskDate, String taskTime, Boolean isComplete);

    @Query("SELECT * FROM Task WHERE taskTitle = :taskTitle AND taskDescription = :taskDescription AND date = :taskDate AND " +
            "time = :taskTime")
    Task getTask(String taskTitle, String taskDescription, String taskDate, String taskTime);
}
