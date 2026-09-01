package com.example.TaskMate;

import android.app.AlertDialog;
import android.graphics.Canvas;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.TaskMate.Adapter.Adapter;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

// Helper class for handling swipe actions in RecyclerView
public class TouchHelper extends ItemTouchHelper.SimpleCallback {

    private Adapter adapter; // Adapter for the RecyclerView

    // Constructor to set adapter and swipe directions
    public TouchHelper(Adapter adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
    }

    // Not used, required to override for move actions
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    // Handles swipe actions for deleting or editing tasks
    // Handles swipe actions for deleting or editing tasks
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition();
        if (position < 0 || position >= adapter.getItemCount()) {
            return; // 防止越界错误
        }

        if (direction == ItemTouchHelper.LEFT) { // 左滑，编辑操作
            adapter.editTask(position); // 调用 Adapter 的 editTask 方法
            adapter.notifyItemChanged(position); // 恢复任务 UI，防止绿色背景保留
        } else if (direction == ItemTouchHelper.RIGHT) { // 右滑，删除操作
            AlertDialog.Builder builder = new AlertDialog.Builder(viewHolder.itemView.getContext()); // 使用有效上下文
            builder.setMessage("Are you sure?")
                    .setTitle("Delete Task")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        adapter.deleteTask(position); // 调用 Adapter 的 deleteTask 方法
                    })
                    .setNegativeButton("No", (dialogInterface, i) -> {
                        adapter.notifyItemChanged(position); // 撤销删除操作
                    })
                    .setOnCancelListener(dialogInterface -> {
                        adapter.notifyItemChanged(position); // 对话框取消时恢复任务 UI
                    }); // 增加取消监听

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }




    // Customizes the background and icon of swiped items
    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeLeftBackgroundColor(Color.GREEN) // 左滑背景色
                .addSwipeLeftActionIcon(android.R.drawable.ic_menu_edit) // 左滑编辑图标
                .addSwipeLeftLabel("EDIT") // 编辑文字
                .setSwipeLeftLabelColor(Color.BLACK) // 文字颜色
                .addSwipeRightBackgroundColor(Color.RED) // 右滑背景色
                .addSwipeRightActionIcon(android.R.drawable.ic_menu_delete) // 右滑删除图标
                .addSwipeRightLabel("DELETE") // 删除文字
                .setSwipeRightLabelColor(Color.BLACK) // 文字颜色
                .create()
                .decorate(); // 应用装饰器

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
