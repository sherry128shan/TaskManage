package com.example.TaskMate.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.TaskMate.Model.Alarm;  // 引用外部的 Alarm 类
import com.example.TaskMate.R;

import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    private List<Alarm> alarmList;

    // 构造函数传入闹钟列表
    public AlarmAdapter(List<Alarm> alarmList) {
        this.alarmList = alarmList;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 为每个闹钟项创建视图
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        Alarm alarm = alarmList.get(position);
        // 设置闹钟显示的时间和日期
        holder.alarmTime.setText(alarm.getTime());
        holder.alarmDate.setText(alarm.getDate());
        // 点击删除按钮时，移除闹钟
        holder.deleteButton.setOnClickListener(v -> {
            alarmList.remove(position);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    // ViewHolder类，用来存储每个视图项的控件
    public static class AlarmViewHolder extends RecyclerView.ViewHolder {

        TextView alarmTime;
        TextView alarmDate;
        ImageView deleteButton;

        public AlarmViewHolder(View itemView) {
            super(itemView);
            alarmTime = itemView.findViewById(R.id.alarm_time);
            alarmDate = itemView.findViewById(R.id.alarm_date);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
