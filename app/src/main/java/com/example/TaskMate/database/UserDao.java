package com.example.TaskMate.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Query;

import com.example.TaskMate.Model.User;

import java.util.List;

@Dao
public interface UserDao {

    // 插入一个用户
    @Insert
    void insert(User user);
    // 更新用户
    @Update
    void update(User user);
    // 删除用户
    @Delete
    void delete(User user);
    // 根据用户名查询用户
    @Query("SELECT * FROM user_table WHERE username = :username LIMIT 1")  // 确保查询的表名与实体类一致
    User getUserByUsername(String username);

    // 获取所有用户
    @Query("SELECT * FROM user_table")  // 确保查询的表名与实体类一致
    List<User> getAllUsers();

}

