package com.example.TaskMate.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.TaskMate.AddTask;
import com.example.TaskMate.MainActivity;
import com.example.TaskMate.Model.Task;
import com.example.TaskMate.R;
import com.example.TaskMate.database.AppDatabase;
import com.example.TaskMate.database.TaskDao;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private List<Task> todoList;
    private MainActivity mainActivity;
    private TaskDao taskDao;
    private Context context;
    // 构造函数中传递上下文
    public Adapter(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public Adapter(MainActivity mainActivity, List<Task> todoList) {
        this.mainActivity = mainActivity;
        this.todoList = todoList;
        AppDatabase db = AppDatabase.getInstance(mainActivity.getApplicationContext());
        taskDao = db.taskDao();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.task_design, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Task task = todoList.get(position);
        holder.checkbox.setText(task.getTask());
        holder.DueDate.setText("Due : " + task.getDueDate());
        holder.TaskDescription.setText(task.getDescription() != null ? task.getDescription() : "No description");
        holder.TaskStatus.setText(task.getStatus() != null ? task.getStatus() : "No status");
        // 根据任务状态更新复选框的状态
        if ("finished".equals(task.getStatus())) {
            holder.checkbox.setChecked(true);
        } else {
            holder.checkbox.setChecked(false);
        }

        // 监听复选框变化，更新任务状态到数据库
        holder.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 更新状态为 "finished" 或 "not start"
            task.setStatus(isChecked ? "finished" : "not start");

            // 更新数据库
            AppDatabase.databaseWriteExecutor.execute(() -> {
                taskDao.update(task);
            });
        });
    }

    @Override
    public int getItemCount() {
        return todoList != null ? todoList.size() : 0;
    }

    // Update the task list when LiveData changes
    @SuppressLint("NotifyDataSetChanged")
    public void updateTasks(List<Task> tasks) {
        this.todoList = tasks;
        notifyDataSetChanged(); // Notify that the data has changed
    }

    // Edit task at a specific position
    public void editTask(int position) {
        Task task = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putBoolean("update", true);  // 标记为更新
        bundle.putString("task", task.getTask());
        bundle.putString("description", task.getDescription());
        bundle.putString("id", String.valueOf(task.getTaskId())); // Convert taskId to String
        bundle.putString("due", task.getDueDate());

        // 创建 Intent 启动 AddTask Activity
        Intent intent = new Intent(mainActivity, AddTask.class);
        intent.putExtras(bundle);  // 将数据通过 Intent 传递

        // 启动 Activity
        mainActivity.startActivity(intent);
    }

    public void deleteTask(int position) {
        if (position < 0 || position >= todoList.size()) {
            // 防止越界错误
            return;
        }
        Task task = todoList.get(position); // 获取要删除的任务
        AppDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.delete(task); // 从数据库中删除任务
            todoList.remove(position); // 从列表中删除任务
            mainActivity.runOnUiThread(() -> notifyItemRemoved(position)); // 更新 UI
        });
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView DueDate;
        TextView TaskDescription;
        TextView TaskStatus;
        CheckBox checkbox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            DueDate = itemView.findViewById(R.id.DueDateDesign);
            TaskDescription = itemView.findViewById(R.id.TaskDescription);
            TaskStatus = itemView.findViewById(R.id.TaskStatus);
            checkbox = itemView.findViewById(R.id.checkbox);
        }
    }
}
