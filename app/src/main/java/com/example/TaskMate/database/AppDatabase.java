package com.example.TaskMate.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.TaskMate.Model.Alarm;
import com.example.TaskMate.Model.Task;
import com.example.TaskMate.Model.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class, Task.class, Alarm.class}, version = 3, exportSchema = false)  // 增加 Alarm 实体类
public abstract class AppDatabase extends RoomDatabase {

    // 数据库实例
    private static volatile AppDatabase instance;

    // Executor 用于在后台线程执行数据库操作
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    // 获取 UserDao、TaskDao 和 AlarmDao 实例
    public abstract UserDao userDao();
    public abstract TaskDao taskDao();
    public abstract AlarmDao alarmDao();  // 增加 AlarmDao

    // 获取数据库实例
    public static AppDatabase getInstance(final Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "task_database") // 使用 task_database 来存储任务数据
                            .fallbackToDestructiveMigration() // 开发阶段使用：每次迁移时销毁旧数据库
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return instance;
    }
}
