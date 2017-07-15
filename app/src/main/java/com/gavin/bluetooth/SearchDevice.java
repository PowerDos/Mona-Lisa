package com.gavin.bluetooth;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SearchDevice extends ListActivity {
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();


    private BroadcastReceiver resultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            devices.add(device);
            showDevices();
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        setContentView(R.layout.activity_search_device);
        if (!bluetoothAdapter.isEnabled()) {
            finish();
            return;
        }

        IntentFilter resultFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(resultReceiver, resultFilter);
        //开始搜索
        bluetoothAdapter.startDiscovery();

    }

    protected void showDevices() {
        List<String> list = new ArrayList<String>();
        for (int i = 0, size = devices.size(); i < size; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            BluetoothDevice device = devices.get(i);
            list.add("MAC：" + device.getAddress() + "\n设备名：" + device.getName());
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, list);
        setListAdapter(adapter);
    }

    protected void onListItemClick(ListView listView, View view, int position, long id) {

        Toast.makeText(this, "连接成功" + devices.get(position), Toast.LENGTH_LONG).show();
        Intent chat = new Intent(this, Communication.class);
        Bundle myBundle = new Bundle();
        myBundle.putParcelable("device", devices.get(position));
        chat.putExtras(myBundle);
        startActivity(chat);
        bluetoothAdapter.cancelDiscovery();
        finish();

    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(resultReceiver);
    }
}
