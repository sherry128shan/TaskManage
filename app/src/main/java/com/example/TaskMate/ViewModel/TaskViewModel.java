package com.example.TaskMate.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.TaskMate.Model.Task;
import com.example.TaskMate.Repository.TaskRepository;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private TaskRepository taskRepository;
    private LiveData<List<Task>> allTasks;

    public TaskViewModel(Application application) {
        super(application);
        taskRepository = new TaskRepository(application);
        allTasks = taskRepository.getAllTasks();  // 这是 LiveData 类型
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public LiveData<List<Task>> getTasksForUser(int userId) {
        return taskRepository.getTasksForUser(userId);
    }

    public void insert(Task task) {
        taskRepository.insert(task);
    }

    public void update(Task task) {
        taskRepository.update(task);
    }

    public void delete(Task task) {
        taskRepository.delete(task);
    }
}
