package com.example.TaskMate;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.TaskMate.Model.User;
import com.example.TaskMate.Model.Task;
import com.example.TaskMate.Model.UserManager;
import com.example.TaskMate.Repository.TaskRepository;

import java.util.Calendar;

public class AddTask extends AppCompatActivity {

    private TextView setDueDate;
    private EditText taskEditText, descriptionEditText;
    private Button saveButton;
    private Spinner progressionSpinner;
    private String dueDate = "";
    private String taskId = "";  // This will store task ID for updates
    private TaskRepository taskRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_task);

        // 初始化组件
        setDueDate = findViewById(R.id.DueDate);
        taskEditText = findViewById(R.id.EnterTask);
        descriptionEditText = findViewById(R.id.EnterDescription);
        saveButton = findViewById(R.id.buttonSave);
        progressionSpinner = findViewById(R.id.spinnerProgress);

        // 设置 Progression Spinner
        ArrayAdapter<CharSequence> progressionAdapter = ArrayAdapter.createFromResource(this,
                R.array.task_progression, android.R.layout.simple_spinner_item);
        progressionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        progressionSpinner.setAdapter(progressionAdapter);

        taskRepository = new TaskRepository(getApplication());  // 初始化 TaskRepository

        // 获取传递的数据
        boolean update;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            update = bundle.getBoolean("update", false);
            // 提取数据进行更新
            String task = bundle.getString("task");
            String description = bundle.getString("description");
            taskId = bundle.getString("id");

            // 设置提取的值到 UI 组件
            taskEditText.setText(task);
            descriptionEditText.setText(description);
            setDueDate.setText(bundle.getString("due"));
            dueDate = bundle.getString("due");

            // 如果任务和描述不为空，则启用保存按钮
            if (!task.isEmpty() && !description.isEmpty()) {
                saveButton.setEnabled(true);
                saveButton.setBackgroundColor(Color.parseColor("#FF6200EE"));
            } else {
                saveButton.setEnabled(false);
                saveButton.setBackgroundColor(Color.GRAY);
            }
        } else {
            update = false;
        }

        // 使用 TextWatcher 启用/禁用保存按钮
        taskEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                checkSaveButtonState(); // 检查是否启用保存按钮
                if (s.toString().isEmpty()) {
                    saveButton.setEnabled(false);
                    saveButton.setBackgroundColor(Color.GRAY);
                } else {
                    saveButton.setEnabled(true);
                    saveButton.setBackgroundColor(Color.parseColor("#FF6200EE"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        descriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                checkSaveButtonState(); // 检查是否启用保存按钮
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        // 设置日期选择器
        setDueDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view1, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                selectedMonth = selectedMonth + 1;  // Adjust month for display
                dueDate = selectedDayOfMonth + "/" + selectedMonth + "/" + selectedYear;
                setDueDate.setText(dueDate);
                checkSaveButtonState(); // 检查是否启用保存按钮
            }, year, month, day);

            datePickerDialog.show();
        });



// 保存按钮点击事件，用于添加或更新任务
        saveButton.setOnClickListener(v -> {
            String taskText = taskEditText.getText().toString().trim();
            String descriptionText = descriptionEditText.getText().toString().trim();
            String progression = progressionSpinner.getSelectedItem().toString();

            // 从 UserManager 获取当前用户
            User currentUser = UserManager.getCurrentUser(this);  // 获取当前用户
            if (currentUser == null) {
                return;
            }
            // 生成 taskId
            int taskId1 = 0;
            if ("".equals(taskId))
                taskId1 = taskRepository.generateTaskId(currentUser.getUserId());
            else
                taskId1 = Integer.parseInt(taskId);

            // 创建 Task 对象
            Task task = new Task(taskId1, currentUser.getUserId(), taskText, descriptionText, dueDate, progression);

            if (update) {  // 如果是更新操作
                // 无论任务内容是否修改，都执行更新
                taskRepository.update(task);  // 更新任务
                Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show();
            } else {  // 如果是插入新任务
                taskRepository.insert(task);  // 添加新任务
                Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show();
            }

            // 设置结果通知 MainActivity 刷新任务列表
            setResult(RESULT_OK);
            finish();
        });
    }
    // 检查是否启用保存按钮的方法
    private void checkSaveButtonState() {
        String taskText = taskEditText.getText().toString().trim();

        if (!taskText.isEmpty()) {
            saveButton.setEnabled(true);
            saveButton.setBackgroundColor(Color.parseColor("#FF6200EE"));
        } else {
            saveButton.setEnabled(false);
            saveButton.setBackgroundColor(Color.GRAY);
        }
    }
        // 重写返回键操作
        @Override
        public void onBackPressed() {
            // 返回任务列表并通知恢复 UI
            setResult(RESULT_CANCELED);
            super.onBackPressed();
        }
    }
