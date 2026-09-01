package com.example.TaskMate.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.lifecycle.LiveData;

import com.example.TaskMate.Model.Task;

import java.util.List;

@Dao
public interface TaskDao {

    // 插入新任务
    @Insert
    void insert(Task task);

    // 更新任务
    @Update
    void update(Task task);

    // 删除任务
    @Delete
    void delete(Task task);

    // 获取所有任务，按用户ID
    @Query("SELECT * FROM task_table WHERE userId = :userId ORDER BY dueDate ASC")
    LiveData<List<Task>> getTasksByUserId(int userId);  // 返回特定用户的任务列表

    // 获取所有任务（如果你需要管理员查看所有任务的功能，可以保留）
    @Query("SELECT * FROM task_table ORDER BY dueDate DESC")
    LiveData<List<Task>> getAllTasks();  // 返回 LiveData 类型的结果
    @Query("SELECT MAX(CAST(SUBSTR(taskId, LENGTH(:userId) + 1) AS INTEGER)) FROM task_table WHERE userId = :userId")
    int getMaxSequenceForUser(int userId);  // 查询当前用户的最大序号

}
