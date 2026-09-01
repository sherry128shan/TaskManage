package com.example.TaskMate;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class AlarmRingActivity extends Activity {
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ring);

        // 设置提示信息
        TextView alarmMessage = findViewById(R.id.alarm_message);
        alarmMessage.setText("The Alarm Rang!");

        // 播放系统默认闹钟音
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            // 如果没有默认闹钟音，则使用通知音
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        mediaPlayer = MediaPlayer.create(this, alarmUri);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        // 设置关闭按钮
        Button stopAlarmButton = findViewById(R.id.stop_alarm_button);
        stopAlarmButton.setOnClickListener(v -> {
            mediaPlayer.stop();
            mediaPlayer.release();
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}
