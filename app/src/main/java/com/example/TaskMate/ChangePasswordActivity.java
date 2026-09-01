package com.example.TaskMate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.TaskMate.Model.UserManager;
import com.example.TaskMate.database.AppDatabase;
import com.example.TaskMate.Model.User;

import androidx.appcompat.app.AppCompatActivity;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText currentPasswordInput, newPasswordInput, confirmNewPasswordInput;
    private Button submitNewPasswordButton, backButton;
    private AppDatabase db; // Room Database 实例

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        setTitle("Change Password");

        // 初始化 UI 组件
        currentPasswordInput = findViewById(R.id.currentPassword);
        newPasswordInput = findViewById(R.id.newPassword);
        confirmNewPasswordInput = findViewById(R.id.confirmNewPassword);
        submitNewPasswordButton = findViewById(R.id.newPasswordButton);
        backButton = findViewById(R.id.backButton);

        db = AppDatabase.getInstance(this); // 获取 Room Database 实例

        // 提交新密码逻辑
        submitNewPasswordButton.setOnClickListener(v -> {
            String currentPassword = currentPasswordInput.getText().toString().trim();
            String newPassword = newPasswordInput.getText().toString().trim();
            String confirmNewPassword = confirmNewPasswordInput.getText().toString().trim();

            // 验证输入
            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            // 检查新密码和确认密码是否匹配
            if (!newPassword.equals(confirmNewPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // 检查密码长度
            if (newPassword.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            // 获取当前用户
            User currentUser = UserManager.getCurrentUser(this);
            if (currentUser == null) {
                Toast.makeText(this, "User not logged in. Please log in again.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 获取用户名并在数据库中查询用户
            String username = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).getString("username", null);
            if (TextUtils.isEmpty(username)) {
                Toast.makeText(this, "Username not found. Please log in again.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 开启子线程进行数据库操作
            new Thread(() -> {
                User user = db.userDao().getUserByUsername(username);

                if (user != null) {
                    // 验证当前密码
                    if (user.getPassword().trim().equals(currentPassword)) {
                        // 更新密码
                        user.setPassword(newPassword);

                        db.userDao().update(user); // 更新数据库中的密码
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "User not found. Please log in again.", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        });

        // 处理返回按钮
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(this, AccountActivity.class));
            finish();
        });
    }
}
