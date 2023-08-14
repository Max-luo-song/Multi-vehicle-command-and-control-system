package com.example.myapplication;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.content.Intent;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.fragment.ControlFragment;
import com.example.myapplication.fragment.NavigationFragment;
import com.example.myapplication.fragment.RecognitionFragment;
import com.example.myapplication.fragment.VideoFragment;
import com.example.myapplication.message.Move;
import com.example.myapplication.message.InitPose;
import com.example.myapplication.rosbridge.ROSBridgeClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private Button startSystemBtn;
    public static EditText car1IpEdit;
    public EditText car1PortEdit;
    public static EditText car2IpEdit;
    public EditText car2PortEdit;
    public EditText car2XEdit;
    public EditText car2YEdit;
    public static EditText car3IpEdit;
    public EditText car3PortEdit;
    public EditText car3XEdit;
    public EditText car3YEdit;

    public static ROSBridgeClient clientCar1;
    public static ROSBridgeClient clientCar2;
    public static ROSBridgeClient clientCar3;

    // 标志是否car2和car3是真正的有效
    private boolean conntectTagCar2_True = false;
    private boolean conntectTagCar3_True = false;
    public boolean connectTag = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_page);
        FindViewById();
        InitView();
    }

    // funciton:从layout中初始化start_page的layout
    private void FindViewById() {
        Log.i("MainActivity","enter FindViewById");
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
        return ;
    }

    // function：如果Car2和Car3是偏移有效的，那么发布话题/initpose_android
    private void InitPosePub() {
        if (conntectTagCar2_True == true) {
            com.example.myapplication.Topic<InitPose> initPoseTopicCar2 = new com.example.myapplication.Topic<InitPose>("/initpose_android", InitPose.class, clientCar2);
            double car2X = Double.parseDouble(car2XEdit.getText().toString());
            double car2Y = Double.parseDouble(car2YEdit.getText().toString());
            InitPose initPose = new InitPose(car2X, car2Y);
            initPoseTopicCar2.publish(initPose);
            Log.i("MainActivity","car2初始位置设置成功！！！");
        }
        if (conntectTagCar3_True == true) {
            com.example.myapplication.Topic<InitPose> initPoseTopicCar3 = new com.example.myapplication.Topic<InitPose>("/initpose_android", InitPose.class, clientCar3);
            double car3X = Double.parseDouble(car3XEdit.getText().toString());
            double car3Y = Double.parseDouble(car3YEdit.getText().toString());
            InitPose initPose = new InitPose(car3X, car3Y);
            initPoseTopicCar3.publish(initPose);
            Log.i("MainActivity","car3初始位置设置成功！！！");
        }
        return ;
    }

    // function：完成三个客户端的连接
    /*
        判断是否连接成功实现思路：
            1.默认Car1启动
            2.通过connect返回的布尔值进行判断，如果存在返回的布尔值为false，则进入start_err_page
            3.为了实现1车或2车启动，把相对坐标的x y设置为999，如果不为999说明是有效的，就启动conntectTagCarX_True
     */
    private void ClientConnect() { // 默认clientCar1是必须连接的，car2和car3是根据是否有偏移x,y有效判断连接(x,y均为999则为无效)，支持只连接1个车或者两个车
        boolean connectTagCar1 = false;
        boolean connectTagCar2 = false;
        boolean connectTagCar3 = false;
        clientCar1 = new ROSBridgeClient("ws://"+car1IpEdit.getText().toString()+":"+car1PortEdit.getText().toString());
        connectTagCar1 = clientCar1.connect();
        if (!car2XEdit.getText().toString().equals("999") && !car2YEdit.getText().toString().equals("999")) {
            clientCar2 = new ROSBridgeClient("ws://"+car2IpEdit.getText().toString()+":"+car2PortEdit.getText().toString());
            connectTagCar2 = clientCar2.connect();
            if (connectTagCar2 == true) {
                conntectTagCar2_True = true;
                Log.i("MainActivity","car2连接成功！！！");
            }
        }
        else {  // 没有使用，默认为true
            connectTagCar2 = true;
        }
        if (!car3XEdit.getText().toString().equals("999") && !car3YEdit.getText().toString().equals("999")) {
            clientCar3 = new ROSBridgeClient("ws://" + car3IpEdit.getText().toString() + ":" + car3PortEdit.getText().toString());
            connectTagCar3 = clientCar3.connect();
            if (connectTagCar3 == true) {
                conntectTagCar3_True = true;
                Log.i("MainActivity","car3连接成功！！！");
            }
        }
        else {
            connectTagCar3 = true;
        }
        if (connectTagCar1 == false || connectTagCar2 == false || connectTagCar3 == false) {  // 有一个连接失败即为失败
            if (connectTagCar1 == false) {
                Log.i("MainActivity","car1连接失败！！！");
            }
            if (connectTagCar2 == false) {
                Log.i("MainActivity","car2连接失败！！！");
            }
            if (connectTagCar3 == false) {
                Log.i("MainActivity","car3连接失败！！！");
            }
            connectTag = false;
        }
        else {
            connectTag = true;
            Log.i("MainActivity","使用小车全部连接成功！！！");
        }
        return ;
    }

    // function：完成按键后的判断逻辑
    private void InitView() {
        startSystemBtn = (Button) findViewById(R.id.start_system_btn);
        startSystemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientConnect();
                InitPosePub();
                if (!connectTag) { // 连接失败，跳转StartErrorPage
                    Intent intent = new Intent(MainActivity.this,StartErrorPage.class);
                    startActivity(intent);
                    Log.i("MainActivity","enter StartErrorPage");
                }
                else {  // 连接成功，跳转NavigationPage
                    Intent intent = new Intent(MainActivity.this,NavigationPage.class);
                    startActivity(intent);
                    Log.i("MainActivity","enter NavigationPage");
                }
            }
        });
        return ;
    }
}

