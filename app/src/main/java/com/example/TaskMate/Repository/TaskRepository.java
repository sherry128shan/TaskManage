package com.example.TaskMate.Repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.TaskMate.Model.Task;
import com.example.TaskMate.database.AppDatabase;
import com.example.TaskMate.database.TaskDao;

import java.util.List;

public class TaskRepository {

    private TaskDao taskDao;
    private LiveData<List<Task>> allTasks;

    public TaskRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        taskDao = db.taskDao();
        allTasks = taskDao.getAllTasks();  // 返回 LiveData，而不是 List
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }
    public LiveData<List<Task>> getTasksForUser(int userId) {
        return taskDao.getTasksByUserId(userId);
    }

    public void insert(Task task) {
        AppDatabase.databaseWriteExecutor.execute(() -> taskDao.insert(task));
    }

    public void update(Task task) {
        AppDatabase.databaseWriteExecutor.execute(() -> taskDao.update(task));
    }

    public void delete(Task task) {
        AppDatabase.databaseWriteExecutor.execute(() -> taskDao.delete(task));
    }

    // 生成 taskId，格式为 UserID + 递增序号
    public int generateTaskId(int userId) {
        int maxSequence = getMaxTaskSequence(userId);
        // 如果没有任务，则从 1 开始
        if (maxSequence == -1) {
            maxSequence = 0;
        }
        // 生成 taskId（通过 userId * 10000 加上序号）
        return userId * 10000 + (maxSequence + 1);
    }

    // 获取当前用户任务的最大序号
    private int getMaxTaskSequence(int userId) {
        // 查询数据库，获取当前用户任务的最大序号
        // 注意：这里你需要在 TaskDao 中实现相应的查询方法
        return taskDao.getMaxSequenceForUser(userId);
    }
}
