package com.example.TaskMate;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "闹钟触发，准备启动 AlarmRingActivity");

        // 播放铃声
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        try {
            RingtoneManager.getRingtone(context, ringtoneUri).play();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "无法播放铃声，请检查闹钟设置", Toast.LENGTH_SHORT).show();
        }

        // 启动震动
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            long[] pattern = {0, 500, 1000}; // 停顿 0ms, 振动 500ms, 停顿 1000ms
            vibrator.vibrate(pattern, -1);   // '-1' 表示只执行一次
        }

        // 启动 AlarmRingActivity
        Intent alarmIntent = new Intent(context, AlarmRingActivity.class);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 重要：确保从广播中启动 Activity
        context.startActivity(alarmIntent);

        // 显示通知（可选）
        showNotification(context);

        // 提示闹钟触发
        Toast.makeText(context, "Alarm triggered!", Toast.LENGTH_SHORT).show();
        Log.d("AlarmReceiver", "AlarmRingActivity 已启动");
    }

    // 显示通知（可选）
    private void showNotification(Context context) {
        Intent intent = new Intent(context, AlarmActivity.class); // 点击通知时打开 AlarmActivity
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "alarm_channel")
                .setSmallIcon(R.drawable.baseline_calendar_month_24)
                .setContentTitle("Alarm")
                .setContentText("Your alarm is ringing")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }
}

