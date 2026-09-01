package com.example.TaskMate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.TaskMate.Model.UserManager;
import com.google.android.material.textfield.TextInputEditText;
import com.example.TaskMate.database.AppDatabase;
import com.example.TaskMate.Model.User;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Button logInButton;
    ProgressBar signInProgressBar;
    AppDatabase db; // Room Database instance
    TextView registerTextView; // "Click to Register" TextView

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        logInButton = findViewById(R.id.loginButton);
        signInProgressBar = findViewById(R.id.loginProgressBar);
        registerTextView = findViewById(R.id.register); // Initialize "Click to Register" TextView

        // Get Room Database instance
        db = AppDatabase.getInstance(this);

        // Handle login button click
        logInButton.setOnClickListener(view -> {
            signInProgressBar.setVisibility(View.VISIBLE);
            String username = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(LoginActivity.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                signInProgressBar.setVisibility(View.GONE);
                return;
            }

            // Query the Room database to verify user credentials
            new Thread(() -> {
                User user = db.userDao().getUserByUsername(username);
                runOnUiThread(() -> {
                    signInProgressBar.setVisibility(View.GONE);
                    Log.d(TAG, "Fetched user: " + user);
                    if (user != null && user.getPassword().equals(password)) {
                        // Save username to SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", username);
                        editor.apply();
                        UserManager.saveCurrentUser(LoginActivity.this, user.getUserId());
                        Log.d(TAG, "Username saved to SharedPreferences: " + username);

                        // Navigate to MainActivity
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // End current activity
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });

        // Handle "Click to Register" click event
        registerTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}


