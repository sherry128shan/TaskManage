package com.example.TaskMate;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.content.SharedPreferences;

public class AccountActivity extends AppCompatActivity {


    private TextView textView;
    private TextView textView02;
    private Button changepassword;
    private  Button groupinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        setTitle("Account Information");

        // 绑定UI控件
        textView = findViewById(R.id.Userinfo); //用户信息
        changepassword = findViewById(R.id.ChangePassword);
        groupinfo = findViewById(R.id.Us);


        // Set up SharedPreferences for local login management
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Remove to password change page
        changepassword.setOnClickListener(view -> {
            Log.d("AccountActivity", "ChangePassword button clicked, opening ChangePasswordActivity.");

            // 创建 Intent 跳转到 ChangePasswordActivity
            Intent intent = new Intent(AccountActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        // Jump to our info
        groupinfo.setOnClickListener(view -> {
            Intent intent = new Intent(AccountActivity.this,about_us.class);
            startActivity(intent);
        });

        // Fetch username from SharedPreferences
        String username = sharedPreferences.getString("username", null);

        // Log the username to see if it is retrieved correctly
        Log.d("AccountActivity", "Retrieved username: " + username);        // Check if username is null or not and update UI accordingly

        textView.setText("Account name: " + username);
    }
    public void onLoginSuccess(String username, int userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);  // 保存用户名
        editor.putInt("userId", userId);  // 保存 userId
        editor.apply();
    }

}
