package com.example.TaskMate;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.app.AlertDialog;
import com.example.TaskMate.Adapter.AlarmAdapter;
import com.example.TaskMate.Model.Alarm;
import com.example.TaskMate.database.AppDatabase;
import com.example.TaskMate.database.AlarmDao;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmActivity extends AppCompatActivity {
    private RecyclerView alarmList;
    private AppCompatImageView addAlarmButton;
    private List<Alarm> alarms;  // 用于存储闹钟数据的列表
    private AlarmDao alarmDao;   // 用于操作数据库的AlarmDao
    private int currentUserId;   // 当前用户的userId

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);
        requestAlarmPermissionAndInit();
    }


    private void requestAlarmPermissionAndInit(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.S_V2) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
                Log.e("-------", "requestAlarmPermissionAndInit");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.USE_EXACT_ALARM}, 10);
            } else {
                init();
            }
        }else{
            init();
        }
    }

    private void init(){

        // 获取当前用户的userId
        currentUserId = getCurrentUserId();  // 假设有方法获取当前用户ID

        // 初始化控件
        alarmList = findViewById(R.id.alarm_list);
        addAlarmButton = findViewById(R.id.add_alarm_button);

        // 获取数据库实例和AlarmDao
        AppDatabase db = AppDatabase.getInstance(this);
        alarmDao = db.alarmDao();

        // 加载当前用户的所有闹钟
        loadUserAlarms();

        // 设置RecyclerView的布局管理器
        alarmList.setLayoutManager(new LinearLayoutManager(this));
        // 设置RecyclerView的适配器
        alarmList.setAdapter(new AlarmAdapter(alarms));

        // 设置添加新闹钟按钮的点击事件
        addAlarmButton.setOnClickListener(v -> showCreateAlarmDialog());

        // 添加滑动删除功能
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false; // 不处理拖拽操作
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition(); // 获取滑动位置
                Alarm alarmToDelete = alarms.get(position);

                // 从数据库删除
                new Thread(() -> {
                    alarmDao.delete(alarmToDelete);
                    runOnUiThread(() -> {
                        // 更新 UI
                        alarms.remove(position);

                        Toast.makeText(AlarmActivity.this, "The Alarm is deleted", Toast.LENGTH_SHORT).show();
                    });
                }).start();
            }
        }).attachToRecyclerView(alarmList);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==10){
            requestAlarmPermissionAndInit();
        }
    }

    // 获取当前用户的userId（假设已经有某种方式来获取当前登录用户的ID）
    private int getCurrentUserId() {
        // 假设从SharedPreferences或Intent中获取当前用户ID
        return 1;  // 示例，实际应用中需从已登录用户获取
    }

    // 加载当前用户的所有闹钟
    private void loadUserAlarms() {
        // 从数据库加载与当前用户ID关联的所有闹钟
        alarms = alarmDao.getAlarmsByUserId(currentUserId);
        if (alarms == null) {
            alarms = new ArrayList<>();
        }
    }

    // 显示新建闹钟的弹窗
    private void showCreateAlarmDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_alarm, null);

        // 创建AlertDialog并设置布局
        AlertDialog.Builder builder = new AlertDialog.Builder(AlarmActivity.this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        Button saveButton = dialogView.findViewById(R.id.save_alarm_button);
        NumberPicker dayPicker = dialogView.findViewById(R.id.day_picker);  // 用于选择日期
        TimePicker timePicker = dialogView.findViewById(R.id.time_picker);  // 用于选择时间

        // 设置NumberPicker数据
        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(31);
        dayPicker.setValue(10);  // 默认值为10号

        // 设置TimePicker
        timePicker.setIs24HourView(true);  // 使用24小时制
        timePicker.setCurrentHour(8);
        timePicker.setCurrentMinute(0);

        saveButton.setOnClickListener(v -> {
            String selectedDate = "2024-12-" + dayPicker.getValue();  // 从NumberPicker获取日期
            String selectedTime = String.format("%02d:%02d", timePicker.getCurrentHour(), timePicker.getCurrentMinute());  // 获取选择的时间
            Log.d("Debug", "已选择时间: " + selectedDate + " " + selectedTime);
            // 创建新的闹钟对象，并设置userId
            Alarm newAlarm = new Alarm(selectedTime, selectedDate, currentUserId);
            Log.d("AlarmActivity", "已经创建闹钟对象 " );
            // 将新闹钟插入数据库
            new Thread(() -> {
                alarmDao.insert(newAlarm);
                Log.d("AlarmActivity", "alarmDao.insert(newAlarm); is ok，debug");
                runOnUiThread(() -> {
                    Log.d("AlarmActivity", "1");
                    alarms.add(newAlarm);
                    Log.d("AlarmActivity", "2");
                    alarmList.getAdapter().notifyDataSetChanged();
                    Log.d("AlarmActivity", "3");
                    setAlarm(newAlarm);
                    Log.d("AlarmActivity", "4");
                    dialog.dismiss();
                    Toast.makeText(AlarmActivity.this, "Your alarm is set successfully: " + selectedDate + " " + selectedTime, Toast.LENGTH_SHORT).show();
                    // 放在此确保不会跳转
                });
            }).start();
        });

        // 显示弹窗
        dialog.show();
    }

    private void setAlarm(Alarm alarm) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // 创建一个 Intent，用于触发 AlarmReceiver
        Intent intent = new Intent(AlarmActivity.this, AlarmReceiver.class);
        //PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // 添加 FLAG_IMMUTABLE 或 FLAG_MUTABLE
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                AlarmActivity.this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE  // 或使用 PendingIntent.FLAG_MUTABLE
        );

        // 解析闹钟时间
        String[] timeParts = alarm.getTime().split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        // 获取当前日期的 Calendar
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // 设置闹钟
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }


}
