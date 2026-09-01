package com.example.TaskMate.ViewModel;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.example.TaskMate.R;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MirrorActivity extends AppCompatActivity {

    private ExecutorService cameraExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mirror);  // Your layout with a PreviewView to show camera

        // Check for camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1001);
        }

        // Initialize camera executor
        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    private void startCamera() {
        // 获取 CameraProvider 实例（使用 addListener 来避免阻塞主线程）
        ProcessCameraProvider.getInstance(this).addListener(() -> {
            try {
                // 获取 cameraProvider 实例
                ProcessCameraProvider cameraProvider = ProcessCameraProvider.getInstance(this).get();

                // 选择前置摄像头
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                        .build();

                // 创建预览用例
                Preview preview = new Preview.Builder().build();

                // 设置预览的 SurfaceProvider
                preview.setSurfaceProvider(((PreviewView) findViewById(R.id.viewFinder)).getSurfaceProvider());

                // 将相机用例绑定到生命周期
                cameraProvider.bindToLifecycle(this, cameraSelector, preview);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));  // 在主线程中执行
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
