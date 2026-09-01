package com.example.TaskMate.Model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "task_table",
        foreignKeys = @ForeignKey(entity = User.class, parentColumns = "userId", childColumns = "userId", onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = "userId")}  // 添加 userId 列的索引
)
public class Task {

    @PrimaryKey(autoGenerate = true)
    private int taskId; // 主键，自动生成

    private int userId; // 外键，关联到用户表
    private String task; // 任务标题
    private String description; // 任务描述
    private String dueDate; // 截止日期
    private String status; // 状态，"not start", "in progress", "finished"

    // 默认构造函数（Room 会使用这个构造函数）
    public Task() {
    }

    // 构造函数用于从数据库中读取任务
    @Ignore
    public Task(int taskId, int userId, String task, String description, String dueDate, String status) {
        this.taskId = taskId;
        this.userId = userId;
        this.task = task;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
    }

    // 构造函数用于手动创建任务
    @Ignore
    public Task(int userId, String task, String description, String dueDate, String status) {
        this.userId = userId;
        this.task = task;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
    }

    // Getter 和 Setter 方法
    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
