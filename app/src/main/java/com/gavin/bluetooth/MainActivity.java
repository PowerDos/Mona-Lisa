package com.gavin.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button openButton;
    Button searchDevice;
    BluetoothAdapter bluetoothAdapter;
    final static String TAG = "MonaLisaBlueTooth";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 获取蓝牙适配器
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // 判断该设备是否支持蓝牙功能
        if (bluetoothAdapter == null){
            showToast("该设备不支持蓝牙功能");
            Log.d(TAG, "该设备不支持蓝牙功能");
            return;
        }
        openButton = (Button) findViewById(R.id.openBluetooth);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 判断蓝牙是否已经打开
                if (bluetoothAdapter.isEnabled()){
                    showToast("蓝牙已经处于打开状态");
                    Log.d(TAG, "蓝牙已经处于打开状态");
                }else {
                    //打开蓝牙
                    boolean isOpen = bluetoothAdapter.enable();
                    if (isOpen){
                        showToast("蓝牙打开成功");
                        Log.d(TAG, "蓝牙打开成功");
                    }else {
                        showToast("蓝牙打开失败");
                        Log.d(TAG, "蓝牙打开失败");
                    }
                }
            }
        });
        searchDevice = (Button) findViewById(R.id.btn_search_device);
        searchDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchDevice.class);
                startActivity(intent);
            }
        });
    }

    public void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
