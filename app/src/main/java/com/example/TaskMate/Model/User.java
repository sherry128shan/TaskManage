package com.example.TaskMate.Model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_table")
public class User {

    @PrimaryKey(autoGenerate = true)
    private int userId; // 将id改为userId，主键，并且会自动生成

    private String username;
    private String password;

    // 无参构造函数，Room 需要这个构造函数
    public User() {
        // 无参构造函数，Room 自动使用这个来实例化对象
    }

    // 带参构造函数
    @Ignore
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter 和 Setter 方法
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    // 添加一个方法，用于返回当前用户的任务专属ID
    public int getTaskOwnerId() {
        return this.userId;
    }
}

