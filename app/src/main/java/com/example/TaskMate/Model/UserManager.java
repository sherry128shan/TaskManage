package com.example.TaskMate.Model;

import android.content.Context;
import android.content.SharedPreferences;

public class UserManager {

    // 获取当前登录的用户
    public static User getCurrentUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);  // 默认值为 -1，表示没有找到

        if (userId == -1) {
            // 如果没有找到用户ID，返回 null，表示用户未登录
            return null;
        }

        User user = new User();
        user.setUserId(userId);  // 设置当前用户的 userId
        return user;
    }

    // 保存当前用户的 userId
    public static void saveCurrentUser(Context context, int userId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("userId", userId);  // 保存 userId
        editor.apply();
    }

    // 清除当前用户的 userId
    public static void clearUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("userId");  // 删除保存的 userId
        editor.apply();
    }
}
