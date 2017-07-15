package com.gavin.bluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Communication extends AppCompatActivity implements View.OnClickListener{
    private final static String TAG = "ServerActivity";
    private BluetoothAdapter mBluetoothAdapter;
    private int REQUEST_ENABLE_BT = 1;
    private Context mContext;

    private Button mBtnBluetoothVisibility;
    private Button mBtnBluetoohDisconnect;
    private Button mBtnSendMessage;
    private EditText mEdttMessage;

    private TextView mBtConnectState;
    private ProgressDialog mProgressDialog;
    private BluetoothUtil mBluetoothUtil;

    private ListView listView;
    private ArrayAdapter<String> adapter;

    private String x;
    private String y;

    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch(msg.what){
                case BluetoothUtil.STATE_CONNECTED:
                    String deviceName = msg.getData().getString(BluetoothUtil.DEVICE_NAME);
                    mBtConnectState.setText("已成功连接到设备" + deviceName);
                    if(mProgressDialog.isShowing()){
                        mProgressDialog.dismiss();
                    }
                    break;
                case BluetoothUtil.STATE_CONNECT_FAILURE:
                    if(mProgressDialog.isShowing()){
                        mProgressDialog.dismiss();
                    }
                    showToast("连接失败");
                    break;
                case BluetoothUtil.MESSAGE_DISCONNECTED:
                    if(mProgressDialog.isShowing()){
                        mProgressDialog.dismiss();
                    }
                    mBtConnectState.setText("与设备断开连接");
                    mBluetoothUtil.startListen();
                    break;
                case BluetoothUtil.MESSAGE_READ:{
                    byte[] buf = msg.getData().getByteArray(BluetoothUtil.READ_MSG);
                    String str = new String(buf,0,buf.length);
                    x = str.substring(0,3);
                    y = str.substring(3,6);
                    Intent serviceIntent = new Intent(Communication.this, ClickService.class);
                    serviceIntent.putExtra("x",x);
                    serviceIntent.putExtra("y",y);
                    startService(serviceIntent);
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            AutoTool.exec("input tap 300 500");
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    showToast("input tap " + x + " " +y);
//                                }
//                            });
////                            Toast.makeText(Communication.this,  , Toast.LENGTH_LONG).show();
//                            Log.d("TEST","onclick");
//                        }
//                    }).start();
                    adapter.add(str);
                    adapter.notifyDataSetChanged();
                    break;
                }
                case BluetoothUtil.MESSAGE_WRITE:{
                    byte[] buf = (byte[]) msg.obj;
                    String str = new String(buf,0,buf.length);
                    showToast("发送成功:" + str);
                    break;
                }
                default:
                    break;
            }
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication);
        mContext = this;
        initView();
        initBluetooth();
        mBluetoothUtil = BluetoothUtil.getInstance(mContext);
        mBluetoothUtil.registerHandler(mHandler);
        BluetoothDevice device = (BluetoothDevice)(getIntent().getParcelableExtra("device"));
        mBluetoothUtil.connect(device);
    }

    private void initView() {

        mBtnBluetoothVisibility = (Button)findViewById(R.id.btn_blth_visiblity);
        mBtnBluetoohDisconnect = (Button)findViewById(R.id.btn_blth_disconnect);
        mBtnSendMessage = (Button)findViewById(R.id.btn_sendmessage);
        mEdttMessage = (EditText)findViewById(R.id.edt_message);
        mBtConnectState = (TextView)findViewById(R.id.tv_connect_state);
        listView = (ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);

        mBtnBluetoothVisibility.setOnClickListener(this);
        mBtnBluetoohDisconnect.setOnClickListener(this);
        mBtnSendMessage.setOnClickListener(this);
        mProgressDialog = new ProgressDialog(this);
    }

    private void initBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {//设备不支持蓝牙
            Toast.makeText(getApplicationContext(), "设备不支持蓝牙", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        //判断蓝牙是否开启
        if (!mBluetoothAdapter.isEnabled()) {//蓝牙未开启
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            //mBluetoothAdapter.enable();此方法直接开启蓝牙，不建议这样用。
        }
        //设置蓝牙可见性
        if (mBluetoothAdapter.isEnabled()) {
            if (mBluetoothAdapter.getScanMode() !=
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                Intent discoverableIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(
                        BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600);
                startActivity(discoverableIntent);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult request="+requestCode+" result="+resultCode);
        if(requestCode == 1){
            if(resultCode == RESULT_OK){

            }else if(resultCode == RESULT_CANCELED){
                finish();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (!mBluetoothAdapter.isEnabled())return;
        if (mBluetoothUtil != null) {
            // 只有国家是state_none，我们知道，我们还没有开始
            if (mBluetoothUtil.getState() == BluetoothUtil.STATE_NONE) {
                // 启动蓝牙聊天服务
                mBluetoothUtil.startListen();
            }else if (mBluetoothUtil.getState() == BluetoothUtil.STATE_CONNECTED){
                BluetoothDevice device = mBluetoothUtil.getConnectedDevice();
                if(null != device && null != device.getName()){
                    mBtConnectState.setText("已成功连接到设备" + device.getName());
                }else {
                    mBtConnectState.setText("已成功连接到设备");
                }
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onClick(View arg0) {
        switch(arg0.getId()){
            case R.id.btn_blth_visiblity:
                if (mBluetoothAdapter.isEnabled()) {
                    if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                        Intent discoveryIntent = new Intent(
                                BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                        discoveryIntent.putExtra(
                                BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                        startActivity(discoveryIntent);
                    }
                }else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.bluetooth_unopened), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_blth_disconnect:
                if (mBluetoothUtil.getState() != BluetoothUtil.STATE_CONNECTED) {
                    Toast.makeText(mContext, "蓝牙未连接", Toast.LENGTH_SHORT).show();
                }else {
                    mBluetoothUtil.disconnect();
                }
                break;
            case R.id.btn_sendmessage:
                String messageSend = mEdttMessage.getText().toString();
                if(null == messageSend || messageSend.length() == 0){
                    return;
                }
                mBluetoothUtil.write(messageSend.getBytes());
                break;
            default:
                break;
        }
    }

    public void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
