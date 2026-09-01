package com.example.TaskMate.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.TaskMate.Model.Alarm;

import java.util.List;

@Dao
public interface AlarmDao {

    // 插入一个闹钟
    @Insert
    void insert(Alarm alarm);

    // 更新闹钟
    @Update
    void update(Alarm alarm);

    // 删除闹钟
    @Delete
    void delete(Alarm alarm);

    // 根据 userId 获取所有闹钟
    @Query("SELECT * FROM alarm_table WHERE userId = :userId")
    List<Alarm> getAlarmsByUserId(int userId);

    // 获取所有闹钟
    @Query("SELECT * FROM alarm_table")
    List<Alarm> getAllAlarms();
}
