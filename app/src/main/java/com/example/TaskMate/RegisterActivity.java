package com.example.TaskMate;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.example.TaskMate.database.AppDatabase;
import com.example.TaskMate.Model.User;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Button registerButton;
    ProgressBar registerProgressBar;
    TextView loginTextView;
    AppDatabase db; // Room Database instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        registerButton = findViewById(R.id.registerButton);
        registerProgressBar = findViewById(R.id.registerProgressBar);
        loginTextView = findViewById(R.id.login);

        db = AppDatabase.getInstance(this); // Get Room Database instance

        // Register Button Click Listener
        registerButton.setOnClickListener(view -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Email and Password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            // 增加密码长度校验
            if (password.length() < 6) {
                Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            // 注册新用户到数据库
            registerProgressBar.setVisibility(View.VISIBLE); // 显示加载动画
            new Thread(() -> {
                User newUser = new User(email, password);
                db.userDao().insert(newUser); // 插入用户到数据库
                runOnUiThread(() -> {
                    registerProgressBar.setVisibility(View.GONE);
                    // 显示成功信息并跳转到登录界面
                    Toast.makeText(RegisterActivity.this, "Registration successful. Please log in.", Toast.LENGTH_SHORT).show();
                    Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                });
            }).start();
        });


        // Login TextView Click Listener (Navigates to Login Activity)
        loginTextView.setOnClickListener(view -> {
            Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        });
    }
}

