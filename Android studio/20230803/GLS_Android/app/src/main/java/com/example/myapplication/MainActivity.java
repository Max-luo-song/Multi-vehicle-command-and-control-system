package com.example.myapplication;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;
import android.widget.EditText;

import com.example.myapplication.fragment.ControlFragment;
import com.example.myapplication.fragment.NavigationFragment;
import com.example.myapplication.fragment.RecognitionFragment;
import com.example.myapplication.fragment.VideoFragment;
import com.example.myapplication.rosbridge.ROSBridgeClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private Button startSystemBtn;
    public EditText car1IpEdit;
    public EditText car1PortEdit;
    public EditText car2IpEdit;
    public EditText car2PortEdit;
    public EditText car2XEdit;
    public EditText car2YEdit;
    public EditText car3IpEdit;
    public EditText car3PortEdit;
    public EditText car3XEdit;
    public EditText car3YEdit;

    public static ROSBridgeClient clientCar1;
    public static ROSBridgeClient clientCar2;
    public static ROSBridgeClient clientCar3;

    public boolean connectFalseTag = false;
    // funciton:从layout中初始化start_page的layout
    private void InitVariable() {
        car1IpEdit = findViewById(R.id.car1_ip_edit);
        car1PortEdit = findViewById(R.id.car1_port_edit);
        car2IpEdit = findViewById(R.id.car2_ip_edit);
        car2PortEdit = findViewById(R.id.car2_port_edit);
        car2XEdit = findViewById(R.id.car2_x_edit);
        car2YEdit = findViewById(R.id.car2_y_edit);
        car3IpEdit = findViewById(R.id.car3_ip_edit);
        car3PortEdit = findViewById(R.id.car3_port_edit);
        car3XEdit = findViewById(R.id.car3_x_edit);
        car3YEdit = findViewById(R.id.car3_y_edit);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_page);
        InitVariable();
        ClientConnect();
        InitView();
    }

    // function：完成三个客户端的连接
    private void ClientConnect() {
        clientCar1 = new ROSBridgeClient("ws://"+car1IpEdit.getText().toString()+":"+car1PortEdit.getText().toString());
        clientCar2 = new ROSBridgeClient("ws://"+car2IpEdit.getText().toString()+":"+car2PortEdit.getText().toString());
        clientCar3 = new ROSBridgeClient("ws://"+car3IpEdit.getText().toString()+":"+car3PortEdit.getText().toString());
        clientCar1.connect();
        clientCar2.connect();
        clientCar3.connect();
        if (有一个连接失败连接失败) {
            connectFalseTag = false;
        }
        else {
            connectFalseTag = true;
        }
    }
    // function：完成按键后的判断逻辑
    private void InitView() {
        startSystemBtn = (Button) findViewById(R.id.start_system_btn);
        startSystemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!connectFalseTag) { // 连接失败，跳转StartErrorPage
                    Intent intent = new Intent(MainActivity.this,StartErrorPage.class);
                    startActivity(intent);
                }
                else {  // 连接成功，跳转NavigationPage
                    Intent intent = new Intent(MainActivity.this,NavigationPage.class);
                    startActivity(intent);
                }
            }
        });
    }
}

