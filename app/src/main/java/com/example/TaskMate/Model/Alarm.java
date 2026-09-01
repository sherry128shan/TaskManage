package com.example.TaskMate.Model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "alarm_table")
public class Alarm {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String time;
    private String date;
    private int userId;  // 与用户的关联

    public Alarm(String time, String date, int userId) {
        this.time = time;
        this.date = date;
        this.userId = userId;
    }

    // Getter 和 Setter 方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
