package com.example.TaskMate.ViewModel;


import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.TaskMate.R;

public class FlashlightActivity extends AppCompatActivity {
    private Switch flashlightSwitch;
    private TextView flashlightStatus;
//    private Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashlight);

        flashlightSwitch = findViewById(R.id.flashlightSwitch);
        flashlightStatus = findViewById(R.id.flashlightStatus);

        // 通过开关控制手电筒
        flashlightSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (camera != null) {
//                camera.getCameraControl().enableTorch(isChecked);  // 打开或关闭手电筒
//            }

            openOrClose();
            // 添加调试信息，确保触发了正确的逻辑
            Log.d("FlashlightActivity", "Flashlight switched: " + (isChecked ? "On" : "Off"));
        });

        openOrClose();


    }

    private void openOrClose(){
        if(flashlightSwitch.isChecked()){
            openFlashLight();
        }else{
            closeFlashLight();
        }
    }

//    private void enableFlashlight(boolean isEnabled) {
//        // 在这里调用开启或关闭手电筒的逻辑
//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//            // 手电筒操作代码
//            camera.getCameraControl().enableTorch(isEnabled);
//        } else {
//            // 请求权限
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1001);
//        }
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == 1001) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // 请求权限成功后再操作
//                enableFlashlight(flashlightSwitch.isChecked());
//            } else {
//                Toast.makeText(this, "权限被拒绝，无法使用手电筒", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }



    private void openFlashLight(){
        try {
            CameraManager mCameraManager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);
            String[] ids = mCameraManager.getCameraIdList();
            for (String id : ids) {
                CameraCharacteristics c = mCameraManager.getCameraCharacteristics(id);
                //查询该摄像头组件是否包含闪光灯
                Boolean flashAvailable = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                Integer lensFacing = c.get(CameraCharacteristics.LENS_FACING);
                if (flashAvailable != null && flashAvailable
                        && lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                    //打开或关闭手电筒
                    mCameraManager.setTorchMode(id, true);
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void closeFlashLight(){
        try {
            CameraManager mCameraManager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);
            //获取当前手机所有摄像头设备ID
            String[] ids = mCameraManager.getCameraIdList();
            for (String id : ids) {
                CameraCharacteristics c = mCameraManager.getCameraCharacteristics(id);
                //查询该摄像头组件是否包含闪光灯
                Boolean flashAvailable = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                Integer lensFacing = c.get(CameraCharacteristics.LENS_FACING);
                if (flashAvailable != null && flashAvailable
                        && lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                    //打开或关闭手电筒
                    mCameraManager.setTorchMode(id, false);
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}
