package com.example.TaskMate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.TaskMate.Adapter.Adapter;
import com.example.TaskMate.Model.User;
import com.example.TaskMate.Model.UserManager;
import com.example.TaskMate.ViewModel.FlashlightActivity;
import com.example.TaskMate.ViewModel.MirrorActivity;
import com.example.TaskMate.ViewModel.TaskViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MainActivity extends AppCompatActivity implements OnDialogCloseListener {

    private RecyclerView taskRecyclerView;
    private FloatingActionButton addTaskButton;
    private TaskViewModel taskViewModel; // ViewModel for tasks
    private Adapter tasksAdapter; // Adapter for RecyclerView
    private Button button; // Logout button
    private TextView textView; // Displays user info
    private Button AccountView;  // Account button
    private DrawerLayout drawerLayout; // Navigation drawer
    private Button menuButton; // Menu button
    private SharedPreferences sharedPreferences;
    private Button alarmButton;
    private Button mirrorButton;
    private Button flashlightButton;

    // For local user authentication
    private static final String TAG = "MainActivity";


    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Set layout
        Log.d(TAG, "onCreate called. MainActivity is starting.");

        // Initialize UI components
        taskRecyclerView = findViewById(R.id.recyclerView);
        addTaskButton = findViewById(R.id.floatingActionButton);
        button = findViewById(R.id.Logout);
        textView = findViewById(R.id.WelcomeMessage);
        AccountView = findViewById(R.id.accountButton);
        drawerLayout = findViewById(R.id.drawerLayout);
        menuButton = findViewById(R.id.menuButton);
        alarmButton = findViewById(R.id.alarmButton);
        mirrorButton = findViewById(R.id.mirror);
        flashlightButton = findViewById(R.id.flashlight);


        // Account button click listener
        AccountView.setOnClickListener(view -> {
            Log.d(TAG, "Account button clicked, opening AccountActivity.");
            Intent intent = new Intent(MainActivity.this, AccountActivity.class);
            startActivity(intent);
        });

        //alarm
        alarmButton.setOnClickListener(View ->{
            Log.d(TAG, "alarm button clicked, opening AlarmActivity.java");
            Intent intent = new Intent(MainActivity.this, AlarmActivity.class);
            startActivity(intent);
        });

        //mirror
        mirrorButton.setOnClickListener(View ->{
            Log.d(TAG, "mirror button clicked, opening camera");
            Intent intent = new Intent(MainActivity.this, MirrorActivity.class);
            startActivity(intent);
        });

        //Flashlight
        flashlightButton.setOnClickListener(View ->{
            Log.d(TAG, "mirror button clicked, opening FlashlightActivity");
            Intent intent = new Intent(MainActivity.this, FlashlightActivity.class);
            startActivity(intent);
        });







        // Set up RecyclerView
        taskRecyclerView.setHasFixedSize(true);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        Log.d(TAG, "RecyclerView setup complete.");

        // Set up SharedPreferences for local login management
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Fetch username from SharedPreferences
        String username = sharedPreferences.getString("username", null);
        if (username == null) {
            Log.d(TAG, "No username found, redirecting to LoginActivity.");
            redirectToLogin();
        } else {
            Log.d(TAG, "User logged in, username: " + username);
            textView.setText("Welcome " + username);
        }

        // Logout button click listener
        button.setOnClickListener(view -> {
            Log.d(TAG, "Logout button clicked, clearing SharedPreferences.");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear(); // Clear saved user data
            editor.apply(); // Apply changes
            redirectToLogin();
        });

        // Set up the task adapter
        tasksAdapter = new Adapter(MainActivity.this, null);
        taskRecyclerView.setAdapter(tasksAdapter);
        Log.d(TAG, "TaskAdapter set.");

        // 添加 TouchHelper 以支持滑动操作
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(taskRecyclerView);
        Log.d(TAG, "TouchHelper attached to RecyclerView.");

        // Initialize the ViewModel for tasks
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        Log.d(TAG, "TaskViewModel initialized.");

        // Added: Observe LiveData for current user tasks
        User currentUser = UserManager.getCurrentUser(this);
        if (currentUser != null) {
            int userId = currentUser.getUserId();
            taskViewModel.getTasksForUser(userId).observe(this, tasks -> {
                Log.d(TAG, "Tasks updated for user ID: " + userId);
                tasksAdapter.updateTasks(tasks);
            });
        } else {
            Log.e(TAG, "No logged-in user found, redirecting to login.");
            redirectToLogin();
        }

        // Add task button click listener
        addTaskButton.setOnClickListener(view -> {
            Log.d(TAG, "Add task button clicked, opening AddTask Activity.");
            Intent intent = new Intent(MainActivity.this, AddTask.class);
            startActivityForResult(intent, 1); // 请求码 1 表示添加任务
        });

        // Navigation drawer menu button click listener
        menuButton.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Side drawer listener
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {}

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {}

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {}

            @Override
            public void onDrawerStateChanged(int newState) {}
        });
    }

    // Redirect to login page if the user is not logged in
    private void redirectToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // Handle dialog close and refresh tasks
    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        Log.d(TAG, "Dialog closed, refreshing tasks.");
        User currentUser = UserManager.getCurrentUser(this);
        if (currentUser != null) {
            int userId = currentUser.getUserId();
            taskViewModel.getTasksForUser(userId).observe(this, tasks -> {
                tasksAdapter.updateTasks(tasks);
            });
        } else {
            Log.e(TAG, "No logged-in user found, unable to refresh tasks.");
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            User currentUser = UserManager.getCurrentUser(this);
            if (currentUser != null) {
                int userId = currentUser.getUserId();
                taskViewModel.getTasksForUser(userId).observe(this, tasks -> {
                    tasksAdapter.updateTasks(tasks); // 刷新任务列表
                });
            }
        }else if (resultCode == RESULT_CANCELED) {
            // 如果编辑任务被取消，恢复任务列表 UI
            tasksAdapter.notifyDataSetChanged();
        }
    }
    // Lifecycle methods for logging
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called. MainActivity is now visible.");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called. MainActivity is in foreground.");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called. MainActivity is no longer in foreground.");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop called. MainActivity is being stopped.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called. MainActivity is being destroyed.");
    }
}

