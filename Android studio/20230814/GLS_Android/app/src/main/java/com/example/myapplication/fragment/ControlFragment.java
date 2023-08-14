package com.example.myapplication.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.VideoPlayer;
import com.example.myapplication.rosbridge.ROSBridgeClient;
import com.example.myapplication.message.Move;
import com.example.myapplication.structure.Vector3;
//import com.example.myapplication.ArrayAdapter;

// spinner多车选择模块头文件
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

public class ControlFragment extends Fragment {

    private VideoPlayer videoPlayer;
    public Button forworkBtn;
    public Button leftBtn;
    public Button stopBtn;
    public Button rightBtn;
    public Button backBtn;
    public static EditText linearVelocityEdit;
    public static EditText angularVelocityEdit;
    public Spinner spinner;
    public Map<String, ROSBridgeClient> mapCar;
    public  ROSBridgeClient clientCar;
    public ControlFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("ControlFragment","in onCreate");
        super.onCreate(savedInstanceState);
        //对视频流信息进行初始化
        videoPlayer = new VideoPlayer(getActivity());
        return ;
    }

    // funciton:从layout中初始化ControlFragment的layout
    public void FindViewById(View view) {
        Log.i("ControlFragment","enter FindViewById");
        forworkBtn = view.findViewById(R.id.forward_btn);
        leftBtn = view.findViewById(R.id.left_btn);
        stopBtn = view.findViewById(R.id.stop_btn);
        rightBtn = view.findViewById(R.id.right_btn);
        backBtn = view.findViewById(R.id.back_btn);
        linearVelocityEdit = view.findViewById(R.id.linear_velocity_edit);
        angularVelocityEdit = view.findViewById(R.id.angular_velocity_edit);
        return ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_control, container, false);
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.i("ControlFragment","enter onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        FindViewById(view);

        Log.i("ControlFragment","start_spinner");
        /*******多车spinner选择模块**********/
        mapCar = new HashMap<String, ROSBridgeClient>() {
            {
                put("车1", MainActivity.clientCar1);  // 设置名称为车1的原因是前面是HashMap顺序不定，保证Car的position后续是第一个
                put("车2", MainActivity.clientCar2);
                put("车3", MainActivity.clientCar3);
            }
        };
        List<String> keys = new ArrayList<>(mapCar.keySet());
        // 配置adapter
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, keys);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (Spinner) view.findViewById(R.id.cars_spinner_control);
        clientCar = mapCar.get("车1");
        clientCar.connect();
        // adapter放入spinner
        spinner.setAdapter(spinnerAdapter);
        // 下拉菜单项选择事件
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view1, int position, long id) {// 此处的view1是有问题的，要使用全局的view才能正常显示，做区别写成了view1

                // position为选择第几个
                String selectedKey = keys.get(position);
                clientCar = mapCar.get(selectedKey);
                clientCar.connect();
                //获取视频流
                if (selectedKey.equals("车1")) {
                    videoPlayer.GetVideo(view, MainActivity.car1IpEdit.getText().toString());
                }
                else if (selectedKey.equals("车2")) {
                    videoPlayer.GetVideo(view, MainActivity.car2IpEdit.getText().toString());
                }
                else if (selectedKey.equals("车3")) {
                    videoPlayer.GetVideo(view, MainActivity.car3IpEdit.getText().toString());
                }
                com.example.myapplication.Topic<Move> moveTopic = new com.example.myapplication.Topic<Move>("/set_speed", Move.class, clientCar);  // 重新选择时重新声明moveTopic
                moveTopic.advertise();
                // 数字顺序：前后左右停
                /*
                    改动：关于移动控制的button之前使用声明新类，传参方式，但是传过去的moveTopic无法Publish，更换方式直接内部设置Listener
                 */
                forworkBtn.setOnClickListener(new View.OnClickListener() {  // 前
                    @Override
                    public void onClick(View view) {
                        Log.i("ControlFragmentBtn","enter forward button onclick!!!");
                        Move move = new Move();
                        move.linear = new Vector3();
                        move.angular = new Vector3();
                        Double linearVelocityEditDouble = Double.parseDouble(linearVelocityEdit.getText().toString());
                        Double angularVelocityEditDouble = Double.parseDouble(angularVelocityEdit.getText().toString());
                        move.linear.x = linearVelocityEditDouble;
                        move.linear.y = 0.0;
                        move.linear.z = 0.0;
                        move.angular.x = 0.0;
                        move.angular.y = 0.0;
                        move.angular.z = 0.0;
                        moveTopic.publish(move);
                        Log.i("ControlFragmentBtn","finish forward button onclick!!!");
                    }
                });
                backBtn.setOnClickListener(new View.OnClickListener() {     // 后
                    @Override
                    public void onClick(View view) {
                        Log.i("ControlFragmentBtn","enter back button onclick!!!");;
                        Move move = new Move();
                        move.linear = new Vector3();
                        move.angular = new Vector3();
                        Double linearVelocityEditDouble = Double.parseDouble(linearVelocityEdit.getText().toString());
                        Double angularVelocityEditDouble = Double.parseDouble(angularVelocityEdit.getText().toString());
                        move.linear.x = -linearVelocityEditDouble;
                        move.linear.y = 0.0;
                        move.linear.z = 0.0;
                        move.angular.x = 0.0;
                        move.angular.y = 0.0;
                        move.angular.z = 0.0;
                        moveTopic.publish(move);
                        Log.i("ControlFragmentBtn","finish back button onclick!!!");
                    }

                });
                leftBtn.setOnClickListener(new View.OnClickListener() {     // 左
                    @Override
                    public void onClick(View view) {
                        Log.i("ControlFragmentBtn","enter left button onclick!!!");;
                        Move move = new Move();
                        move.linear = new Vector3();
                        move.angular = new Vector3();
                        Double linearVelocityEditDouble = Double.parseDouble(linearVelocityEdit.getText().toString());
                        Double angularVelocityEditDouble = Double.parseDouble(angularVelocityEdit.getText().toString());
                        move.linear.x = 0.0;
                        move.linear.y = 0.0;
                        move.linear.z = 0.0;
                        move.angular.x = 0.0;
                        move.angular.y = 0.0;
                        move.angular.z = angularVelocityEditDouble;
                        moveTopic.publish(move);
                        Log.i("ControlFragmentBtn","finish left button onclick!!!");;
                    }
                });
                rightBtn.setOnClickListener(new View.OnClickListener() {    // 右
                    @Override
                    public void onClick(View view) {
                        Log.i("ControlFragmentBtn","enter right button onclick!!!");;
                        Move move = new Move();
                        move.linear = new Vector3();
                        move.angular = new Vector3();
                        Double linearVelocityEditDouble = Double.parseDouble(linearVelocityEdit.getText().toString());
                        Double angularVelocityEditDouble = Double.parseDouble(angularVelocityEdit.getText().toString());
                        move.linear.x = 0.0;
                        move.linear.y = 0.0;
                        move.linear.z = 0.0;
                        move.angular.x = 0.0;
                        move.angular.y = 0.0;
                        move.angular.z = -angularVelocityEditDouble;
                        moveTopic.publish(move);
                        Log.i("ControlFragmentBtn","finish right button onclick!!!");;
                    }
                });
                stopBtn.setOnClickListener(new View.OnClickListener() {     // 停
                    @Override
                    public void onClick(View view) {
                        Log.i("ControlFragmentBtn","enter stop button onclick!!!");;
                        Move move = new Move();
                        move.linear = new Vector3();
                        move.angular = new Vector3();
                        Double linearVelocityEditDouble = Double.parseDouble(linearVelocityEdit.getText().toString());
                        Double angularVelocityEditDouble = Double.parseDouble(angularVelocityEdit.getText().toString());
                        move.linear.x = 0.0;
                        move.linear.y = 0.0;
                        move.linear.z = 0.0;
                        move.angular.x = 0.0;
                        move.angular.y = 0.0;
                        move.angular.z = 0.0;
                        moveTopic.publish(move);
                        Log.i("ControlFragmentBtn","finish stop button onclick!!!");;
                    }
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {  // 无点击时默认选择car1
                Log.i("ControlFragment","enter onNothingSelected");
                clientCar = mapCar.get("车1");
                clientCar.connect();
                return ;
            }
        });
        /*******多车spinner选择模块结束**********/
        Log.i("ControlFragment","after_spinner");
        return ;
    }
}